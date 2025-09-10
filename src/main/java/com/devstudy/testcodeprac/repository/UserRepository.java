package com.devstudy.testcodeprac.repository;

import com.devstudy.testcodeprac.domain.User;
import com.devstudy.testcodeprac.domain.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

  List<User> findByNameContaining(String name);

  List<User> findByStatus(UserStatus status);

  List<User> findByAgeGreaterThanEqual(Integer age);

  @Query("SELECT u FROM User u WHERE u.age >= :minAge AND u.status = :status")
  List<User> findAdultActiveUsers(@Param("minAge") Integer minAge, @Param("status") UserStatus status);

  boolean existsByEmail(String email);

  long countByStatus(UserStatus status);
}