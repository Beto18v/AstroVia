package com.astrovia.config;

import com.astrovia.entity.Usuario;
import com.astrovia.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// Eliminado AntPathRequestMatcher (deprecated) – usamos patrones directos en requestMatchers

/**
 * Configuración central de seguridad con JWT y control de roles.
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, PasswordEncoder passwordEncoder, UsuarioRepository usuarioRepository) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
    }

    /** Carga usuarios desde la base de datos convirtiendo el Rol en autoridad ROLE_<ROL>. */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Usuario u = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
            return User.withUsername(u.getUsername())
                    .password(u.getPassword())
                    .roles(u.getRol().name())
                    .disabled(!Boolean.TRUE.equals(u.getActivo()))
                    .build();
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                String username = (authentication.getPrincipal() == null) ? null : authentication.getName();
                String rawPassword = (String) authentication.getCredentials();
                if (username == null) throw new AuthenticationCredentialsNotFoundException("Username requerido");
                var userDetails = userDetailsService().loadUserByUsername(username);
                if (!passwordEncoder.matches(rawPassword, userDetails.getPassword())) {
                    throw new BadCredentialsException("Credenciales inválidas");
                }
                return new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return org.springframework.security.authentication.UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/api/auth/**",
                "/api/tracking/public/**",
                "/",
                "/index.html",
                "/assets/**",
                "/static/**"
            ).permitAll()
            .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
