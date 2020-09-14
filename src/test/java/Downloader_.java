import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;

import java.io.*;
import java.net.*;
import java.util.Base64;

public class Downloader_ {


    private final String username = "ismael096";
    private final String pass = "Test_password";

    @Test
    public void test() throws IOException {
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials
                = new UsernamePasswordCredentials("ismael096", "Test_password");
        provider.setCredentials(AuthScope.ANY, credentials);

        HttpClient client = HttpClientBuilder.create()
                .setDefaultCredentialsProvider(provider)
                .build();

        HttpResponse response = null;
        try {
            response = client.execute(
                    new HttpGet("https://scihub.copernicus.eu/dhus/odata/v2/Products('b31dd4b9-3812-4eaa-824c-87f8fb7e397f')/$value"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int statusCode = response.getStatusLine()
                .getStatusCode();
        System.out.println(statusCode);


    }

    @Test
    public void test2() throws IOException {
        URL url = null;
        try {
            url = new URL("https://scihub.copernicus.eu/dhus/odata/v1/Products('79eadf28-84eb-4eea-a937-fc48d78fd4d6')/$value");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        URLConnection urlConnection = url.openConnection();
        String userpass = username + ":" + pass;
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
        urlConnection.setRequestProperty ("Authorization", basicAuth);
        IOUtils.copy(urlConnection.getInputStream(), new FileOutputStream("test.zip"));
    }



}

class MyAuthenticator extends Authenticator {
    final PasswordAuthentication authentication;

    public MyAuthenticator(String userName, String password) {
        authentication = new PasswordAuthentication(userName, password.toCharArray());
    }
}
