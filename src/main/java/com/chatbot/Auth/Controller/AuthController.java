package com.chatbot.Auth.Controller;

import com.chatbot.Auth.Class.User;
import com.chatbot.Auth.Controller.UserController;
import com.chatbot.Auth.Repo.UserService;
import com.chatbot.Auth.SecurityConfig.JwtUtil;
import com.chatbot.Auth.SecurityConfig.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @PostMapping
    public ResponseEntity<String> saveUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Collecte les messages d'erreur des validations
            String errors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                    .orElse("Invalid input");

            // Retourne les erreurs de validation
            return ResponseEntity.badRequest().body(errors);
        }

        // Enregistre l'utilisateur et retourne un succès
        userService.saveUser(user);
        return ResponseEntity.ok("Le user est bien enregistré !");
    }


    @GetMapping("/{email}")
    public User getUser(@PathVariable String email) {
        logger.info("Fetching user with email: {}", email);
        return userService.getUserByEmail(email);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        User user = userService.getUserByEmail(request.getEmail());

        // Vérifier si l'utilisateur existe
        if (user == null) {
            return ResponseEntity.badRequest().body("Utilisateur introuvable");
        }

        // Vérifier si le mot de passe est correct
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Mot de passe incorrect");
        }

        String token = jwtUtil.generateToken(user.getEmail(),user.getId());
        return ResponseEntity.ok(token);
    }

    @Autowired
    private TokenBlacklistService blacklistService;

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String token) {
        try {
            // Supprimer le préfixe "Bearer " du token
            token = token.replace("Bearer ", "");

            // Décoder le token JWT
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtUtil.SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();

            // Ajouter le token à la liste noire
            blacklistService.addTokenToBlacklist(token, claims.getExpiration());

            Map<String, String> response = new HashMap<>();
            response.put("message", "Déconnexion réussie");

            return ResponseEntity.ok(response);
        } catch (ExpiredJwtException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Token expiré"));
        } catch (SignatureException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Signature du token invalide"));
        } catch (JwtException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Token invalide ou incorrect"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Une erreur interne est survenue"));
        }
    }


}

