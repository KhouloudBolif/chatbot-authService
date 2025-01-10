package com.chatbot.Auth.Repo;

import com.chatbot.Auth.Class.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepo extends MongoRepository<User, String> {
    User findByEmail(String email);
}

