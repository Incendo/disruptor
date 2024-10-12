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
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apiguardian.api.API;
import org.incendo.disruptor.DisruptorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@API(status = API.Status.INTERNAL, since = "1.0.0")
final class LastingTrigger implements DisruptionTrigger {

    private static final Logger LOGGER = LoggerFactory.getLogger(LastingTrigger.class);

    private final Lock lock = new ReentrantLock();
    private final Duration duration;
    private final DisruptionTrigger trigger;

    private Instant disruptionEnd;

    LastingTrigger(final Duration duration, final DisruptionTrigger trigger) {
        this.duration = Objects.requireNonNull(duration, "duration");
        this.trigger = Objects.requireNonNull(trigger, "trigger");
        this.disruptionEnd = Instant.EPOCH;
    }

    @Override
    public boolean shouldTrigger(final DisruptorContext context) {
        this.lock.lock();
        try {
            if (Instant.now().isBefore(this.disruptionEnd)) {
                return true;
            }
            if (!this.trigger.shouldTrigger(context)) {
                return false;
            }
            this.disruptionEnd = Instant.now().plus(this.duration);
            LOGGER.info("Lasting disruption for group {} started and will end at {}", context.group(), this.disruptionEnd);
            return true;
        } finally {
            this.lock.unlock();
        }
    }
}
