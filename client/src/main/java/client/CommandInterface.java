package client;

public interface CommandInterface {
    String getName();
    String getAliases();
    String getUsage();
    int getMinArgs();
    int getMaxArgs();
    boolean requiresLogin();
    void validate(String[] args, UserState userState);
    void execute(String[] args, UserState userState);
}
