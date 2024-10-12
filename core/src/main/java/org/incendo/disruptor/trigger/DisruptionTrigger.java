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
import java.util.Objects;
import org.apiguardian.api.API;
import org.incendo.disruptor.DisruptorContext;

/**
 * Trigger that determines whether a disruption should be actived.
 *
 * @since 1.0.0
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public interface DisruptionTrigger {

    /**
     * Returns a trigger that activates every time the number of invocations reaches {@code targetInvocations}, resetting
     * each time it does.
     *
     * @param targetInvocations target invocation count
     * @return the trigger
     */
    static DisruptionTrigger counting(final int targetInvocations) {
        return new CountingTrigger(targetInvocations);
    }

    /**
     * Returns a trigger that activates randomly based on the given {@code chance} in the range 0 to 1.
     *
     * @param chance chance in the range [0, 1]
     * @return the trigger
     */
    static DisruptionTrigger random(final float chance) {
        return new RandomTrigger(chance);
    }

    /**
     * Returns a trigger that never activates.
     *
     * @return the trigger
     */
    static DisruptionTrigger never() {
        return new NeverTrigger();
    }

    /**
     * Returns whether the disruption should trigger.
     *
     * @param context current context
     * @return {@code true} if the disruption should trigger, else {@code false}
     */
    boolean shouldTrigger(DisruptorContext context);

    /**
     * Returns a variant of {@code this} trigger that will remember the activation
     * status for a period of time after {@code this} trigger has activated.
     *
     * @param duration duration the trigger should be active for
     * @return the lasting trigger
     */
    default DisruptionTrigger lasting(final Duration duration) {
        Objects.requireNonNull(duration, "duration");
        return new LastingTrigger(duration, this);
    }
}
