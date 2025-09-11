package com.devstudy.testcodeprac.repository;

import com.devstudy.testcodeprac.domain.User;
import com.devstudy.testcodeprac.domain.enums.UserStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("사용자 생성 테스트")
  void saveUser_Success() {
    // Given
    User user = new User("홍길동", "hong@test.com", 25);

    // When
    User savedUser = userRepository.save(user);

    // Then
    assertThat(savedUser.getId()).isNotNull();
    assertThat(savedUser.getName()).isEqualTo("홍길동");

    System.out.println("저장된 사용자: " + savedUser.getName()); // 로그로 확인
  }
}