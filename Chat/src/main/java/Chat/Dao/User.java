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

    private Boolean enabled;

    private Boolean accountNonExpired;

    private Boolean accountNonLocked;

    private boolean credentialsNonExpired;

    public User(String email, String username, String password, Collection<Long> chats, Boolean enabled, Collection<? extends GrantedAuthority> authorities){
        this.email = email;
        this.username = username;
        this.password = password;
        this.chats = chats;
        this.enabled = enabled;
        this.authorities = authorities;
        this.accountNonExpired = true;
        this.credentialsNonExpired = true;
        this.accountNonLocked = true;
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
}
