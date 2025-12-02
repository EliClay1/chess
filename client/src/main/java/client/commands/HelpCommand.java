package client.commands;

import client.ClientState;
import client.UserStateData;
import client.results.*;
import websocket.commands.UserGameCommand;

import java.util.Collection;
import java.util.List;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class HelpCommand implements CommandInterface{

    private final int argumentCount = 0;

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public List<String> getAliases() {
        return List.of("h");
    }

    @Override
    public String getUsage() {
        return "Display this help message: \"h\", \"help\"\n";
    }

    @Override
    public Collection<ClientState> allowedStates() {
        return List.of(ClientState.LOGGED_OUT, ClientState.LOGGED_IN, ClientState.PLAYING_GAME, ClientState.OBSERVING_GAME);
    }

    @Override
    public ValidationResult validate(String[] args, UserStateData userStateData) {
        if (args.length == argumentCount) {
            return new ValidationResult(true, "").ok();
        }
        return new ValidationResult(false, "Help doesn't take any arguments.").error();
    }

    @Override
    public CommandResult execute(String[] args, UserStateData userStateData, CommandRegistry registery,
                                 UserGameCommand.CommandType commandType) {
        System.out.printf("%sOptions:\n", SET_TEXT_COLOR_BLUE);
        for (CommandInterface command : registery.getAllCommands()) {
            if (command.allowedStates().contains(userStateData.clientState())) {
                System.out.print(" - " + command.getUsage());
            }
        }
        if (userStateData.clientState() == ClientState.PLAYING_GAME) {
            System.out.printf("\u001b[38;5;%dm%s%s", 6, "[Playing] >>> ", RESET_TEXT_COLOR);
        } else if (userStateData.clientState() == ClientState.OBSERVING_GAME) {
            System.out.printf("\u001b[38;5;%dm%s%s", 6, "[Observing] >>> ", RESET_TEXT_COLOR);
        }
        return new CommandResult(true, "");
    }
}
