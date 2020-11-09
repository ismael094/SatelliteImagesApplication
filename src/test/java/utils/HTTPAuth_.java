package utils;

import javax.net.ssl.HttpsURLConnection;

import model.exception.AuthenticationException;
import org.junit.Test;
import utils.http.CopernicusHTTPAuthManager;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class HTTPAuth_ {

    @Test
    public void setConnection() throws IOException, AuthenticationException {
        HttpsURLConnection connection = mock(HttpsURLConnection.class);
        doReturn(200).when(connection).getResponseCode();


    }
}
