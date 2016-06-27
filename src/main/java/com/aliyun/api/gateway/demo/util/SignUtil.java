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
package com.aliyun.api.gateway.demo.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.RequestBuilder;

import com.aliyun.api.gateway.demo.constant.Constants;
import com.aliyun.api.gateway.demo.constant.HttpHeader;
import com.aliyun.api.gateway.demo.constant.SystemHeader;

/**
 * 签名工具<br>
 * 本签名工具使用HMAC+SHA256加密算法对url及header进行加密和签名。
 * 
 * @author qiming.wqm 2016/06/24
 */
public class SignUtil {

    /**
     * 计算签名
     *
     * @param requestBuilder
     *            HTTP请求构造器
     * @param url
     *            Path+Query
     * @param formParamMap
     *            POST表单参数
     * @param secret
     *            APP密钥
     * @param signHeaderPrefixes
     *            自定义参与签名Header前缀
     * @return 签名后的字符串
     */
    public static String sign(RequestBuilder requestBuilder, String url, Map<String, String> formParamMap,
            String secret, String[] signHeaderPrefixes) {
        String sign = buildStringToSign(requestBuilder, url, formParamMap, signHeaderPrefixes);
        System.out.println(sign);
        return new String(Base64.encodeBase64(HmacUtils.hmacSha256(secret, sign)), Constants.ENCODING);
    }

    /**
     * 构建待签名字符串
     *
     * @param requestBuilder
     *            HTTP请求构造器
     * @param url
     *            Path+Query
     * @param formParamMap
     *            POST表单参数
     * @param signHeaderPrefixes
     *            自定义参与签名Header前缀
     * @return 签名字符串
     */
    private static String buildStringToSign(RequestBuilder requestBuilder, String url, Map<String, String> formParamMap,
            String[] signHeaderPrefixes) {
        Map<String, String> headers = new HashMap<>();
        for (Header header : requestBuilder.build().getAllHeaders()) {
            headers.put(header.getName(), header.getValue());
        }
        StringBuilder sb = new StringBuilder();
        sb.append(requestBuilder.getMethod().toUpperCase()).append(Constants.LF);
        if (headers.get(HttpHeader.HTTP_HEADER_ACCEPT) != null) {
            sb.append(headers.get(HttpHeader.HTTP_HEADER_ACCEPT));
        }
        sb.append(Constants.LF);
        if (headers.get(HttpHeader.HTTP_HEADER_CONTENT_MD5) != null) {
            sb.append(headers.get(HttpHeader.HTTP_HEADER_CONTENT_MD5));
        }
        sb.append(Constants.LF);
        if (headers.get(HttpHeader.HTTP_HEADER_CONTENT_TYPE) != null) {
            sb.append(headers.get(HttpHeader.HTTP_HEADER_CONTENT_TYPE));
        }
        sb.append(Constants.LF);
        if (headers.get(HttpHeader.HTTP_HEADER_DATE) != null) {
            sb.append(headers.get(HttpHeader.HTTP_HEADER_DATE));
        }
        sb.append(Constants.LF);
        sb.append(buildHeaders(requestBuilder, headers, signHeaderPrefixes));
        sb.append(buildResource(url, formParamMap));
        return sb.toString();
    }

    /**
     * 构建待签名Path+Query+FormParams
     *
     * @param url
     *            Path+Query
     * @param formParamMap
     *            POST表单参数
     * @return 待签名Path+Query+FormParams
     */
    private static String buildResource(String url, Map<String, String> formParamMap) {
        if (url.contains("?")) {
            String[] fregments = url.split("\\?");
            String path = fregments[0];
            String queryString = fregments[1];
            url = path;
            formParamMap = ObjectUtils.defaultIfNull(formParamMap, new HashMap<String, String>());
            if (StringUtils.isNotBlank(queryString)) {
                for (String query : queryString.split("\\&")) {
                    fregments = query.split("\\=");
                    String key = fregments[0];
                    String value = fregments.length == 2 ? fregments[1] : "";
                    if (formParamMap.get(key) == null) {
                        formParamMap.put(key, value);
                    }
                }
            }
        }
        StringBuilder sb = new StringBuilder(url);
        if (MapUtils.isNotEmpty(formParamMap)) {
            sb.append('?');
            //参数Key按字典排序
            SortedMap<String, String> sortMap = new TreeMap<>(formParamMap);
            for (Entry<String, String> e : sortMap.entrySet()) {
                sb.append(e.getKey());
                if (StringUtils.isNotBlank(e.getValue())) {
                    sb.append("=").append(e.getValue());
                }
                sb.append('&');
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        }
        return sb.toString();
    }

    /**
     * 构建待签名HTTP头，并在header的Map里加入签名头信息
     * 
     * @param requestBuilder
     *            HTTP请求构造器，本方法将会在HTTP请求构造器中添加头部信息：{@link SystemHeader#X_CA_SIGNATURE_HEADERS}
     * @param headers
     *            请求中所有的Http头
     * @param signHeaderPrefixes
     *            自定义参与签名Header前缀
     * @return 待签名HTTP头
     */
    private static String buildHeaders(RequestBuilder requestBuilder, Map<String, String> headers,
            final String[] signHeaderPrefixes) {
        Map<String, String> headersToSign = new TreeMap<String, String>();
        if (headers != null) {
            StringBuilder signHeadersStringBuilder = new StringBuilder();
            for (Entry<String, String> header : headers.entrySet()) {
                if (isHeaderToSign(header.getKey(), signHeaderPrefixes)) {
                    signHeadersStringBuilder.append(header.getKey()).append(',');
                    headersToSign.put(header.getKey(), header.getValue());
                }
            }
            if (signHeadersStringBuilder.length() > 0) {
                signHeadersStringBuilder.deleteCharAt(signHeadersStringBuilder.length() - 1);
            }
            requestBuilder.addHeader(SystemHeader.X_CA_SIGNATURE_HEADERS, signHeadersStringBuilder.toString());
        }
        StringBuilder sb = new StringBuilder();
        for (Entry<String, String> e : headersToSign.entrySet()) {
            sb.append(e.getKey()).append(':').append(e.getValue()).append(Constants.LF);
        }
        return sb.toString();
    }

    /**
     * 
     * 判断给定的HTTP头是否参与签名
     * 
     * @param headerName
     *            HTTP头名称
     * @param signHeaderPrefixes
     *            自定义参与签名的头前缀
     * @return
     * 
     */
    private static boolean isHeaderToSign(final String headerName, final String[] signHeaderPrefixes) {
        //headerName不能为null
        if (StringUtils.isBlank(headerName)) {
            return false;
        }
        //系统header参与签名
        if (headerName.startsWith(Constants.CA_HEADER_TO_SIGN_PREFIX_SYSTEM)) {
            return true;
        }
        //判断是否是自定义参与签名的header
        if (ArrayUtils.isEmpty(signHeaderPrefixes)) {
            return StringUtils.startsWithAny(headerName, signHeaderPrefixes);
        }
        return false;
    }
}
