package com.fizzgate.aggregate.web.flow.extension;

import com.fizzgate.aggregate.web.util.ApplicationContextUtils;
import com.fizzgate.proxy.FizzWebClient;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

public class MockRequestClient {

    public static ClientResponse doMock( String traceId, String url, HttpMethod method, String body, HttpStatus status, HttpHeaders respHttpHeaders, FizzWebClient fizzWebClient) {
        if (fizzWebClient == null){
            fizzWebClient = ApplicationContextUtils.APPLICATION_CONTEXT.getBean(FizzWebClient.class);
        }
        ClientResponse clientResponse = MockUtils.buildResponse(body, respHttpHeaders);
        if (status != null){
            Mockito.when(clientResponse.statusCode()).thenReturn(status);
        }

        // mock request client
        Mockito.when(fizzWebClient.send(Mockito.eq(traceId), Mockito.eq(method), Mockito.eq(url),
                Mockito.any(),Mockito.any()
                , Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong())).thenReturn(Mono.just(clientResponse));
        return clientResponse;

    }
}
