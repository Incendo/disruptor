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

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.apiguardian.api.API;
import org.incendo.disruptor.disruption.Disruption;
import org.incendo.disruptor.trigger.DisruptionTrigger;

@API(status = API.Status.STABLE, since = "1.0.0")
public interface DisruptionConfig {

    /**
     * Returns a new mutable {@link DisruptionConfig} builder.
     *
     * @return the builder
     */
    static Builder builder() {
        return new Builder();
    }

    /**
     * Returns the disruption trigger.
     *
     * @return the trigger
     */
    DisruptionTrigger trigger();

    /**
     * Return the disruptions, in the order they will be invoked.
     *
     * @return the disruptions
     */
    List<Disruption> disruptions();

    /**
     * Returns the disruption mode.
     *
     * @return the mode
     */
    DisruptionMode mode();

    @API(status = API.Status.STABLE, since = "1.0.0")
    final class Builder {

        private final List<Disruption> disruptions = new ArrayList<>();
        private DisruptionTrigger trigger = DisruptionTrigger.never();
        private DisruptionMode mode = DisruptionMode.BEFORE;

        private Builder() {
        }

        /**
         * Sets the trigger.
         *
         * @param trigger new trigger
         * @return {@code this}
         */
        public Builder trigger(final DisruptionTrigger trigger) {
            this.trigger = Objects.requireNonNull(trigger, "trigger");
            return this;
        }

        /**
         * Adds the given {@code disruptions}.
         *
         * @param disruptions disruptions to add
         * @return {@code this}
         */
        public Builder disruptions(final Disruption... disruptions) {
            Objects.requireNonNull(disruptions, "disruptions");
            for (final Disruption disruption : disruptions) {
                Objects.requireNonNull(disruption, "disruption");
            }
            this.disruptions.addAll(Arrays.asList(disruptions));
            return this;
        }

        /**
         * Adds the given {@code disruptions}.
         *
         * @param disruptions disruptions to add
         * @return {@code this}
         */
        public Builder disruptions(final List<Disruption> disruptions) {
            Objects.requireNonNull(disruptions, "disruptions");
            for (final Disruption disruption : disruptions) {
                Objects.requireNonNull(disruption, "disruption");
            }
            this.disruptions.addAll(disruptions);
            return this;
        }

        /**
         * Adds a {@link Disruption#delaying(Duration)} disruption.
         *
         * @param duration duration to delay for
         * @return {@code this}
         */
        public Builder delay(final Duration duration) {
            return this.disruptions(Disruption.delaying(duration));
        }

        /**
         * Adds a {@link Disruption#throwing(Function)} disruption.
         *
         * @param generator throwable generator
         * @return {@code this}
         */
        public Builder throwException(final Function<DisruptorContext, Throwable> generator) {
            return this.disruptions(Disruption.throwing(generator));
        }

        /**
         * Sets the disruption mode to the given {@code mode}.
         *
         * @param mode new mode
         * @return {@code this}
         */
        public Builder mode(final DisruptionMode mode) {
            this.mode = Objects.requireNonNull(mode, "mode");
            return this;
        }

        /**
         * Build a new {@link DisruptionConfig} instance using {@code this} builder.
         *
         * @return the config instance
         */
        public DisruptionConfig build() {
            return new DisruptionConfigImpl(this.trigger, List.copyOf(this.disruptions), this.mode);
        }
    }
}
