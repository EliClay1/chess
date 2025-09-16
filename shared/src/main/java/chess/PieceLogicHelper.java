package chess;

import java.util.Collection;
import java.util.List;

public class PieceLogicHelper {

    List<ChessMove> listOfPossibleMoves = new java.util.ArrayList<>(List.of());
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
                directionalHelper(board, currentPosition, currentPosition.getRow(), currentPosition.getColumn(), dx, dy, teamColor, ChessPiece.PieceType.KING);
            }
        }

        if (typeOfPiece == ChessPiece.PieceType.BISHOP) {
            for (int[] dir : possibleBishopDirections) {
                var dx = dir[0];
                var dy = dir[1];
                directionalHelper(board, currentPosition, currentPosition.getRow(), currentPosition.getColumn(), dx, dy, teamColor, ChessPiece.PieceType.BISHOP);
            }
        }

        if (typeOfPiece == ChessPiece.PieceType.QUEEN) {
            for (int[] dir : possibleRoyaltyDirections) {
                var dx = dir[0];
                var dy = dir[1];
                directionalHelper(board, currentPosition, currentPosition.getRow(), currentPosition.getColumn(), dx, dy, teamColor, ChessPiece.PieceType.QUEEN);
            }
        }

        if (typeOfPiece == ChessPiece.PieceType.ROOK) {
            for (int[] dir : possibleRookDirections) {
                var dx = dir[0];
                var dy = dir[1];
                directionalHelper(board, currentPosition, currentPosition.getRow(), currentPosition.getColumn(), dx, dy, teamColor, ChessPiece.PieceType.ROOK);
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
                    listOfPossibleMoves.add(new ChessMove(currentPosition, new ChessPosition(dx, dy), null));
                    continue;
                }
                if (pieceAtNextPosition.getTeamColor() == teamColor) {
                    continue;
                }
                listOfPossibleMoves.add(new ChessMove(currentPosition, new ChessPosition(dx, dy), null));
            }
        }

        if (typeOfPiece == ChessPiece.PieceType.PAWN) {
            pawnHelper(board, isStartingPiece(board, currentPosition), currentPosition, teamColor);
        }

        return listOfPossibleMoves;
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

    public void directionalHelper(ChessBoard board, ChessPosition basePosition, int x, int y, int directionX, int directionY, ChessGame.TeamColor teamColor, ChessPiece.PieceType pieceType) {
        var nextX = x + directionX;
        var nextY = y + directionY;

        if (isNotWithinBoardBounds(board, nextX, nextY)) {
            return;
        }

        ChessPiece pieceAtNextPosition = board.getPiece(new ChessPosition(nextX, nextY));

        if (pieceAtNextPosition == null) {
            listOfPossibleMoves.add(new ChessMove(basePosition, new ChessPosition(nextX, nextY), null));
            if (pieceType != ChessPiece.PieceType.KING) {
                directionalHelper(board, basePosition, nextX, nextY, directionX, directionY, teamColor, pieceType);
            }
        } else if (pieceAtNextPosition.getTeamColor() != teamColor) {
            listOfPossibleMoves.add(new ChessMove(basePosition, new ChessPosition(nextX, nextY), null));
        }
    }

    public int getTeamDirection(ChessGame.TeamColor teamColor) {
        if (teamColor == ChessGame.TeamColor.WHITE) {
            return 1;
        } else {
            return -1;
        }
    }

    public boolean canPromote(int currentRow, ChessGame.TeamColor teamColor) {
        return (teamColor == ChessGame.TeamColor.WHITE && currentRow == 8)
                || (teamColor == ChessGame.TeamColor.BLACK && currentRow == 1);
    }

    public void pawnHelper(ChessBoard board, Boolean isStartingPiece, ChessPosition position, ChessGame.TeamColor teamColor) {
        int direction = getTeamDirection(teamColor);
        int[][] possiblePawnDirections = {{0,1}, {1,1}, {-1,1}};

        for (var dir: possiblePawnDirections) {
            var nextRow = position.getRow() + direction;
            var nextCol = position.getColumn() + dir[0];
            // if is within board bounds,
            if (isNotWithinBoardBounds(board, nextRow, nextCol)) {
                continue;
            }

            // check for forward move, so if dir[0] is == 0
            var nextPosition = new ChessPosition(nextRow, nextCol);
            if (dir[0] == 0) {
                if (board.getPiece(nextPosition) == null) {
                    if (canPromote(nextRow, teamColor)) {
                        for (ChessPiece.PieceType piece : List.of(ChessPiece.PieceType.QUEEN,
                                ChessPiece.PieceType.ROOK,
                                ChessPiece.PieceType.BISHOP,
                                ChessPiece.PieceType.KNIGHT)) {
                            listOfPossibleMoves.add(new ChessMove(position, nextPosition, piece));
                        }
                        continue;
                    }
                    listOfPossibleMoves.add(new ChessMove(position, nextPosition, null));
                    if (isStartingPiece) {
                        var secondNextPosition = new ChessPosition(nextRow + direction, nextCol);
                        if (board.getPiece(nextPosition) == null && board.getPiece(secondNextPosition) == null) {
                            listOfPossibleMoves.add(new ChessMove(position, secondNextPosition, null));
                        }
                    }
                }
            } else {
                // diagonal code
                if (board.getPiece(nextPosition) != null && board.getPiece(nextPosition).getTeamColor() != teamColor) {
                    if (canPromote(nextRow, teamColor)) {
                        for (ChessPiece.PieceType piece : List.of(ChessPiece.PieceType.QUEEN,
                                ChessPiece.PieceType.ROOK,
                                ChessPiece.PieceType.BISHOP,
                                ChessPiece.PieceType.KNIGHT)) {
                            listOfPossibleMoves.add(new ChessMove(position, nextPosition, piece));
                        }
                        continue;
                    }
                    listOfPossibleMoves.add(new ChessMove(position, nextPosition, null));
                }
            }
        }
    }
}
