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
import java.io.IOException;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.nosceon.titanite.Method.POST;

/**
 * @author Johan Siebens
 */
public class FileUploadTest extends AbstractE2ETest {

    private static final String TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.";

    private Shutdownable shutdownable;

    private int port;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File uploadFolder;

    @Before
    public void setUp() throws IOException {
        uploadFolder = temporaryFolder.newFolder("upload");

        port = findFreePort();
        shutdownable =
            newServer(port)
                .register(POST, "/post", (r) -> {
                    r.body.asForm().getMultiPart("file").ifPresent(mp -> mp.renameTo(new File(uploadFolder, mp.filename())));
                    return ok().body(r.body.asForm().getString("lorem")).toFuture();
                })
                .start();
    }

    @After
    public void tearDown() {
        shutdownable.stop();
    }

    @Test
    public void test() throws IOException {
        File file = temporaryFolder.newFile("hello1.txt");
        Files.write(TEXT, file, Charsets.UTF_8);

        given()
            .formParam("lorem", "ipsum")
            .multiPart("file", file).expect().statusCode(200).body(equalTo("ipsum")).when().post(uri(port, "/post"));

        File uploadedFile = new File(uploadFolder, "hello1.txt");

        assertThat(uploadedFile.exists(), is(true));
        assertThat(Files.toString(file, Charsets.UTF_8), equalTo(TEXT));
    }

}
