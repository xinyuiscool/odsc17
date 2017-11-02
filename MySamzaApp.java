/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package samza.examples.cookbook;

import org.apache.samza.application.StreamApplication;
import org.apache.samza.config.Config;
import org.apache.samza.operators.KV;
import org.apache.samza.operators.MessageStream;
import org.apache.samza.operators.OutputStream;
import org.apache.samza.operators.StreamGraph;
import org.apache.samza.operators.windows.Windows;
import org.apache.samza.serializers.IntegerSerde;
import org.apache.samza.serializers.JsonSerdeV2;
import org.apache.samza.serializers.KVSerde;
import org.apache.samza.serializers.StringSerde;
import samza.examples.cookbook.data.PageView;

import java.time.Duration;

public class MySamzaApp implements StreamApplication {
    private static final String INPUT = "pageview-join-input";
    private static final String OUTPUT = "my-output";

    @Override
    public void init(StreamGraph streamGraph, Config config) {
        MessageStream<KV<String, PageView>> pageViewInput =
                streamGraph.getInputStream(INPUT, KVSerde.of(new StringSerde(), new JsonSerdeV2<>(PageView.class)));
        OutputStream<KV<String, String>> myOutput =
                streamGraph.getOutputStream(OUTPUT, KVSerde.of(new StringSerde(), new StringSerde()));

        pageViewInput
                .map(kv -> kv.getValue().country)
                .window(Windows.keyedTumblingWindow(c -> c,
                        Duration.ofSeconds(5),
                        () -> 0,
                        (m, v) -> v + 1,
                        new StringSerde(),
                        new IntegerSerde()),
                        "my-window")
                .map(pane -> KV.of(pane.getKey().getKey(), String.valueOf(pane.getMessage())))
                .sendTo(myOutput);
    }
}
