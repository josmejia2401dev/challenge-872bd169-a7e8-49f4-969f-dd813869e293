package com.fintech.userservice.infrastructure;

import com.fintech.userservice.domain.User;
import com.fintech.userservice.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public User save(User user) {
        return mongoTemplate.save(user);
    }

    @Override
    public User findById(String id) {
        return mongoTemplate.findById(id, User.class);
    }

    @Override
    public List<User> findAll() {
        return mongoTemplate.findAll(User.class);
    }

    @Override
    public void deleteById(String id) {
        mongoTemplate.remove(mongoTemplate.findById(id, User.class));
    }
}