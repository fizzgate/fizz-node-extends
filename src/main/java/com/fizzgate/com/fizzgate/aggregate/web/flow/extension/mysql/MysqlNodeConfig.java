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

import com.fizzgate.aggregate.core.exception.FizzRuntimeException;
import com.fizzgate.aggregate.web.flow.RPCConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
/**
 * @author linwaiwai
 */
public class MysqlNodeConfig extends RPCConfig {
    private String url;
    private String sql;

    public MysqlNodeConfig(Map<String, Object> configMap) {

        super(configMap);
        Map<String, Object> configBody = (Map<String, Object>) configMap.get("properties");
        String url = (String) configBody.get("url");
        if (StringUtils.isBlank(url)) {
            throw new FizzRuntimeException("url can not be blank");
        }
        setUrl(url);

        String sql = (String) configBody.get("sql");
        if (StringUtils.isBlank(sql)) {
            throw new FizzRuntimeException("sql can not be blank");
        }

        setSql(sql);
    }

    private void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }


    public String getSql() {
        return this.sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }


}
