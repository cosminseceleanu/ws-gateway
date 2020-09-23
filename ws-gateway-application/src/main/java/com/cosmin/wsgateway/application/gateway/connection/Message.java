package com.cosmin.wsgateway.application.gateway.connection;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Message {
    private final String payload;
    private final Type type;

    public static Message ping() {
        return new Message(Type.PING.name(), Type.PING);
    }

    public static Message pong() {
        return new Message(Type.PONG.name(), Type.PONG);
    }

    public static Message text(String payload) {
        return new Message(payload, Type.TEXT);
    }

    public static Message poisonPill(String reason) {
        return new Message(reason, Type.POISON_PILL);
    }

    public enum Type {
        PING,
        PONG,
        TEXT,
        POISON_PILL,
    }

    public boolean isHeartbeat() {
        return type == Type.PING || type == Type.PONG;
    }

    public boolean isPing() {
        return type == Type.PING;
    }

    public boolean isText() {
        return type == Type.TEXT;
    }

    public boolean isPong() {
        return type == Type.PONG;
    }

    public boolean isPoisonPill() {
        return type == Type.POISON_PILL;
    }
}
