package com.example.animals;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.model.RequestResponsePact;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

@ExtendWith(PactConsumerTestExt.class)
public class AnimalsTest {
    @Pact(consumer= "consumingApp", provider = "producerApi")
    public RequestResponsePact getAnimals(PactDslWithProvider builder) {
        return builder
                .given("")
                .uponReceiving("Successful get of an Animal")
                .path("/api/v1/animals")
                .method("GET")
                .headers("Accepts", "application/json")
                .willRespondWith()
                .status(200)
                .body(
                        new PactDslJsonBody()
                                .array("animals")
                                .stringType("cat")
                                .stringType("chicken")
                                .stringType("cow")
                                .closeArray()
                                .asBody()
                )
                .headers(responseHeaders())
                .toPact();
    }

    private Map<String, String> responseHeaders() {
        return new HashMap();
    }

    @Test
    @PactTestFor(pactMethod = "getAnimals", port="1234")
    public void verifyGetAnimalsPact(MockServer mockServer) throws IOException {
        HttpResponse httpResponse = Request.Get(mockServer.getUrl() + "/api/v1/animals").addHeader("Accepts", "application/json").execute().returnResponse();
        assertThat(httpResponse.getStatusLine().getStatusCode(), is(equalTo(200)));
    }
}
