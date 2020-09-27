package com.cosmin.wsgateway.api.utils;

import io.netty.buffer.UnpooledByteBufAllocator;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

@Slf4j
public class HttpLogger {

    private static final String NEW_LINE = System.getProperty("line.separator");

    private static final Set<HttpMethod> METHODS_WITH_BODY = Set.of(
            HttpMethod.PATCH,
            HttpMethod.PUT,
            HttpMethod.POST
    );

    public static DataBuffer logResponse(DataBuffer dataBuffer, ServerHttpResponse response, HttpRequest request) {
        if (!log.isDebugEnabled()) {
            return dataBuffer;
        }
        try {
            var outputStream = toByteArrayOutputStream(dataBuffer.asInputStream());
            NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(
                    new UnpooledByteBufAllocator(false)
            );
            DataBufferUtils.release(dataBuffer);
            log.debug(buildResponseLogMessage(
                    request.getURI(),
                    response,
                    outputStream.toString(StandardCharsets.UTF_8)).toString()
            );
            return nettyDataBufferFactory.wrap(outputStream.toByteArray());
        } catch (IOException e) {
            log.warn("Failed to read response data", e);
            return dataBuffer;
        }
    }

    private static StringBuilder buildResponseLogMessage(URI uri, ServerHttpResponse response, String bodyString) {
        var message = new StringBuilder();
        message.append("Send response ")
                .append(uri)
                .append(" ")
                .append(response.getRawStatusCode())
                .append(" ")
                .append(NEW_LINE)
                .append("Headers: ")
                .append(NEW_LINE);
        addHeaders(response.getHeaders(), message);
        message.append("Body: ")
                .append(bodyString);
        return message;
    }

    public static void logServerRequest(ServerHttpRequest request) {
        if (!log.isDebugEnabled()) {
            return;
        }
        if (METHODS_WITH_BODY.contains(request.getMethod())) {
            logRequestWithBody(request);
        } else {
            log.debug(getBaseRequestLogMessage(
                    request.getURI(),
                    request.getMethodValue(),
                    request.getHeaders()
            ).toString());
        }
    }

    private static void logRequestWithBody(ServerHttpRequest request) {
        request.getBody().doOnNext(body -> {
            try {
                var baseMessage = getBaseRequestLogMessage(
                        request.getURI(),
                        request.getMethodValue(),
                        request.getHeaders()
                );
                baseMessage.append("Body: ")
                        .append(NEW_LINE)
                        .append(inputStreamToString(body.asInputStream()));
                log.debug(baseMessage.toString());
            } catch (IOException e) {
                log.warn("Failed to read request body", e);
            }
        });
    }

    private static StringBuilder getBaseRequestLogMessage(URI uri, String method, HttpHeaders headers) {
        var builder = new StringBuilder();

        builder.append("Request received ")
                .append(uri.toString())
                .append(" ")
                .append(method)
                .append(" ")
                .append(NEW_LINE)
                .append("Headers: ")
                .append(NEW_LINE);
        addHeaders(headers, builder);

        return builder;
    }

    private static void addHeaders(HttpHeaders headers, StringBuilder builder) {
        headers.toSingleValueMap()
                .forEach((name, value) -> builder.append("   > ")
                        .append(name)
                        .append(": ")
                        .append(value)
                        .append(NEW_LINE));
    }

    private static String inputStreamToString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buf = toByteArrayOutputStream(inputStream);

        return buf.toString(StandardCharsets.UTF_8.name());
    }

    private static ByteArrayOutputStream toByteArrayOutputStream(InputStream inputStream) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while (result != -1) {
            buf.write((byte) result);
            result = bis.read();
        }
        return buf;
    }
}
