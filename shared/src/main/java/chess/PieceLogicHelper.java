package chess;

import java.util.Collection;
import java.util.List;

public class PieceLogicHelper {

    List<ChessPosition> listOfPossibleMoves = new java.util.ArrayList<>(List.of());

    public Collection<ChessMove> definePieceLogic (ChessBoard board, ChessPosition currentPosition, ChessPiece.PieceType typeOfPiece) {

        ChessGame.TeamColor teamColor = board.getPiece(currentPosition).getTeamColor();

        if (typeOfPiece == ChessPiece.PieceType.KING) {
            for (int x = currentPosition.getRow() - 1; x <= currentPosition.getRow() + 1; x++) {
                for (int y = currentPosition.getColumn() - 1; y <= currentPosition.getColumn() + 1; y++) {

                    if (isNotWithinBoardBounds(board, x, y)) {
                        continue;
                    }

                    ChessPosition newPosition = new ChessPosition(x, y);
                    if (newPosition.equals(currentPosition)) {
                        continue;
                    }

                    // TODO - This entire block should be able to be turned into a helper function.
                    ChessPiece pieceAtNewPosition = board.getPiece(newPosition);
                    ChessPiece pieceAtCurrentPosition = board.getPiece(currentPosition);

                    if (pieceAtNewPosition != null) {
                        if (pieceAtNewPosition.getTeamColor() != pieceAtCurrentPosition.getTeamColor()) {
                            listOfPossibleMoves.add(newPosition);
                        }
                        continue;
                    }
                    listOfPossibleMoves.add(newPosition);
                }
            }
        }


        if (typeOfPiece == ChessPiece.PieceType.QUEEN) {
            throw new RuntimeException("Not implemented");
        }

        if (typeOfPiece == ChessPiece.PieceType.BISHOP) {

            // checks up-right
            bishopHelper(board, currentPosition.getRow(), currentPosition.getColumn(), 1, 1, teamColor);
//             checks up-left
            bishopHelper(board, currentPosition.getRow(), currentPosition.getColumn(), -1, 1, teamColor);
//            // checks down-right
            bishopHelper(board, currentPosition.getRow(), currentPosition.getColumn(), 1, -1, teamColor);
//            // checks down-left
            bishopHelper(board, currentPosition.getRow(), currentPosition.getColumn(), -1, -1, teamColor);
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
        // changed to -1 to account for the fact that the board goes 1 - 8, while the array goes 0 - 7
        int boardDimensions = board.gameBoard.length - 1;
        return row > boardDimensions || col > boardDimensions || row <= 0 || col <= 0;
    }

    public void bishopHelper(ChessBoard board, int x, int y, int directionX, int directionY, ChessGame.TeamColor teamColor) {
        var nextX = x + directionX;
        var nextY = y + directionY;

        if (isNotWithinBoardBounds(board, nextX, nextY)) {
            return;
        }

        ChessPiece pieceAtNextPosition = board.getPiece(new ChessPosition(nextX, nextY));

        if (pieceAtNextPosition == null) {
            listOfPossibleMoves.add(new ChessPosition(nextX, nextY));
            bishopHelper(board, nextX, nextY, directionX, directionY, teamColor);
        } else if (pieceAtNextPosition.getTeamColor() != teamColor) {
            listOfPossibleMoves.add(new ChessPosition(nextX, nextY));
        }
    }
}
