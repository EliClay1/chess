import chess.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        boolean isActive = true;
        while (isActive) {
            System.out.printf("Type your numbers%n>>> ");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
//            var numbers = line.split(" ");
        }
    }
}