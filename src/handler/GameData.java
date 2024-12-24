package handler;

public class GameData {
    private final String playerName;
    private int turnTime = -1;
    private int playerTime = -1;
    private int firstPlayer = 0;
    private int withdrawCount = -1;
    private String playerFlag = "1";
    
    // For server use
    public GameData(String playerFlag, String playerName, String turnTime, String playerTime,
                   int firstPlayer, String withdrawCount) {
        this.playerFlag = playerFlag;            
        this.playerName = playerName;
        this.turnTime = !turnTime.equals("∞") ? Integer.parseInt(turnTime.substring(0, turnTime.length() - 1)) : -1;
        this.playerTime = !playerTime.equals("∞") ? Integer.parseInt(playerTime.substring(0, playerTime.length() - 3)) : -1;
        this.firstPlayer = firstPlayer;
        this.withdrawCount = !withdrawCount.equals("∞") ? Integer.parseInt(withdrawCount) : -1;
    }

    // For client use
    public GameData(String playerFlag, String playerName) {
        this.playerName = playerName;
        this.playerFlag = playerFlag;
    }
    
    public String toClient() {
        return String.format("INIT:%s|%s|%d|%d|%d|%d",
            playerFlag, playerName, turnTime, playerTime, firstPlayer, withdrawCount);
    }

    public String toServer() {
        return String.format("INIT:%s|%s", playerFlag, playerName);
    }
}