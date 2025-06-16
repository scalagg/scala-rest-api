package gg.scala.restapi.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Value("${api.key}")
    private String apiKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("SecurityConfig loaded: API key = " + apiKey);
        return http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new ApiKeyAuthFilter(apiKey), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(
                                    new AntPathRequestMatcher("/swagger-ui/**"),
                                    new AntPathRequestMatcher("/v3/api-docs/**"),
                                    new AntPathRequestMatcher("/swagger-ui.html"),
                                    new AntPathRequestMatcher("/swagger-resources/**"),
                                    new AntPathRequestMatcher("/webjars/**"),
                                    new AntPathRequestMatcher("/css/**"),
                                    new AntPathRequestMatcher("/js/**"),
                                    new AntPathRequestMatcher("/images/**"),
                                    new AntPathRequestMatcher("/actuator/health"),
                                    new AntPathRequestMatcher("/auth/**"),
                                    new AntPathRequestMatcher("/profile/**"),
                                    new AntPathRequestMatcher("/practiceprofile/**"),
                                    new AntPathRequestMatcher("/forum/**"),
                                    new AntPathRequestMatcher("/grant/**"),
                                    new AntPathRequestMatcher("/otherapi/**"),
                                    new AntPathRequestMatcher("/forum/account/**")
                                ).permitAll()
                                .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}