package chess;

import java.util.Collection;
import java.util.List;

public class PieceLogicHelper {

    List<ChessPosition> listOfPossibleMoves = new java.util.ArrayList<>(List.of());
    int[][] possibleBishopDirections = {{1,1}, {-1,1}, {1,-1}, {-1,-1}};
    int[][] possibleRoyaltyDirections = {{1,1}, {1,0}, {1,-1}, {0,1}, {0,-1}, {-1,1}, {-1,0}, {-1,-1}};

    public Collection<ChessMove> definePieceLogic (ChessBoard board, ChessPosition currentPosition, ChessPiece.PieceType typeOfPiece) {

        ChessGame.TeamColor teamColor = board.getPiece(currentPosition).getTeamColor();

        if (typeOfPiece == ChessPiece.PieceType.KING) {

            for (int[] dir : possibleRoyaltyDirections) {
                var dx = dir[0];
                var dy = dir[1];
                directionalHelper(board, currentPosition.getRow(), currentPosition.getColumn(), dx, dy, teamColor, ChessPiece.PieceType.KING);
            }
        }

        if (typeOfPiece == ChessPiece.PieceType.BISHOP) {
            for (int[] dir : possibleBishopDirections) {
                var dx = dir[0];
                var dy = dir[1];
                directionalHelper(board, currentPosition.getRow(), currentPosition.getColumn(), dx, dy, teamColor, ChessPiece.PieceType.BISHOP);
            }
        }

        if (typeOfPiece == ChessPiece.PieceType.QUEEN) {
            throw new RuntimeException("Not implemented");
        }

        if (typeOfPiece == ChessPiece.PieceType.KNIGHT) {
            throw new RuntimeException("Not implemented");
        }

        if (typeOfPiece == ChessPiece.PieceType.ROOK) {
            throw new RuntimeException("Not implemented");
        }

        if (typeOfPiece == ChessPiece.PieceType.PAWN) {
            throw new RuntimeException("Not implemented");
        }

        List<ChessMove> chessMoves = new java.util.ArrayList<>(List.of());
        for (var nextPosition : listOfPossibleMoves) {
            chessMoves.add(new ChessMove(currentPosition, nextPosition, null));
        }

        return chessMoves;
    }

    public boolean isNotWithinBoardBounds(ChessBoard board, int row, int col) {
        int boardDimensions = board.gameBoard.length;
        return row > boardDimensions || col > boardDimensions || row <= 0 || col <= 0;
    }

    public void directionalHelper(ChessBoard board, int x, int y, int directionX, int directionY, ChessGame.TeamColor teamColor, ChessPiece.PieceType pieceType) {
        var nextX = x + directionX;
        var nextY = y + directionY;

        if (isNotWithinBoardBounds(board, nextX, nextY)) {
            return;
        }

        ChessPiece pieceAtNextPosition = board.getPiece(new ChessPosition(nextX, nextY));

        if (pieceAtNextPosition == null) {
            listOfPossibleMoves.add(new ChessPosition(nextX, nextY));
            if (pieceType != ChessPiece.PieceType.KING) {
                directionalHelper(board, nextX, nextY, directionX, directionY, teamColor, pieceType);
            }
        } else if (pieceAtNextPosition.getTeamColor() != teamColor) {
            listOfPossibleMoves.add(new ChessPosition(nextX, nextY));
        }
    }
}
