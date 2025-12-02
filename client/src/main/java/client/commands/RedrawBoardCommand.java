package client.commands;

import client.ClientState;
import client.ServerFacade;
import client.UserStateData;
import client.results.CommandResult;
import client.results.ValidationResult;

import java.util.Collection;
import java.util.List;

public class RedrawBoardCommand extends BaseCommand {

    public final int argumentCount = 0;
    private final ServerFacade serverFacade = new ServerFacade();
    private UserStateData userStateData;

    @Override
    public String getName() {
        return "redraw";
    }

    @Override
    public List<String> getAliases() {
        return List.of("rd");
    }

    @Override
    public String getUsage() {
        return "Redraw the game board: \"rd\", \"redraw\"";
    }

    @Override
    public Collection<ClientState> allowedStates() {
        return List.of(ClientState.PLAYING_GAME, ClientState.OBSERVING_GAME);
    }

    @Override
    public ValidationResult validate(String[] args, UserStateData userStateData) {
        if (args.length == argumentCount) {
            return new ValidationResult(true, "");
        }
        return new ValidationResult(false, String.format("Incorrect amount of arguments, expected %d.", argumentCount));
    }

    @Override
    public CommandResult execute(String[] args, UserStateData userState, CommandRegistry registery) throws Exception {
        this.userStateData = userState;
        serverFacade.printBoard(userStateData.getActiveTeamColor(), userStateData.);
    }
}
