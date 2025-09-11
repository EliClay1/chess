package chess;

import java.util.Collection;
import java.util.List;

public class PieceLogicHelper {
    public Collection<ChessMove> definePieceLogic (ChessBoard board, ChessPosition currentPosition, ChessPiece.PieceType typeOfPiece) {

        // TODO - re-modifying to make the code more concise.
        if (typeOfPiece == ChessPiece.PieceType.KING) {
            List<ChessMove> listOfPossibleMoves = new java.util.ArrayList<>(List.of());

            for (int x = currentPosition.getRow() - 1; x <= currentPosition.getRow() + 1; x++) {
                for (int y = currentPosition.getColumn() - 1; y <= currentPosition.getColumn() + 1; y++) {

                    if (!isWithinBoardBounds(board, x, y)) {
                        continue;
                    }

                    ChessPosition newPosition = new ChessPosition(x, y);
                    if (newPosition.equals(currentPosition)) {
                        continue;
                    }

                    ChessPiece pieceAtNewPosition = board.getPiece(newPosition);
                    ChessPiece pieceAtCurrentPosition = board.getPiece(currentPosition);

                    if (pieceAtNewPosition != null) {
                        if (pieceAtNewPosition.getTeamColor() != pieceAtCurrentPosition.getTeamColor()) {
                            listOfPossibleMoves.add(new ChessMove(currentPosition, newPosition, null));
                        }
                        continue;
                    }
                    listOfPossibleMoves.add(new ChessMove(currentPosition, newPosition, null));
                }
            }
            return listOfPossibleMoves;
        }


        if (typeOfPiece == ChessPiece.PieceType.QUEEN) {
            throw new RuntimeException("Not implemented");
        }

        if (typeOfPiece == ChessPiece.PieceType.BISHOP) {
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


        throw new RuntimeException("Non-designed / Created function");
    }

    public boolean isWithinBoardBounds(ChessBoard board, int row, int col) {
        // changed to -1 to account for the fact that the board goes 1 - 8, while the array goes 0 - 7
        int boardDimensions = board.gameBoard.length - 1;
        return row <= 7 && col <= 7 && row >= 0 && col >= 0;
    }

    // TODO - finish implementation of helper function;
    public boolean isEnemyPieceAt(ChessBoard board, ChessPosition position, ChessPiece piece) {
        var teamColor = piece.getTeamColor();
        return false;
    }
}
