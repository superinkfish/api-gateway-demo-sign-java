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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import com.aliyun.api.gateway.demo.constant.Constants;

/**
 * 消息摘要工具<br>
 * 本工具对字符串或字节数组进行加密，首先采用MD5算法进行编码，然后使用Base64再次进行编码，最终得到加密后的字符串。
 * 
 * @author qiming.wqm 2016/06/24
 */
public class MessageDigestUtil {
    /**
     * 先进行MD5摘要再进行Base64编码获取摘要字符串
     *
     * @param str
     *            需要加密的字符串，不能为null
     * @return 加密后的字符串
     * @throws IllegalArgumentException
     *             如果参数str为null则抛出异常
     */
    public static String base64AndMD5(String str) {
        if (str == null) {
            throw new IllegalArgumentException("Parameter str cannot be null");
        }
        return base64AndMD5(str.getBytes(Constants.ENCODING));
    }

    /**
     * 先进行MD5摘要再进行Base64编码获取摘要字符串
     *
     * @param bytes
     *            需要加密的字节数组，不能为null
     * @return 加密后的字符串
     * @throws IllegalArgumentException
     *             如果参数bytes为null则抛出异常
     */
    public static String base64AndMD5(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("Parameter bytes cannot be null");
        }
        return Base64.encodeBase64String(DigestUtils.md5(bytes));
    }
}
