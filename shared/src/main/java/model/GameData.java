package model;

import chess.ChessGame;

public record GameData() {

    static int gameId;
    static String whiteUsername;
    static String blackUsername;
    static String gameName;
    static ChessGame game;

    public static int getGameId() {
        return gameId;
    }

    public static void setGameId(int gameId) {
        GameData.gameId = gameId;
    }

    public static String getWhiteUsername() {
        return whiteUsername;
    }

    public static void setWhiteUsername(String whiteUsername) {
        GameData.whiteUsername = whiteUsername;
    }

    public static String getBlackUsername() {
        return blackUsername;
    }

    public static void setBlackUsername(String blackUsername) {
        GameData.blackUsername = blackUsername;
    }

    public static String getGameName() {
        return gameName;
    }

    public static void setGameName(String gameName) {
        GameData.gameName = gameName;
    }

    public static ChessGame getGame() {
        return game;
    }

    public static void setGame(ChessGame game) {
        GameData.game = game;
    }

    @Override
    public String toString() {
        return "GameData{}";
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
