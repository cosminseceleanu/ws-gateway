package com.cosmin.wsgateway.api.representation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TerminalExpressionRepresentation {
  private String path;
  private Object value;
}
