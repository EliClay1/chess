package chess.chessmovement;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;

public interface MovementLogic {
    Collection<ChessMove> getMoves(ChessBoard board, ChessPosition position, ChessPiece piece);
}
