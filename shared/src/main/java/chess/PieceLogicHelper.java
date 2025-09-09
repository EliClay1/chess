package chess;

import java.util.Collection;
import java.util.List;

public class PieceLogicHelper {
    public Collection<ChessMove> definePieceLogic (ChessBoard board, ChessPosition currentPosition, ChessPiece.PieceType typeOfPiece) {

        if (typeOfPiece == ChessPiece.PieceType.KING) {
            /* Can move any direction by one space.
            * This means that there are 8 possible directions that it can move in one turn.
            * Take the current position, add 1 to the y-axis (if board is within that range), and mark that position.
            *
            * */

            for (int x = currentPosition.getRow() - 1; x <= currentPosition.getRow() + 1; x++) {
                for (int y = currentPosition.getColumn() - 1; y <= currentPosition.getColumn() + 1; y++) {
                    ChessPosition newPositionToAdd = new ChessPosition(x, y);
                    if (newPositionToAdd.equals(currentPosition)) {
                        System.out.println("match found!");
                    }
                }
            }

            return List.of(new ChessMove(new ChessPosition(1, 5), new ChessPosition(2, 6), null));

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
}
