package com.devstudy.testcodeprac.domain;

import com.devstudy.testcodeprac.domain.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private Integer age;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserStatus status = UserStatus.ACTIVE;  // 기본값은 ACTIVE

  @CreationTimestamp
  private LocalDateTime createdAt;

  public User(String name, String email, Integer age) {
    this.name = name;
    this.email = email;
    this.age = age;
  }

  public boolean isAdult() {
    return this.age >= 18;
  }

  public void activate() {
    this.status = UserStatus.ACTIVE;
  }

  public void deactivate() {
    this.status = UserStatus.INACTIVE;
  }
}