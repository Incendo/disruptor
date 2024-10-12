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
package org.incendo.disruptor.disruption;

import org.incendo.disruptor.DisruptionException;
import org.incendo.disruptor.DisruptorContext;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ThrowingTest {

    @Test
    void Trigger_RuntimeException_ForwardsException() {
        // Arrange
        final Exception exception = new RuntimeException();
        final Disruption disruption = Disruption.throwing(ctx -> exception);

        // Act & Assert
        final Exception result = assertThrows(
                RuntimeException.class,
                () -> disruption.trigger(DisruptorContext.of("group"))
        );
        assertThat(result).isEqualTo(exception);
    }

    @Test
    void Trigger_Throwable_WrapsException() {
        // Arrange
        final Throwable throwable = new Throwable();
        final Disruption disruption = Disruption.throwing(ctx -> throwable);

        // Act & Assert
        final Exception result = assertThrows(
                DisruptionException.class,
                () -> disruption.trigger(DisruptorContext.of("group"))
        );
        assertThat(result).hasCauseThat().isEqualTo(throwable);
    }
}
