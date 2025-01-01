package handler;

import ui.*;

import java.io.*;
import java.net.Socket;
import java.time.ZonedDateTime;
import java.util.ArrayList;


public class Controller {
    private static InitialUI initUI;
    private static GameUI gameUI;
    private PrintWriter out;
    private BufferedReader in;
    private final Socket socket;
    private final boolean isServer;
    private boolean isGameStarted;
    private boolean isWin;
    private boolean isLoggerInit;
    private static GameData gameData;
    protected GameState gameState;

    private int lastRow, lastCol;

    private final ArrayList<String> gameDataList;


    public Controller(InitialUI ui, GameUI gameUi, Socket socket, boolean isServer) throws IOException {
        isGameStarted = false;
        isWin = false;
        isLoggerInit = false;
        initUI = ui;
        gameUI = gameUi;
        gameUI.setController(this);
        gameUI.gameBoardComponent.setController(this);
        this.socket = socket;
        this.isServer = isServer;
        ZonedDateTime dateTime = ZonedDateTime.now();
        gameState = new GameState();
        gameDataList = new ArrayList<>();
        gameDataList.add("GAME INITIALIZED AT " + dateTime);
        gameDataList.add(socket.toString());
        initInputOutput(this.socket);
    }

    private void initInputOutput(Socket socket) throws IOException {
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void requestInit(String message) {
        gameDataList.add(message);
        updateGameDataList();
        out.println(message);
    }

    public void setGameData(GameData data) {
        gameData = data;
    }

    public void setIsLoggerInit(boolean isLoggerInit) {
        this.isLoggerInit = isLoggerInit;
    }

    public void addGameData(String data) {
        gameDataList.add(data);
    }

    public void processMessage(String message) {
        String[] parts = message.split(":", 2);
        String command = parts[0];
        String data = parts.length > 1 ? parts[1] : "";

        switch (command) {
            // request from client
            case GameFlags.CLIENT_INIT:
                if (!isGameStarted) {
                    out.println(gameData.toClient());
                }
                break;
            // request from server
            case GameFlags.SERVER_INIT:
                if (!isGameStarted) {
                    out.println(gameData.toServer());
                }
                break;
            case GameFlags.INIT:
                handleUIData(data);
                break;
            case GameFlags.MOVE:
                handleMove(data);
                break;
            case GameFlags.READY:
                out.println(GameFlags.START);
                break;
            case GameFlags.START:
                gameDataList.add("START");
                updateGameDataList();
                handleStart();

                break;
            case GameFlags.WITHDRAW:
                if (data.equals("OK")) requestWithdraw();
                else if (data.equals("DENIED")) {
                    gameUI.showMessage("Withdraw Denied");
                    gameDataList.add("REQUEST RESULT: DENIED");
                    updateGameDataList();
                }
                else handleWithdraw();
                break;
            case GameFlags.CAN_PLAY:
                gameUI.gameBoardComponent.setCanPlay(true);
                break;
            case GameFlags.WIN:
                handleWin(data);
                break;
            case GameFlags.LOSE:
                handleLose(data);
                break;
            case GameFlags.TIE:
                handleTie();
                break;
            case GameFlags.PATTERN:
                handlePattern(data);
                break;
            case GameFlags.RESTART_INIT:
                restartInit();
                break;
            case GameFlags.RESTART:
                handleRestart();
                break;
            case GameFlags.TURN_TIME:
                handleTurnTime(data);
                break;
            case GameFlags.PLAYER_TIME:
                handlePlayerTime(data);
                break;
            case GameFlags.BYE:
                closeConnection();
                break;
        }
    }

    private void handleUIData(String data) {
        gameDataList.add("INIT DATA: " + data);
        updateGameDataList();
        String[] initData = data.split("\\|");
        String flag = initData[0];
        String name = initData[1];

        if (flag.equals("1")) { // i'm server
            String turnTime = initUI.getTurnTime();

            if (!turnTime.equals("∞")) {
                turnTime = turnTime.substring(0, turnTime.length() - 1);
            } else turnTime = "-1";

            String playerTime = initUI.getPlayerTime();
            if (!playerTime.equals("∞")) {
                playerTime = playerTime.substring(0, playerTime.length() - 3);
            } else playerTime = "-1";

            String withdrawCount = initUI.getWithdrawCount();
            if (withdrawCount.equals("∞")) withdrawCount = "-1";

            setupUI(flag, "2", initUI.getPlayerName(), name, turnTime, playerTime,
                    initUI.getFirstPlayer(), withdrawCount, true);
        }
        else if (flag.equals("2")) { // i'm client
            String turnTime = initData[2];
            String playerTime = initData[3];
            int firstPlayer = Integer.parseInt(initData[4]);
            String withdrawCount = initData[5];
            setupUI(flag, "1", initUI.getPlayerName(), name, turnTime, playerTime,
                    firstPlayer, withdrawCount, false);
        }
    }

    private void setupUI(String flag1, String flag2, String p1Name, String p2Name, String turnTime,  String playerTime,
                          int firstPlayer, String withdrawCount, boolean isBlack) {
        gameUI.setPlayerFlag(flag1);
        gameUI.setPlayerName(flag1, p1Name); // server set server name
        gameUI.setPlayerName(flag2, p2Name);
        gameUI.setTurnTime(turnTime);
        gameUI.setPlayerTime(playerTime);
        gameUI.setFirstPlayer(firstPlayer);
        gameUI.setWithdrawCount(withdrawCount);
        gameUI.setWinRound(-1);
//        gameState.setWithdrawCount(Integer.parseInt(withdrawCount));
        gameUI.gameBoardComponent.setIsBlack(isBlack);
        gameUI.gameBoardComponent.setPlayer();
        gameUI.gameBoardComponent.setTurnTime(Integer.parseInt(turnTime));
        gameUI.gameBoardComponent.setPlayerTime(!playerTime.equals("-1") ? Double.parseDouble(playerTime) * 60 : -1);
        gameUI.gameBoardComponent.setConstantTime(Integer.parseInt(turnTime));
        out.println(GameFlags.READY);
        gameDataList.add("READY");
        updateGameDataList();
    }

    private void handleMove(String moveData) {
        String[] coords = moveData.split("\\|");
        int player = Integer.parseInt(coords[0]);
        int row = Integer.parseInt(coords[1]);
        int col = Integer.parseInt(coords[2]);

        if (gameState.isValidMove(row, col)) {
            gameState.setCurrentPlayer(player);
            String playerColor = (player == 1) ? "Black" : "White";
            gameState.makeMove(row, col);
            String message = playerColor + " placed stone at " + Character.toString(col + 'A') + (row + 1);
            gameDataList.add(message);
            updateGameDataList();
            lastRow = row;
            lastCol = col;
            updateGameBoard(player, row, col);

            if (!isWin) {
                if (gameState.checkWin(row, col)) {
                    gameUI.setWinRound(player);
                    handleWin(playerColor);
                    out.println("WIN:" + playerColor);
                }
                else if (gameState.isTieGame()) {
                    handleTie();
                    out.println(GameFlags.TIE);
                } 
                else {
                    String pattern = gameState.checkPatterns(row, col);
                    gameUI.gameBoardComponent.setCanPlay(false);
                    if (player == gameUI.getPlayerFlag()) {
                        out.println("CAN_PLAY");
                        if (!pattern.isEmpty()) out.println("PATTERN:" + pattern);
                    }
                }
            }
        }
    }

    private void handleStart() {
        isGameStarted = true;
        if (isServer && gameUI.getFirstPlayer() == 1) {
            gameUI.gameBoardComponent.setCanPlay(true);
        }
        else if (!isServer && gameUI.getFirstPlayer() == 2) {
            gameUI.gameBoardComponent.setCanPlay(true);
        }
    }

    private void handleWithdraw() {
        String player = gameState.getCurrentPlayer() == 1 ? "White" : "Black";
        gameDataList.add(player + " REQUEST WITHDRAW");
        if (gameState.withdrawMove()) {
            gameUI.gameBoardComponent.clearPosition(lastRow, lastCol);
            gameUI.gameBoardComponent.setFlagIcon(gameState.getCurrentPlayer());
            gameUI.gameBoardComponent.setCanPlay(false);
            out.println("CAN_PLAY");
            out.println("WITHDRAW:OK");
            gameDataList.add("REQUEST RESULT: OK");
        }
        else {
            out.println("WITHDRAW:DENIED");
            gameDataList.add("REQUEST RESULT: DENIED");
        }
        updateGameDataList();
    }

    private void requestWithdraw() {
        if (gameUI.gameBoardComponent.getCanPlay()) {
            if (gameState.withdrawMove()) {
                gameUI.gameBoardComponent.setFlagIcon(gameState.getCurrentPlayer());
                gameUI.gameBoardComponent.clearPosition(lastRow, lastCol);
                gameUI.setWithdrawCount(gameUI.getWithdrawCount() - 1);
                gameDataList.add("REQUEST RESULT: OK");
            }
            else {
                gameUI.showMessage("Withdraw failed");
                gameDataList.add("REQUEST RESULT: FAILED");
            }
        }
        updateGameDataList();
    }

    private void updateGameBoard(int player, int row, int col) {
        gameUI.gameBoardComponent.updateGameBoard(player, row, col);
        gameUI.gameBoardComponent.setCanPlay(false);
    }

    private void handleWin(String winner) {
        if (!isWin) {
            isWin = true;
            String message = winner + " wins!";
            gameUI.showMessage(message);
            gameUI.gameBoardComponent.setCanPlay(false);
            gameDataList.add(message);
            updateGameDataList();
        }
    }

    private void handleLose(String loseData) {
        String loser = (Integer.parseInt(loseData) == 1) ? "Black" : "White";
        String winner = loser.equals("Black") ? "White" : "Black";
        if (!isWin) {
            gameUI.showMessage(loser + " lose! You win!");
            gameUI.gameBoardComponent.setCanPlay(false);
            isWin = true;
            gameDataList.add(loser + " lose! " + winner + " win!");
            updateGameDataList();
        }
    }

    private void handleTie() {
        if (!isWin) {
            gameUI.showMessage("Tie Game!");
            gameUI.gameBoardComponent.setCanPlay(false);
            gameDataList.add("TIE GAME");
            updateGameDataList();
        }
    }

    private void handlePattern(String pattern) {
        if (!isWin) {
            gameDataList.add(pattern);
            gameUI.showMessage(pattern);
            updateGameDataList();
        }
    }

    private void handleTurnTime(String data) {
        int time = Integer.parseInt(data);
        gameUI.gameBoardComponent.updateTurnTime(time);
    }

    private void handlePlayerTime(String data) {
        double time = Double.parseDouble(data);
        gameUI.gameBoardComponent.updatePlayerTime(time);
    }

    private void restartInit() {
        ZonedDateTime dateTime = ZonedDateTime.now();
        gameDataList.add("");
        gameDataList.add("RESTART AT " + dateTime);
        updateGameDataList();
        gameUI.gameBoardComponent.stopTimer();
        gameUI.gameBoardComponent.setCanPlay(false);
        isGameStarted = false;
        gameState = new GameState();
        isWin = false;
    }

    private void handleRestart() {
        if (isServer) requestInit(GameFlags.SERVER_INIT);
        else requestInit(GameFlags.CLIENT_INIT);
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

    public void closeConnection() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
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
            }
            else {
                initUI.getStartServerBtn().setText("Start Server");
                initUI.getConnectBtn().setEnabled(true);
                initUI.setStatusText("Disconnected and waiting for connection.");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
