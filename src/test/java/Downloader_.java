import model.SentinelData;
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
import services.download.DownloadManager;
import services.download.DownloadItem;

import javax.net.ssl.HttpsURLConnection;
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
        System.out.println(
                urlConnection.getHeaderField(0)+ " " + urlConnection.getHeaderFieldKey(0));
        System.out.println(
                "Header Field Date: " + urlConnection.getHeaderField("date"));
        InputStream inputstream = urlConnection.getInputStream();
        System.out.printf(inputstream.available()+"");
        IOUtils.copy(urlConnection.getInputStream(), new FileOutputStream("test.zip"));
    }

    @Test
    public void test3() throws IOException {
        URL url = null;
        HttpsURLConnection connection;
        String userpass = username + ":" + pass;
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
        try {
            url = new URL("https://scihub.copernicus.eu/dhus/odata/v1/Products('79eadf28-84eb-4eea-a937-fc48d78fd4d6')/$value");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("Accept-Encoding", "identity");
        connection.setRequestProperty ("Authorization", basicAuth);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.89 Safari/537.36 RuxitSynthetic/1.0 v6418838628 t38550 ath9b965f92 altpub");
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-length", "0");
        connection.setRequestProperty("Range","bytes=0-1023");
        System.out.println(connection.getHeaderField("Accept-Ranges"));
        System.out.println(connection.getContentLength());
    }

    @Test
    public void the_test() throws InterruptedException {
        DownloadManager downloadManager = new DownloadManager(2);
        Thread thread = new Thread(downloadManager);
        thread.start();
        Thread.sleep(1000);
    }




}

class MyAuthenticator extends Authenticator {
    final PasswordAuthentication authentication;

    public MyAuthenticator(String userName, String password) {
        authentication = new PasswordAuthentication(userName, password.toCharArray());
    }
}
