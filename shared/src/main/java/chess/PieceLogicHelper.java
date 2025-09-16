package chess;

import java.util.Collection;
import java.util.List;

public class PieceLogicHelper {

    List<ChessPosition> listOfPossibleMoves = new java.util.ArrayList<>(List.of());
    int[][] possibleBishopDirections = {{1,1}, {-1,1}, {1,-1}, {-1,-1}};
    int[][] possibleRookDirections = {{0,1}, {0,-1}, {1,0}, {-1,0}};
    int[][] possibleRoyaltyDirections = {{1,1}, {1,0}, {1,-1}, {0,1}, {0,-1}, {-1,1}, {-1,0}, {-1,-1}};
    int[][] possibleKnightDirections = {{-1,2}, {-2,1}, {-1,-2}, {-2,-1}, {1,-2}, {2,-1}, {1,2}, {2,1}};

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
            for (int[] dir : possibleRoyaltyDirections) {
                var dx = dir[0];
                var dy = dir[1];
                directionalHelper(board, currentPosition.getRow(), currentPosition.getColumn(), dx, dy, teamColor, ChessPiece.PieceType.QUEEN);
            }
        }

        if (typeOfPiece == ChessPiece.PieceType.ROOK) {
            for (int[] dir : possibleRookDirections) {
                var dx = dir[0];
                var dy = dir[1];
                directionalHelper(board, currentPosition.getRow(), currentPosition.getColumn(), dx, dy, teamColor, ChessPiece.PieceType.ROOK);
            }
        }

        if (typeOfPiece == ChessPiece.PieceType.KNIGHT) {
            for (int[] dir : possibleKnightDirections) {
                var dx = currentPosition.getRow() + dir[0];
                var dy = currentPosition.getColumn() + dir[1];
                if (isNotWithinBoardBounds(board, dx, dy)) {
                    continue;
                }
                ChessPiece pieceAtNextPosition = board.getPiece(new ChessPosition(dx, dy));
                if (pieceAtNextPosition == null) {
                    listOfPossibleMoves.add(new ChessPosition(dx, dy));
                    continue;
                }
                if (pieceAtNextPosition.getTeamColor() == teamColor) {
                    continue;
                }
                listOfPossibleMoves.add(new ChessPosition(dx, dy));
            }
        }

        if (typeOfPiece == ChessPiece.PieceType.PAWN) {
            pawnHelper(board, isStartingPiece(board, currentPosition), currentPosition, teamColor);
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

    public boolean isStartingPiece(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE && position.getRow() == 2) {
            return true;
        } else return piece.getTeamColor() == ChessGame.TeamColor.BLACK && position.getRow() == 7;
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

    public int getTeamDirection(ChessGame.TeamColor teamColor) {
        if (teamColor == ChessGame.TeamColor.WHITE) {
            return 1;
        } else {
            return -1;
        }
    }

    public void pawnHelper(ChessBoard board, Boolean isStartingPiece, ChessPosition position, ChessGame.TeamColor teamColor) {
        int direction = getTeamDirection(teamColor);
        var x = position.getRow();
        var y = position.getColumn();
        boolean frontBlocked = false;

        if (board.getPiece(new ChessPosition(x+direction, y)) == null) {
            listOfPossibleMoves.add(new ChessPosition(x+direction, y));
            frontBlocked = true;
        }

        if (isStartingPiece && frontBlocked) {
            if (board.getPiece(new ChessPosition(x+direction+direction, y)) == null) {
                listOfPossibleMoves.add(new ChessPosition(x+direction+direction, y));
            }
        }

        // Checking diagonal directions.
        if (!isNotWithinBoardBounds(board, x+direction, y+1)) {
            var diagonalMove1 = board.getPiece(new ChessPosition(x+direction, y+1));
            if (diagonalMove1 != null && diagonalMove1.getTeamColor() != teamColor) {
                listOfPossibleMoves.add(new ChessPosition(x+direction, y+1));
            }
        }

        if (!isNotWithinBoardBounds(board, x+direction, y-1)) {
            var diagonalMove2 = board.getPiece(new ChessPosition(x+direction, y-1));
            if (diagonalMove2 != null && diagonalMove2.getTeamColor() != teamColor) {
                listOfPossibleMoves.add(new ChessPosition(x+direction, y-1));
            }
        }

        // TODO - Promotion Logic
    }
}
