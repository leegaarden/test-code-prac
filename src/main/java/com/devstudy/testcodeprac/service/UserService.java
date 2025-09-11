package com.devstudy.testcodeprac.service;

import com.devstudy.testcodeprac.config.exception.*;
import com.devstudy.testcodeprac.domain.User;
import com.devstudy.testcodeprac.domain.enums.UserStatus;
import com.devstudy.testcodeprac.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;
  private final EmailService emailService;

  @Transactional
  public User createUser(String name, String email, Integer age) {
    // 1. 입력값 검증
    if (name == null || name.trim().isEmpty()) {
      throw new InvalidInputException("이름을 입력해주세요.");
    }
    if (email == null || email.trim().isEmpty()) {
      throw new InvalidInputException("이메일을 입력해주세요.");
    }
    if (age == null || age < 0) {
      throw new InvalidInputException("올바른 나이를 입력해주세요.");
    }

    // 2. 이메일 유효성 검증
    if (!emailService.isEmailValid(email)) {
      throw new InvalidEmailException("유효하지 않은 이메일 형식입니다: " + email);
    }

    // 3. 이메일 중복 체크
    if (userRepository.existsByEmail(email)) {
      throw new DuplicateEmailException("이미 존재하는 이메일입니다: " + email);
    }

    // 4. 사용자 생성
    User user = new User(name.trim(), email.trim(), age);
    User savedUser = userRepository.save(user);

    // 5. 환영 이메일 발송
    emailService.sendWelcomeEmail(email, name);

    return savedUser;
  }

  public User getUserById(Long id) {
    if (id == null || id <= 0) {
      throw new InvalidInputException("올바른 사용자 ID를 입력해주세요.");
    }
    return userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다. ID: " + id));
  }

  public User getUserByEmail(String email) {
    if (email == null || email.trim().isEmpty()) {
      throw new InvalidInputException("이메일을 입력해주세요.");
    }
    return userRepository.findByEmail(email.trim())
        .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다. Email: " + email));
  }

  public List<User> getAllActiveUsers() {
    return userRepository.findByStatus(UserStatus.ACTIVE);
  }

  public List<User> searchUsersByName(String name) {
    if (name == null || name.trim().isEmpty()) {
      throw new InvalidInputException("검색할 이름을 입력해주세요.");
    }
    return userRepository.findByNameContaining(name.trim());
  }

  @Transactional
  public User updateUser(Long id, String name, Integer age) {
    User user = getUserById(id);

    if (name != null && !name.trim().isEmpty()) {
      user.setName(name.trim());
    }
    if (age != null && age > 0) {
      user.setAge(age);
    }

    return userRepository.save(user);
  }

  @Transactional
  public void deactivateUser(Long id) {
    User user = getUserById(id);

    if (user.getStatus() == UserStatus.INACTIVE) {
      throw new InvalidUserStatusException("이미 비활성화된 사용자입니다.");
    }

    user.setStatus(UserStatus.INACTIVE);
    userRepository.save(user);

    // 비활성화 알림 이메일 발송
    emailService.sendDeactivationEmail(user.getEmail(), user.getName());
  }

  @Transactional
  public void reactivateUser(Long id) {
    User user = getUserById(id);

    if (user.getStatus() != UserStatus.INACTIVE) {
      throw new InvalidUserStatusException("비활성 상태의 사용자만 재활성화할 수 있습니다.");
    }

    user.setStatus(UserStatus.ACTIVE);
    userRepository.save(user);

    // 재활성화 알림 이메일 발송
    emailService.sendReactivationEmail(user.getEmail(), user.getName());
  }

  @Transactional
  public void deleteUser(Long id) {
    User user = getUserById(id);
    userRepository.delete(user);
  }

  public long getActiveUserCount() {
    return userRepository.countByStatus(UserStatus.ACTIVE);
  }

  public List<User> getAdultUsers() {
    return userRepository.findByAgeGreaterThanEqual(18);
  }
}