package handler;

import ui.*;

import java.io.*;
import java.net.Socket;
import java.time.ZonedDateTime;
import java.util.ArrayList;

/**
 * Controller coordinates game logic, UI updates, and network communication.
 * Implements MessageRouter.MessageHandler for structured message handling.
 * 
 * Refactored to use:
 * - Connection for socket I/O abstraction
 * - MessageRouter for protocol parsing
 * - SessionPresenter for UI updates (optional)
 * - GameLog for structured logging
 */
public class Controller implements MessageRouter.MessageHandler {

    // UI references (instance, not static)
    private final InitialUI initUI;
    private final GameUI gameUI;

    // Network
    private final Connection connection;
    private final boolean isServer;
    private final MessageCodec codec;
    private final MessageRouter router;

    // Game state
    private boolean isGameStarted;
    private boolean isWin;
    private boolean isLoggerInit;
    private GameData gameData;
    protected GameState gameState;

    private int lastRow, lastCol;

    // Logging
    private final GameLog gameLog;
    private final ArrayList<String> gameDataList; // Keep for backward compatibility

    // Legacy socket reference for backward compatibility
    private final Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    /**
     * Create a new Controller with the given UI components and socket.
     */
    public Controller(InitialUI ui, GameUI gameUi, Socket socket, boolean isServer) throws IOException {
        this.initUI = ui;
        this.gameUI = gameUi;
        this.socket = socket;
        this.isServer = isServer;

        // Initialize connection wrapper
        this.connection = new Connection(socket);

        // Initialize protocol handling
        this.codec = new MessageCodec();
        this.router = new MessageRouter(this);

        // Initialize game state
        this.isGameStarted = false;
        this.isWin = false;
        this.isLoggerInit = false;
        this.gameState = new GameState();

        // Initialize logging
        this.gameLog = new GameLog();
        this.gameDataList = new ArrayList<>();

        ZonedDateTime dateTime = ZonedDateTime.now();
        gameLog.add("GAME INITIALIZED AT " + dateTime);
        gameLog.add(socket.toString());
        gameDataList.add("GAME INITIALIZED AT " + dateTime);
        gameDataList.add(socket.toString());

        // Legacy I/O setup
        initInputOutput(socket);

        // Wire up UI
        gameUI.setController(this);
        gameUI.gameBoardComponent.setController(this);
    }

    private void initInputOutput(Socket socket) throws IOException {
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    /**
     * Send a raw message over the network.
     */
    public void sendMessage(String message) {
        connection.sendMessage(message);
    }

    /**
     * Send a typed command.
     */
    public void sendCommand(GameCommand command) {
        connection.sendMessage(codec.encode(command));
    }

    /**
     * Send a typed command with payload.
     */
    public void sendCommand(GameCommand command, String payload) {
        connection.sendMessage(codec.encode(command, payload));
    }

    public void requestInit(String message) {
        addLog(message);
        updateGameDataList();
        connection.sendMessage(message);
    }

    public void setGameData(GameData data) {
        gameData = data;
    }

    public void setIsLoggerInit(boolean isLoggerInit) {
        this.isLoggerInit = isLoggerInit;
    }

    public void addGameData(String data) {
        addLog(data);
    }

    /**
     * Add entry to both logging systems for backward compatibility.
     */
    private void addLog(String message) {
        gameLog.add(message);
        gameDataList.add(message);
    }

    /**
     * Process a raw protocol message.
     * Routes through MessageRouter for structured handling.
     */
    public void processMessage(String message) {
        router.route(message);
    }

    // ========== MessageRouter.MessageHandler Implementation ==========

    @Override
    public void handleServerInit() {
        if (!isGameStarted && gameData != null) {
            connection.sendMessage(gameData.toServer());
        }
    }

    @Override
    public void handleClientInit() {
        if (!isGameStarted && gameData != null) {
            connection.sendMessage(gameData.toClient());
        }
    }

    @Override
    public void handleInit(String data) {
        handleUIData(data);
    }

    @Override
    public void handleMove(String moveData) {
        processMoveData(moveData);
    }

    @Override
    public void handleReady() {
        sendCommand(GameCommand.START);
    }

    @Override
    public void handleStart() {
        addLog("START");
        updateGameDataList();
        processStart();
    }

    @Override
    public void handleWithdraw(String data) {
        if ("OK".equals(data)) {
            requestWithdraw();
        } else if ("DENIED".equals(data)) {
            gameUI.showMessage("Withdraw Denied");
            addLog("REQUEST RESULT: DENIED");
            updateGameDataList();
        } else {
            processWithdrawRequest();
        }
    }

    @Override
    public void handleCanPlay() {
        gameUI.gameBoardComponent.setCanPlay(true);
    }

    @Override
    public void handleWin(String winner) {
        processWin(winner);
    }

    @Override
    public void handleLose(String loser) {
        processLose(loser);
    }

    @Override
    public void handleTie() {
        processTie();
    }

    @Override
    public void handlePattern(String pattern) {
        processPattern(pattern);
    }

    @Override
    public void handleRestartInit() {
        restartInit();
    }

    @Override
    public void handleRestart() {
        processRestart();
    }

    @Override
    public void handleTurnTime(String time) {
        int t = Integer.parseInt(time);
        gameUI.gameBoardComponent.updateTurnTime(t);
    }

    @Override
    public void handlePlayerTime(String time) {
        double t = Double.parseDouble(time);
        gameUI.gameBoardComponent.updatePlayerTime(t);
    }

    @Override
    public void handleBye() {
        closeConnection();
    }

    @Override
    public void handleUnknown(String rawMessage) {
        System.err.println("Unknown message received: " + rawMessage);
    }

    // ========== Internal Message Handlers ==========

    private void handleUIData(String data) {
        addLog("INIT DATA: " + data);
        updateGameDataList();
        String[] initData = data.split("\\|");
        String flag = initData[0];
        String name = initData[1];

        if (flag.equals("1")) { // i'm server
            String turnTime = initUI.getTurnTime();

            if (!turnTime.equals("∞")) {
                turnTime = turnTime.substring(0, turnTime.length() - 1);
            } else
                turnTime = "-1";

            String playerTime = initUI.getPlayerTime();
            if (!playerTime.equals("∞")) {
                playerTime = playerTime.substring(0, playerTime.length() - 3);
            } else
                playerTime = "-1";

            String withdrawCount = initUI.getWithdrawCount();
            if (withdrawCount.equals("∞"))
                withdrawCount = "-1";

            setupUI(flag, "2", initUI.getPlayerName(), name, turnTime, playerTime,
                    initUI.getFirstPlayer(), withdrawCount, true);
        } else if (flag.equals("2")) { // i'm client
            String turnTime = initData[2];
            String playerTime = initData[3];
            int firstPlayer = Integer.parseInt(initData[4]);
            String withdrawCount = initData[5];
            setupUI(flag, "1", initUI.getPlayerName(), name, turnTime, playerTime,
                    firstPlayer, withdrawCount, false);
        }
    }

    private void setupUI(String flag1, String flag2, String p1Name, String p2Name, String turnTime, String playerTime,
            int firstPlayer, String withdrawCount, boolean isBlack) {
        gameUI.setPlayerFlag(flag1);
        gameUI.setPlayerName(flag1, p1Name); // server set server name
        gameUI.setPlayerName(flag2, p2Name);
        gameUI.setTurnTime(turnTime);
        gameUI.setPlayerTime(playerTime);
        gameUI.setFirstPlayer(firstPlayer);
        gameUI.setWithdrawCount(withdrawCount);
        gameUI.setWinRound(-1);
        gameUI.gameBoardComponent.setIsBlack(isBlack);
        gameUI.gameBoardComponent.setPlayer();
        gameUI.gameBoardComponent.setTurnTime(Integer.parseInt(turnTime));
        gameUI.gameBoardComponent.setPlayerTime(!playerTime.equals("-1") ? Double.parseDouble(playerTime) * 60 : -1);
        gameUI.gameBoardComponent.setConstantTime(Integer.parseInt(turnTime));
        sendCommand(GameCommand.READY);
        addLog("READY");
        updateGameDataList();
    }

    private void processMoveData(String moveData) {
        String[] coords = moveData.split("\\|");
        int player = Integer.parseInt(coords[0]);
        int row = Integer.parseInt(coords[1]);
        int col = Integer.parseInt(coords[2]);

        if (gameState.isValidMove(row, col)) {
            gameState.setCurrentPlayer(player);
            String playerColor = (player == 1) ? "Black" : "White";
            gameState.makeMove(row, col);
            String message = playerColor + " placed stone at " + Character.toString(col + 'A') + (row + 1);
            addLog(message);
            updateGameDataList();
            lastRow = row;
            lastCol = col;
            updateGameBoard(player, row, col);

            if (!isWin) {
                if (gameState.checkWin(row, col)) {
                    gameUI.setWinRound(player);
                    processWin(playerColor);
                    sendCommand(GameCommand.WIN, playerColor);
                } else if (gameState.isTieGame()) {
                    processTie();
                    sendCommand(GameCommand.TIE);
                } else {
                    String pattern = gameState.checkPatterns(row, col);
                    gameUI.gameBoardComponent.setCanPlay(false);
                    if (player == gameUI.getPlayerFlag()) {
                        sendCommand(GameCommand.CAN_PLAY);
                        if (!pattern.isEmpty()) {
                            sendCommand(GameCommand.PATTERN, pattern);
                        }
                    }
                }
            }
        }
    }

    private void processStart() {
        isGameStarted = true;
        if (isServer && gameUI.getFirstPlayer() == 1) {
            gameUI.gameBoardComponent.setCanPlay(true);
        } else if (!isServer && gameUI.getFirstPlayer() == 2) {
            gameUI.gameBoardComponent.setCanPlay(true);
        }
    }

    private void processWithdrawRequest() {
        String player = gameState.getCurrentPlayer() == 1 ? "White" : "Black";
        addLog(player + " REQUEST WITHDRAW");
        if (gameState.withdrawMove()) {
            gameUI.gameBoardComponent.clearPosition(lastRow, lastCol);
            gameUI.gameBoardComponent.setFlagIcon(gameState.getCurrentPlayer());
            gameUI.gameBoardComponent.setCanPlay(false);
            sendCommand(GameCommand.CAN_PLAY);
            sendCommand(GameCommand.WITHDRAW, "OK");
            addLog("REQUEST RESULT: OK");
        } else {
            sendCommand(GameCommand.WITHDRAW, "DENIED");
            addLog("REQUEST RESULT: DENIED");
        }
        updateGameDataList();
    }

    private void requestWithdraw() {
        if (gameUI.gameBoardComponent.getCanPlay()) {
            if (gameState.withdrawMove()) {
                gameUI.gameBoardComponent.setFlagIcon(gameState.getCurrentPlayer());
                gameUI.gameBoardComponent.clearPosition(lastRow, lastCol);
                if (gameUI.getWithdrawCount() != -1)
                    gameUI.setWithdrawCount(gameUI.getWithdrawCount() - 1);
                addLog("REQUEST RESULT: OK");
            } else {
                gameUI.showMessage("Withdraw failed");
                addLog("REQUEST RESULT: FAILED");
            }
        }
        updateGameDataList();
    }

    private void updateGameBoard(int player, int row, int col) {
        gameUI.gameBoardComponent.updateGameBoard(player, row, col);
        gameUI.gameBoardComponent.setCanPlay(false);
    }

    private void processWin(String winner) {
        if (!isWin) {
            isWin = true;
            String message = winner + " wins!";
            gameUI.showMessage(message);
            gameUI.gameBoardComponent.setCanPlay(false);
            addLog(message);
            updateGameDataList();
        }
    }

    private void processLose(String loseData) {
        String loser = (Integer.parseInt(loseData) == 1) ? "Black" : "White";
        String winner = loser.equals("Black") ? "White" : "Black";
        if (!isWin) {
            gameUI.showMessage(loser + " lose! You win!");
            gameUI.gameBoardComponent.setCanPlay(false);
            isWin = true;
            addLog(loser + " lose! " + winner + " win!");
            updateGameDataList();
        }
    }

    private void processTie() {
        if (!isWin) {
            gameUI.showMessage("Tie Game!");
            gameUI.gameBoardComponent.setCanPlay(false);
            addLog("TIE GAME");
            updateGameDataList();
        }
    }

    private void processPattern(String pattern) {
        if (!isWin) {
            addLog(pattern);
            gameUI.showMessage(pattern);
            updateGameDataList();
        }
    }

    private void restartInit() {
        ZonedDateTime dateTime = ZonedDateTime.now();
        addLog("");
        addLog("RESTART AT " + dateTime);
        updateGameDataList();
        gameUI.gameBoardComponent.stopTimer();
        gameUI.gameBoardComponent.setCanPlay(false);
        isGameStarted = false;
        gameState = new GameState();
        isWin = false;
    }

    private void processRestart() {
        if (isServer)
            requestInit(GameFlags.SERVER_INIT);
        else
            requestInit(GameFlags.CLIENT_INIT);
        gameUI.gameBoardComponent.clearButtonIcons();
    }

    private void updateGameDataList() {
        if (isLoggerInit) {
            gameUI.setDataList(gameDataList);
        }
    }

    public ArrayList<String> getGameDataList() {
        return gameDataList;
    }

    /**
     * Get the game log for structured logging access.
     */
    public GameLog getGameLog() {
        return gameLog;
    }

    public void closeConnection() {
        try {
            connection.close();
            gameUI.gameBoardComponent.clearButtonIcons();
            gameUI.gameBoardComponent.setCanPlay(false);
            gameUI.setGameStarted(false);
            if (gameUI.getCurrentFrame() != null) {
                initUI.setPlayButtonEnable(false);
                initUI.setClientState(false);
                gameUI.getCurrentFrame().dispose();
            }
            if (!isServer) {
                initUI.getConnectBtn().setText("Connect");
                initUI.getStartServerBtn().setEnabled(true);
                initUI.setStatusText("Disconnected and waiting for connection.");
            } else {
                initUI.getStartServerBtn().setText("Start Server");
                initUI.getConnectBtn().setEnabled(true);
                initUI.setStatusText("Disconnected and waiting for connection.");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
