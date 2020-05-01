package com.cosmin.wsgateway.api.fixtures;

import java.util.List;
import java.util.Map;

public final class ExpressionFixtures {
  private ExpressionFixtures() {}

  public static Map<String, Object> createTerminalExpression(String path, Object value, String expressionName) {
    return Map.of(expressionName, Map.of(
        "path", path,
        "value", value
    ));
  }

  public static Map<String, Object> createBooleanExpression(String expressionName, Map<String, Object> left, Map<String, Object> right) {
    return Map.of(expressionName, List.of(left, right));
  }
}
