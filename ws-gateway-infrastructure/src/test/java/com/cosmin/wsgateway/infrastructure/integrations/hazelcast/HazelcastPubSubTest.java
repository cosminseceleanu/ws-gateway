package com.cosmin.wsgateway.infrastructure.integrations.hazelcast;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cosmin.wsgateway.application.gateway.PubSub;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.topic.ITopic;
import com.hazelcast.topic.Message;
import com.hazelcast.topic.MessageListener;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class HazelcastPubSubTest {
    private static final String TOPIC_NAME = "topic";

    @Mock
    private HazelcastInstance hazelcastInstance;

    @InjectMocks
    private HazelcastPubSub subject;

    @Mock
    private ITopic<String> topicInstance;

    @Test
    public void testSubscribe_givenATopic_shouldReturnSubscription() {
        var subscriptionId = UUID.randomUUID();
        when(topicInstance.addMessageListener(any())).thenReturn(subscriptionId);
        when(hazelcastInstance.<String>getTopic(TOPIC_NAME)).thenReturn(topicInstance);

        var result = subject.subscribe(TOPIC_NAME);

        assertEquals(subscriptionId.toString(), result.getId());
        assertNotNull(subscriptionId.toString(), result.getId());
    }

    @Test
    public void testSubscribe_whenTopicReceivesAMessage_shouldBePushedToSubscriptionEventsFlux() {
        var subscriptionId = UUID.randomUUID();
        when(topicInstance.addMessageListener(any())).thenReturn(subscriptionId);
        when(hazelcastInstance.<String>getTopic(TOPIC_NAME)).thenReturn(topicInstance);

        var subscription = subject.subscribe(TOPIC_NAME);
        ArgumentCaptor<MessageListener<String>> argumentCaptor = ArgumentCaptor.forClass(MessageListener.class);
        verify(topicInstance, times(1)).addMessageListener(argumentCaptor.capture());

        String event = "hello world";
        assertTimeout(Duration.ofMillis(100), () -> {
            subscription.getEvents().subscribe(msg -> {
                assertEquals(event, msg);
            });
        });
        argumentCaptor.getValue().onMessage(new Message<>(TOPIC_NAME, event, 1L, null));
    }

    @Test
    public void testPublish_whenMessageIsSuccessfullyPublished_shouldReturnEmptyMono() {
        when(hazelcastInstance.<String>getTopic(TOPIC_NAME)).thenReturn(topicInstance);

        var result = subject.publish(TOPIC_NAME, "msg");
        StepVerifier.create(result)
                .verifyComplete();
        verify(topicInstance, times(1)).publish("msg");
    }

    @Test
    public void testPublish_whenPublishThrowsException_shouldReturnEmptyAnErrorMono() {
        when(hazelcastInstance.<String>getTopic(TOPIC_NAME)).thenReturn(topicInstance);
        doThrow(new RuntimeException()).when(topicInstance).publish(anyString());

        var result = subject.publish(TOPIC_NAME, "msg");

        StepVerifier.create(result)
                .verifyError();
    }

    @Test
    public void testUnsubscribe_whenSubscriptionIsActive_shouldCloseIt() {
        var subscriptionId = UUID.randomUUID();
        when(hazelcastInstance.<String>getTopic(TOPIC_NAME)).thenReturn(topicInstance);
        when(topicInstance.addMessageListener(any())).thenReturn(subscriptionId);

        var subscription = subject.subscribe(TOPIC_NAME);
        var result = subject.unsubscribe(subscription);

        StepVerifier.create(result).verifyComplete();
        verify(topicInstance, times(1)).removeMessageListener(subscriptionId);
    }

    @Test
    public void testUnsubscribe_whenSubscriptionIsNotActive_shouldDoNoNothing() {
        var subscriptionId = UUID.randomUUID();
        var result = subject.unsubscribe(new PubSub.Subscription(subscriptionId.toString(), Flux.empty()));

        StepVerifier.create(result).verifyComplete();
        verify(topicInstance, never()).removeMessageListener(subscriptionId);
    }
}