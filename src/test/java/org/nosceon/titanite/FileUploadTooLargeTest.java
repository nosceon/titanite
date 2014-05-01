/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nosceon.titanite;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static com.jayway.restassured.RestAssured.given;
import static org.nosceon.titanite.Method.POST;

/**
 * @author Johan Siebens
 */
public class FileUploadTooLargeTest extends AbstractE2ETest {

    private static final String TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.";

    private Shutdownable shutdownable;

    private int port;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() {

        port = findFreePort();
        shutdownable =
            new HttpServer(new HttpServerConfig.Default().port(port).ioWorkerCount(2).maxRequestSize(5))
                .register(POST, "/post", (r) -> {
                    r.body.asForm();
                    return Responses.ok().toFuture();
                })
                .start();
    }

    @After
    public void tearDown() {
        shutdownable.stop();
    }

    @Test
    public void test() throws Exception {
        File file = temporaryFolder.newFile("hello1.txt");
        Files.write(TEXT, file, Charsets.UTF_8);

        given()
            .multiPart("file", file).expect().statusCode(413).when().post(uri(port, "/post"));
    }

}