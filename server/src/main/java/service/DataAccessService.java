package service;

import dataaccess.DataAccess;

public record DataAccessService(DataAccess dataAccess) {
    public void clearAllData() throws Exception {
        try {
            dataAccess.clear();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
