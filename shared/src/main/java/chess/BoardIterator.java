package chess;

import java.util.ArrayList;
import java.util.List;

public class BoardIterator {

    List<ChessPiece> returnPieceLocationList(ChessBoard board, ChessPiece.PieceType pieceType) {
        List<ChessPiece> listOfPieces = new ArrayList<>(List.of());

        for (int row = 0; row < board.gameBoard.length; row++) {
            for (int col = 0; col < board.gameBoard.length; col++) {
                System.out.printf("[%d,%d]", row, col);
            }
        }

        return listOfPieces;
    }
}
