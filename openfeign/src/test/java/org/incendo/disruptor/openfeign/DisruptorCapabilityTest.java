//
// MIT License
//
// Copyright (c) 2024 Incendo
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package org.incendo.disruptor.openfeign;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import feign.Feign;
import feign.RequestLine;
import org.incendo.disruptor.DisruptionMode;
import org.incendo.disruptor.Disruptor;
import org.incendo.disruptor.DisruptorGroup;
import org.incendo.disruptor.trigger.DisruptionTrigger;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WireMockTest
class DisruptorCapabilityTest {

    @Test
    void testBefore() {
        // Arrange
        final Disruptor disruptor = Disruptor.builder()
                .group(
                        "feign",
                        DisruptorGroup.builder().config(config -> config.mode(DisruptionMode.BEFORE)
                                .trigger(DisruptionTrigger.random(1.0f))
                                .throwException(ctx -> new RuntimeException("test"))
                        ).build()
                ).build();
        final TestTarget client = Feign.builder()
                .addCapability(DisruptorCapability.of(disruptor, "feign"))
                .target(TestTarget.class, "http://localhost");

        // Act & Assert
        final RuntimeException exception = assertThrows(
                RuntimeException.class,
                client::get
        );
        assertThat(exception).hasMessageThat().isEqualTo("test");
    }

    @Test
    void testAfter(final WireMockRuntimeInfo wireMockRuntimeInfo) {
        // Arrange
        WireMock.stubFor(WireMock.get("/").willReturn(WireMock.jsonResponse("\"yay\"", 200)));

        final Disruptor disruptor = Disruptor.builder()
                .group(
                        "feign",
                        DisruptorGroup.builder().config(config -> config.mode(DisruptionMode.AFTER)
                                .trigger(DisruptionTrigger.random(1.0f))
                                .throwException(ctx -> new RuntimeException("test"))
                        ).build()
                ).build();
        final TestTarget client = Feign.builder()
                .addCapability(DisruptorCapability.of(disruptor, "feign"))
                .target(TestTarget.class, wireMockRuntimeInfo.getHttpBaseUrl());

        // Act & Assert
        final RuntimeException exception = assertThrows(
                RuntimeException.class,
                client::get
        );
        assertThat(exception).hasMessageThat().isEqualTo("test");
    }

    interface TestTarget {

        @RequestLine("GET /")
        String get();
    }
}
