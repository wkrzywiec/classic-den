package io.wkrzywiec.den

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

@Subject(AddEntryFacade)
class AddEntryFacadeSpec extends Specification {

    AddEntryFacade facade
    GitHubClient gitHub = Stub(GitHubClient)

    def setup() {
        facade = new AddEntryFacade(new ObjectMapper(), gitHub)
    }

    @Unroll
    def "Invalid request body: #requestBody"() {
        when:
        facade.proccessRequest(requestBody)

        then:
        thrown(exception)

        where:
        requestBody                         || exception
        ""                                  || JsonProcessingException
        "{}"                                || IllegalArgumentException
        '{"title": "a" }'                   || IllegalArgumentException
        '{"title": "a", "message": "b" }'   || IllegalArgumentException
    }
}
