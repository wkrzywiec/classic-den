package io.wkrzywiec.den;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpResponseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

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

        String entriesHtmlContent = gitHub.loadFileFrom("wkrzywiec/classic-den", "main", "web-page/entries.html");

        Document entriesDoc = parseEntriesHtlm(entriesHtmlContent);
        entriesDoc = addEntryToDocument(entriesDoc, requestMap);

        gitHub.updateFile("wkrzywiec/classic-den", "main", "web-page/entries.html", entriesDoc.body().html());
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

    private Document parseEntriesHtlm(String entriesHtmlContent) {
        return Jsoup.parse(entriesHtmlContent);

    }

    private Document addEntryToDocument(Document entriesDoc, Map<String, String> requestMap) {
        Element element = entriesDoc.select("#entries-container").first();

        element.append(format("<div class=\"span6\"><h4>%s</h4><p>%s</p></div>",
                requestMap.get("title"), requestMap.get("message"))
        );
        return entriesDoc;
    }
}
