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

import io.netty.handler.codec.http.HttpHeaders;
import org.nosceon.titanite.body.Body;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;
import static org.nosceon.titanite.MediaType.ANY;
import static org.nosceon.titanite.MediaType.bestCandidate;

/**
 * @author Johan Siebens
 */
public final class Request {

    private final Method method;

    private final String path;

    private final HeaderParams headers;

    private final CookieParams cookies;

    private final QueryParams queryParams;

    private final PathParams pathParams;

    private final Body body;

    private final Attributes attributes;

    private final boolean secure;

    Request(boolean secure, Method method, String path, HeaderParams headers, CookieParams cookies, PathParams pathParams, QueryParams queryParams, Body body) {
        this.secure = secure;
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.cookies = cookies;
        this.queryParams = queryParams;
        this.pathParams = pathParams;
        this.body = body;
        this.attributes = new Attributes();
    }

    public Method method() {
        return method;
    }

    public String path() {
        return path;
    }

    public HeaderParams headers() {
        return headers;
    }

    public CookieParams cookies() {
        return cookies;
    }

    public QueryParams queryParams() {
        return queryParams;
    }

    public PathParams pathParams() {
        return pathParams;
    }

    public Body body() {
        return body;
    }

    public Attributes attributes() {
        return attributes;
    }

    public MediaType contentType() {
        return ofNullable(headers.getString(HttpHeaders.Names.CONTENT_TYPE)).map(MediaType::valueOf).orElse(null);
    }

    public List<MediaType> acceptableTypes() {
        return ofNullable(headers.getString(HttpHeaders.Names.ACCEPT)).map(MediaType::valuesOf).orElse(singletonList(ANY));
    }

    public MediaType acceptableType(Collection<MediaType> candidates) {
        return bestCandidate(acceptableTypes(), candidates);
    }

    public boolean accepts(MediaType mediaType) {
        return MediaType.accepts(acceptableTypes(), mediaType);
    }

    public List<AcceptableLanguage> acceptableLanguages() {
        return ofNullable(headers.getString(HttpHeaders.Names.ACCEPT_LANGUAGE)).map(AcceptableLanguage::valuesOf).orElse(singletonList(AcceptableLanguage.ANY));
    }

    public Locale acceptableLanguage(Collection<Locale> candidates) {
        return AcceptableLanguage.bestCandidate(acceptableLanguages(), candidates);
    }

    public boolean acceptsLanguauge(Locale locale) {
        return AcceptableLanguage.accepts(acceptableLanguages(), locale);
    }

    public String baseUri() {
        return protocol() + "://" + headers.getString(HttpHeaders.Names.HOST);
    }

    public String protocol() {
        return secure ? "https" : "http";
    }

    public Request withAttribute(String key, Object value) {
        this.attributes.set(key, value);
        return this;
    }

    boolean isSecure() {
        return secure;
    }

}
