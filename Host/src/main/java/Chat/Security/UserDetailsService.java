package Chat.Security;

import Chat.Dao.User;
import Chat.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("UserDetailsService")
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepository.findUserByEmail(username);
        if(u!=null) {
            return u;
        }
        else{
            throw  new UsernameNotFoundException("Username not found");
        }
    }
}
