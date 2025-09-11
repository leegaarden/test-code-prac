package com.devstudy.testcodeprac.service;

import com.devstudy.testcodeprac.domain.User;
import com.devstudy.testcodeprac.domain.enums.UserStatus;
import com.devstudy.testcodeprac.config.exception.DuplicateEmailException;
import com.devstudy.testcodeprac.config.exception.InvalidEmailException;
import com.devstudy.testcodeprac.config.exception.InvalidInputException;
import com.devstudy.testcodeprac.config.exception.UserNotFoundException;
import com.devstudy.testcodeprac.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)  // Mockito 활성화
class UserServiceTest {

  @Mock
  private UserRepository userRepository;  // Mock 객체

  @Mock
  private EmailService emailService;      // Mock 객체

  @InjectMocks
  private UserService userService;        // Mock들이 주입될 실제 객체

  @Test
  @DisplayName("올바른 정보로 사용자 생성 시 성공적으로 생성되고 환영 이메일이 발송된다")
  void createUser_Success() {
    // Given: 테스트 데이터 준비
    String name = "홍길동";
    String email = "hong@test.com";
    Integer age = 25;

    User expectedUser = new User(name, email, age);
    expectedUser.setId(1L);  // 저장 후 ID가 생성되었다고 가정

    // Mock 동작 정의 (Stubbing)
    when(emailService.isEmailValid(email)).thenReturn(true);
    when(userRepository.existsByEmail(email)).thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(expectedUser);

    // When: 실제 테스트할 메서드 호출
    User result = userService.createUser(name, email, age);

    // Then: 결과 검증
    assertThat(result.getName()).isEqualTo(name);
    assertThat(result.getEmail()).isEqualTo(email);
    assertThat(result.getAge()).isEqualTo(age);
    assertThat(result.getId()).isEqualTo(1L);

    // Mock 호출 검증 (Verification)
    verify(emailService).isEmailValid(email);
    verify(userRepository).existsByEmail(email);
    verify(userRepository).save(any(User.class));
    verify(emailService).sendWelcomeEmail(email, name);
  }

  @Test
  @DisplayName("이름이 null이거나 빈 문자열일 때 예외가 발생한다")
  void createUser_InvalidName_ThrowsException() {
    // Given
    String email = "hong@test.com";
    Integer age = 25;

    // When & Then - null인 경우
    assertThatThrownBy(() -> userService.createUser(null, email, age))
        .isInstanceOf(InvalidInputException.class)
        .hasMessage("이름을 입력해주세요.");

    // When & Then - 빈 문자열인 경우
    assertThatThrownBy(() -> userService.createUser("", email, age))
        .isInstanceOf(InvalidInputException.class)
        .hasMessage("이름을 입력해주세요.");

    // When & Then - 공백만 있는 경우
    assertThatThrownBy(() -> userService.createUser("   ", email, age))
        .isInstanceOf(InvalidInputException.class)
        .hasMessage("이름을 입력해주세요.");

    // 어떤 메서드도 호출되지 않아야 함
    verify(emailService, never()).isEmailValid(anyString());
    verify(userRepository, never()).existsByEmail(anyString());
  }

  @Test
  @DisplayName("이메일이 null이거나 빈 문자열일 때 예외가 발생한다")
  void createUser_InvalidEmailInput_ThrowsException() {
    // Given
    String name = "홍길동";
    Integer age = 25;

    // When & Then - null인 경우
    assertThatThrownBy(() -> userService.createUser(name, null, age))
        .isInstanceOf(InvalidInputException.class)
        .hasMessage("이메일을 입력해주세요.");

    // When & Then - 빈 문자열인 경우
    assertThatThrownBy(() -> userService.createUser(name, "", age))
        .isInstanceOf(InvalidInputException.class)
        .hasMessage("이메일을 입력해주세요.");

    // 어떤 메서드도 호출되지 않아야 함
    verify(emailService, never()).isEmailValid(anyString());
  }

  @Test
  @DisplayName("유효하지 않은 이메일로 사용자 생성 시 예외가 발생한다")
  void createUser_InvalidEmail_ThrowsException() {
    // Given
    String name = "홍길동";
    String invalidEmail = "invalid-email";
    Integer age = 25;

    when(emailService.isEmailValid(invalidEmail)).thenReturn(false);

    // When & Then
    assertThatThrownBy(() -> userService.createUser(name, invalidEmail, age))
        .isInstanceOf(InvalidEmailException.class)
        .hasMessage("유효하지 않은 이메일 형식입니다: " + invalidEmail);

    // 예외 발생 시 다른 메서드들은 호출되지 않아야 함
    verify(emailService).isEmailValid(invalidEmail);
    verify(userRepository, never()).existsByEmail(anyString());
    verify(userRepository, never()).save(any(User.class));
    verify(emailService, never()).sendWelcomeEmail(anyString(), anyString());
  }

  @Test
  @DisplayName("중복된 이메일로 사용자 생성 시 예외가 발생한다")
  void createUser_DuplicateEmail_ThrowsException() {
    // Given
    String name = "홍길동";
    String email = "hong@test.com";
    Integer age = 25;

    when(emailService.isEmailValid(email)).thenReturn(true);
    when(userRepository.existsByEmail(email)).thenReturn(true);  // 이미 존재

    // When & Then
    assertThatThrownBy(() -> userService.createUser(name, email, age))
        .isInstanceOf(DuplicateEmailException.class)
        .hasMessage("이미 존재하는 이메일입니다: " + email);

    verify(emailService).isEmailValid(email);
    verify(userRepository).existsByEmail(email);
    verify(userRepository, never()).save(any(User.class));
    verify(emailService, never()).sendWelcomeEmail(anyString(), anyString());
  }

  @Test
  @DisplayName("나이가 null이거나 음수일 때 예외가 발생한다")
  void createUser_InvalidAge_ThrowsException() {
    // Given
    String name = "홍길동";
    String email = "hong@test.com";

    // When & Then - null인 경우
    assertThatThrownBy(() -> userService.createUser(name, email, null))
        .isInstanceOf(InvalidInputException.class)
        .hasMessage("올바른 나이를 입력해주세요.");

    // When & Then - 음수인 경우
    assertThatThrownBy(() -> userService.createUser(name, email, -1))
        .isInstanceOf(InvalidInputException.class)
        .hasMessage("올바른 나이를 입력해주세요.");

    // 어떤 메서드도 호출되지 않아야 함
    verify(emailService, never()).isEmailValid(anyString());
  }

  @Test
  @DisplayName("ID로 사용자 조회 시 올바른 사용자를 반환한다")
  void getUserById_Success() {
    // Given
    Long userId = 1L;
    User expectedUser = new User("홍길동", "hong@test.com", 25);
    expectedUser.setId(userId);

    when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

    // When
    User result = userService.getUserById(userId);

    // Then
    assertThat(result.getId()).isEqualTo(userId);
    assertThat(result.getName()).isEqualTo("홍길동");
    assertThat(result.getEmail()).isEqualTo("hong@test.com");
    verify(userRepository).findById(userId);
  }

  @Test
  @DisplayName("존재하지 않는 ID로 사용자 조회 시 예외가 발생한다")
  void getUserById_NotFound_ThrowsException() {
    // Given
    Long userId = 999L;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> userService.getUserById(userId))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessage("사용자를 찾을 수 없습니다. ID: " + userId);

    verify(userRepository).findById(userId);
  }

  @Test
  @DisplayName("유효하지 않은 ID로 조회 시 예외가 발생한다")
  void getUserById_InvalidId_ThrowsException() {
    // When & Then - null ID
    assertThatThrownBy(() -> userService.getUserById(null))
        .isInstanceOf(InvalidInputException.class)
        .hasMessage("올바른 사용자 ID를 입력해주세요.");

    // When & Then - 0 이하 ID
    assertThatThrownBy(() -> userService.getUserById(0L))
        .isInstanceOf(InvalidInputException.class)
        .hasMessage("올바른 사용자 ID를 입력해주세요.");

    assertThatThrownBy(() -> userService.getUserById(-1L))
        .isInstanceOf(InvalidInputException.class)
        .hasMessage("올바른 사용자 ID를 입력해주세요.");

    // Repository 호출되지 않아야 함
    verify(userRepository, never()).findById(anyLong());
  }

  @Test
  @DisplayName("이메일로 사용자 조회 시 올바른 사용자를 반환한다")
  void getUserByEmail_Success() {
    // Given
    String email = "hong@test.com";
    User expectedUser = new User("홍길동", email, 25);
    expectedUser.setId(1L);

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(expectedUser));

    // When
    User result = userService.getUserByEmail(email);

    // Then
    assertThat(result.getEmail()).isEqualTo(email);
    assertThat(result.getName()).isEqualTo("홍길동");
    verify(userRepository).findByEmail(email);
  }

  @Test
  @DisplayName("활성 사용자 목록을 조회한다")
  void getAllActiveUsers_Success() {
    // Given
    User user1 = new User("홍길동", "hong@test.com", 25);
    User user2 = new User("김철수", "kim@test.com", 30);
    List<User> expectedUsers = Arrays.asList(user1, user2);

    when(userRepository.findByStatus(UserStatus.ACTIVE)).thenReturn(expectedUsers);

    // When
    List<User> result = userService.getAllActiveUsers();

    // Then
    assertThat(result).hasSize(2);
    assertThat(result).extracting(User::getName)
        .containsExactly("홍길동", "김철수");
    verify(userRepository).findByStatus(UserStatus.ACTIVE);
  }

  @Test
  @DisplayName("이름으로 사용자를 검색한다")
  void searchUsersByName_Success() {
    // Given
    String searchName = "홍";
    User user1 = new User("홍길동", "hong1@test.com", 25);
    User user2 = new User("홍영희", "hong2@test.com", 23);
    List<User> expectedUsers = Arrays.asList(user1, user2);

    when(userRepository.findByNameContaining(searchName)).thenReturn(expectedUsers);

    // When
    List<User> result = userService.searchUsersByName(searchName);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result).extracting(User::getName)
        .containsExactly("홍길동", "홍영희");
    verify(userRepository).findByNameContaining(searchName);
  }
}