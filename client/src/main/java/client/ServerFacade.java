package client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exceptions.InvalidException;

import javax.lang.model.type.ExecutableType;

import static ui.EscapeSequences.*;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO - implement port into initialization.


/*TODO
*  Register ---
*  Login ---
*  Logout ---
*  Create ---
*  Observe
*  List ---
*  Join
* */

public class ServerFacade {
    private static final java.net.http.HttpClient httpClient = java.net.http.HttpClient.newHttpClient();

    public int status;

    public void registerUser(String host, int port, String path, String username,
                             String password, String email) throws Exception {

        // TODO - putting a # causes the password input and email inputs to break. Check to ensure those characters aren't in the email.
        if (invalidCharacters(password) || invalidCharacters(email) || invalidCharacters(username)) {
            throw new InvalidException();
        }
        // TODO - This makes it impossible to send a bad password, but the double handling checks never hurt anyone.

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(
                String.format("{\"username\": \"%s\", \"password\": \"%s\", \"email\": \"%s\"}", username, password, email));
        String url = String.format(Locale.getDefault(), "http://%s:%d%s", host, port, path);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .timeout(Duration.ofMillis(5000))
                .POST(body)
                .build();

        // What in the heckerdundooskis is going on here.
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        status = response.statusCode();

        if (status >= 200 && status < 300) {
            System.out.printf("%sSuccessfully registed!\n%s",
                    SET_TEXT_COLOR_MAGENTA, RESET_TEXT_COLOR);
        } else if (status == 403) {
            System.out.printf("%sThat user already exists! Try again.\n%s",
                    "\u001b[38;5;1m", RESET_TEXT_COLOR);
        } else if (status == 406) {
            System.out.printf("%sBad inputs! Try again.\n%s",
                    "\u001b[38;5;1m", RESET_TEXT_COLOR);
        } else if (status == 500) {
            System.out.printf("%sAn error occurred. Please try again.\n%s",
                    "\u001b[38;5;1m", RESET_TEXT_COLOR);
        }
        else {
            System.out.println("Error: recieved status code: " + status);
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

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        status = response.statusCode();

        if (status >= 200 && status < 300) {
            System.out.printf("%sYou are now logged in!\n%s",
                    SET_TEXT_COLOR_MAGENTA, RESET_TEXT_COLOR);
        } else if (status == 401) {
            System.out.printf("%sIncorrect Password! Try again.\n%s",
                    "\u001b[38;5;1m", RESET_TEXT_COLOR);
        } else if (status == 406) {
            System.out.printf("%sBad inputs! Try again.\n%s",
                    "\u001b[38;5;1m", RESET_TEXT_COLOR);
        } else if (status == 500) {
            System.out.printf("%sAn error occurred. Please try again.\n%s",
                    "\u001b[38;5;1m", RESET_TEXT_COLOR);
        }
        else {
            System.out.println("Error: recieved status code: " + status);
        }

        return jsonParser(response.body(), "username", "authToken").getFirst();
    }

    public void logoutUser(String host, int port, String path, String authToken) throws Exception {
        String url = String.format(Locale.getDefault(), "http://%s:%d%s", host, port, path);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .timeout(Duration.ofMillis(5000))
                .DELETE()
                .header("Authorization", authToken)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        status = response.statusCode();
        if (status >= 200 && status < 300) {
            System.out.printf("%sLogged Out.\n%s",
                    SET_TEXT_COLOR_MAGENTA, RESET_TEXT_COLOR);
        } else {
            System.out.printf("%sError: received status code: %s\n%s",
                    "\u001b[38;5;1m", status, RESET_TEXT_COLOR);
        }
    }

    public void listGames(String host, int port, String path, String authToken) throws Exception {
        // TODO - Maybe say if no games are created, and suggest making one?
        String url = String.format(Locale.getDefault(), "http://%s:%d%s", host, port, path);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .timeout(Duration.ofMillis(5000))
                .GET()
                .header("Authorization", authToken)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        status = response.statusCode();
        var parsedResponse = jsonParser(response.body(), "gameID", "gameName", "whiteUsername", "blackUsername");
        if (status >= 200 && status < 300) {
            for (Map<String, String> gameData : parsedResponse) {
                System.out.printf("%s%s. Game Name: %s, White: %s, Black: %s\n%s", SET_TEXT_COLOR_BLUE, gameData.get("gameID"),
                        gameData.get("gameName"), gameData.get("whiteUsername"), gameData.get("blackUsername"), RESET_TEXT_COLOR);
            }
        } else {
            System.out.printf("%sError: received status code: %s\n%s",
                    "\u001b[38;5;1m", status, RESET_TEXT_COLOR);
        }
    }

    public void createGame(String host, int port, String path, String authToken, String gameName) throws Exception {
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

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        status = response.statusCode();
        jsonParser(response.body(), "gameID", "gameName", "whiteUsername", "blackUsername");

        if (status >= 200 && status < 300) {
            System.out.printf("%sSuccessfully created game: %s\n%s", SET_TEXT_COLOR_BLUE, gameName, RESET_TEXT_COLOR);
        } else {
            System.out.printf("%sError: received status code: %s\n%s",
                    "\u001b[38;5;1m", status, RESET_TEXT_COLOR);
        }
    }

    public void joinGame(String host, int port, String path, String authToken, String gameID, String playerColor) throws Exception {
        if (invalidCharacters(playerColor)) {
            throw new InvalidException();
        }

        // TODO - Convert GameID to int, if it fails, no bueno, throw invalid exception.

        String url = String.format(Locale.getDefault(), "http://%s:%d%s", host, port, path);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(
                String.format("{\"playerColor\": \"%s\"}, \"gameID\": \"%s\"", playerColor, gameID));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .timeout(Duration.ofMillis(5000))
                .PUT(body)
                .header("Authorization", authToken)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        status = response.statusCode();
        jsonParser(response.body(), "gameID", "gameName", "whiteUsername", "blackUsername");

        if (status >= 200 && status < 300) {
            // TODO - Generate board print code. Don't worry about calculation of moves.
//            System.out.printf("%sSuccessfully created game: %s\n%s", SET_TEXT_COLOR_BLUE, gameName, RESET_TEXT_COLOR);
        } else {
            System.out.printf("%sError: received status code: %s\n%s",
                    "\u001b[38;5;1m", status, RESET_TEXT_COLOR);
        }
    }

    public void printBoard(String color) {



        // white square always in left corner
        String[] layer1 = {"   ", " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", "   "};
        String[] leftNumbers = {" 8 ", " 7 ", " 6 ", " 5 ", " 4 ", " 3 ", " 2 ", " 1 ", "   "};
        String[] rightNumbers = {"   ", " 8 ", " 7 ", " 6 ", " 5 ", " 4 ", " 3 ", " 2 ", " 1 "};

        for (var part : layer1) {
            System.out.printf("%s%s%s%s%s", SET_BG_COLOR_DARK_GREEN, SET_TEXT_COLOR_WHITE, part, RESET_TEXT_COLOR, RESET_BG_COLOR);
        }
        for (int x = 0; x < 8; x++) {
            System.out.print("\n");
            System.out.printf("%s%s%s%s%s", SET_BG_COLOR_DARK_GREEN, SET_TEXT_COLOR_WHITE, leftNumbers[x], RESET_TEXT_COLOR, RESET_BG_COLOR);
            for (int y = 0; y < 8; y++) {
                // print black pieces

                if ((x + y) % 2 == 0 && (x > 0 && x < 3)) {
                    System.out.printf("%s   %s", SET_BG_COLOR_WHITE, RESET_BG_COLOR);
                } else {
                    System.out.printf("%s   %s", SET_BG_COLOR_BLACK, RESET_BG_COLOR);
                }
            }
            System.out.printf("%s%s%s%s%s", SET_BG_COLOR_DARK_GREEN, SET_TEXT_COLOR_WHITE, rightNumbers[x], RESET_TEXT_COLOR, RESET_BG_COLOR);
        }
        System.out.print("\n");
        for (var part : layer1) {
            System.out.printf("%s%s%s%s%s", SET_BG_COLOR_DARK_GREEN, SET_TEXT_COLOR_WHITE, part, RESET_TEXT_COLOR, RESET_BG_COLOR);
        }
        System.out.print("\n");

//        for (var part : layer1) {
//            System.out.printf("%s%s%s%s%s", SET_BG_COLOR_DARK_GREEN, SET_BG_COLOR_BLACK, part, RESET_TEXT_COLOR, RESET_BG_COLOR);
//        }
//        System.out.print("\n");
//        System.out.printf("%s%s%s\n",SET_BG_COLOR_BLACK, WHITE_KING, RESET_BG_COLOR);

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
