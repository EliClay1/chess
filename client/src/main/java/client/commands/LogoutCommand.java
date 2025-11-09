package client.commands;

import client.ServerFacade;
import client.UserState;
import client.results.CommandResult;
import client.results.ValidationResult;
import exceptions.InvalidException;
import exceptions.UnauthorizedException;

import java.util.List;
import java.util.Map;

public class LogoutCommand implements CommandInterface{

    private final ServerFacade serverFacade = new ServerFacade();

    @Override
    public String getName() {
        return "logout";
    }

    @Override
    public List<String> getAliases() {
        return List.of();
    }

    @Override
    public String getUsage() {
        return "Logout: \"logout\"\n";
    }

    @Override
    public int getMinArgs() {
        return 0;
    }

    @Override
    public int getMaxArgs() {
        return 0;
    }

    @Override
    public boolean requiresLogin() {
        return true;
    }

    @Override
    public ValidationResult validate(String[] args, UserState userState) {
        if (args.length == 0) {
            return new ValidationResult(true, "").ok();
        }
        return new ValidationResult(false, "Logout doesn't take any arguments.").error();
    }

    @Override
    public CommandResult execute(String[] args, UserState userState, CommandRegistry registery) {

        try {
            serverFacade.logoutUser("localhost", 8080, "/session", userState.getAuthToken());
            userState.setAuthToken(null);
            userState.setUsername(null);
            userState.setLoggedIn(false);
            return new CommandResult(true, "Successfully logged out.\n");
        } catch (Exception e) {
            return new CommandResult(false, "Error: " + e.getMessage());
        }
    }
}
