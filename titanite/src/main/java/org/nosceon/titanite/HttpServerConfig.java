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
public interface HttpServerConfig {

    int DEFAULT_PORT = 8080;

    int DEFAULT_IO_WORKER_COUNT = Runtime.getRuntime().availableProcessors() * 2;

    long DEFAULT_MAX_REQUEST_SIZE = 1024 * 128; // 128K

    long DEFAULT_MAX_MULTI_PART_REQUEST_SIZE = -1; // unbounded

    int getPort();

    int getIoWorkerCount();

    long getMaxRequestSize();

    long getMaxMultipartRequestSize();

}
