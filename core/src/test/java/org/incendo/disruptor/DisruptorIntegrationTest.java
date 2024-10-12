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
package org.incendo.disruptor;

import org.incendo.disruptor.trigger.DisruptionTrigger;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DisruptorIntegrationTest {

    @Test
    void exceptionPropagation() {
        // Arrange
        final Disruptor disruptor = Disruptor.builder()
                .group(
                        "test",
                        group -> group.config(config -> config.trigger(new AlwaysTrigger())
                                .throwException(ctx -> new RuntimeException("test"))
                        )
                )
                .build();

        // Act & Assert
        final RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> disruptor.disruptWithoutResult("test", () -> {})
        );
        assertThat(exception).hasMessageThat().isEqualTo("test");
    }

    @Test
    void resultPropagation() {
        // Arrange
        final Disruptor disruptor = Disruptor.builder()
                .group(
                        "test",
                        group -> group.config(config -> config.trigger(DisruptionTrigger.never())
                                .throwException(ctx -> new RuntimeException("test"))
                        )
                )
                .build();

        // Act
        final String result = disruptor.disrupt(
                "test",
                () -> "hello world"
        );

        // Assert
        assertThat(result).isEqualTo("hello world");
    }

    private static final class AlwaysTrigger implements DisruptionTrigger {

        @Override
        public boolean shouldTrigger(final DisruptorContext context) {
            return true;
        }
    }
}
