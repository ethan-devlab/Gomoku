package handler;

public class GameState {
    private int[][] board;
    private int currentPlayer;
    private int withdrawCount;
    private int lastRow; // Add last move tracking
    private int lastCol;
    
    public GameState() {
        board = new int[15][15];
        currentPlayer = 1; // 1 for black, 2 for white
        withdrawCount = 0; // Allow one withdraw per player
        lastRow = -1;
        lastCol = -1;
    }
    
    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setWithdrawCount(int withdrawCount) {
        this.withdrawCount = withdrawCount;
    }

    public int getWithdrawCount() {
        return this.withdrawCount;
    }

    public int getLastRow() {
        return lastRow;
    }

    public int getLastCol() {
        return lastCol;
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
        int[][] directions = {{0,1}, {1,0}, {1,1}, {1,-1}}; // horizontal, vertical, diagonal

        for (int[] dir : directions) {
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
    
    public boolean withdrawMove(boolean isSelf) {
        if (withdrawCount > 0 || withdrawCount == -1 && lastRow != -1 && lastCol != -1) {
            board[lastRow][lastCol] = 0;
            currentPlayer = (currentPlayer == 1) ? 2 : 1;
            if (isSelf && withdrawCount != -1) withdrawCount--;  // if is infinite or not self, pass
            lastRow = -1;
            lastCol = -1;
            return true;
        }
        return false;
    }
}