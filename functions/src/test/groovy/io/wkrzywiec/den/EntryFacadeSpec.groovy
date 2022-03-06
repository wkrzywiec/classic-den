package io.wkrzywiec.den

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import java.time.Clock
import java.time.Instant
import java.time.ZoneId

@Subject(EntryFacade)
class EntryFacadeSpec extends Specification {

    EntryFacade facade
    GitHubClient gitHub = Mock(GitHubClient)
    def testTime = Instant.parse("2022-03-08T10:15:30.00Z")

    def setup() {
        facade = new EntryFacade(new ObjectMapper(), gitHub, Clock.fixed(testTime, ZoneId.of("Europe/Paris")))
    }

    def "Add new Entry"() {
        given: "entries.html file"
        gitHub.loadFileFrom(_, _, _) >> entriesFileContent()

        and: "New request"
        def requestBody = '''
        {
            "title": "Simple Title",
            "message": "Simple message added",
            "author": "John Doe"
        }
        '''

        when:
        facade.proccessRequest(requestBody)

        then:
        1* gitHub.updateFile(_, _, _, {content ->
            content.replaceAll("\\s+","").contains('''
            <div class="span6" id="1646734530000">
                 <h4>Simple Title</h4>
                 <p class="entry-message">Simple message added</p>
                 <p class="entry-published">2022-03-08<span class="entry-author">John Doe</span></p>
            </div>
            '''.replaceAll("\\s+",""))
        })
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

    def String entriesFileContent() {
        return """
        <div class="row-fluid marketing" id="entries-container">
            <div class="span6">
                <h4>Subheading</h4>
                <p>Donec id elit non mi porta gravida at eget metus. Maecenas faucibus mollis interdum.</p>
            </div>
            <div class="span6">
                <h4>Subheading</h4>
                <p>Morbi leo risus, porta ac consectetur ac, vestibulum at eros. Cras mattis consectetur purus sit amet
                    fermentum.</p>
            </div>
            <div class="span6">
                <h4>Subheading</h4>
                <p>Maecenas sed diam eget risus varius blandit sit amet non magna.</p>
            </div>
        </div>
        """

    }
}
