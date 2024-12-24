package handler;

import ui.*;

import java.io.*;
import java.net.Socket;
import java.security.interfaces.RSAPrivateCrtKey;


public class Controller {
    private static InitialUI initUI;
    private static GameUI gameUI;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;
    private boolean isServer;
    private boolean isGameStarted;
    private boolean isWin;
    private static GameData gameData;
    protected GameState gameState;

    private int lastRow, lastCol;
    
    public Controller(InitialUI ui, GameUI gameUi, Socket socket, boolean isServer) throws IOException {
        isGameStarted = false;
        isWin = false;
        initUI = ui;
        gameUI = gameUi;
        gameUI.setController(this);
        gameUI.gameBoardComponent.setController(this);
        this.socket = socket;
        this.isServer = isServer;
        gameState = new GameState();
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
        out.println(message);
    }

    public void setGameData(GameData data) {
        gameData = data;
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
                handleStart();
                break;
            case GameFlags.WITHDRAW:
                if (data.equals("OK")) requestWithdraw();
                else if (data.equals("DENIED")) gameUI.showMessage("Withdraw Denied");
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
        gameState.setWithdrawCount(Integer.parseInt(withdrawCount));
        gameUI.gameBoardComponent.setIsBlack(isBlack);
        gameUI.gameBoardComponent.setPlayer();
        gameUI.gameBoardComponent.setTurnTime(Integer.parseInt(turnTime));
        gameUI.gameBoardComponent.setPlayerTime(!playerTime.equals("-1") ? Double.parseDouble(playerTime) * 60 : -1);
        gameUI.gameBoardComponent.setConstantTime(Integer.parseInt(turnTime));
        out.println(GameFlags.READY);
    }

    private void handleMove(String moveData) {
        String[] coords = moveData.split("\\|");
        int player = Integer.parseInt(coords[0]);
        int row = Integer.parseInt(coords[1]);
        int col = Integer.parseInt(coords[2]);

        if (gameState.isValidMove(row, col)) {
            gameState.setCurrentPlayer(player);
            gameState.makeMove(row, col);
            lastRow = row;
            lastCol = col;
            updateGameBoard(player, row, col);

            if (!isWin) {
                if (gameState.checkWin(row, col)) {
                    isWin = true;
                    String winner = (player == 1) ? "Black" : "White";
                    gameUI.setWinRound(player);
                    gameUI.showMessage(winner + " wins!");
                    gameUI.gameBoardComponent.setCanPlay(false);
                    out.println("WIN:" + player);
                } else {
                    gameUI.gameBoardComponent.setCanPlay(false);
                    if (player == gameUI.getPlayerFlag()) out.println("CAN_PLAY");
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
        if (gameState.withdrawMove(false)) {
            gameUI.gameBoardComponent.clearPosition(lastRow, lastCol);
            gameUI.gameBoardComponent.setFlagIcon(gameState.getCurrentPlayer());
            gameUI.gameBoardComponent.setCanPlay(false);
            out.println("CAN_PLAY");
            out.println("WITHDRAW:OK");
        }
        else {
            out.println("WITHDRAW:DENIED");
        }
    }

    private void requestWithdraw() {
        if (gameUI.gameBoardComponent.getCanPlay()) {
            if (gameState.withdrawMove(true)) {
                gameUI.gameBoardComponent.setFlagIcon(gameState.getCurrentPlayer());
                gameUI.gameBoardComponent.clearPosition(lastRow, lastCol);
                gameUI.setWithdrawCount(gameUI.getWithdrawCount() - 1);
            }
            else gameUI.showMessage("Withdraw failed");
        }
    }

    private void updateGameBoard(int player, int row, int col) {
        gameUI.gameBoardComponent.updateGameBoard(player, row, col);
        gameUI.gameBoardComponent.setCanPlay(false);
    }

    private void handleWin(String winData) {
        int winner = Integer.parseInt(winData);
        String winnerText = (winner == 1) ? "Black" : "White";
        if (!isWin) {
            gameUI.showMessage(winnerText + " wins!");
            gameUI.gameBoardComponent.setCanPlay(false);
            isWin = true;
        }
    }

    private void handleLose(String loseData) {
        String loser = (Integer.parseInt(loseData) == 1) ? "Black" : "White";
        if (!isWin) {
            gameUI.showMessage(loser + " lose! You win!");
            gameUI.gameBoardComponent.setCanPlay(false);
            isWin = true;
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
        gameState = new GameState();
        isWin = false;
        isGameStarted = false;
        sendMessage(GameFlags.RESTART);
    }

    private void handleRestart() {
        if (isServer) requestInit(GameFlags.SERVER_INIT);
        else requestInit(GameFlags.CLIENT_INIT);
        gameUI.gameBoardComponent.clearButtonIcons();
    }

    public void closeConnection() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
            gameUI.gameBoardComponent.clearButtonIcons();
            gameUI.gameBoardComponent.setCanPlay(false);
            gameUI.setGameStarted(false);
            if (gameUI.getCurrentFrame() != null) gameUI.getCurrentFrame().dispose();
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
            e.printStackTrace();
        }
    }
}
