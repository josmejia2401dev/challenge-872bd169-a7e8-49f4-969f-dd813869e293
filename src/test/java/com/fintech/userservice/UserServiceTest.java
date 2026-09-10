package com.fintech.userservice;

import com.fintech.userservice.application.UserService;
import com.fintech.userservice.domain.User;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureDataMongo
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    public void cleanDatabase() {
        mongoTemplate.dropCollection(User.class);
    }

    @Test
    public void testCreateUser() {
        User user = new User("1", "John Doe", "john.doe@example.com");
        User createdUser = userService.createUser(user);
        assertNotNull(createdUser);
        assertEquals(user.getName(), createdUser.getName());
        assertEquals(user.getEmail(), createdUser.getEmail());
    }

    @Test
    public void testGetUserById() {
        User user = new User("2", "Jane Doe", "jane.doe@example.com");
        User createdUser = userService.createUser(user);
        User retrievedUser = userService.getUserById(createdUser.getId());
        assertNotNull(retrievedUser);
        assertEquals(createdUser.getName(), retrievedUser.getName());
        assertEquals(createdUser.getEmail(), retrievedUser.getEmail());
    }

    @Test
    public void testUpdateUser() {
        User user = new User("3", "Jim Beam", "jim.beam@example.com");
        User createdUser = userService.createUser(user);
        User updatedUser = new User(createdUser.getId(), "Jim Beam Updated", "jim.beam.updated@example.com");
        User result = userService.updateUser(createdUser.getId(), updatedUser);
        assertNotNull(result);
        assertEquals(updatedUser.getName(), result.getName());
        assertEquals(updatedUser.getEmail(), result.getEmail());
    }

    @Test
    public void testDeleteUser() {
        User user = new User("4", "Jack Daniels", "jack.daniels@example.com");
        User createdUser = userService.createUser(user);
        userService.deleteUser(createdUser.getId());
        User deletedUser = userService.getUserById(createdUser.getId());
        assertNull(deletedUser);
    }
}
