package com.devstudy.testcodeprac.controller;

import com.devstudy.testcodeprac.domain.User;
import com.devstudy.testcodeprac.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping
  public ResponseEntity<User> createUser(@RequestParam String name,
                                       @RequestParam String email,
                                       @RequestParam Integer age) {
    User user = userService.createUser(name, email, age);
    return ResponseEntity.status(HttpStatus.CREATED).body(user);
  }

  @GetMapping("/{id}")
  public ResponseEntity<User> getUserById(@PathVariable Long id) {
    User user = userService.getUserById(id);
    return ResponseEntity.ok(user);
  }

  @GetMapping("/email/{email}")
  public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
    User user = userService.getUserByEmail(email);
    return ResponseEntity.ok(user);
  }

  @GetMapping
  public ResponseEntity<List<User>> getAllActiveUsers() {
    List<User> users = userService.getAllActiveUsers();
    return ResponseEntity.ok(users);
  }

  @GetMapping("/search")
  public ResponseEntity<List<User>> searchUsersByName(@RequestParam String name) {
    List<User> users = userService.searchUsersByName(name);
    return ResponseEntity.ok(users);
  }

  @PutMapping("/{id}")
  public ResponseEntity<User> updateUser(@PathVariable Long id,
                                       @RequestParam(required = false) String name,
                                       @RequestParam(required = false) Integer age) {
    User user = userService.updateUser(id, name, age);
    return ResponseEntity.ok(user);
  }

  @PutMapping("/{id}/deactivate")
  public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
    userService.deactivateUser(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}/reactivate")
  public ResponseEntity<Void> reactivateUser(@PathVariable Long id) {
    userService.reactivateUser(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/count")
  public ResponseEntity<Long> getActiveUserCount() {
    long count = userService.getActiveUserCount();
    return ResponseEntity.ok(count);
  }

  @GetMapping("/adults")
  public ResponseEntity<List<User>> getAdultUsers() {
    List<User> users = userService.getAdultUsers();
    return ResponseEntity.ok(users);
  }
}
