package io.wkrzywiec.den

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import java.text.MessageFormat
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

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
        gitHub.loadFileFrom(_, _, _) >> entriesFileContent(testTime.minusSeconds(10))

        and: "New request"
        def requestBody = '''
        {
            "title": "Simple Title",
            "message": "Simple message added",
            "author": "John Doe"
        }
        '''

        when:
        facade.proccessCreateRequest(requestBody)

        then:
        1* gitHub.updateFile(_, _, _, {content ->
            totalEntries(content) == 4

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
        facade.proccessCreateRequest(requestBody)

        then:
        thrown(exception)

        where:
        requestBody                         || exception
        ""                                  || JsonProcessingException
        "{}"                                || IllegalArgumentException
        '{"title": "a" }'                   || IllegalArgumentException
        '{"title": "a", "message": "b" }'   || IllegalArgumentException
    }

    def "Remove old Entry"() {
        given: "There are 15 days old entries"
        gitHub.loadFileFrom(_, _, _) >> entriesFileContent(testTime.minus(15, ChronoUnit.DAYS))

        when:
        facade.removeOutdatedEntries()

        then: "Old entries are removed"
        1* gitHub.updateFile(_, _, _, {content ->
            content.replaceAll("\\s+","").contains('''
            <div class="row-fluid marketing" id="entries-container">
            </div>
            '''.replaceAll("\\s+",""))
        })
    }

    def "Do not remove #day old entry"() {
        given: "There are not outdated entries"
        gitHub.loadFileFrom(_, _, _) >> entriesFileContent(testTime.minus(day, ChronoUnit.DAYS))

        when:
        facade.removeOutdatedEntries()

        then: "Old entries are not removed"
        1* gitHub.updateFile(_, _, _, {content ->
            content.replaceAll("\\s+","").contains('entry-message'.replaceAll("\\s+",""))
        })

        where:
        day << [0, 1, 5, 13, 14]
    }

    private String entriesFileContent(Instant timeCreated) {
        return MessageFormat.format("""
        <div class="row-fluid marketing" id="entries-container">
            <div class="span6" id="{0}">
                 <h4>Simple Title</h4>
                 <p class="entry-message">Simple message added</p>
                 <p class="entry-published">2022-03-08<span class="entry-author">John Doe</span></p>
            </div>
            <div class="span6" id="{0}">
                 <h4>Simple Title</h4>
                 <p class="entry-message">Simple message added</p>
                 <p class="entry-published">2022-03-08<span class="entry-author">John Doe</span></p>
            </div>
            <div class="span6" id="{0}">
                 <h4>Simple Title</h4>
                 <p class="entry-message">Simple message added</p>
                 <p class="entry-published">2022-03-08<span class="entry-author">John Doe</span></p>
            </div>
        </div>
        """, String.valueOf(timeCreated.toEpochMilli()))
    }

    int totalEntries(String content) {
        return (content.length() - content.replaceAll("span6","").length()) / 5
    }
}
