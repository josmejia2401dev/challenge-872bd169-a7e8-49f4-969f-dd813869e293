package com.fintech.userservice.domain;

import java.util.List;

public interface UserRepository {

    User save(User user);

    User findById(String id);

    List<User> findAll();

    void deleteById(String id);
}
