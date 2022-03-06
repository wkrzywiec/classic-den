package io.wkrzywiec.den

import spock.lang.Specification
import spock.lang.Subject

import static io.restassured.RestAssured.given
import static java.lang.String.format
import static org.hamcrest.Matchers.containsString

@Subject(GitHubClientImpl)
class GitHubClientImplSpec extends Specification {

    GitHubClientImpl github

    def setup() {
        github = new GitHubClientImpl()
    }

    def "Load indexhtml file content"() {
        when:
        String indexHtml = github.loadFileFrom("wkrzywiec/classic-den", "main", "web-page/entries.html")

        then:
        indexHtml.contains("entries-container")
    }

    def "Upload file"() {
        given:
        def repository = "wkrzywiec/classic-den"
        def branch = "test-branch"
        def filePath = "web-page/index.html"
        def content = indexHtlm()

        and:
        def placeholder = UUID.randomUUID().toString()
        content = content.replace("%placeholder%", placeholder)

        when:
        github.updateFile(repository, branch, filePath, content)

        then:
        Thread.sleep(5_000)
        given()
                .log().all()
                .get(format("https://raw.githubusercontent.com/%s/%s/%s", repository, branch, filePath))
                .prettyPeek()
        .then().statusCode(200)
                .body(containsString(placeholder))
    }

    def String indexHtlm() {
        return """
        <div class="row-fluid marketing" id="entries-container">
            <div class="span6">
                <h4>%placeholder%</h4>
                <p>Donec id elit non mi porta gravida at eget metus. Maecenas faucibus mollis interdum.</p>
            </div>
        </div>
        """
    }
}
