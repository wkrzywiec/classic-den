package io.wkrzywiec.den;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.apache.http.client.HttpResponseException;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static java.lang.String.format;

interface GitHubClient {
    String loadFileFrom(String repository, String branch, String filePath) throws HttpResponseException;
    void updateFile(String repository, String branch, String filePath, String content) throws HttpResponseException, JsonProcessingException;
}

class GitHubClientImpl implements GitHubClient {

    private static final String GITHUB_RAW_FILE_PATH_PATTERN = "https://raw.githubusercontent.com/%s/%s/%s";
    private static final String GITHUB_API_CONTENT_PATH_PATTERN = "https://api.github.com/repos/%s/contents/%s?ref=%s";

    @Override
    public String loadFileFrom(String repository, String branch, String filePath) throws HttpResponseException {

        Response response = get(format(GITHUB_RAW_FILE_PATH_PATTERN, repository, branch, filePath));

        if (response.statusCode() != 200) {
            throw new HttpResponseException(400, "Unable to load file");
        }
        return response.getBody().asString();
    }

    @Override
    public void updateFile(String repository, String branch, String filePath, String content) throws HttpResponseException, JsonProcessingException {

        String sha = getFileSha(repository, branch, filePath);

        Map<String,String> requestBody = new HashMap<>();
        requestBody.put("sha", sha);
        requestBody.put("branch", branch);
        requestBody.put("message", "add new entry");
        requestBody.put("content", Base64.getEncoder().encodeToString(content.getBytes()));

        String accessToken = System.getenv("CLASSIC_DEN_GITHUB_ACCESS_TOKEN");

        Response response = given()
                .log().all()
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/vnd.github.v3+json")
                .body(new ObjectMapper().writeValueAsString(requestBody))
        .put(format(GITHUB_API_CONTENT_PATH_PATTERN, repository, filePath, branch)).prettyPeek();

        if (response.statusCode() != 200 && response.statusCode() != 201) {
            throw new HttpResponseException(400, "Unable to update a file");
        }
    }

    private String getFileSha(String repository, String branch, String filePath) {
        return given()
                .log().all()
                .header("Accept", "application/vnd.github.v3+json")
        .get(format(GITHUB_API_CONTENT_PATH_PATTERN, repository, filePath, branch))
                .prettyPeek()
                .then().extract().response()
                .jsonPath().get("sha");
    }
}
