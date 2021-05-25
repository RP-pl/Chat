package Chat.Controllers;

import Chat.Dao.Chat;
import Chat.Dao.User;
import Chat.Repositories.ChatRepository;
import Chat.Repositories.StyleRepository;
import Chat.Repositories.UserRepository;
import Chat.Web.HttpSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ChatRepository chatRepository;
    @Autowired
    StyleRepository styleRepository;
    @Autowired
    HttpSender httpSender;

    @GetMapping("/register")
    public String register(){
        return "register.html";
    }
    @PostMapping("/register")
    public String register(String email,String username,String password) throws IOException, InterruptedException {
        if(userRepository.findUserByUsername(username) == null) {
            if(userRepository.findUserByEmail(email) == null) {
                List<GrantedAuthority> auth = new ArrayList<GrantedAuthority>();
                auth.add(new SimpleGrantedAuthority("ROLE_USER"));
                User u = new User(email, username, passwordEncoder.encode(password), new LinkedList<>(), false, auth);
                userRepository.save(u);
                Authentication authentication = new UsernamePasswordAuthenticationToken(u, null, auth);
                httpSender.send(username, email);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return "redirect:/login?authorize";
            }
            else{
                return "redirect:/register?exists";
            }
        }
        else{
            return "redirect:/register?exists";
        }
    }
    @GetMapping("/login")
    public String login(){
        return "login.html";
    }
    @GetMapping("/prc")
    @ResponseBody
    public Principal prc(Principal p){
        return  p;
    }
    @GetMapping("/create")
    public String create(Model model){
        model.addAttribute("styles",styleRepository.findAll());
        return "create";
    }
    @PostMapping("/create")
    public String create(Principal principal,Long id,String password,String style){
        if(chatRepository.findChatById(id) == null && id != 0) {
            User user = userRepository.findUserByUsername(principal.getName());
            Chat newChat = new Chat(id, password, style, new LinkedList<>());
            chatRepository.save(newChat);
            user.addChat(newChat.id);
            userRepository.save(user);
            return "redirect:/";
        }
        else{
            return "redirect:/create?exists";
        }
    }
    @GetMapping("/chat")
    public String hello() {
        return "index.html";
    }
    @GetMapping("/add")
    public String addChat(@RequestParam(required = false) String error){
        return "add.html";
    }
    @PostMapping("/add")
    public String addChat(Long id, String password, Principal principal){
        User user = userRepository.findUserByUsername(principal.getName());
        Chat chat = chatRepository.findChatById(id);
        if(chat == null){
            return "redirect:/add?error";
        }
        else if(!user.getChats().contains(id)){
            user.addChat(id);
            userRepository.save(user);
            return "redirect:/";
        }
        else if(!chat.password.equals(password)){
            return "redirect:/add?error";
        }
        else {
            return "redirect:/";
        }
    }
    @GetMapping("/")
    public String home(Model model,Principal principal){
        User u = userRepository.findUserByUsername(principal.getName());
        model.addAttribute("username",u.getUsername());
        model.addAttribute("chats",u.getChats());
        return "home";
    }
    @GetMapping("/authorize/{auth}")
    public String authorize(@PathVariable String auth) throws IOException, InterruptedException {
        httpSender.authorize(auth);
        return "redirect:/login";
    }
    @GetMapping("/settings")
    public String settings(Model model,Principal principal) {
        User u = userRepository.findUserByUsername(principal.getName());
        model.addAttribute("username", u.getUsername());
        model.addAttribute("password", u.getPassword());
        model.addAttribute("email", u.getEmail());
        return "settings";
    }
    @PostMapping("/cuname")
    public String change_username(String username,Principal principal){
        if(userRepository.findUserByUsername(username)==null){
            User user = userRepository.findUserByUsername(principal.getName());
            user.setUsername(username);
            userRepository.save(user);
            return "redirect:/settings?uname";
        }
        else {
            return "redirect:/settings?taken";
        }
    }
    @PostMapping("/cpwd")
    public String change_password(String password,Principal principal){
        User user = userRepository.findUserByUsername(principal.getName());
        user.setUsername(password);
        userRepository.save(user);
        return "redirect:/settings?pwd";
    }
    @PostMapping("/cemail")
    public String change_email(String email,Principal principal){
        User user = userRepository.findUserByUsername(principal.getName());
        user.setUsername(email);
        userRepository.save(user);
        return "redirect:/settings?email";
    }
}
