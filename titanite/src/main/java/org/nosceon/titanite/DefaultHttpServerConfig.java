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

/**
 * @author Johan Siebens
 */
@Deprecated
public final class DefaultHttpServerConfig implements HttpServerConfig {

    public static DefaultHttpServerConfig config() {
        return new DefaultHttpServerConfig();
    }

    private Integer port;

    private Integer ioWorkerCount;

    private Long maxRequestSize;

    private Long maxMultipartRequestSize;

    public DefaultHttpServerConfig port(int port) {
        this.port = port;
        return this;
    }

    public DefaultHttpServerConfig ioWorkerCount(int ioWorkerCount) {
        this.ioWorkerCount = ioWorkerCount;
        return this;
    }

    public DefaultHttpServerConfig maxRequestSize(long maxRequestSize) {
        this.maxRequestSize = maxRequestSize;
        return this;
    }

    public DefaultHttpServerConfig maxMultipartRequestSize(Long maxMultipartRequestSize) {
        this.maxMultipartRequestSize = maxMultipartRequestSize;
        return this;
    }

    @Override
    public int getPort() {
        if (port == null) {
            String property = System.getProperty("titanite.port");
            if (property != null) {
                return Integer.valueOf(property);
            }
            return DEFAULT_PORT;
        }
        return port;
    }

    @Override
    public int getIoWorkerCount() {
        if (ioWorkerCount == null) {
            String property = System.getProperty("titanite.io-worker-count");
            if (property != null) {
                return Integer.valueOf(property);
            }
            return DEFAULT_IO_WORKER_COUNT;
        }
        return ioWorkerCount;
    }

    @Override
    public long getMaxRequestSize() {
        if (maxRequestSize == null) {
            String property = System.getProperty("titanite.max-request-size");
            if (property != null) {
                return Long.valueOf(property);
            }
            return DEFAULT_MAX_REQUEST_SIZE;
        }
        return maxRequestSize;
    }

    @Override
    public long getMaxMultipartRequestSize() {
        if (maxMultipartRequestSize == null) {
            String property = System.getProperty("titanite.max-multipart-request-size");
            if (property != null) {
                return Long.valueOf(property);
            }
            return DEFAULT_MAX_MULTI_PART_REQUEST_SIZE;
        }
        return maxMultipartRequestSize;
    }

}
