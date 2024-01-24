/*
 *  Copyright (C) 2020 the original author or authors.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.fizzgate.com.fizzgate.aggregate.web.flow.extension.mysql;

import com.fizzgate.aggregate.core.flow.FlowContext;
import com.fizzgate.aggregate.core.flow.INode;
import com.fizzgate.aggregate.core.flow.INodeBuilder;
import com.fizzgate.aggregate.web.flow.RPCNode;
import com.fizzgate.aggregate.web.flow.RPCResponse;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * @author linwaiwai
 */
public class MysqlNode extends RPCNode<MysqlNodeConfig> {
    public static final String TYPE = "MYSQL";
    public MysqlNode(MysqlNodeConfig rpcConfig, FlowContext flowContext) {
        super(rpcConfig, flowContext);
    }

    public static class MysqlNodeBuilder implements INodeBuilder {
        @Override
        public INode build(Map<String, Object> nodeConfig, FlowContext context) {
            return new MysqlNode(new MysqlNodeConfig(nodeConfig), context);
        }
    }
    @Override
    protected void doResponseMapping(Object responseBody) {
        Map<String, Object> response = (Map<String, Object>) context.getNode(super.getName()).get("response");
        synchronized (context) {
            // 在上下文中的response放入body
            response.put("body", responseBody);
        }
    }
    @Override
    protected void doOnBodySuccess(Object resp, long elapsedMillis) {
    }
    @Override
    protected Mono<RPCResponse> getClientSpecFromContext() {
        ConnectionFactory connectionFactory = ConnectionFactories.get(
                this.config.getUrl()
        );
        return Mono.from(connectionFactory.create())
                .flatMapMany(connection ->{
                    String sql = this.config.getSql();
                    return Flux.from(connection.createStatement(sql)
                                    .execute())
                            .flatMap(result ->result.map((row, metadata) ->{
                                Map data = new HashMap<String, Object>();
                                metadata.getColumnNames().forEach(column->{
                                     data.put(column, row.get(column));
                                });
                                return data;
                            }));
                }).collectList().flatMap(cr -> {
                    MysqlRPCResponse response = new MysqlRPCResponse();
                    response.setBodyMono(Mono.just(cr));
                    return Mono.just(response);
                });
    }
}
