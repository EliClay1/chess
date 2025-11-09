package client.commands;

import client.ServerFacade;
import client.UserState;
import client.results.CommandResult;
import client.results.ValidationResult;
import exceptions.AlreadyTakenException;
import exceptions.InvalidException;
import exceptions.UnauthorizedException;

import java.util.List;
import java.util.Map;

public class LoginCommand implements CommandInterface{

    private final ServerFacade serverFacade = new ServerFacade();

    @Override
    public String getName() {
        return "login";
    }

    @Override
    public List<String> getAliases() {
        return List.of("l");
    }

    @Override
    public String getUsage() {
        return "Login as an existing user: \"l\", \"login\" <USERNAME> <PASSWORD>\n";
    }

    @Override
    public int getMinArgs() {
        return 2;
    }

    @Override
    public int getMaxArgs() {
        return 2;
    }

    @Override
    public boolean requiresLogin() {
        return false;
    }

    @Override
    public ValidationResult validate(String[] args, UserState userState) {
        // argument length check
        if (args.length == getMinArgs()) {
            return new ValidationResult(true, "");
        }
        return new ValidationResult(false, "Incorrect amount of arguments, expected 2.");
    }

    @Override
    public CommandResult execute(String[] args, UserState userState, CommandRegistry registery) {
        String username = args[0];
        String password = args[1];

        try {
            Map<String, String> body = serverFacade.loginUser("localhost", 8080, "/session", username, password);
            userState.setAuthToken(body.get("authToken"));
            userState.setUsername(body.get("username"));
            userState.setLoggedIn(true);
            return new CommandResult(true, "Successfully logged in.\n");
        } catch (Exception e) {
            if (e instanceof InvalidException) {
                return new CommandResult(false, "Invalid characters.");
            } else if (e instanceof UnauthorizedException) {
                return new CommandResult(false, "Incorrect password.");
            } else {
                return new CommandResult(false, "Error: " + e.getMessage());
            }
        }
    }
}
