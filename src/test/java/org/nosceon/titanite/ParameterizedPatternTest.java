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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @author Johan Siebens
 */
public class ParameterizedPatternTest {

    @Test
    public void testParameterizedPattern() {
        ParameterizedPattern pattern = new ParameterizedPattern("/hello/:name/id/:id");
        ParameterizedPattern.Matcher matcher = pattern.matcher("/hello/world/id/123");
        assertThat(matcher.matches(), is(true));
        assertThat(matcher.parameters().size(), equalTo(2));
        assertThat(matcher.parameters().get("name"), equalTo("world"));
        assertThat(matcher.parameters().get("id"), equalTo("123"));

        assertThat(pattern.matcher("/lorem/ipsum").matches(), is(false));
    }

    @Test
    public void testMoreThanOneSegment() {
        ParameterizedPattern pattern = new ParameterizedPattern("/hello/:name/id/*path");
        ParameterizedPattern.Matcher matcher = pattern.matcher("/hello/world/id/123/567/89");
        assertThat(matcher.matches(), is(true));
        assertThat(matcher.parameters().size(), equalTo(2));
        assertThat(matcher.parameters().get("name"), equalTo("world"));
        assertThat(matcher.parameters().get("path"), equalTo("123/567/89"));

        assertThat(pattern.matcher("/lorem/ipsum").matches(), is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMultSegmentCanOnlyBeSetOnEnd() {
        new ParameterizedPattern("/hello/*name/id/*path");
    }

    @Test
    public void testSimplePattern() {
        ParameterizedPattern pattern = new ParameterizedPattern("/hello/world");
        ParameterizedPattern.Matcher matcher = pattern.matcher("/hello/world");
        assertThat(matcher.matches(), is(true));
        assertThat(matcher.parameters().size(), equalTo(0));

        assertThat(pattern.matcher("/lorem/ipsum").matches(), is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIdentifierOccursMoreThanOnce() {
        new ParameterizedPattern("/hello/:name/id/:name");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIdentifierOccursMoreThanOnceWithMultiSegment() {
        new ParameterizedPattern("/hello/:name/id/*name");
    }

}