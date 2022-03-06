package io.wkrzywiec.den;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpResponseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

class EntryFacade {

    private final ObjectMapper objectMapper;
    private final GitHubClient github;
    private final Clock clock;
    private final DateTimeFormatter dtf;

    public EntryFacade(ObjectMapper objectMapper, GitHubClient github, Clock clock) {
        this.objectMapper = objectMapper;
        this.github = github;
        this.clock = clock;
        dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(clock.getZone());
    }

    void proccessRequest(String body) throws JsonProcessingException, IllegalArgumentException, HttpResponseException {

        Map<String, String> requestMap = parseRequestBody(body);
        validateBody(requestMap);

        String entriesHtmlContent = github.loadFileFrom("wkrzywiec/classic-den", "main", "web-page/entries.html");

        Document entriesDoc = parseEntriesHtlm(entriesHtmlContent);
        entriesDoc = addEntryToDocument(entriesDoc, requestMap);

        github.updateFile("wkrzywiec/classic-den", "main", "web-page/entries.html", entriesDoc.body().html());
    }

    void removeOutdatedEntries() throws HttpResponseException, JsonProcessingException {

        String entriesHtmlContent = github.loadFileFrom("wkrzywiec/classic-den", "main", "web-page/entries.html");
        Document entriesDoc = parseEntriesHtlm(entriesHtmlContent);

        entriesDoc = removeOutdatedEntriesFromDoc(entriesDoc);

        github.updateFile("wkrzywiec/classic-den", "main", "web-page/entries.html", entriesDoc.body().html());
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

        Instant now = clock.instant();

        element.append(format("<div class=\"span6\" id=\"%s\"><h4>%s</h4><p class=\"entry-message\">%s</p><p class=\"entry-published\">%s<span class=\"entry-author\">%s</span></p></div>",
                now.toEpochMilli(), requestMap.get("title"), requestMap.get("message"), dtf.format(now), requestMap.get("author"))
        );
        return entriesDoc;
    }

    private Document removeOutdatedEntriesFromDoc(Document entriesDoc) {
        Element entriesContainer = entriesDoc.select("#entries-container").first();
        Elements entries = entriesContainer.children();

        for (Element entry: entries) {
            String elementId = extractElementId(entry);
            if (elementId == null) continue;

            Instant elementCreatedTime = convertIdToInstant(elementId);
            if (elementCreatedTime == null) continue;

            if (elementCreatedTime.plus(2 * 7, ChronoUnit.DAYS).isBefore(clock.instant())) {
                entry.remove();
            }
        }

        return entriesDoc;
    }

    private String extractElementId(Element entry) {
        String elementId = entry.id();

        if (elementId.isEmpty()) {
            System.out.print("Could not remove entry. No id found");
            return null;
        }
        return elementId;
    }

    private Instant convertIdToInstant(String elementId) {
        long elementCreatedEpoch;
        try {
            elementCreatedEpoch = Long.parseLong(elementId);
        } catch (NumberFormatException e) {
            System.out.printf("Could not remove entry with id: %s. Could not convert it to long", elementId);
            return null;
        }

        Instant elementCreatedTime = Instant.ofEpochMilli(elementCreatedEpoch);
        return elementCreatedTime;
    }
}
