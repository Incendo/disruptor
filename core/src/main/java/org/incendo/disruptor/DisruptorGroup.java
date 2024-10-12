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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import org.apiguardian.api.API;
import org.incendo.disruptor.trigger.DisruptionTrigger;

/**
 * A disruptor configuration group.
 *
 * <p><b>Note:</b> This interface should not be implemented. An instance should be built using {@link #builder()}.</p>

 * @since 1.0.0
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public interface DisruptorGroup {

    /**
     * Creates a new {@link DisruptorGroup} builder. The builder is mutable.
     *
     * @return a mutable builder
     */
    static Builder builder() {
        return new Builder();
    }

    /**
     * Returns the disruption configurations.
     *
     * @return the configurations
     */
    List<DisruptionConfig> configurations();

    @API(status = API.Status.STABLE, since = "1.0.0")
    final class Builder {

        private final List<DisruptionConfig> configurations = new ArrayList<>();

        private Builder() {
        }

        /**
         * Adds the given {@code config} to the group.
         *
         * @param config the config
         * @return {@code this}
         */
        public Builder config(final DisruptionConfig config) {
            Objects.requireNonNull(config, "config");
            this.configurations.add(config);
            return this;
        }

        /**
         * Adds the given {@code config} to the group.
         *
         * @param config the config
         * @return {@code this}
         */
        public Builder config(final DisruptionConfig.Builder config) {
            return this.config(config.build());
        }

        /**
         * Adds a config to the group after letting the given {@code decorator} decorate the config builder.
         *
         * @param decorator the decorator
         * @return {@code this}
         */
        public Builder config(final Consumer<DisruptionConfig.Builder> decorator) {
            Objects.requireNonNull(decorator, "config");
            final DisruptionConfig.Builder builder = DisruptionConfig.builder();
            decorator.accept(builder);
            return this.config(builder);
        }

        /**
         * Adds a config to the group after letting the given {@code decorator} decorate the config builder.
         *
         * @param trigger the disruption trigger
         * @param decorator the decorator
         * @return {@code this}
         */
        public Builder config(final DisruptionTrigger trigger, final Consumer<DisruptionConfig.Builder> decorator) {
            Objects.requireNonNull(decorator, "config");
            final DisruptionConfig.Builder builder = DisruptionConfig.builder().trigger(trigger);
            decorator.accept(builder);
            return this.config(builder);
        }

        /**
         * Build a new {@link DisruptorGroup} instance using {@code this} builder.
         *
         * @return the disruptor group instance
         */
        public DisruptorGroup build() {
            return new DisruptorGroupImpl(List.copyOf(this.configurations));
        }
    }
}
