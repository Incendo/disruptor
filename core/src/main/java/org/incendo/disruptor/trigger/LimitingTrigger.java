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
final class LimitingTrigger implements DisruptionTrigger {

    private static final Logger LOGGER = LoggerFactory.getLogger(LastingTrigger.class);

    private final Lock lock = new ReentrantLock();
    private final Duration period;
    private final int limit;
    private final DisruptionTrigger trigger;

    LimitingTrigger(
            final int limit,
            final Duration period,
            final DisruptionTrigger trigger
    ) {
        this.limit = limit;
        this.period = Objects.requireNonNull(period, "period");
        this.trigger = Objects.requireNonNull(trigger, "trigger");
    }

    private Instant limitEnd = Instant.EPOCH;
    private int count;

    @Override
    public boolean shouldTrigger(final DisruptorContext context) {
        this.lock.lock();
        try {
            if (Instant.now().isAfter(this.limitEnd)) {
                this.limitEnd = Instant.now().plus(this.period);
                this.count = 0;
            }

            if (this.count >= this.limit) {
                return false;
            }

            final boolean shouldTrigger = this.trigger.shouldTrigger(context);
            if (shouldTrigger) {
                this.count++;
            }

            if (this.count >= this.limit) {
                LOGGER.info("Limit of disruption for group {} reached and will reset at {}", context.group(), this.limitEnd);
            }

            return shouldTrigger;
        } finally {
            this.lock.unlock();
        }
    }
}
