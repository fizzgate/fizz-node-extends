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
package com.fizzgate.plugin;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component(LogPlugin.PLUGIN_ID) // 必须，且为插件 id
public class LogPlugin implements FizzPluginFilter {
    public static final String PLUGIN_ID = "logPlugin"; // 插件 id
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, Map<String, Object> config) {
        System.err.println("this is log plugin"); // 本插件只输出这个
        return FizzPluginFilterChain.next(exchange); // 执行后续逻辑
    }
}
