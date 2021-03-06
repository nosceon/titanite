/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nosceon.titanite;

import javax.activation.MimetypesFileTypeMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.CharBuffer;
import java.security.Key;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;

/**
 * @author Johan Siebens
 */
public class Utils {

    private static final String UTF_8 = "UTF-8";

    private static MimetypesFileTypeMap MIME_TYPES;

    static {
        try {
            MIME_TYPES = new MimetypesFileTypeMap(getResource("META-INF/mime.types").openStream());
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> T checkNotNull(T value, String errorMessage) {
        return checkNotNull(value, () -> new IllegalArgumentException(errorMessage));
    }

    public static <T> T checkNotNull(T value, Supplier<RuntimeException> error) {
        if (value != null) {
            return value;
        }
        throw error.get();
    }

    public static String checkNotEmpty(String value, String errorMessage) {
        if (isNotEmpty(value)) {
            return value;
        }
        throw new IllegalArgumentException(errorMessage);
    }

    public static boolean isNotEmpty(String value) {
        return (value != null && value.trim().length() > 0);
    }

    public static String trimLeading(char c, String value) {
        if (value.charAt(0) == c) {
            return value.substring(1);
        }
        return value;
    }

    public static String trimTrailing(char c, String value) {
        int end = value.length() - 1;
        if (value.charAt(end) == c) {
            return value.substring(0, end);
        }
        return value;
    }

    public static String trim(char c, String value) {
        return trimTrailing(c, trimLeading(c, value));
    }

    public static String padStart(String string, int minLength, char padChar) {
        if (string.length() >= minLength) {
            return string;
        }
        StringBuilder sb = new StringBuilder(minLength);
        for (int i = string.length(); i < minLength; i++) {
            sb.append(padChar);
        }
        sb.append(string);
        return sb.toString();
    }

    public static String padEnd(String string, int minLength, char padChar) {
        if (string.length() >= minLength) {
            return string;
        }
        StringBuilder sb = new StringBuilder(minLength);
        sb.append(string);
        for (int i = string.length(); i < minLength; i++) {
            sb.append(padChar);
        }
        return sb.toString();
    }

    public static String toString(Readable r) throws IOException {
        StringBuilder sb = new StringBuilder();
        CharBuffer buf = CharBuffer.allocate(0x800);
        while (r.read(buf) != -1) {
            buf.flip();
            sb.append(buf);
            buf.clear();
        }
        return sb.toString();
    }

    public static long copy(InputStream from, OutputStream to) throws IOException {
        byte[] buf = new byte[0x1000];
        long total = 0;
        while (true) {
            int r = from.read(buf);
            if (r == -1) {
                break;
            }
            to.write(buf, 0, r);
            total += r;
        }
        return total;
    }

    public static MediaType getMediaTypeFromFileName(String name) {
        return MediaType.valueOf(MIME_TYPES.getContentType(name));
    }

    public static URL getResource(String name) {
        URL url = null;

        ClassLoader ccl = Thread.currentThread().getContextClassLoader();

        if (ccl != null) {
            url = ccl.getResource(name);
        }

        if (url == null) {
            ClassLoader cl = Titanite.class.getClassLoader();
            if (cl != null && cl != ccl) {
                url = cl.getResource(name);
            }
        }

        if (url == null) {
            url = ClassLoader.getSystemResource(name);
        }

        return url;
    }

    public static String serialize(Map<String, String> values) {
        return
            values.entrySet()
                .stream()
                .filter(e -> isNotEmpty(e.getValue()))
                .map(e -> urlencode(e.getKey(), e.getValue()))
                .reduce("", (s, s2) -> s.length() == 0 ? s2 : s + '&' + s2);
    }

    public static Map<String, String> deserialize(String values) {
        return
            ofNullable(values)
                .filter(Utils::isNotEmpty)
                .map(v -> {
                    Map<String, String> result = new LinkedHashMap<>();
                    String[] split = v.split("&");
                    for (String s : split) {
                        String[] keyValue = s.split("=");
                        result.put(
                            urldecode(keyValue[0]),
                            urldecode(keyValue[1])
                        );
                    }
                    return result;
                })
                .orElseGet(Collections::emptyMap);
    }

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    public static String sign(String secret, String message) {
        return
            callUnchecked(() -> {
                Key secretKey = new SecretKeySpec(secret.getBytes(UTF_8), HMAC_SHA1_ALGORITHM);
                Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
                mac.init(secretKey);
                byte[] bytes = mac.doFinal(message.getBytes(UTF_8));
                return encodeHexString(bytes);
            });
    }

    public static boolean verify(String secret, String signature, String message) {
        String a = signature;
        String b = sign(secret, message);

        if (a.length() != b.length()) {
            return false;
        }
        else {
            int equal = 0;
            for (int i = 0; i < a.length(); i++) {
                equal |= a.charAt(i) ^ b.charAt(i);
            }
            return equal == 0;
        }
    }

    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static String encodeHexString(final byte[] data) {
        final int l = data.length;
        final char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS[0x0F & data[i]];
        }
        return new String(out);
    }

    private static String urlencode(String key, String value) {
        return callUnchecked(() -> URLEncoder.encode(key, UTF_8) + '=' + URLEncoder.encode(value, UTF_8));
    }

    private static String urldecode(String value) {
        return callUnchecked(() -> URLDecoder.decode(value, UTF_8));
    }

    public static <T> T callUnchecked(Callable<T> callable) {
        try {
            return callable.call();
        }
        catch (InternalRuntimeException e1) {
            throw e1;
        }
        catch (Exception e11) {
            throw new InternalRuntimeException(e11);
        }
    }

    public static void runUnchecked(Action runnable) {
        try {
            runnable.run();
        }
        catch (InternalRuntimeException e1) {
            throw e1;
        }
        catch (Exception e11) {
            throw new InternalRuntimeException(e11);
        }
    }

    @FunctionalInterface
    public static interface Action {

        void run() throws Exception;

    }

}
