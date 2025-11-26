package handler;

public class GameState {
    private final int[][] board;
    private int currentPlayer;
    private int lastRow;
    private int lastCol;
    final int[][] DIRECTIONS = {{0,1}, {1,0}, {1,1}, {1,-1}};
    
    public GameState() {
        board = new int[15][15];
        currentPlayer = 1; // 1 for black, 2 for white
        lastRow = -1;
        lastCol = -1;
    }
    
    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isValidMove(int row, int col) {
        return row >= 0 && row < 15 && col >= 0 && col < 15 && board[row][col] == 0;
    }
    
    public void makeMove(int row, int col) {
        board[row][col] = currentPlayer;
        lastRow = row;
        lastCol = col;
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
    }

    public boolean checkWin(int row, int col) {
        int player = board[row][col];

        for (int[] dir : DIRECTIONS) {
            int count = 1;
            
            // Check forward direction
            for (int i = 1; i < 5; i++) {
                int newRow = row + dir[0] * i;
                int newCol = col + dir[1] * i;
                if (newRow < 0 || newRow >= 15 || newCol < 0 || newCol >= 15 
                    || board[newRow][newCol] != player) {
                    break;
                }
                count++;
            }
            
            // Check backward direction
            for (int i = 1; i < 5; i++) {
                int newRow = row - dir[0] * i;
                int newCol = col - dir[1] * i;
                if (newRow < 0 || newRow >= 15 || newCol < 0 || newCol >= 15 
                    || board[newRow][newCol] != player) {
                    break;
                }
                count++;
            }
            
            if (count >= 5) return true;
        }
        return false;
    }
    
    public boolean withdrawMove() {
        if (lastRow != -1 && lastCol != -1) {
            board[lastRow][lastCol] = 0;
            currentPlayer = (currentPlayer == 1) ? 2 : 1;
            lastRow = -1;
            lastCol = -1;
            return true;
        }
        return false;
    }

    public boolean isTieGame() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (board[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public String checkPatterns(int row, int col) {
        int player = board[row][col];
        String message = "";
        for (int[] dir : DIRECTIONS) {
            message = checkPattern(row, col, dir[0], dir[1], player);
            if (!message.isEmpty()) {
                break;
            }
        }
        return message;
    }

    private String checkPattern(int row, int col, int dx, int dy, int player) {
        int count = 1;
        boolean leftBlocked = false;
        boolean rightBlocked = false;
        int patternType = 0;
        String patternName;
        String playerColor = (player == 1) ? "Black" : "White";

        // Check left
        for (int i = 1; i <= 4; i++) {
            int newRow = row - dx * i;
            int newCol = col - dy * i;
            
            if (newRow < 0 || newRow >= 15 || newCol < 0 || newCol >= 15) {
                leftBlocked = true;
                break;
            }
            
            if (board[newRow][newCol] == 0) {
                break;
            }
            
            if (board[newRow][newCol] != player) {
                leftBlocked = true;
                break;
            }
            count++;
        }

        // Check right
        for (int i = 1; i <= 4; i++) {
            int newRow = row + dx * i;
            int newCol = col + dy * i;
            
            if (newRow < 0 || newRow >= 15 || newCol < 0 || newCol >= 15) {
                rightBlocked = true;
                break;
            }
            
            if (board[newRow][newCol] == 0) {
                break;
            }
            
            if (board[newRow][newCol] != player) {
                rightBlocked = true;
                break;
            }
            count++;
        }

        // Determine pattern type
        if (count == 3) {
            if (!leftBlocked && !rightBlocked) {
                patternType = GameFlags.ALIVE_THREE;
            } else if (!leftBlocked || !rightBlocked) {
                patternType = GameFlags.DEAD_THREE;
            }
        } else if (count == 4) {
            if (!leftBlocked && !rightBlocked) {
                patternType = GameFlags.ALIVE_FOUR;
            } else if (!leftBlocked || !rightBlocked) {
                patternType = GameFlags.DEAD_FOUR;
            }
        }

        switch (patternType) {
            case GameFlags.DEAD_THREE:
                patternName = "Dead Three";
                break;
            case GameFlags.ALIVE_THREE:
                patternName = "Alive Three";
                break;
            case GameFlags.DEAD_FOUR:
                patternName = "Dead Four";
                break;
            case GameFlags.ALIVE_FOUR:
                patternName = "Alive Four";
                break;
            default:
                return "";
        }

        return playerColor + " has formed " + patternName + "!";
    }
}