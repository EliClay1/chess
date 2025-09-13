package chess;

import java.util.Collection;
import java.util.List;

public class PieceLogicHelper {

    List<ChessMove> listOfPossibleMoves = new java.util.ArrayList<>(List.of());

    public Collection<ChessMove> definePieceLogic (ChessBoard board, ChessPosition currentPosition, ChessPiece.PieceType typeOfPiece) {
        /**
         * Implementation of the King Piece logic. This works with everything EXCEPT checks on if the king is in danger.
         */

        if (typeOfPiece == ChessPiece.PieceType.KING) {
            for (int x = currentPosition.getRow() - 1; x <= currentPosition.getRow() + 1; x++) {
                for (int y = currentPosition.getColumn() - 1; y <= currentPosition.getColumn() + 1; y++) {

                    if (!isWithinBoardBounds(board, x, y)) {
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
        return row <= boardDimensions && col <= boardDimensions && row >= 0 && col >= 0;
    }

    public void bishopHelper(ChessBoard board, int x, int y) {

        if (isWithinBoardBounds(board, x, y)) {

            ChessPosition currentPosition = new ChessPosition(x, y);
            ChessPosition newPosition = new ChessPosition(x + 1, y + 1);

            ChessPiece pieceAtNewPosition = board.getPiece(newPosition);
            ChessPiece pieceAtCurrentPosition = board.getPiece(currentPosition);

            // is not on the team color
            if (pieceAtNewPosition.getTeamColor() == pieceAtCurrentPosition.getTeamColor()) {
                // not a viable move
                return;
            }
            if (pieceAtNewPosition.getTeamColor() != pieceAtCurrentPosition.getTeamColor()) {
                // is a viable move, but no moves can be made further in this direction.
                listOfPossibleMoves.add(new ChessMove(currentPosition, newPosition, null));
            }
            bishopHelper(board, x + 1, y + 1);
        }
    }
}
