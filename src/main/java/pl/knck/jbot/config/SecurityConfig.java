package pl.knck.jbot.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.knck.jbot.security.APIKeyAuthFilter;

@Configuration
public class SecurityConfig {

    @Value("${API_KEY:dev-api-key}")
    private String apiKey;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/v1/**").authenticated()
                        .anyRequest().authenticated()
                ).addFilterBefore(new APIKeyAuthFilter(apiKey), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}