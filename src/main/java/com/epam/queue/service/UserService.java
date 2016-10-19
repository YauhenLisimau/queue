package com.epam.queue.service;

import com.epam.queue.domain.User;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.mongodb.client.model.Filters.eq;

@Service
public class UserService extends BaseMongoService {

    private static final String collectionName = "user";

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(MongoDatabase mongoDatabase, PasswordEncoder passwordEncoder) {
        super(mongoDatabase, collectionName);
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void initCollection() {
        mongoCollection.createIndex(new Document("username", 1), new IndexOptions().unique(true));
    }

    public User findByUsername(String username) {
        Document document = username == null ? null : mongoCollection.find(eq("username", username.toLowerCase())).first();
        return document == null ? null : new User(document);
    }

    public void setUsername(Document user, String username) {
        user.put("username", username);
    }

    public String getUsername(Document user) {
        return user.getString("username");
    }

    public void setPassword(Document user, String password) {
        user.put("passwordHash", passwordEncoder.encode(password));
    }
}
