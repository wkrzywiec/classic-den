package io.wkrzywiec.den

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

@Subject(AddEntryFacade)
class AddEntryFacadeSpec extends Specification {

    AddEntryFacade facade
    GitHubClient gitHub = Mock(GitHubClient)

    def setup() {
        facade = new AddEntryFacade(new ObjectMapper(), gitHub)
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
            <div class="span6">
                 <h4>Simple Title</h4>
                 <p>Simple message added</p>
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
