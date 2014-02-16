package org.nosceon.titanite;

import io.netty.handler.codec.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;

import static org.nosceon.titanite.Responses.methodNotAllowed;

/**
 * @author Johan Siebens
 */
public final class Router {

    private static final Logger log = LoggerFactory.getLogger(Router.class);

    public static final RoutingResult METHOD_NOT_ALLOWED = new RoutingResult(Collections.emptyMap(), (r) -> methodNotAllowed());

    private final Map<ParameterizedPattern, Map<HttpMethod, Function<Request, Response>>> mapping = new LinkedHashMap<>();

    private final RoutingResult fallback;

    private final String id;

    public Router(String id, List<Filter<Request, Response, Request, Response>> filters, List<Routing<Request, Response>> routings, Function<Request, Response> fallback) {
        this.id = id;
        this.fallback = new RoutingResult(Collections.emptyMap(), fallback);
        for (Routing<Request, Response> r : routings) {
            add(filters, r.method(), r.pattern(), r.function());
        }
    }

    public RoutingResult find(HttpMethod method, String pattern) {
        for (Map.Entry<ParameterizedPattern, Map<HttpMethod, Function<Request, Response>>> entry : mapping.entrySet()) {
            ParameterizedPattern.Matcher matcher = entry.getKey().matcher(pattern);
            if (matcher.matches()) {
                Function<Request, Response> f = entry.getValue().get(method);
                return f != null ? new RoutingResult(matcher.parameters(), f) : METHOD_NOT_ALLOWED;
            }
        }
        return fallback;
    }

    private Router add(List<Filter<Request, Response, Request, Response>> filters, HttpMethod method, String pattern, Function<Request, Response> function) {
        ParameterizedPattern pp = new ParameterizedPattern(pattern);
        Map<HttpMethod, Function<Request, Response>> map = mapping.get(pp);
        if (map == null) {
            map = new HashMap<>();
            mapping.put(pp, map);
        }
        if (map.putIfAbsent(method, createFunction(filters, function)) == null) {
            log.info("Router [" + id + "] registered handler for " + method + " " + pattern);
        }
        return this;
    }

    public static Function<Request, Response> createFunction(List<Filter<Request, Response, Request, Response>> filters, Function<Request, Response> function) {
        if (filters.isEmpty()) {
            return function;
        }

        Filter<Request, Response, Request, Response> result = (r, f) -> f.apply(r);
        for (Filter<Request, Response, Request, Response> filter : filters) {
            result = result.andThen(filter);
        }
        return result.andThen(function);
    }

}