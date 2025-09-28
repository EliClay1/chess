package chess;

import java.util.ArrayList;
import java.util.List;

public class BoardIterator {

    List<ChessPiece> returnPieceLocationList(ChessBoard board, ChessPiece.PieceType pieceType, ChessGame.TeamColor teamColor) {
        List<ChessPiece> listOfPieces = new ArrayList<>(List.of());

        for (int row = 1; row <= board.gameBoard.length; row++) {
            for (int col = 1; col <= board.gameBoard.length; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece == null) {
                    continue;
                }
                if (piece.getTeamColor() == teamColor && piece.getPieceType() == pieceType) {
                    listOfPieces.add(piece);
                }
            }
        }
        return listOfPieces;
    }
}
