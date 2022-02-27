package io.wkrzywiec.den

import spock.lang.Specification
import spock.lang.Subject

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
}
