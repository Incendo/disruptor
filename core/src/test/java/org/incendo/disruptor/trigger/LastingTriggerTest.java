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
package org.incendo.disruptor.trigger;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import org.incendo.disruptor.DisruptorContext;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class LastingTriggerTest {

    @Test
    void ShouldTrigger_OnceTrigger_LastsForDuration() {
        // Arrange
        final AtomicBoolean triggered = new AtomicBoolean(false);
        final DisruptionTrigger baseTrigger = context -> triggered.get();
        final DisruptionTrigger trigger = baseTrigger.lasting(Duration.ofMinutes(5L));
        final DisruptorContext context = DisruptorContext.of("test" /* group */);

        // Act & Assert
        assertThat(trigger.shouldTrigger(context)).isFalse();
        triggered.set(true);
        assertThat(trigger.shouldTrigger(context)).isTrue();
        triggered.set(false);
        assertThat(trigger.shouldTrigger(context)).isTrue();
    }
}
