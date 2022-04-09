package Chat.Controllers;

import Chat.Dao.User;
import Chat.Repositories.UserRepository;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.Principal;
import java.time.Duration;

@RestController
public class FileController {
    @Autowired
    UserRepository userRepository;
    @GetMapping("/file/{chat}/{file}")
    public ResponseEntity<StreamingResponseBody> returnFile(Principal principal, @PathVariable String chat, @PathVariable String file) throws IOException {
        User usr = userRepository.findUserByUsername(principal.getName());
        if(usr.getChats().contains(Long.valueOf(chat))) {
            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(180)).build();
            StreamingResponseBody responseBody = outputStream -> {
                HttpURLConnection urlConnection = (HttpURLConnection) (new URL("http://172.16.238.105:5000/file/return/" + chat + "/" + file)).openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(false);
                InputStream connectionStream = urlConnection.getInputStream();
                for (byte b : connectionStream.readAllBytes()) {
                    outputStream.write(b);
                }
                outputStream.flush();
                urlConnection.disconnect();
            };
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(responseBody);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    @PostMapping("/file/{chat}/{file}/{ext}")
    public ResponseEntity<String> createFile(Principal principal,@RequestBody String fileContents, @PathVariable String chat, @PathVariable String file, @PathVariable String ext) throws IOException, InterruptedException {
        User usr = userRepository.findUserByUsername(principal.getName());
        if (usr.getChats().contains(Long.valueOf(chat))) {
            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(180)).build();
            HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofByteArray(Base64.decodeBase64(fileContents))).uri(URI.create("http://172.16.238.105:5000/file/create/" + chat + "/" + file + "/" + ext)).build();
            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(resp.body());
            return ResponseEntity.ok().body(resp.body());
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
