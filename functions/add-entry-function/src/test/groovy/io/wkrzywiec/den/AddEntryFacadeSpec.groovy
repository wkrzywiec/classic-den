package io.wkrzywiec.den

import com.fasterxml.jackson.core.JsonProcessingException
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

@Subject(AddEntryFacade)
class AddEntryFacadeSpec extends Specification {

    AddEntryFacade facade

    def setup() {
        facade = new AddEntryFacade()
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
