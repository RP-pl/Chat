package Chat.Controllers;

import Chat.Dao.Chat;
import Chat.Dao.Group;
import Chat.Dao.User;
import Chat.Repositories.ChatRepository;
import Chat.Repositories.GroupRepository;
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

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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
    GroupRepository groupRepository;
    @Autowired
    HttpSender httpSender;

    @GetMapping("/register")
    public String register(){
        return "register.html";
    }
    @PostMapping("/register")
    public String register(String email,String username,String password,String description,@RequestParam("g-recaptcha-response") String captcha) throws IOException, InterruptedException {
        if(!httpSender.send_captcha(captcha)){
            return "redirect:/register?captcha";
        }
        if(userRepository.findUserByUsername(username) == null) {
            if(userRepository.findUserByEmail(email) == null) {
                List<GrantedAuthority> auth = new ArrayList<GrantedAuthority>();
                auth.add(new SimpleGrantedAuthority("ROLE_USER"));
                User u = new User(email, username, passwordEncoder.encode(password),description, new LinkedList<>(),new LinkedList<>(),new LinkedList<>(),new LinkedList<>(), false, auth);
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
            Chat newChat = new Chat(id, password, style, new LinkedList<>(),user.getId());
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
    public String chat() {
        return "index.html";
    }
    @GetMapping("/group")
    public String group(Model model,Principal principal) {
        List<Group> groups = new LinkedList<>();
        for(Group g: groupRepository.findAll()){
            if(g.usernames.contains(principal.getName())){
                groups.add(g);
            }
        }
        User u = userRepository.findUserByUsername(principal.getName());
        List<String> friends = new LinkedList<>(u.getFriendUsernames());
        model.addAttribute("friends",friends);
        model.addAttribute("groups",groups);
        return "group_index.html";
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
        model.addAttribute("descr",u.getDescription());
        return "home";
    }
    @GetMapping("/authorize/{auth}")
    public String authorize(@PathVariable String auth) throws IOException, InterruptedException {
        httpSender.authorize(auth);
        return "redirect:/login";
    }
    @GetMapping("/forgot")
    public String forgot_get() throws IOException, InterruptedException {
        return "forgot";
    }
    @PostMapping("/forgot")
    public String forgot_post(String email) throws IOException, InterruptedException {
        httpSender.send_forgot(email);
        return "redirect:/login";
    }
    @GetMapping("/forgot/{seed}")
    public String change_pwd_get(@PathVariable String seed,Model model) throws IOException, InterruptedException {
        model.addAttribute("seed","/forgot/"+seed);
        return "change_pwd";
    }
    @PostMapping("/forgot/{seed}")
    public String change_pwd_post(@PathVariable String seed,String passwd,String repeat) throws IOException, InterruptedException {
        httpSender.change_pwd(seed,passwd);
        return "redirect:/login";
    }
    @GetMapping("/settings")
    public String settings(Model model,Principal principal) {
        User u = userRepository.findUserByUsername(principal.getName());
        System.out.println(principal.getName());
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
            Authentication authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "redirect:/settings?uname";
        }
        else {
            return "redirect:/settings?taken";
        }
    }
    @PostMapping("/cpwd")
    public String change_password(String password,Principal principal){
        User user = userRepository.findUserByUsername(principal.getName());
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return "redirect:/settings?pwd";
    }
    @PostMapping("/cemail")
    public String change_email(String email,Principal principal){
        User user = userRepository.findUserByUsername(principal.getName());
        user.setEmail(email);
        userRepository.save(user);
        return "redirect:/settings?email";
    }
    @GetMapping("/mfr")
    public String makeFriendRequestGet(){
        return "make_friend_request";
    }

    @PostMapping("/mfr")
    public String makeFriendRequestPost(String username,Principal principal){
        User requested = userRepository.findUserByUsername(username);
        requested.addFriendRequest(userRepository.findUserByUsername(principal.getName()));
        userRepository.save(requested);
        return "redirect:/?mfr";
    }
    @GetMapping("/check/if/friends")
    @ResponseBody
    public String checkFriendRequest(HttpServletRequest request,Principal principal){
        User user = userRepository.findUserByUsername(principal.getName());
        User requested = userRepository.findUserByUsername(request.getHeader("username"));
        if(requested == null){
            return "Null";
        }
        if(requested.getFriendRequests().contains(user.getUsername())||requested.getFriendUsernames().contains(user.getUsername())) {
            System.out.println("True");
            return "True";
        }
        return "False";
    }
    @GetMapping("/view")
    public String view(Model model,Principal principal){
        User user = userRepository.findUserByUsername(principal.getName());
        List<User> friends = new ArrayList<>();
        for(String friend : user.getFriendUsernames()){
            friends.add(userRepository.findUserByUsername(friend));
        }
        model.addAttribute("friends",friends);
        return "friends";
    }
    @GetMapping("/check")
    public String checkRequests(Model model,Principal principal){
        User user = userRepository.findUserByUsername(principal.getName());
        List<User> friends = new ArrayList<>();
        for(String friend : user.getFriendRequests()){
            friends.add(userRepository.findUserByUsername(friend));
        }
        model.addAttribute("friendRequests",friends);
        return "friend_requests";
    }
    @GetMapping("/accept")
    @ResponseBody
    public String accept(HttpServletRequest request,Principal principal){
        User user = userRepository.findUserByUsername(principal.getName());
        String requestingUsername = request.getHeader("username");
        if(user.getFriendRequests().contains(requestingUsername)) {
            user.addFriend(userRepository.findUserByUsername(requestingUsername));
            user.friend_requester_usernames.remove(requestingUsername);
            User requestingUser = userRepository.findUserByUsername(requestingUsername);
            requestingUser.addFriend(user);
            userRepository.save(user);
            userRepository.save(requestingUser);
        }
        return "OK";
    }
    @GetMapping("/decline")
    @ResponseBody
    public String decline(HttpServletRequest request,Principal principal){
        User user = userRepository.findUserByUsername(principal.getName());
        String requestingUsername = request.getHeader("username");
        if(user.getFriendRequests().contains(requestingUsername)) {
            user.friend_requester_usernames.remove(requestingUsername);
            userRepository.save(user);
        }
        return "OK";
    }
    @GetMapping("/friendList")
    @ResponseBody
    public List<String> friendList(Principal principal){
        User user = userRepository.findUserByUsername(principal.getName());
        List<String> userList = new LinkedList<>();
        userList.addAll(user.getFriendUsernames());
        return userList;
    }
    @GetMapping("/create/group")
    public String createGroup(Model model,Principal principal){
        model.addAttribute("friends",userRepository.findUserByUsername(principal.getName()).getFriendUsernames());
        model.addAttribute("styles",styleRepository.findAll());
        return "create_group";
    }
    @PostMapping("/create/group")
    public String createGroup(Principal principal,String style,String[] friends,String name){
            User u = userRepository.findUserByUsername(principal.getName());
            Random r = new Random();
            List<String> f = new ArrayList<>(List.of(friends));
            boolean unique = true;
            long id = 0;
            while(!unique || id == 0){
                id = Math.abs(r.nextLong());
                unique=true;
                for(Group g: groupRepository.findAll()){
                    if(id==g.getId()){
                        unique=false;
                    }
                }
            }
            if(style == null){
                style = "default";
            }
            Group group = new Group(id,f,name,style,new LinkedList<>());
            group.usernames.add(u.getUsername());
            groupRepository.save(group);
            u.groups.add(id);
            userRepository.save(u);
            for(String friend : friends){
                User usr = userRepository.findUserByUsername(friend);
                usr.groups.add(id);
                userRepository.save(usr);
            }
            return "redirect:/";
    }
    @GetMapping("/invite/friend")
    @ResponseBody
    public String invite(Principal principal,HttpServletRequest request){
        String username = request.getHeader("username");
        Long groupId = Long.getLong(request.getHeader("group"));
        User friend = userRepository.findUserByUsername(username);
        User user = userRepository.findUserByUsername(principal.getName());
        if(user.getFriendUsernames().contains(friend.getUsername()) && !friend.groups.contains(groupId)){
            friend.groups.add(groupId);
            userRepository.save(friend);
            return "OK";
        }
        return "!Ok";
    }
}
