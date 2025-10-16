package model;

import exceptions.AlreadyTakenException;

public record AuthData(String username, String authToken) {

}
