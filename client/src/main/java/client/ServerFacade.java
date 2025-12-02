package client;

import chess.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exceptions.AlreadyTakenException;
import exceptions.InvalidException;
import exceptions.UnauthorizedException;

import static ui.EscapeSequences.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

public class ServerFacade {
    public static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    public int status;

    // TODO - Cannot send ANY error codes back.

    public Map<String, String> registerUser(String host, int port, String path, String username,
                                            String password, String email) throws Exception {

        // TODO - putting a # causes the password input and email inputs to break. Check to ensure those characters aren't in the email
        if (invalidCharacters(password) || invalidCharacters(email) || invalidCharacters(username)) {
            throw new InvalidException();
        }

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(
                String.format("{\"username\": \"%s\", \"password\": \"%s\", \"email\": \"%s\"}", username, password, email));
        String url = String.format(Locale.getDefault(), "http://%s:%d%s", host, port, path);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .timeout(Duration.ofMillis(5000))
                .POST(body)
                .build();

        // What in the heckerdundooskis is going on here.
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        status = response.statusCode();

        if (status >= 200 && status < 300) {
            return jsonParser(response.body(), "username", "authToken").getFirst();
        } else if (status == 403) {
            throw new AlreadyTakenException();
        } else if (status == 400) {
            throw new InvalidException();
        } else {
            throw new Exception("Invalid");
        }
    }

    // TODO - duplicate code.
    public Map<String, String> loginUser(String host, int port, String path, String username,
                             String password) throws Exception {

        if (invalidCharacters(password) || invalidCharacters(username)) {
            throw new InvalidException();
        }

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(
                String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password));
        String url = String.format(Locale.getDefault(), "http://%s:%d%s", host, port, path);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .timeout(Duration.ofMillis(5000))
                .POST(body)
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        status = response.statusCode();

        if (status >= 200 && status < 300) {
            return jsonParser(response.body(), "username", "authToken").getFirst();
        } else if (status == 401) {
            throw new UnauthorizedException();
        } else if (status == 406) {
            throw new InvalidException();
        } else {
            throw new Exception("Invalid");
        }
    }

    public void logoutUser(String host, int port, String path, String authToken) throws Exception {
        String url = String.format(Locale.getDefault(), "http://%s:%d%s", host, port, path);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .timeout(Duration.ofMillis(5000))
                .DELETE()
                .header("Authorization", authToken)
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        status = response.statusCode();
        if (!(status >= 200 && status < 300)) {
            throw new Exception("Invalid");
        }
    }

    public List<Map<String, String>> listGames(String host, int port, String path, String authToken) throws Exception {
        // TODO - Maybe say if no games are created, and suggest making one?
        String url = String.format(Locale.getDefault(), "http://%s:%d%s", host, port, path);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .timeout(Duration.ofMillis(5000))
                .GET()
                .header("Authorization", authToken)
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        status = response.statusCode();
        if (!(status >= 200 && status < 300)) {
            throw new Exception(String.format("%d", status));
        }
        return jsonParser(response.body(), "gameID", "gameName", "whiteUsername", "blackUsername", "gameData");
    }

    // FIXME - Change the Game Name size allowed by the system. Too long could break it. Check the spec.
    public String createGame(String host, int port, String path, String authToken, String gameName) throws Exception {
        if (invalidCharacters(gameName)) {
            throw new InvalidException();
        }

        String url = String.format(Locale.getDefault(), "http://%s:%d%s", host, port, path);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(
                String.format("{\"gameName\": \"%s\"}", gameName));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .timeout(Duration.ofMillis(5000))
                .POST(body)
                .header("Authorization", authToken)
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        status = response.statusCode();
        // TODO - what is the purpose of this?
        jsonParser(response.body(), "gameID", "gameName", "whiteUsername", "blackUsername");

        if (!(status >= 200 && status < 300)) {
            throw new Exception("Invalid");
        }
        return jsonParser(response.body(), "gameID").getFirst().get("gameID");
    }

    public void joinGame(String host, int port, String path, String authToken, String gameID, String playerColor) throws Exception {
        if (invalidCharacters(playerColor) || !Arrays.asList(new String[]{"white", "black"}).contains(playerColor.toLowerCase())) {
            throw new InvalidException();
        }

        String url = String.format(Locale.getDefault(), "http://%s:%d%s", host, port, path);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(
                String.format("{\"playerColor\": \"%s\", \"gameID\": \"%s\"}", playerColor.toLowerCase(), gameID));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .timeout(Duration.ofMillis(5000))
                .PUT(body)
                .header("Authorization", authToken)
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        status = response.statusCode();

        // TODO - 11/28/2025 - parse the game and print the game. Code should be functioning now.

        if (status >= 200 && status < 300) {
            System.out.print("\n");
        } else if (status == 400) {
            throw new NumberFormatException();
        } else if (status == 403) {
            throw new AlreadyTakenException();
        }
        else {
            throw new Exception("Invalid");
        }
    }

    // FIXME - Error handling for invalid game ID. Check to ensure that the gameID actually exists.
    public void observeGame(String gameID, List<Map<String, String>> activeGames) throws Exception {

        if (activeGames == null || activeGames.isEmpty()) {
            throw new InvalidException();
        }

        if (gameID == null || gameID.isEmpty()) {
            throw new Exception("GameID doesn't exist.");
        } else {
            try {
                Integer.parseInt(gameID);
            } catch (NumberFormatException e) {
                throw new Exception("GameID must be a number.");
            }
        }

        // FIXME - Check if the gameID actually exists
        boolean found = false;
        for (var game : activeGames) {
            if (game.containsValue(gameID)) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new Exception("GameID doesn't exist.");
        }
    }

    public void printBoard(String color, ChessGame chessGame, ChessPosition piecePosition) {
        final boolean blackView = "black".equalsIgnoreCase(color);
        Set<ChessPosition> targetPositions = new HashSet<>(Set.of());
        if (piecePosition != null) {
            var validMoves = chessGame.validMoves(piecePosition);
            for (var move : validMoves) {
                targetPositions.add(move.getEndPosition());
            }
        }
        String[] lettersAtoH = {"   ", " \u2009a ", " \u2007b ", " \u2004c ", " \u2007d ", " \u2004e ", " \u2007f ",
                " \u2004g ", " \u2007h ", "   \u200A"};
        String[] lettersHtoA = {"   ", " \u2009h ", " \u2007g ", " \u2004f ", " \u2007e ", " \u2004d ", " \u2007c ",
                " \u2004b ", " \u2007a ", "   \u200A"};
        String[] letters = blackView ? lettersHtoA : lettersAtoH;
        String[] numsWhite = {" 8 ", " 7 ", " 6 ", " 5 ", " 4 ", " 3 ", " 2 ", " 1 "};
        String[] numsBlack = {" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 "};
        String[] numbers = blackView ? numsBlack : numsWhite;
        System.out.print("\n");
        for (var letter : letters) {
            System.out.printf("%s%s%s%s%s", SET_BG_COLOR_BORDER, SET_TEXT_COLOR_WHITE,
                    letter, RESET_TEXT_COLOR, RESET_BG_COLOR);
        }
        for (int x = 0; x < 8; x++) {
            System.out.print("\n");

            System.out.printf("%s%s%s%s%s", SET_BG_COLOR_BORDER, SET_TEXT_COLOR_WHITE,
                    numbers[x], RESET_TEXT_COLOR, RESET_BG_COLOR);
            for (int y = 0; y < 8; y++) {
                int bx = blackView ? x : 7 - x;
                int by = blackView ? 7 - y : y;
                int xPos = bx + 1;
                int yPos = by + 1;
                ChessPosition currentPosition = new ChessPosition(xPos, yPos);
                boolean isSourcePiece = false;
                boolean isTargetPiece = false;

                if (piecePosition != null) {
                    isSourcePiece = currentPosition.equals(piecePosition);
                    isTargetPiece = targetPositions.contains(currentPosition);
                }
                String bg;
                boolean dark = ((bx + by) & 1) == 1;
                bg = dark ? SET_BOARD_BLACK : SET_BOARD_WHITE;

                if (piecePosition != null) {
                    if (isSourcePiece) {
                        bg = HIGHLIGHT_SELECTED;
                    } else if (isTargetPiece) {
                        bg = dark ? HIGHLIGHT_TARGET_BLACK : HIGHLIGHT_TARGET_WHITE;
                    }
                }
                ChessPiece chessPiece = chessGame.getBoard().getPiece(currentPosition);
                String piece = null;
                String pieceColor = null;
                if (chessPiece != null) {
                    switch (chessPiece.getPieceType()) {
                        case PAWN -> piece = (chessPiece.getTeamColor() == ChessGame.TeamColor.WHITE)
                                ? WHITE_PAWN : BLACK_PAWN;
                        case ROOK -> piece = (chessPiece.getTeamColor() == ChessGame.TeamColor.WHITE)
                                ? WHITE_ROOK : BLACK_ROOK;
                        case KNIGHT -> piece = (chessPiece.getTeamColor() == ChessGame.TeamColor.WHITE)
                                ? WHITE_KNIGHT : BLACK_KNIGHT;
                        case BISHOP -> piece = (chessPiece.getTeamColor() == ChessGame.TeamColor.WHITE)
                                ? WHITE_BISHOP : BLACK_BISHOP;
                        case QUEEN -> piece = (chessPiece.getTeamColor() == ChessGame.TeamColor.WHITE)
                                ? WHITE_QUEEN : BLACK_QUEEN;
                        case KING -> piece = (chessPiece.getTeamColor() == ChessGame.TeamColor.WHITE)
                                ? WHITE_KING : BLACK_KING;
                    }
                    pieceColor = (chessPiece.getTeamColor() == ChessGame.TeamColor.WHITE)
                            ? SET_PIECE_COLOR_WHITE
                            : SET_PIECE_COLOR_BLACK;
                }
                if (piece != null) {
                    String cell = dark ? piece : piece + HAIRSPACE;
                    System.out.printf("%s%s%s%s", bg, pieceColor, cell, RESET_BG_COLOR);
                } else {
                    System.out.printf("%s%s%s", bg, EMPTY, RESET_BG_COLOR);
                }
            }
            System.out.printf("%s%s%s%s%s", SET_BG_COLOR_BORDER, SET_TEXT_COLOR_WHITE,
                    numbers[x], RESET_TEXT_COLOR, RESET_BG_COLOR);
        }
        System.out.print("\n");
        for (var letter : letters) {
            System.out.printf("%s%s%s%s%s", SET_BG_COLOR_BORDER, SET_TEXT_COLOR_WHITE,
                    letter, RESET_TEXT_COLOR, RESET_BG_COLOR);
        }
        System.out.print("\n");
        System.out.print("\n");
    }

    private boolean invalidCharacters(String string) {
        String[] invalidCharacters = {"\"", " ", "#", "%", "&", "<", ">", "{", "}", "|", "\\", "~", "`", "'", "/", "="};
        for (var character : invalidCharacters) {
            if (string.contains(character)) {
                return true;
            }
        }
        return false;
    }

    // TODO - so... pretty sure this can be replace with a Gson.fromJson(jsonString, Map.class);...
    // 11/28/2025 update: Will fix this later, not important atm. this will cause a lot of refactoring.

    private List<Map<String, String>> jsonParser(String json, String... args) {
        List<Map<String, String>> resultList = new ArrayList<>();
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        if (jsonObject.has("games") && jsonObject.get("games").isJsonArray()) {
            JsonArray gamesArray = jsonObject.getAsJsonArray("games");
            for (JsonElement element : gamesArray) {
                JsonObject gameObject = element.getAsJsonObject();
                Map<String, String> extractedValues = new HashMap<>();
                for (String argument : args) {
                    if (gameObject.has(argument)) {
                        extractedValues.put(argument, gameObject.get(argument).getAsString());
                    }
                }
                resultList.add(extractedValues);
            }
        } else {
            Map<String, String> extractedValues = new HashMap<>();
            for (var argument : args) {
                if (jsonObject.has(argument)) {
                    extractedValues.put(argument, jsonObject.get(argument).getAsString());
                }
            }
            resultList.add(extractedValues);
        }
        return resultList;
    }
}
