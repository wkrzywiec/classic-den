package io.wkrzywiec.den;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpResponseException;

import java.util.HashMap;
import java.util.Map;

class AddEntryFacade {

    private final ObjectMapper objectMapper;
    private final GitHubClient gitHub;

    public AddEntryFacade(ObjectMapper objectMapper, GitHubClient gitHub) {
        this.objectMapper = objectMapper;
        this.gitHub = gitHub;
    }


    void proccessRequest(String body) throws JsonProcessingException, IllegalArgumentException, HttpResponseException {

        Map<String, String> requestMap = parseRequestBody(body);
        validateBody(requestMap);

        String indexHtmlContent = gitHub.loadFileFrom("wkrzywiec/classic-den", "main", "web-page/entries.html");

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
