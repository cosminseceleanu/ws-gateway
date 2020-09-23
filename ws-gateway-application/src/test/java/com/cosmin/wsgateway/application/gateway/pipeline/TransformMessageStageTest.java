package com.cosmin.wsgateway.application.gateway.pipeline;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.cosmin.wsgateway.application.gateway.PayloadTransformer;
import com.cosmin.wsgateway.application.gateway.connection.Message;
import com.cosmin.wsgateway.domain.events.UserMessage;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class TransformMessageStageTest {
    @Mock
    private PayloadTransformer transformer;

    private TransformMessageStage subject;

    @BeforeEach
    void setUp() {
        subject = TransformMessageStage.newInstance(transformer, "id");
    }

    private static Stream<Arguments> provideInboundMessages() {
        return Stream.of(
                Arguments.of("", ""),
                Arguments.of("hello", "hello"),
                Arguments.of("\"foo\":\"bar\"}", Map.of("foo", "bar")),
                Arguments.of("1", 1),
                Arguments.of("[1, 2, 3]", List.of(1, 2, 3))
        );
    }

    @ParameterizedTest
    @MethodSource("provideInboundMessages")
    public void testApply_rawMessageFlux_shouldBeTransformedToInboundEvents(String json, Object expectedPayload) {
        when(transformer.toPayload(eq(json), any())).thenReturn(expectedPayload);

        var initial = Flux.just(Message.text(json));
        var result = subject.apply(initial);

        StepVerifier.create(result)
                .expectNext(new UserMessage("id", expectedPayload, json))
                .verifyComplete();
    }
}