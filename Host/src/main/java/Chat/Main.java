package Chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@SpringBootApplication
@EnableAutoConfiguration
public class Main{
    public static void main(String[] args){
        SpringApplication.run(Main.class,args);
    }
}