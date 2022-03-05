package io.wkrzywiec.den;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.apache.http.client.HttpResponseException;

import java.util.Optional;

import static java.lang.String.format;

public class Function {

    private final AddEntryFacade facade = new AddEntryFacade(new ObjectMapper(), new GitHubClientImpl());

    @FunctionName("addEntry")
    public HttpResponseMessage addEntry(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        String requestBody = request.getBody().orElse("");
        context.getLogger().info(format("Adding entry: %s", requestBody));

        try {
            facade.proccessRequest(requestBody);

        } catch (JsonProcessingException e) {
            request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Could not parse request body")
                    .build();
        } catch (IllegalArgumentException | HttpResponseException e) {
            request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage())
                    .build();
        }

        return request.createResponseBuilder(HttpStatus.ACCEPTED)
                .build();
    }
}
