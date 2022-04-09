package Chat.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    @Qualifier("UserDetailsService")
    UserDetailsService userDetailsService;


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin().loginPage("/login").loginProcessingUrl("/loginprc").defaultSuccessUrl("/",false);
        http.authorizeRequests()
                .antMatchers("/prc").hasRole("USER")
                .antMatchers("/").hasRole("USER")
                .antMatchers("/create").hasRole("USER")
                .antMatchers("/check").hasRole("USER")
                .antMatchers("/add").hasRole("USER")
                .antMatchers("/chat").hasRole("USER")
                .antMatchers("/settings").hasRole("USER")
                .antMatchers("/file").hasRole("USER")
                .antMatchers("/mfr").hasRole("USER")
                .antMatchers("/check").hasRole("USER")
                .antMatchers("/view").hasRole("USER")
                .antMatchers("/friendList").hasRole("USER")
                .antMatchers("/create/group").hasRole("USER")
                .antMatchers("/group").hasRole("USER");
        http.csrf().disable();
    }

    @Override
    protected UserDetailsService userDetailsService() {
        return super.userDetailsService();
    }
    @SuppressWarnings("deprecation")
    @Bean
    public static NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }
}
