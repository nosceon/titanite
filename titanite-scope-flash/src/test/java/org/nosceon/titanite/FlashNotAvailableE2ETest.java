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

import org.junit.Test;
import org.nosceon.titanite.exception.FlashNotAvailableException;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.nosceon.titanite.Method.GET;
import static org.nosceon.titanite.Response.ok;
import static org.nosceon.titanite.ExceptionsFilter.onException;
import static org.nosceon.titanite.scope.Flash.flash;
import static org.nosceon.titanite.scope.FlashFilter.DEFAULT_FLASH_COOKIE_NAME;

/**
* @author Johan Siebens
*/
public class FlashNotAvailableE2ETest extends AbstractE2ETest {

    @Override
    protected Shutdownable configureAndStartHttpServer(HttpServer server) throws Exception {
        return
            server
                .setFilter(
                    onException().match(FlashNotAvailableException.class, () -> ok().text("Flash not available!!"))
                )
                .register(GET, "/flash", req -> ok().text(flash(req).getString("name")).toFuture())
                .start();
    }

    @Test
    public void test() {
        given()
            .cookie(DEFAULT_FLASH_COOKIE_NAME, "\"name=ipsum\"")
            .expect().statusCode(200).body(equalTo("Flash not available!!")).when().get(uri("/flash"));
    }

}
