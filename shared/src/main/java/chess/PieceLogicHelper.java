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

            /*
            * TODO - Pawns are special because if they reach the end of the board they get promoted. Also, if the piece
            *  is a at a starting position they have the ability to move twice, while any time afterwards only once.
            * The system for something like this will need to be two different types, where the code will change depending on
            * which team the piece is on.
            * Additionally, the pawn cannot ever be on the first row of it's team. It must start on the second row.
            * */

            // white team - starts at rows 1 and 2
            // if on row two, allow for 2 movements forward, otherwise don't. Write a helper function to determine if the piece is in starting.
            var x = currentPosition.getRow();
            var y = currentPosition.getColumn();

            // check the next available space and if it is blocked, do not allow a move, otherwise do.
            // the 1 in (y+1) should be able to be changed accordingly if it is the starting piece.
            if (board.getPiece(new ChessPosition(x, y+1)) != null) {
                System.out.println("This move is not allowed.");
            } else {
                System.out.println("Good to go!");
            }

            // check the diagonals to see if there are any pieces there. Allow moves if this is the case
            // check if the movable positions are within bounds. This is only the case for diagonal movements.

            if (isNotWithinBoardBounds(board, x + 1, y + 1)) {
                System.out.println("This move is not allowed.");
            }
            var diagonalMove1 = board.getPiece(new ChessPosition(x+1, y+1));

            if (isNotWithinBoardBounds(board, x - 1, y + 1)) {
                System.out.println("This move is not allowed.");
            }

            var diagonalMove2 = board.getPiece(new ChessPosition(x-1, y+1));

            if (((diagonalMove1 != null) && (diagonalMove1.getTeamColor() != teamColor))
                    || ((diagonalMove2 != null) && (diagonalMove2.getTeamColor() != teamColor))) {
                System.out.println("Good to go!");
            } else {
                System.out.println("This move is not allowed.");
            }

            if (isStartingPiece(board, currentPosition)) {
                System.out.println("Hey, I'm a starting pawn! I'm allowed to take 2 steps forward!");
            }

            // TODO - Promotional Ability

            // black team - starts at rows 8 and 7
            // changing the above code to a negative on the y-axis should be the only change that needs to be made.
            // Find a way to make it a helper function.

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
    }
}
