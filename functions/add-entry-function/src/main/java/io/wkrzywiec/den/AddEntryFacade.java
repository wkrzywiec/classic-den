package io.wkrzywiec.den;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

class AddEntryFacade {

    ObjectMapper objectMapper = new ObjectMapper();
    GitHubClient gitHub = new GitHubClient();

    void proccessRequest(String body) throws JsonProcessingException, IllegalArgumentException {

        Map<String, String> requestMap = parseRequestBody(body);
        validateBody(requestMap);

        String indexHtmlContent = gitHub.loadFileFrom("wkrzywiec/classic-den", "web-page/index.html");

    }

    private Map<String, String> parseRequestBody(String body) throws JsonProcessingException {
        return objectMapper.readValue(body, HashMap.class);
    }

    private void validateBody(Map<String, String> requestMap) throws IllegalArgumentException {
        if (!requestMap.containsKey("title") ||
                !requestMap.containsKey("message") ||
                !requestMap.containsKey("author")) {

            throw new IllegalArgumentException("Expected to have following fields in a request: title, message, author");
        }
    }
}
