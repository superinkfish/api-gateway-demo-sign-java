/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.aliyun.api.gateway.demo.enums;

import org.apache.http.client.methods.RequestBuilder;

import com.aliyun.api.gateway.demo.constant.ContentType;
import com.aliyun.api.gateway.demo.constant.HttpHeader;

/**
 * Http请求方法
 * 
 * @author lipengfei 2016/03/17
 * @author qiming.wqm 2016/06/24
 */
public enum Method {
    GET {
        @Override
        public RequestBuilder requestbuilder() {
            return RequestBuilder.get();
        }
    },
    POST_FORM {
        @Override
        public RequestBuilder requestbuilder() {
            return RequestBuilder.post().addHeader(HttpHeader.HTTP_HEADER_CONTENT_TYPE, ContentType.CONTENT_TYPE_FORM);
        }
    },
    POST_STRING {
        @Override
        public RequestBuilder requestbuilder() {
            return RequestBuilder.post();
        }
    },
    POST_BYTES {
        @Override
        public RequestBuilder requestbuilder() {
            return RequestBuilder.post();
        }
    },
    PUT_FORM {
        @Override
        public RequestBuilder requestbuilder() {
            return RequestBuilder.put();
        }
    },
    PUT_STRING {
        @Override
        public RequestBuilder requestbuilder() {
            return RequestBuilder.put();
        }
    },
    PUT_BYTES {
        @Override
        public RequestBuilder requestbuilder() {
            return RequestBuilder.put();
        }
    },
    DELETE {
        @Override
        public RequestBuilder requestbuilder() {
            return RequestBuilder.delete();
        }
    };

    /**
     * @return 该Method对应的{@link RequestBuilder}
     */
    public abstract RequestBuilder requestbuilder();
}
