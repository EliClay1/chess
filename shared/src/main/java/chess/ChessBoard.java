package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    /*TODO - Create a data structure that represents the chess board. (an 8x8 grid of some kind) A reminder
    *  That the data structure of choice must be an object type of the ChessPiece class. */

    final ChessPiece[][] gameBoard = new ChessPiece[8][8];

    public ChessBoard() {

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        // this is an array base 0, we need it to be an array base 1.
        gameBoard[position.getRow() - 1][position.getColumn() - 1] = piece;
    }
    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return gameBoard[position.getRow()-1][position.getColumn()-1];
    }

    public void removePiece(ChessPosition position) {
        gameBoard[position.getRow()-1][position.getColumn()-1] = null;
    }

    public void makeMove(ChessMove move) {
        ChessPiece movingPiece = getPiece(move.getStartPosition());
        removePiece(move.getStartPosition());
        addPiece(move.getEndPosition(), movingPiece);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        ChessPiece.PieceType[] frontRowPieces = {ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KING, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.ROOK};

        for (int col = 1; col <= 8; col++) {
            addPiece(new ChessPosition(1, col), new ChessPiece(ChessGame.TeamColor.WHITE, frontRowPieces[col - 1]));
            addPiece(new ChessPosition(8, col), new ChessPiece(ChessGame.TeamColor.BLACK, frontRowPieces[col - 1]));
            addPiece(new ChessPosition(2, col), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
            addPiece(new ChessPosition(7, col), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
    }

    public ChessBoard deepCopy() {
        ChessBoard board = new ChessBoard();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = this.gameBoard[i][j];
                if (piece != null) {
                    // Use the copy constructor or clone method of ChessPiece as appropriate
                    board.gameBoard[i][j] = new ChessPiece(piece.getTeamColor(), piece.getPieceType()); // Or piece.clone()
                } else {
                    board.gameBoard[i][j] = null;
                }
            }
        }
        return board;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {

        return super.clone();
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "gameBoard=" + Arrays.toString(gameBoard) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(gameBoard, that.gameBoard);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(gameBoard);
    }
}
