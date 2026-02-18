package PPs.ShoppingApp.security;


import PPs.ShoppingApp.security.dto.AuthResponse;
import PPs.ShoppingApp.security.dto.LoginRequest;
import PPs.ShoppingApp.security.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;



import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepo, PasswordEncoder encoder,
                          AuthenticationManager authManager, JwtService jwtService) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (userRepo.existsByUsername(req.username())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        AppUser user = new AppUser();
        user.setUsername(req.username());
        user.setPasswordHash(encoder.encode(req.password()));
        user.setRoles(Set.of(Role.ROLE_USER)); // later we will create admin
        userRepo.save(user);

        return ResponseEntity.ok("Registered");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );

        String token = jwtService.generateToken(req.username());
        return ResponseEntity.ok(new AuthResponse(token));
    }

}
