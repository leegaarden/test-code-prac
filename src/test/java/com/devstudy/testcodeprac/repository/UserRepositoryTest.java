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
  @DisplayName("이메일로 사용자 조회 성공")
  void findByEmail_Success() {
    // Given
    User user = new User("홍길동", "hong@test.com", 25);
    entityManager.persistAndFlush(user);

    // When
    Optional<User> foundUser = userRepository.findByEmail("hong@test.com");

    // Then
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getName()).isEqualTo("홍길동");
    assertThat(foundUser.get().getEmail()).isEqualTo("hong@test.com");
    assertThat(foundUser.get().getAge()).isEqualTo(25);
  }

  @Test
  @DisplayName("존재하지 않는 이메일로 조회 시 빈 Optional 반환")
  void findByEmail_NotFound() {
    // When
    Optional<User> foundUser = userRepository.findByEmail("notfound@test.com");

    // Then
    assertThat(foundUser).isEmpty();
  }

  @Test
  @DisplayName("이름에 특정 문자열이 포함된 사용자들 조회")
  void findByNameContaining_Success() {
    // Given
    User user1 = new User("홍길동", "hong1@test.com", 25);
    User user2 = new User("홍영희", "hong2@test.com", 23);
    User user3 = new User("김철수", "kim@test.com", 30);
    User user4 = new User("홍진호", "hong3@test.com", 35);

    entityManager.persist(user1);
    entityManager.persist(user2);
    entityManager.persist(user3);
    entityManager.persist(user4);
    entityManager.flush();

    // When
    List<User> hongUsers = userRepository.findByNameContaining("홍");
    List<User> kimUsers = userRepository.findByNameContaining("김");
    List<User> notFoundUsers = userRepository.findByNameContaining("박");

    // Then
    assertThat(hongUsers).hasSize(3);
    assertThat(hongUsers).extracting(User::getName)
        .containsExactlyInAnyOrder("홍길동", "홍영희", "홍진호");

    assertThat(kimUsers).hasSize(1);
    assertThat(kimUsers.get(0).getName()).isEqualTo("김철수");

    assertThat(notFoundUsers).isEmpty();
  }

  @Test
  @DisplayName("특정 상태의 사용자들 조회")
  void findByStatus_Success() {
    // Given
    User activeUser1 = new User("홍길동", "hong@test.com", 25);
    User activeUser2 = new User("김철수", "kim@test.com", 30);
    User inactiveUser = new User("이영희", "lee@test.com", 28);
    inactiveUser.setStatus(UserStatus.INACTIVE);

    entityManager.persist(activeUser1);
    entityManager.persist(activeUser2);
    entityManager.persist(inactiveUser);
    entityManager.flush();

    // When
    List<User> activeUsers = userRepository.findByStatus(UserStatus.ACTIVE);
    List<User> inactiveUsers = userRepository.findByStatus(UserStatus.INACTIVE);
    List<User> suspendedUsers = userRepository.findByStatus(UserStatus.SUSPENDED);

    // Then
    assertThat(activeUsers).hasSize(2);
    assertThat(activeUsers).extracting(User::getName)
        .containsExactlyInAnyOrder("홍길동", "김철수");

    assertThat(inactiveUsers).hasSize(1);
    assertThat(inactiveUsers.get(0).getName()).isEqualTo("이영희");

    assertThat(suspendedUsers).isEmpty();
  }

  @Test
  @DisplayName("특정 나이 이상의 사용자들 조회")
  void findByAgeGreaterThanEqual_Success() {
    // Given
    User minor = new User("홍길동", "hong@test.com", 17);
    User adult1 = new User("김철수", "kim@test.com", 18);
    User adult2 = new User("이영희", "lee@test.com", 25);
    User adult3 = new User("박민수", "park@test.com", 35);

    entityManager.persist(minor);
    entityManager.persist(adult1);
    entityManager.persist(adult2);
    entityManager.persist(adult3);
    entityManager.flush();

    // When
    List<User> adults = userRepository.findByAgeGreaterThanEqual(18);
    List<User> over30 = userRepository.findByAgeGreaterThanEqual(30);
    List<User> over50 = userRepository.findByAgeGreaterThanEqual(50);

    // Then
    assertThat(adults).hasSize(3);
    assertThat(adults).extracting(User::getName)
        .containsExactlyInAnyOrder("김철수", "이영희", "박민수");

    assertThat(over30).hasSize(1);
    assertThat(over30.get(0).getName()).isEqualTo("박민수");

    assertThat(over50).isEmpty();
  }

  @Test
  @DisplayName("성인 활성 사용자 커스텀 쿼리 테스트")
  void findAdultActiveUsers_Success() {
    // Given
    User adultActive = new User("홍길동", "hong@test.com", 25);
    User adultInactive = new User("김철수", "kim@test.com", 30);
    adultInactive.setStatus(UserStatus.INACTIVE);
    User minorActive = new User("이영희", "lee@test.com", 16);

    entityManager.persist(adultActive);
    entityManager.persist(adultInactive);
    entityManager.persist(minorActive);
    entityManager.flush();

    // When
    List<User> result = userRepository.findAdultActiveUsers(18, UserStatus.ACTIVE);

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("홍길동");
    assertThat(result.get(0).getAge()).isGreaterThanOrEqualTo(18);
    assertThat(result.get(0).getStatus()).isEqualTo(UserStatus.ACTIVE);
  }

  @Test
  @DisplayName("이메일 중복 여부 확인")
  void existsByEmail_Test() {
    // Given
    User user = new User("홍길동", "hong@test.com", 25);
    entityManager.persistAndFlush(user);

    // When & Then
    assertThat(userRepository.existsByEmail("hong@test.com")).isTrue();
    assertThat(userRepository.existsByEmail("notfound@test.com")).isFalse();
  }

  @Test
  @DisplayName("상태별 사용자 수 카운트")
  void countByStatus_Test() {
    // Given
    User activeUser1 = new User("홍길동", "hong@test.com", 25);
    User activeUser2 = new User("김철수", "kim@test.com", 30);
    User inactiveUser = new User("이영희", "lee@test.com", 28);
    inactiveUser.setStatus(UserStatus.INACTIVE);

    entityManager.persist(activeUser1);
    entityManager.persist(activeUser2);
    entityManager.persist(inactiveUser);
    entityManager.flush();

    // When & Then
    assertThat(userRepository.countByStatus(UserStatus.ACTIVE)).isEqualTo(2);
    assertThat(userRepository.countByStatus(UserStatus.INACTIVE)).isEqualTo(1);
    assertThat(userRepository.countByStatus(UserStatus.SUSPENDED)).isEqualTo(0);
  }
}