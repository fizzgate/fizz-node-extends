package com.fizzgate.aggregate.web.flow.extension.mysql;

import com.fizzgate.aggregate.core.flow.*;
import com.fizzgate.aggregate.web.flow.extension.MockUtils;
import com.fizzgate.com.fizzgate.aggregate.web.flow.extension.mysql.MysqlNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MysqlNodeTests {
    private String traceIdHeader = "X-FIZZ-ID";
    private String traceId = "trace-id-1";
    FlowContext context;
    @BeforeEach
    public void setUp(){
        NodeFactory.registerBuilder(MysqlNode.TYPE, new MysqlNode.MysqlNodeBuilder());
        String instance = "demo.fizzgate.com";
        context = MockUtils.prepare(instance);
        Map<String, Object> input = new ConcurrentHashMap<>();
        Map<String, Object> request = new ConcurrentHashMap<>();
        input.put("request", request);
        request.put("method", "GET");
        request.put("path", "/test/test");
        context.put("input", input);
    }

    @SuppressWarnings("unchecked")
    @Test
    void singleNodeTest() {

        String flowConfigStr = MockUtils.readStrFromFile("node/mysql/single_node_config.json");
        String body = "{}";
        HttpHeaders respHttpHeaders = new HttpHeaders();
        respHttpHeaders.add("content-type", "text/html");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json; charset=UTF-8");
        httpHeaders.add(traceIdHeader, traceId);

        // run engine
        Flow flow = Flow.build(flowConfigStr, context);
        FlowResponse flowResponse =  flow.run().subscribeOn(Schedulers.elastic()).block();
        Map<String, Object> response = (Map<String, Object>) context.getNode("req").get("response");
        Assertions.assertNotNull(response, "request node not run");
        List<Object> responseBody = (List<Object>)response.get("body");
        Assertions.assertTrue(responseBody.size()>0);
    }

}
