package chess;

import java.util.Collection;
import java.util.List;

public class PieceLogicHelper {
    public Collection<ChessMove> definePieceLogic (ChessBoard board, ChessPosition currentPosition, ChessPiece.PieceType typeOfPiece) {

        if (typeOfPiece == ChessPiece.PieceType.KING) {
            /* Can move any direction by one space.
            * This means that there are 8 possible directions that it can move in one turn.
             */

            /*
            * TODO - Check to see if there is already a piece in a possible position. Save that position to a separate
            *  list, if the user chooses to move there, piece-taking functionality will go into play. There will be
            *  an additional helper function to cover all of that.
            */

            List<ChessMove> listOfPossibleMoves = new java.util.ArrayList<>(List.of());
            List<ChessMove> listOfAttackableMoves = new java.util.ArrayList<>(List.of());

            for (int x = currentPosition.getRow() - 1; x <= currentPosition.getRow() + 1; x++) {
                for (int y = currentPosition.getColumn() - 1; y <= currentPosition.getColumn() + 1; y++) {
                    if (!isWithinBoardBounds(board, x, y)) {
                        continue;
                    }
                    ChessPosition newPositionToAdd = new ChessPosition(x, y);
                    if (!currentPosition.equals(newPositionToAdd)) {
                        if (board.getPiece(newPositionToAdd) != null) {
                            if (board.getPiece(newPositionToAdd).getTeamColor() != board.getPiece(currentPosition).getTeamColor()) {
                                listOfAttackableMoves.add(new ChessMove(currentPosition, newPositionToAdd, null));
                                listOfPossibleMoves.add(new ChessMove(currentPosition, newPositionToAdd, null));
                            }
                            continue;
                        }
                        listOfPossibleMoves.add(new ChessMove(currentPosition, newPositionToAdd, null));
                    }
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
}
