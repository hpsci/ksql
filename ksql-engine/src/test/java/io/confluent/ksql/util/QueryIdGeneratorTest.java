/*
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.confluent.ksql.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Before;
import org.junit.Test;

public class QueryIdGeneratorTest {

  private QueryIdGenerator generator;

  @Before
  public void setUp() throws Exception {
    generator = new QueryIdGenerator("");
  }

  @Test
  public void shouldGenerateMonotonicallyIncrementingIds() {
    assertThat(generator.getNextId(), is("0"));
    assertThat(generator.getNextId(), is("1"));
    assertThat(generator.getNextId(), is("2"));
  }

  @Test
  public void shouldGeneratePostFixedIdes() {
    // Given:
    generator = new QueryIdGenerator("_post");

    // Then:
    assertThat(generator.getNextId(), is("0_post"));
    assertThat(generator.getNextId(), is("1_post"));
    assertThat(generator.getNextId(), is("2_post"));
  }

  @Test
  public void shouldBeThreadSafe() {
    // Given:
    final int iterations = 10_000;

    // When:
    final Set<String> ids = IntStream.range(0, iterations).parallel()
        .mapToObj(idx -> generator.getNextId())
        .collect(Collectors.toSet());

    // Then:
    assertThat(ids, hasSize(iterations));
    assertThat(ids, hasItems("0", String.valueOf(iterations - 1)));
  }
}