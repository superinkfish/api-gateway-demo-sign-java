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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyun.api.gateway.demo.constant.Constants;
import com.aliyun.api.gateway.demo.constant.ContentType;
import com.aliyun.api.gateway.demo.constant.SystemHeader;
import com.aliyun.api.gateway.demo.util.SignUtil;

/**
 * Client
 * 
 * @author qiming.wqm 2016/06/24
 */
public class Client implements AutoCloseable {

    /** APP Key */
    private String appKey;
    /** APP密钥 */
    private String appSecret;
    /** HttpClient实例， */
    private CloseableHttpClient httpClient = null;
    /** 是否是测试环境，true为测试环境，false为生产环境 */
    private boolean testEnv;
    private static final Logger log = LoggerFactory.getLogger(Client.class);

    /**
     * 构造器<br>
     * 当Client需要访问https的URL时，需要设置keystore文件及其storePassword。keystore文件的生成方法可以参考 《
     * <a href="http://docs.oracle.com/javase/6/docs/technotes/tools/windows/keytool.html">keytool使用文档</a>》
     * 
     * @param appKey
     *            APP Key，可从API网关中创建的APP信息中获得
     * @param appSecret
     *            APP密钥，可从API网关中创建的APP信息中获得
     * @param testEnv
     *            是否是测试环境，true为测试环境，false为生产环境
     */
    public Client(String appKey, String appSecret, boolean testEnv) {
        HttpClientBuilder builder = HttpClients.custom();
        try {
            SSLContext sslContext = null;
            if (testEnv) {
                sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                    @Override
                    public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        //一概返回true仅在测试环境中，如果生产环境中永远返回true存在安全风险
                        return true;
                    }
                }).build();
            } else {
                //若出现错误，请用keytool生成keystore并在本段逻辑中指定该keystore
                KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
                ks.load(null, null);
                sslContext = SSLContexts.custom().loadTrustMaterial(ks, new TrustSelfSignedStrategy()).build();
            }
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new String[] { "TLSv1" },
                    null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
            builder.setSSLSocketFactory(sslsf);
        } catch (KeyStoreException | KeyManagementException | NoSuchAlgorithmException | CertificateException
                | IOException e) {
            log.error(e.getMessage(), e);
        }
        httpClient = builder.setUserAgent(Constants.USER_AGENT).build();
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.testEnv = testEnv;
    }

    /** 关闭HttpClient */
    @Override
    public void close() {
        if (httpClient != null) {
            try {
                httpClient.close();
                httpClient = null;
            } catch (Exception ex) {
                //leave empty
            }
        }
    }

    /**
     * 发送请求
     *
     * @param request
     *            request对象，如果传入null则会产生{@link NullPointerException}
     * @return HttpResponse HTTP响应
     * @throws IOException
     *             HTTP访问异常时，含网络等异常，抛出异常
     * @throws ClientProtocolException
     *             如果不支持的协议，则抛出异常
     */
    public HttpResponse execute(Request request) throws ClientProtocolException, IOException {
        if (request == null) {
            return null;
        }
        RequestBuilder requestBuilder = request.getMethod().requestbuilder().setUri(request.getUrl().toString());
        requestBuilder.setConfig(RequestConfig.custom().setConnectTimeout(getTimeout(request.getTimeout())).build());
        requestBuilder.addHeader(SystemHeader.X_CA_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        requestBuilder.addHeader(SystemHeader.X_CA_NONCE, UUID.randomUUID().toString());
        requestBuilder.addHeader(SystemHeader.X_CA_KEY, appKey);
        if (testEnv) {
            requestBuilder.addHeader(SystemHeader.X_CA_STAGE, "test");
        }
        requestBuilder.build().getAllHeaders();
        initialBasicHeader(requestBuilder, request.getHeaders(), request.getUrl(), request.getFormBody(),
                request.getSignHeaderPrefixes());
        HttpEntity entity = getEntity(request);
        if (entity != null) {
            requestBuilder.setEntity(entity);
        }
        return httpClient.execute(requestBuilder.build());
    }

    /**
     * 构建FormEntity
     * 
     * @param formParam
     * @return
     */
    private UrlEncodedFormEntity buildFormEntity(Map<String, String> formParam, final Charset charset) {
        if (formParam != null) {
            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            for (Entry<String, String> entry : formParam.entrySet()) {
                nameValuePairList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairList, charset);
            formEntity.setContentType(ContentType.CONTENT_TYPE_FORM);
            return formEntity;
        }
        return null;
    }

    private HttpEntity getEntity(Request request) {
        HttpEntity entity = null;
        if (request.getFormBody() != null) {
            entity = buildFormEntity(request.getFormBody(), Constants.ENCODING);
        } else if (StringUtils.isNotBlank(request.getStringBody())) {
            entity = new StringEntity(request.getStringBody(), Constants.ENCODING);
        } else if (request.getBytesBody() != null) {
            entity = new ByteArrayEntity(request.getBytesBody());
        }
        return entity;
    }

    /**
     * 读取超时时间
     * 
     * @param timeout
     * @return 如果timeout不是正整数，则返回默认的超时时间（{@link Constants#DEFAULT_TIMEOUT}）
     */
    private int getTimeout(int timeout) {
        if (timeout <= 0) {
            return Constants.DEFAULT_TIMEOUT;
        }
        return timeout;
    }

    /**
     * 初始化基础Header
     * 
     * @param requestBuilder
     *            请求构造器
     * @param headers
     *            Http头
     * @param url
     *            http://host+path+query
     * @param formParam
     *            表单参数
     * @param signHeaderPrefixes
     *            自定义参与签名Header前缀
     * @return 基础Header
     * @throws MalformedURLException
     */
    private void initialBasicHeader(RequestBuilder requestBuilder, Map<String, String> headers, URL url,
            Map<String, String> formParam, String[] signHeaderPrefixes) throws MalformedURLException {
        if (headers != null) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                requestBuilder.removeHeaders(e.getKey()).addHeader(e.getKey(), e.getValue());
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (StringUtils.isNotBlank(url.getPath())) {
            stringBuilder.append(url.getPath());
        }
        if (StringUtils.isNotBlank(url.getQuery())) {
            stringBuilder.append("?");
            stringBuilder.append(url.getQuery());
        }
        requestBuilder.addHeader(SystemHeader.X_CA_SIGNATURE,
                SignUtil.sign(requestBuilder, stringBuilder.toString(), formParam, appSecret, signHeaderPrefixes));
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

}
