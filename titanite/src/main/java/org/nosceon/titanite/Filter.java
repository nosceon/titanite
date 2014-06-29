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

import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static org.nosceon.titanite.Controller.newController;

/**
 * @author Johan Siebens
 */
@FunctionalInterface
public interface Filter {

    default Filter andThen(Filter next) {
        return (fi, f) -> apply(fi, (i) -> next.apply(i, f));
    }

    default Function<Request, CompletionStage<Response>> andThen(Function<Request, CompletionStage<Response>> next) {
        return (i) -> apply(i, next);
    }

    default Controller andThen(Controller controller) {
        return newController(controller.get().stream().map(r -> new Route(r.method(), r.pattern(), (i) -> apply(i, r.function()))).collect(toList()));
    }

    CompletionStage<Response> apply(Request request, Function<Request, CompletionStage<Response>> function);

}