package com.fizzgate.aggregate.web.flow.extension;

import com.fizzgate.aggregate.core.flow.FlowContext;
import com.fizzgate.aggregate.web.loader.AppConfigProperties;
import com.fizzgate.aggregate.web.util.ApplicationContextUtils;
import com.fizzgate.config.SystemConfig;
import com.fizzgate.proxy.FizzWebClient;
import com.fizzgate.proxy.RpcInstanceService;
import com.fizzgate.proxy.http.HttpInstanceService;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MockUtils {
    public static String traceId = "trace-id-1";
    public static String traceIdHeader = "X-FIZZ-ID";
    public static Map<String, Object> buildInput() {
        Map<String, Object> input = new ConcurrentHashMap<>();
        Map<String, Object> request = new ConcurrentHashMap<>();
        input.put("request", request);
        request.put("method", "POST");
        request.put("path", "/proxy/order/weather");
        return request;
    }
    public static   FlowContext prepare(String configStr) {

        // prepare flow context
        FlowContext context = new FlowContext();
        context.setTraceId(MockUtils.traceId);

        ApplicationContext ac = Mockito.mock(ApplicationContext.class);
        RpcInstanceService rpcInstanceService = Mockito.mock(RpcInstanceService.class);
        Mockito.when(rpcInstanceService.getInstance(Mockito.anyByte(),Mockito.any())).thenReturn(configStr);

        HttpInstanceService httpInstanceService = Mockito.mock(HttpInstanceService.class);
        Mockito.when(httpInstanceService.getInstance(Mockito.any())).thenReturn(configStr);
        Mockito.when(ac.getBean(HttpInstanceService.class)).thenReturn(httpInstanceService);


        SystemConfig systemConfig = Mockito.mock(SystemConfig.class);
        Mockito.when(systemConfig.getProxySetHeaders()).thenReturn(Collections.emptyList());
        Mockito.when(systemConfig.fizzTraceIdHeader()).thenReturn(MockUtils.traceIdHeader);
        Mockito.when(ac.getBean(SystemConfig.class)).thenReturn(systemConfig);
        Mockito.when(ac.getBean(RpcInstanceService.class)).thenReturn(rpcInstanceService);

        AppConfigProperties appConfigProperties = Mockito.mock(AppConfigProperties.class);
        Mockito.when(appConfigProperties.getEnv()).thenReturn("test");
        Mockito.when(ac.getBean(AppConfigProperties.class)).thenReturn(appConfigProperties);

        FizzWebClient fizzWebClient = Mockito.mock(FizzWebClient.class);
        Mockito.when(ac.getBean(FizzWebClient.class)).thenReturn(fizzWebClient);

        ApplicationContextUtils.APPLICATION_CONTEXT = ac;
        return context;
    }

    public static String readStrFromFile(String file) {
        // read the file single_node_config.json in the resources folder into a string
        InputStream inputStream = MockUtils.class.getClassLoader().getResourceAsStream(file);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        bufferedReader.lines().forEach(stringBuilder::append);
        return stringBuilder.toString();
    }

    public static ClientResponse buildResponse(String body, HttpHeaders respHttpHeaders) {
        ClientResponse clientResponse = Mockito.mock(ClientResponse.class);
        ClientResponse.Headers respHeaders = Mockito.mock(ClientResponse.Headers.class);

        if (respHeaders == null){
            respHttpHeaders = new HttpHeaders();
            respHttpHeaders.add(traceIdHeader, traceId);
        }

        Mockito.when(respHeaders.asHttpHeaders()).thenReturn(respHttpHeaders);

        Mockito.when(clientResponse.headers()).thenReturn(respHeaders);
        Mockito.when(clientResponse.bodyToMono(String.class)).thenReturn(Mono.just(body));
        return clientResponse;
    }
}
