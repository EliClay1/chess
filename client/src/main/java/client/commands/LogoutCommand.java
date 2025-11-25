package client.commands;

import client.ServerFacade;
import client.UserStateData;
import client.results.CommandResult;
import client.results.ValidationResult;
import model.UserData;

import java.util.List;

public class LogoutCommand implements CommandInterface{

    private final ServerFacade serverFacade = new ServerFacade();
    private final int argumentCount = 0;

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
    public boolean requiresLogin() {
        return true;
    }

    @Override
    public ValidationResult validate(String[] args, UserStateData userStateData) {
        if (args.length == argumentCount) {
            return new ValidationResult(true, "").ok();
        }
        return new ValidationResult(false, "Logout doesn't take any arguments.").error();
    }

    @Override
    public CommandResult execute(String[] args, UserStateData userStateData, CommandRegistry registery) {

        try {
            serverFacade.logoutUser("localhost", 8080, "/session", userStateData.authToken());
            userStateData.setAuthToken(null);
            userStateData.setUsername(null);
            userStateData.setLoggedIn(false);
            return new CommandResult(true, "Successfully logged out.\n");
        } catch (Exception e) {
            return new CommandResult(false, "Error: " + e.getMessage());
        }
    }
}
