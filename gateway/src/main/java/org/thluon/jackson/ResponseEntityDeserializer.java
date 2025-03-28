package org.thluon.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public class ResponseEntityDeserializer extends JsonDeserializer<ResponseEntity<?>> {
    @Override
    public ResponseEntity<?> deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        int status = node.has("statusCodeValue") ? node.get("statusCodeValue").asInt() : HttpStatus.OK.value();
        Object body = null;
        if (node.has("body") && !node.get("body").isNull()) {
            JsonNode bodyNode = node.get("body");
            if (bodyNode.isObject() || bodyNode.isArray()) {
                body = p.getCodec().treeToValue(bodyNode, Object.class); // Convert to JSON Object
            } else {
                body = bodyNode.asText(); // Handle simple text cases
            }
        }
        HttpHeaders headers = new HttpHeaders();
        if (node.has("headers")) {
            JsonNode headersNode = node.get("headers");
            if (headersNode.has("Content-Type")) {
                headers.setContentType(MediaType.valueOf(headersNode.get("Content-Type").get(0).asText()));
            }
        }
        return ResponseEntity.status(status).headers(headers).body(body);
    }
}
