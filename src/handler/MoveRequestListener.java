package handler;

/**
 * Interface for receiving move requests from the game board.
 * Decouples the UI button handling from the controller.
 */
public interface MoveRequestListener {

    /**
     * Called when a move is requested on the board.
     * 
     * @param player the player making the move (1 = black, 2 = white)
     * @param row    the row of the requested move
     * @param col    the column of the requested move
     */
    void onMoveRequested(int player, int row, int col);
}
