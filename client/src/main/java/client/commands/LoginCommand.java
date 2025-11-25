package client.commands;

import client.ServerFacade;
import client.UserStateData;
import client.results.CommandResult;
import client.results.ValidationResult;
import exceptions.InvalidException;
import exceptions.UnauthorizedException;

import java.util.List;
import java.util.Map;

public class LoginCommand implements CommandInterface{

    // TODO - allow login and list to both use l. Essentially block running auth-required commands. Figure this out.

    private final int argumentCount = 2;
    private final ServerFacade serverFacade = new ServerFacade();

    @Override
    public String getName() {
        return "login";
    }

    @Override
    public List<String> getAliases() {
        return List.of();
    }

    @Override
    public String getUsage() {
        return "Login as an existing user: \"login\" <USERNAME> <PASSWORD>\n";
    }

    @Override
    public boolean requiresLogin() {
        return false;
    }

    @Override
    public ValidationResult validate(String[] args, UserStateData userStateData) {
        // argument length check
        if (args.length == argumentCount) {
            return new ValidationResult(true, "");
        }
        return new ValidationResult(false, "Incorrect amount of arguments, expected 2.");
    }

    @Override
    public CommandResult execute(String[] args, UserStateData userStateData, CommandRegistry registery) {
        String username = args[0];
        String password = args[1];

        try {
            Map<String, String> body = serverFacade.loginUser("localhost", 8080, "/session", username, password);
            userStateData.setAuthToken(body.get("authToken"));
            userStateData.setUsername(body.get("username"));
            userStateData.setLoggedIn(true);
            return new CommandResult(true, "Successfully logged in.\n");
        } catch (Exception e) {
            if (e instanceof InvalidException) {
                return new CommandResult(false, "Invalid characters.");
            } else if (e instanceof UnauthorizedException) {
                return new CommandResult(false, "Incorrect password or that username doesn't exist.");
            } else {
                return new CommandResult(false, "Error: " + e.getMessage());
            }
        }
    }
}
