package io.wkrzywiec.den;

import static io.restassured.RestAssured.get;
import static java.lang.String.format;

class GitHubClient {

    String loadFileFrom(String repository, String filePath) {

        get(format("https://api.github.com/repos/%s/contents/%s", repository, filePath))
                .header("Accept: application/vnd.github.v3+json");
        return "";
    }
}
