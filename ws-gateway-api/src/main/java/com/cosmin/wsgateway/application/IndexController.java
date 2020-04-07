package com.cosmin.wsgateway.application;

import lombok.Builder;
import lombok.Data;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

  @GetMapping("/api/user")
  public User get() {
    return User.builder().name("cosmin").gender("male").build();
  }

  @Data
  @Builder
  private static class User {
    private String name;
    private String gender;
  }
}
