package com.aliyun.api.gateway.demo;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyun.api.gateway.demo.constant.Constants;
import com.aliyun.api.gateway.demo.constant.ContentType;
import com.aliyun.api.gateway.demo.constant.HttpHeader;
import com.aliyun.api.gateway.demo.enums.Method;
import com.aliyun.api.gateway.demo.util.MessageDigestUtil;

/**
 * 本类为API网关调用的示例类。开发者只需要：<br>
 * <ul>
 * <li>修改APP_KEY、APP_SECRET、TEST_ENV、CUSTOM_HEADERS_TO_SIGN_PREFIX等参数为合理的值；
 * <li>针对自己在API网关中已经配置的接口， 修改相应的方法，对于不需要用到的方法可以删除或者注释掉。
 * </ul>
 * 在生产使用中，本类不需要用到，只需要依照本类中的用法调用{@link Client}即可。
 * 
 * @author qiming.wqm 2016/06/27
 */
public class Main {
    private final static Logger log = LoggerFactory.getLogger(Main.class);
    /** APP Key，请替换成真实的APP Key */
    private final static String APP_KEY = "app_key";

    /** APP密钥，请替换成真实的APP密钥 */
    private final static String APP_SECRET = "app_secret";

    /** 是否是测试环境 */
    private final static boolean TEST_ENV = true;

    /** 自定义参与签名Header前缀（可选,默认只有"X-Ca-"开头的参与到Header签名），一般不需要修改或设置成空字符串 */
    private final static String[] CUSTOM_HEADERS_TO_SIGN_PREFIX = new String[] { "Custom" };

    public static void main(String[] args) {
        try (Client client = new Client(APP_KEY, APP_SECRET, TEST_ENV)) {
            //请选择合适的方法留下，其余方法删除
            get(client);
            postForm(client);
            postString(client);
            postBytes(client);
            putForm(client);
            putString(client);
            putBytesBody(client);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * HTTP GET
     *
     * @param client
     *            Client，提供HTTP访问服务
     * @throws IOException
     *             IO/网络出错
     */
    public static void get(Client client) throws IOException {
        //请求URL
        URL url = new URL("http://host:port/demo/get?qk1=qv2&qkn=qvn");

        Map<String, String> headers = new HashMap<String, String>();
        //（可选）响应内容序列化格式,默认application/json,目前仅支持application/json
        headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "application/json");
        headers.put("CustomHeader", "demo");

        Request request = new Request(Method.GET, url, headers, CUSTOM_HEADERS_TO_SIGN_PREFIX);

        //调用服务端
        HttpResponse response = client.execute(request);
        print(response);
    }

    /**
     * HTTP POST 表单
     *
     * @param client
     *            Client，提供HTTP访问服务
     * @throws IOException
     *             IO/网络出错
     */
    public static void postForm(Client client) throws IOException {
        //请求URL
        URL url = new URL("http://host:port/demo/post/form");

        Map<String, String> bodyParam = new HashMap<String, String>();
        bodyParam.put("FormParamKey", "FormParamValue");

        Map<String, String> headers = new HashMap<String, String>();
        //（可选）响应内容序列化格式,默认application/json,目前仅支持application/json
        headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "application/json");

        Request request = new Request(Method.POST_FORM, url, headers, CUSTOM_HEADERS_TO_SIGN_PREFIX);
        request.setFormBody(bodyParam);

        //调用服务端
        HttpResponse response = client.execute(request);
        print(response);
    }

    /**
     * HTTP POST 字符串
     *
     * @param client
     *            Client，提供HTTP访问服务
     * @throws IOException
     *             IO/网络出错
     */
    public static void postString(Client client) throws IOException {
        //请求URL
        URL url = new URL("http://host:port/demo/post/string");
        //Body内容
        String body = "demo string body content";

        Map<String, String> headers = new HashMap<String, String>();
        //（可选）响应内容序列化格式,默认application/json,目前仅支持application/json
        headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "application/json");
        //（可选）Body MD5,服务端会校验Body内容是否被篡改,建议Body非Form表单时添加此Header
        headers.put(HttpHeader.HTTP_HEADER_CONTENT_MD5, MessageDigestUtil.base64AndMD5(body));
        //（POST/PUT请求必选）请求Body内容格式
        headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, ContentType.CONTENT_TYPE_TEXT);

        Request request = new Request(Method.POST_STRING, url, headers, CUSTOM_HEADERS_TO_SIGN_PREFIX);
        request.setStringBody(body);

        //调用服务端
        HttpResponse response = client.execute(request);
        print(response);
    }

    /**
     * HTTP POST 字节数组
     *
     * @param client
     *            Client，提供HTTP访问服务
     * @throws IOException
     *             IO/网络出错
     */
    public static void postBytes(Client client) throws IOException {
        //请求URL
        URL url = new URL("http://host:port/demo/post/bytes");
        //Body内容
        byte[] bytesBody = "demo bytes body content".getBytes(Constants.ENCODING);

        Map<String, String> headers = new HashMap<String, String>();
        //（可选）响应内容序列化格式,默认application/json,目前仅支持application/json
        headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "application/json");
        //（可选）Body MD5,服务端会校验Body内容是否被篡改,建议Body非Form表单时添加此Header
        headers.put(HttpHeader.HTTP_HEADER_CONTENT_MD5, MessageDigestUtil.base64AndMD5(bytesBody));
        //（POST/PUT请求必选）请求Body内容格式
        headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, ContentType.CONTENT_TYPE_TEXT);

        Request request = new Request(Method.POST_BYTES, url, headers, CUSTOM_HEADERS_TO_SIGN_PREFIX);
        request.setBytesBody(bytesBody);

        //调用服务端
        HttpResponse response = client.execute(request);
        print(response);
    }

    /**
     * HTTP PUT 表单
     *
     * @param client
     *            Client，提供HTTP访问服务
     * @throws IOException
     *             IO/网络出错
     */
    public static void putForm(Client client) throws IOException {
        //请求URL
        URL url = new URL("http://host:port/demo/put/form");

        Map<String, String> bodyParam = new HashMap<String, String>();
        bodyParam.put("FormParamKey", "FormParamValue");

        Map<String, String> headers = new HashMap<String, String>();
        //（可选）响应内容序列化格式,默认application/json,目前仅支持application/json
        headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "application/json");

        Request request = new Request(Method.PUT_FORM, url, headers, CUSTOM_HEADERS_TO_SIGN_PREFIX);
        request.setFormBody(bodyParam);

        //调用服务端
        HttpResponse response = client.execute(request);
        print(response);
    }

    /**
     * HTTP PUT 字符串
     *
     * @param client
     *            Client，提供HTTP访问服务
     * @throws IOException
     *             IO/网络出错
     */
    public static void putString(Client client) throws IOException {
        //请求URL
        URL url = new URL("http://host:port/demo/put/string");
        //Body内容
        String body = "demo string body content";

        Map<String, String> headers = new HashMap<String, String>();
        //（可选）响应内容序列化格式,默认application/json,目前仅支持application/json
        headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "application/json");
        //（可选）Body MD5,服务端会校验Body内容是否被篡改,建议Body非Form表单时添加此Header
        headers.put(HttpHeader.HTTP_HEADER_CONTENT_MD5, MessageDigestUtil.base64AndMD5(body));
        //（POST/PUT请求必选）请求Body内容格式
        headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, ContentType.CONTENT_TYPE_TEXT);

        Request request = new Request(Method.PUT_STRING, url, headers, CUSTOM_HEADERS_TO_SIGN_PREFIX);
        request.setStringBody(body);

        //调用服务端
        HttpResponse response = client.execute(request);
        print(response);
    }

    /**
     * HTTP PUT 字节数组
     *
     * @param client
     *            Client，提供HTTP访问服务
     * @throws IOException
     *             IO/网络出错
     */
    public static void putBytesBody(Client client) throws IOException {
        //请求URL
        URL url = new URL("http://host:port/demo/put/bytes");
        //Body内容
        byte[] bytesBody = "demo bytes body content".getBytes(Constants.ENCODING);

        Map<String, String> headers = new HashMap<String, String>();
        //（可选）响应内容序列化格式,默认application/json,目前仅支持application/json
        headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "application/json");
        //（可选）Body MD5,服务端会校验Body内容是否被篡改,建议Body非Form表单时添加此Header
        headers.put(HttpHeader.HTTP_HEADER_CONTENT_MD5, MessageDigestUtil.base64AndMD5(bytesBody));
        //（POST/PUT请求必选）请求Body内容格式
        headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, ContentType.CONTENT_TYPE_TEXT);

        Request request = new Request(Method.PUT_BYTES, url, headers, CUSTOM_HEADERS_TO_SIGN_PREFIX);
        request.setBytesBody(bytesBody);

        //调用服务端
        HttpResponse response = client.execute(request);
        print(response);
    }

    /**
     * HTTP DELETE
     * 
     * @param client
     *            Client，提供HTTP访问服务
     * @throws IOException
     *             IO/网络出错
     */
    public static void delete(Client client) throws IOException {
        //请求URL
        URL url = new URL("http://host:port/demo/delete");

        Map<String, String> headers = new HashMap<String, String>();
        //（可选）响应内容序列化格式,默认application/json,目前仅支持application/json
        headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "application/json");

        Request request = new Request(Method.DELETE, url, headers, CUSTOM_HEADERS_TO_SIGN_PREFIX);

        //调用服务端
        HttpResponse response = client.execute(request);
        print(response);
    }

    /**
     * 打印Response
     *
     * @param response
     *            HTTP响应信息包装类
     * @throws IOException
     *             IO/网络出错
     */
    private static void print(HttpResponse response) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("Status line: ").append(response.getStatusLine().getStatusCode()).append(Constants.LF);
        for (Header header : response.getAllHeaders()) {
            sb.append(header.toString()).append(Constants.LF);
        }
        sb.append(EntityUtils.toString(response.getEntity(), Constants.ENCODING)).append(Constants.LF);
        System.out.println(sb.toString());
    }

}
