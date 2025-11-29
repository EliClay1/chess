package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BoardSearcher implements PieceFilter {
    @Override
    public boolean matches(ChessPiece piece) {
        return false;
    }

    public List<ChessPosition> findChessPieces(ChessBoard board, PieceFilter filter) {
        List<ChessPosition> matchedPositions = new ArrayList<>();
        for (ChessPosition position : allBoardLocations(board)) {
            ChessPiece piece = board.getPiece(position);
            if (piece != null && filter.matches(piece)) {
                matchedPositions.add(position);
            }
        }
        return matchedPositions;
    }

    public List<ChessPosition> allBoardLocations(ChessBoard board) {
        List<ChessPosition> locations = new ArrayList<>();
        for (int row = 1; row <= board.gameBoard.length; row++) {
            for (int col = 1; col <= board.gameBoard.length; col++) {
                locations.add(new ChessPosition(row, col));
            }
        }
        return locations;
    }

    boolean isPositionAttacked(ChessBoard board, ChessPosition position, List<ChessPosition> enemyPositions) {
        for (ChessPosition enemyPosition : enemyPositions) {
            Collection<ChessMove> possibleEnemyMoves = board.getPiece(enemyPosition).pieceMoves(board, enemyPosition);
            for (ChessMove move : possibleEnemyMoves) {
                if (move.getEndPosition().equals(position)) {
                    return true;
                }
            }
        }
        return false;
    }
}
