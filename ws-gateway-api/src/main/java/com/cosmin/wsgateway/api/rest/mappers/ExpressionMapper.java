package com.cosmin.wsgateway.api.rest.mappers;

import com.cosmin.wsgateway.domain.exceptions.IncorrectExpressionException;
import com.cosmin.wsgateway.domain.model.Expression;
import com.cosmin.wsgateway.domain.model.expressions.And;
import com.cosmin.wsgateway.domain.model.expressions.Equal;
import com.cosmin.wsgateway.domain.model.expressions.Matches;
import com.cosmin.wsgateway.domain.model.expressions.Or;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExpressionMapper implements RepresentationMapper<Map<String, Object>, Expression<Boolean>> {
    private final ObjectMapper objectMapper;

    @Override
    public Expression<Boolean> toModel(Map<String, Object> representation) {
        JsonNode jsonNode = objectMapper.convertValue(representation, JsonNode.class);
        if (jsonNode instanceof ObjectNode) {
            return parseJsonValue((ObjectNode) jsonNode, 0);
        }
        throw new IncorrectExpressionException("Expression root must be an object");
    }

    private Expression<Boolean> parseJsonValue(ObjectNode jsonNode, int nestedLevel) {
        if (nestedLevel > 2) {
            throw new IncorrectExpressionException(String.format("Expression can at most %s nested levels", 2));
        }
        List<Map.Entry<String, JsonNode>> fields = getFieldsAsList(jsonNode);
        if (fields.size() > 1) {
            throw new IncorrectExpressionException("Expression can have only one root");
        }
        Map.Entry<String, JsonNode> head = fields.get(0);
        return doParse(jsonNode, head, nestedLevel);
    }

    private Expression<Boolean> doParse(ObjectNode jsonNode, Map.Entry<String, JsonNode> head, int nestedLevel) {
        Expression.Name name = Expression.Name.fromValue(head.getKey());
        switch (name) {
            case AND:
            case OR:
                return createBooleanExpression(name, (ObjectNode) head.getValue(), nestedLevel);
            case EQUAL:
                return createExpression(name, jsonNode, Equal.class);
            case MATCHES:
                return createExpression(name, jsonNode, Matches.class);
            default:
                throw new IncorrectExpressionException(String.format("Expression %s is not supported yet", name));
        }
    }

    private List<Map.Entry<String, JsonNode>> getFieldsAsList(ObjectNode jsonNode) {
        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = jsonNode.fields();
        List<Map.Entry<String, JsonNode>> fields = new ArrayList<>();
        fieldsIterator.forEachRemaining(fields::add);

        return fields;
    }

    private Expression<Boolean> createExpression(Expression.Name name, ObjectNode jsonNode, Class<?> expectedType) {
        try {
            return (Expression<Boolean>) objectMapper.treeToValue(jsonNode, expectedType);
        } catch (JsonProcessingException e) {
            log.error("error while reading expression={}", name, e);
            throw new IncorrectExpressionException(e);
        }
    }

    private Expression<Boolean> createBooleanExpression(Expression.Name name, JsonNode jsonNode, int nestedLevel) {
        if (!(jsonNode instanceof ArrayNode)) {
            throw new IncorrectExpressionException(String.format("Boolean expression %s must be array", name.getValue()));
        }
        ArrayNode arrayNode = (ArrayNode) jsonNode;
        if (arrayNode.size() != 2) {
            throw new IncorrectExpressionException(String.format("Boolean expression %s must have exactly 2 child expressions", name.getValue()));
        }
        try {
            Expression<Boolean> left = parseJsonValue((ObjectNode) arrayNode.get(0), nestedLevel + 1);
            Expression<Boolean> right = parseJsonValue((ObjectNode) arrayNode.get(1), nestedLevel + 1);
            if (Expression.Name.AND == name) {
                return new And(left, right);
            }
            return new Or(left, right);
        } catch (ClassCastException e) {
            throw new IncorrectExpressionException("Boolean expressions must have exactly child as json objects");
        }
    }

    @Override
    public Map<String, Object> toRepresentation(Expression<Boolean> domain) {
        return null;
    }
}
