package com.chatbot.Auth.Repo;
import com.chatbot.Auth.Class.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepository;
    private PasswordEncoder passwordEncoder;
    @Autowired
    public UserService(UserRepo userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public User saveUser(User user) {
        // Hachage du mot de passe
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }


    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
