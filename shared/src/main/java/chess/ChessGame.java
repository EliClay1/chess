package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard startingBoard = new ChessBoard();
    private TeamColor currentTurn = TeamColor.WHITE;

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        // TODO - Figure out what the heck is going on here. Also, look into prior code design; match the principles.
        Collection<ChessMove> validMoves = new ArrayList<>();

        ChessPiece piece = getBoard().getPiece(startPosition);
        if (piece == null) {
            return List.of();
        }
        TeamColor teamColor = piece.getTeamColor();
        Collection<ChessMove> possibleMoves = piece.pieceMoves(getBoard(), startPosition);

        for (ChessMove chessMove : possibleMoves) {
            ChessGame clonedGame = deepCopy();
            clonedGame.getBoard().makeMove(chessMove);
            // don't forget to add checkmate and stalemate checking.
            if (!clonedGame.isInCheck(teamColor)) {
                validMoves.add(chessMove);
            }
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // Validate that it’s the current player’s piece.
        //
        //Check that the move is valid according to validMoves for that piece.
        //
        //Update the board to reflect the move.
        //
        //Switch the turn to the other player.
        //
        //Throw InvalidMoveException if move not valid.

        ChessPosition startingPosition = move.getStartPosition();

        var piece = getBoard().getPiece(startingPosition);
        if (piece == null || piece.getTeamColor() != currentTurn) {
            throw new InvalidMoveException("That isn't a valid move!");
        }

        // TODO - This needs to be redone. Think through the code design.
        var validMoves = validMoves(startingPosition);
        boolean notinMoves = false;
        for (var possibleMove : validMoves) {
            if (possibleMove != move) {
                notinMoves = true;
                break;
            }
        }
        if (notinMoves) {
            throw new InvalidMoveException("That isn't a valid move!");
        }

        // TODO - Need to create the function that makes moves possible.
//        getBoard().makeMove(move);
        switchTurn();



        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingsPosition = findKingsPosition(teamColor);
        List<ChessPosition> allEnemyPositions = getAllEnemyPositions(getOppositeTeamColor(teamColor));
        return new BoardSearcher().isPositionAttacked(getBoard(), kingsPosition, allEnemyPositions);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {

        /*
        * 1. run isInCheck(); if this returns true, then check if any valid move from the teamColor could get out of check
        * if this returns false, then return false for this function as well.
        *
        * 2. If no such move exists, return true;
        * */
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        /*
        * 1. run isInCheck();
        * if this is false, return false;
        *
        * 2. Check if teamColor has any valid moves. If the list is empty, return true;
        * */


        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        startingBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return startingBoard;
    }

    // function to switch the player turn after the move has been made.
    private void switchTurn() {
        if (currentTurn == TeamColor.WHITE) {
            currentTurn =TeamColor.BLACK;
        } else if (currentTurn == TeamColor.BLACK) {
            currentTurn = TeamColor.WHITE;
        }
    }

    private TeamColor getOppositeTeamColor(TeamColor color) {
        if (color == TeamColor.WHITE) {
            return TeamColor.BLACK;
        }
        return TeamColor.WHITE;
    }

    private ChessPosition findKingsPosition(TeamColor teamColor) {
        ChessPosition kingsPosition = null;
        PieceFilter currentTeamKingPosition = piece -> piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor;
        for (var position : new BoardSearcher().findChessPieces(getBoard(), currentTeamKingPosition)) {
            kingsPosition = position;
        }
        return kingsPosition;
    }

    private List<ChessPosition> getAllEnemyPositions(TeamColor enemyColor) {
        PieceFilter enemyPositions = piece -> piece.getTeamColor() == enemyColor;
        return new ArrayList<>(new BoardSearcher().findChessPieces(getBoard(), enemyPositions));
    }

    public ChessGame deepCopy() {
        ChessGame copy = new ChessGame();
        copy.startingBoard = this.getBoard().deepCopy();
        copy.currentTurn = this.currentTurn;
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(startingBoard, chessGame.startingBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(startingBoard);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "startingBoard=" + startingBoard +
                '}';
    }
}
