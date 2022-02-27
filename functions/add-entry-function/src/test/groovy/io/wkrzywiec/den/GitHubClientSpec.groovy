package io.wkrzywiec.den

import spock.lang.Specification
import spock.lang.Subject

@Subject(GitHubClient)
class GitHubClientSpec extends Specification {

    GitHubClient github

    def setup() {
        github = new GitHubClient()
    }

    def "Load indexhtml file content"() {
        when:
        String indexHtml = github.loadFileFrom("wkrzywiec/classic-den", "main", "web-page/index.html")

        then:
        indexHtml.contains("Classic Den")
    }
}
