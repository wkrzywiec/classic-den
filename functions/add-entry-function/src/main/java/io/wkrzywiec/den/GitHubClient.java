package io.wkrzywiec.den;


import io.restassured.response.Response;
import org.apache.http.client.HttpResponseException;

import static io.restassured.RestAssured.get;
import static java.lang.String.format;

interface GitHubClient {
    String loadFileFrom(String repository, String branch, String filePath) throws HttpResponseException;
}

class GitHubClientImpl implements GitHubClient {

    private static final String GITHUB_FILE_PATH_PATTERN = "https://raw.githubusercontent.com/%s/%s/%s";

    public String loadFileFrom(String repository, String branch, String filePath) throws HttpResponseException {

        Response response = get(format(GITHUB_FILE_PATH_PATTERN, repository, branch, filePath));

        if (response.statusCode() != 200) {
            throw new HttpResponseException(400, "Unable to load file from GitHub");
        }
        return response.getBody().asString();
    }
}
