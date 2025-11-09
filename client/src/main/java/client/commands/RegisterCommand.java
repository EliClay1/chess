package client.commands;

import client.ServerFacade;
import client.UserState;
import client.results.CommandResult;
import client.results.ValidationResult;
import exceptions.AlreadyTakenException;
import exceptions.InvalidException;

import java.util.List;
import java.util.Map;

public class RegisterCommand implements CommandInterface{

    private final ServerFacade serverFacade = new ServerFacade();

    @Override
    public String getName() {
        return "register";
    }

    @Override
    public List<String> getAliases() {
        return List.of("r");
    }

    @Override
    public String getUsage() {
        return "Register as a new user: \"r\", \"register\" <USERNAME> <PASSWORD> <EMAIL>\n";
    }

    @Override
    public int getMinArgs() {
        return 3;
    }

    @Override
    public int getMaxArgs() {
        return 3;
    }

    @Override
    public boolean requiresLogin() {
        return false;
    }

    @Override
    public ValidationResult validate(String[] args, UserState userState) {
        // argument length check
        if (args.length == 3) {
            return new ValidationResult(true, "");
        }
        return new ValidationResult(false, "Incorrect amount of arguments, expected 3.");
    }

    @Override
    public CommandResult execute(String[] args, UserState userState, CommandRegistry registery) {
        String username = args[0];
        String password = args[1];
        String email = args[2];

        try {
            Map<String, String> body = serverFacade.registerUser("localhost", 8080, "/user", username, password, email);
            userState.setAuthToken(body.get("authToken"));
            userState.setUsername(body.get("username"));
            userState.setLoggedIn(true);
            return new CommandResult(true, String.format("Successfully registered new user %s.\n", username));
        } catch (Exception e) {
            if (e instanceof InvalidException) {
                return new CommandResult(false, "Invalid characters.");
            } else if (e instanceof AlreadyTakenException) {
                return new CommandResult(false, "That username has already been taken.");
            } else {
                return new CommandResult(false, "Error: " + e.getMessage());
            }
        }
    }
}
