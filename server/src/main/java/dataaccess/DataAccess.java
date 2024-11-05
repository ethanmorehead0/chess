package dataaccess;

import exception.ResponseException;
import model.AuthData;

public interface DataAccess {
    void clear() throws ResponseException;
    AuthData login() throws ResponseException;
}
