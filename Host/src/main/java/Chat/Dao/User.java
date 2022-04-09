package Chat.Dao;

import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Document(collection = "Users")
public class User implements UserDetails {
    private Collection<? extends GrantedAuthority> authorities;

    @Id
    private String id;
    private String email;

    private Collection<Long> chats;

    private String password;

    private String username;
    private String description;
    private Collection<String> friend_usernames;

    private Boolean enabled;

    private Boolean accountNonExpired;

    private Boolean accountNonLocked;

    private boolean credentialsNonExpired;

    public Collection<String> friend_requester_usernames;

    public Collection<Long> groups;

    public User(String email, String username, String password,String description, Collection<Long> chats,Collection<String> friend_usernames,Collection<String> friend_requester_usernames,Collection<Long> groups, Boolean enabled, Collection<? extends GrantedAuthority> authorities){
        this.email = email;
        this.username = username;
        this.password = password;
        this.chats = chats;
        this.enabled = enabled;
        this.authorities = authorities;
        this.accountNonExpired = true;
        this.credentialsNonExpired = true;
        this.accountNonLocked = true;
        this.friend_usernames = friend_usernames;
        this.friend_requester_usernames = friend_requester_usernames;
        this.description = description;
        this.groups = groups;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
    public Collection<Long> getChats(){
        return chats;
    }
    public void setChats(Collection<Long> chats){
        this.chats = chats;
    }
    public void addChat(Long id){
        chats.add(id);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<String> getFriendUsernames() {
        return friend_usernames;
    }
    public void addFriend(User friend){
        friend_usernames.add(friend.username);
    }
    public void addFriendRequest(User friend){
        if(!friend_usernames.contains(friend.username) && !friend_requester_usernames.contains(friend.username)){
            friend_requester_usernames.add(friend.username);
        }
    }
    public Collection<String> getFriendRequests() {
        return friend_requester_usernames;
    }

    @Override
    public String toString() {
        return "User{" +
                "authorities=" + authorities +
                ", id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", chats=" + chats +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", description='" + description + '\'' +
                ", friend_usernames=" + friend_usernames +
                ", enabled=" + enabled +
                ", accountNonExpired=" + accountNonExpired +
                ", accountNonLocked=" + accountNonLocked +
                ", credentialsNonExpired=" + credentialsNonExpired +
                ", friend_requester_usernames=" + friend_requester_usernames +
                '}';
    }
}
