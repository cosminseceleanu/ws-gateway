# WSGateway - WebSocket API Gateway
TBD... Work in progress

## Development

### Code style

- https://plugins.jetbrains.com/plugin/1065-checkstyle-idea
- https://google.github.io/styleguide/javaguide.html - except indentation

### Tests name convention
1. Unit tests
    - Class name [Class_Under_Test]Test.java
    - Test methods test[MethodName_StateUnderTest_ExpectedBehavior] or test[feature_StateUnderTest_ExpectedBehavior]

2. Integration tests
    - Class name [Feature]IT.java
    - Test methods [feature to be tested]