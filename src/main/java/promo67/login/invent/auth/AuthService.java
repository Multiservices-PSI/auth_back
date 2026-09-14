package promo67.login.invent.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import promo67.login.invent.jwt.JwtService;
import promo67.login.invent.models.Role;
import promo67.login.invent.models.User;
import promo67.login.invent.models.UserRepository;

@Service
@RequiredArgsConstructor
class AuthService {

    private final UserRepository repo;
    private final JwtService serv;
    private final AuthenticationManager authman;
    private final PasswordEncoder encoder;

    public AuthResponse login(LoginRequest request) {
            authman.authenticate(new UsernamePasswordAuthenticationToken
                (request.getUsername(), request.getPassword()));
                UserDetails user = repo.findByUsernameOrEmail(request.getUsername(), request.getUsername()).orElseThrow();
            return AuthResponse.builder()
                .token(serv.getToken(user)).build();
    }

    public AuthResponse register(RegisterRequest request) {

        if (repo.findByUsernameOrEmail(request.getUsername(), request.getUsername()).isPresent()) {
        throw new IllegalArgumentException("El nombre de usuario ya está en uso,"+
        " o el correo electrónico ya está registrado."); 
        }

        User user = User.builder()
            .username(request.getUsername())
            .password(encoder.encode(request.getPassword()))
            .email(request.getEmail())
            .role(Role.USER)
            .build();

        repo.save(user);

        return AuthResponse.builder()
            .token(serv.getToken(user)).build();
    }

}
