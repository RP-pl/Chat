package Chat.Web;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service("HttpSender")
public class HttpSender {
    HttpClient client = HttpClient.newBuilder().build();
    public void send(String username,String email) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().GET().header("username",username).header("email",email).uri(URI.create("http://172.16.238.103:8082/send")).build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
    public void authorize(String hash) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("http://172.16.238.103:8082/authorize/"+hash)).build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}
