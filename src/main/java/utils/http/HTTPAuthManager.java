package utils.http;

import model.exception.AuthenticationException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public interface HTTPAuthManager {

    void login() throws AuthenticationException;

    void setCredentials(String username, String password);

    InputStream getContentFromURL(URL url) throws IOException, AuthenticationException;

    void closeConnection();
}
