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
package com.aliyun.api.gateway.demo;

import java.net.URL;
import java.util.Map;

import com.aliyun.api.gateway.demo.constant.Constants;
import com.aliyun.api.gateway.demo.enums.Method;

/**
 * Request包装Bean
 * 
 * @author qiming.wqm 2016/06/24
 */
public class Request {

    /**
     * （可选）字节数组类型Body体
     */
    private byte[] bytesBody;

    /**
     * （可选）表单参数
     */
    private Map<String, String> formBody;

    /**
     * （可选） HTTP头
     */
    private Map<String, String> headers;

    /**
     * （必选）请求方法
     */
    private Method method;

    /**
     * （可选）自定义参与签名Header前缀
     */
    private String[] signHeaderPrefixes;

    /**
     * （可选）字符串Body体
     */
    private String stringBody;

    /**
     * （必选）超时时间，单位毫秒，设置零默认使用com.aliyun.apigateway.demo.constant.Constants.DEFAULT_TIMEOUT
     */
    private int timeout;

    /**
     * （必选）Host+Path+Query
     */
    private URL url;

    public Request() {
    }

    public Request(Method method, URL url) {
        this(method, url, Constants.DEFAULT_TIMEOUT, null, null);
    }

    public Request(Method method, URL url, int timeout) {
        this(method, url, timeout, null, null);
    }

    public Request(Method method, URL url, Map<String, String> headers, String[] signHeaderPrefixes) {
        this(method, url, Constants.DEFAULT_TIMEOUT, headers, signHeaderPrefixes);
    }

    public Request(Method method, URL url, int timeout, Map<String, String> headers, String[] signHeaderPrefixes) {
        this.method = method;
        this.url = url;
        this.timeout = timeout;
        this.headers = headers;
        this.signHeaderPrefixes = signHeaderPrefixes;
    }

    public byte[] getBytesBody() {
        return bytesBody;
    }

    public Map<String, String> getFormBody() {
        return formBody;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Method getMethod() {
        return method;
    }

    public String[] getSignHeaderPrefixes() {
        return signHeaderPrefixes;
    }

    public String getStringBody() {
        return stringBody;
    }

    public int getTimeout() {
        return timeout;
    }

    public URL getUrl() {
        return url;
    }

    public void setBytesBody(byte[] bytesBody) {
        this.bytesBody = bytesBody;
    }

    public void setFormBody(Map<String, String> formBody) {
        this.formBody = formBody;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public void setSignHeaderPrefixList(String[] signHeaderPrefixes) {
        this.signHeaderPrefixes = signHeaderPrefixes;
    }

    public void setStringBody(String stringBody) {
        this.stringBody = stringBody;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
}
