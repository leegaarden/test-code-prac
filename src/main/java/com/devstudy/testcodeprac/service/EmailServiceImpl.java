package com.devstudy.testcodeprac.service;

import org.springframework.stereotype.Service;

/**
 * EmailService의 간단한 구현체
 * 실제로는 SMTP나 외부 API를 사용하지만, 학습 목적으로 로깅만 수행
 */
@Service
public class EmailServiceImpl implements EmailService {

  @Override
  public void sendWelcomeEmail(String email, String name) {
    System.out.println("환영 이메일 발송: " + name + "님 (" + email + ")");
    // 실제로는 이메일 발송 로직이 들어감
  }

  @Override
  public void sendDeactivationEmail(String email, String name) {
    System.out.println("계정 비활성화 알림 이메일 발송: " + name + "님 (" + email + ")");
  }

  @Override
  public void sendReactivationEmail(String email, String name) {
    System.out.println("계정 재활성화 알림 이메일 발송: " + name + "님 (" + email + ")");
  }

  @Override
  public boolean isEmailValid(String email) {
    // 간단한 이메일 유효성 검증
    if (email == null || email.trim().isEmpty()) {
      return false;
    }

    // 기본적인 이메일 형식 확인
    return email.contains("@") &&
        email.contains(".") &&
        email.indexOf("@") > 0 &&
        email.lastIndexOf(".") > email.indexOf("@") + 1 &&
        email.length() > 5;
  }
}