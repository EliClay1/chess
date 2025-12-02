package client.commands.command_implementation;

import java.util.*;

public class CommandRegistry {

    private final Map<String, CommandInterface> commandMap = new HashMap<>();
    public void register(CommandInterface command) {
        commandMap.put(command.getName(), command);
        for (String alias : command.getAliases()) {
            commandMap.put(alias, command);
        }
    }

    public CommandInterface find(String cmdName) {
        return commandMap.get(cmdName);
    }

    public Collection<CommandInterface> getAllCommands() {
        return new HashSet<>(commandMap.values());
    }
}
