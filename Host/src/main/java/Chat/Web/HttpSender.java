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
    public void send_forgot(String email) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().GET().header("email",email).uri(URI.create("http://172.16.238.107:8084/send")).build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
    public void change_pwd(String seed,String newPassword) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().GET().header("password",newPassword).uri(URI.create("http://172.16.238.107:8084/forgot/"+seed)).build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
    public boolean send_captcha(String captcha) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString("secret=6LcOFBkcAAAAAMfWs05GUcPFJe9OaktIdoXzdopK&response="+captcha)).header("Content-Type","application/x-www-form-urlencoded").uri(URI.create("https://www.google.com/recaptcha/api/siteverify")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body().contains("\"success\": true");
    }
}
