package com.devstudy.testcodeprac.service;

/**
 * 이메일 발송을 담당하는 외부 서비스
 * (실제로는 외부 API를 호출하거나 SMTP 서버를 사용)
 */
public interface EmailService {

  /**
   * 환영 이메일 발송
   */
  void sendWelcomeEmail(String email, String name);

  /**
   * 계정 비활성화 알림 이메일 발송
   */
  void sendDeactivationEmail(String email, String name);

  /**
   * 계정 재활성화 알림 이메일 발송
   */
  void sendReactivationEmail(String email, String name);

  /**
   * 이메일 형식 유효성 검증
   */
  boolean isEmailValid(String email);
}