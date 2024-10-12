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

@API(status = API.Status.STABLE, since = "1.0.0")
public final class DisruptorGroupBuilder {

    private final List<DisruptionConfig> configurations = new ArrayList<>();

    DisruptorGroupBuilder() {
    }

    /**
     * Adds the given {@code config} to the group.
     *
     * @param config the config
     * @return {@code this}
     */
    public DisruptorGroupBuilder config(final DisruptionConfig config) {
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
    public DisruptorGroupBuilder config(final DisruptionConfigBuilder config) {
        return this.config(config.build());
    }

    /**
     * Adds a config to the group after letting the given {@code decorator} decorate the config builder.
     *
     * @param decorator the decorator
     * @return {@code this}
     */
    public DisruptorGroupBuilder config(final Consumer<DisruptionConfigBuilder> decorator) {
        Objects.requireNonNull(decorator, "config");
        final DisruptionConfigBuilder builder = DisruptionConfig.builder();
        decorator.accept(builder);
        return this.config(builder);
    }

    /**
     * Adds a config to the group after letting the given {@code decorator} decorate the config builder.
     *
     * @param trigger   the disruption trigger
     * @param decorator the decorator
     * @return {@code this}
     */
    public DisruptorGroupBuilder config(final DisruptionTrigger trigger, final Consumer<DisruptionConfigBuilder> decorator) {
        Objects.requireNonNull(decorator, "config");
        final DisruptionConfigBuilder builder = DisruptionConfig.builder().trigger(trigger);
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
