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

import org.junit.After;
import org.junit.Before;

import java.net.ServerSocket;

/**
 * @author Johan Siebens
 */
public abstract class AbstractE2ETest {

    private int port;

    private Shutdownable shutdownable;

    @Before
    public void setUpHttpServer() throws Exception {
        this.port = findFreePort();
        this.shutdownable = configureAndStartHttpServer(newServer(port));
    }

    @After
    public void tearDownHttpServer() {
        this.shutdownable.stop();
    }

    protected abstract Shutdownable configureAndStartHttpServer(HttpServer server) throws Exception;

    protected String uri(String path) {
        return "http://localhost:" + port + path;
    }

    protected String ws(String path) {
        return "ws://localhost:" + port + path;
    }

    private int findFreePort() {
        int port;
        try {
            ServerSocket socket = new ServerSocket(0);
            port = socket.getLocalPort();
            socket.close();
        }
        catch (Exception e) {
            port = -1;
        }
        return port;
    }

    private HttpServer newServer(int port) {
        return new HttpServer(new DefaultHttpServerConfig().port(port).ioWorkerCount(2).maxRequestSize(maxRequestSize()));
    }

    protected long maxRequestSize() {
        return HttpServerConfig.DEFAULT_MAX_REQUEST_SIZE;
    }

}
