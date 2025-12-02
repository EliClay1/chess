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

    private ChessBoard startingBoard = new ChessBoard().createStartingBoard();
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
        Collection<ChessMove> validMoves = new ArrayList<>();

        ChessPiece piece = getBoard().getPiece(startPosition);
        if (piece == null) {
            return List.of();
        }
        TeamColor teamColor = piece.getTeamColor();
        Collection<ChessMove> possibleMoves = piece.pieceMoves(getBoard(), startPosition);

        for (ChessMove chessMove : possibleMoves) {
            ChessGame clonedGame = this.deepCopy();
            clonedGame.getBoard().makeMove(chessMove);
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
        ChessPosition startingPosition = move.getStartPosition();
        ChessPiece piece = getBoard().getPiece(startingPosition);
        if (piece == null || piece.getTeamColor() != currentTurn) {
            throw new InvalidMoveException("It's not your turn");
        }

        // scans through entire collection. Look into streams for other parts of this project.
        boolean isValid = validMoves(startingPosition).stream().anyMatch(chessMove -> chessMove.equals(move));

        if (!isValid) {
            throw new InvalidMoveException("That isn't a valid move!");
        }

        getBoard().makeMove(move);
        switchTurn();
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingsPosition = findKingsPosition(teamColor);
        List<ChessPosition> allEnemyPositions = getAllPositions(getOppositeTeamColor(teamColor));
        return new BoardSearcher().isPositionAttacked(getBoard(), kingsPosition, allEnemyPositions);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {

        if (isInCheck(teamColor)) {
            return !canGetOutOfCheck(teamColor);
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        return !hasValidMoves(teamColor);
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

    private void switchTurn() {
        if (currentTurn == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        } else if (currentTurn == TeamColor.BLACK) {
            setTeamTurn(TeamColor.WHITE);
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

    private boolean canGetOutOfCheck(TeamColor teamColor) {
        PieceFilter currentTeamPieces = piece -> piece.getTeamColor() == teamColor;
        for (var position : new BoardSearcher().findChessPieces(getBoard(), currentTeamPieces)) {
            for (ChessMove move : validMoves(position)) {
                ChessGame copy = this.deepCopy();
                copy.getBoard().makeMove(move);
                if (!copy.isInCheck(teamColor)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasValidMoves(TeamColor teamColor) {
        PieceFilter currentTeamPieces = piece -> piece.getTeamColor() == teamColor;
        for (var position : new BoardSearcher().findChessPieces(getBoard(), currentTeamPieces)) {
            if (!validMoves(position).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private List<ChessPosition> getAllPositions(TeamColor teamColor) {
        PieceFilter positions = piece -> piece.getTeamColor() == teamColor;
        return new ArrayList<>(new BoardSearcher().findChessPieces(getBoard(), positions));
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
        return Objects.equals(startingBoard, chessGame.startingBoard) && currentTurn == chessGame.currentTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startingBoard, currentTurn);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "startingBoard=" + startingBoard +
                ", currentTurn=" + currentTurn +
                '}';
    }
}
