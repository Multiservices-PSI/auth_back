package promo67.login.invent.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    String principal = (request.getUsername() != null && !request.getUsername().isEmpty()) 
                        ? request.getUsername() 
                        : request.getEmail();

    Authentication authentication = authman.authenticate(
            new UsernamePasswordAuthenticationToken(principal, request.getPassword()));

    UserDetails user = (UserDetails) authentication.getPrincipal();

    return AuthResponse.builder()
            .token(serv.getToken(user))
            .build();
}

    public AuthResponse register(RegisterRequest request) {

        if (repo.findByUsername(request.getUsername()).isPresent()) {
        throw new IllegalArgumentException("El nombre de usuario ya está en uso."); 
        }
        else if (repo.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado."); 
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
