package client;

import java.util.List;

public interface CommandInterface {
    String getName();
    List<String> getAliases();
    String getUsage();
    int getMinArgs();
    int getMaxArgs();
    boolean requiresLogin();
    ValidationResult validate(String[] args, UserState userState);
    CommandResult execute(String[] args, UserState userState);
}
