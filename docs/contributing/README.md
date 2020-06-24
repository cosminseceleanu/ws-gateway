## Code style

- https://plugins.jetbrains.com/plugin/1065-checkstyle-idea
- https://google.github.io/styleguide/javaguide.html - except indentation

## Tests name convention
1. Unit tests
    - Class name [Class_Under_Test]Test.java
    - Test methods test[MethodName_StateUnderTest_ExpectedBehavior] or test[feature_StateUnderTest_ExpectedBehavior]

2. Integration tests
    - Class name [Feature]IT.java
    - Test methods [feature to be tested]
    
## Utils
    
  - start a kafka consumer: ```/opt/kafka/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic service.2.connect.topic --from-beginning --max-messages 100```
  - set java 11 version: ```export JAVA_HOME=`/usr/libexec/java_home -v 11` ``` 
  - set minikube docker env: ```eval $(minikube docker-env)``` 