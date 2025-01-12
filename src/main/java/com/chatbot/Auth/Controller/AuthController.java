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

import java.util.*;

@CrossOrigin(origins = "http://localhost:4200")
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
    public ResponseEntity<Map<String, Object>> saveUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Collecte les messages d'erreur des validations
            List<String> errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .toList();

            // Retourne les erreurs sous forme de JSON
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Validation échouée",
                    "errors", errorMessages
            ));
        }

        // Enregistre l'utilisateur et retourne un succès
        userService.saveUser(user);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Le user est bien enregistré !"
        ));
    }


    @GetMapping("/{email}")
    public User getUser(@PathVariable String email) {
        logger.info("Fetching user with email: {}", email);
        return userService.getUserByEmail(email);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        User user = userService.getUserByEmail(request.getEmail());

        // Vérifier si l'utilisateur existe
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Utilisateur introuvable"
            ));
        }

        // Vérifier si le mot de passe est correct
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Mot de passe incorrect"
            ));
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getFirstName(), user.getLastName());

        // Retourner une réponse JSON avec le token
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Authentification réussie",
                "token", token
        ));
    }


    @Autowired
    private TokenBlacklistService blacklistService;

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader("Authorization") String token) {
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

            // Réponse en cas de succès
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Déconnexion réussie"
            ));
        } catch (ExpiredJwtException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Token expiré"
            ));
        } catch (SignatureException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Signature du token invalide"
            ));
        } catch (JwtException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Token invalide ou incorrect"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Une erreur interne est survenue"
            ));
        }
    }



}

