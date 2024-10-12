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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import org.apiguardian.api.API;

/**
 * Builder for {@link Disruptor} instances. The builder should be constructed using {@link Disruptor#builder()}.
 *
 * @since 1.0.0
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public final class DisruptorBuilder {

    private final Map<String, DisruptorGroup> groups = new HashMap<>();

    DisruptorBuilder() {
    }

    /**
     * Adds the given {@code group} with the given {@code name} to the disruptor instance.
     *
     * @param name  group name
     * @param group group
     * @return {@code this}
     */
    public DisruptorBuilder group(
            final String name,
            final DisruptorGroup group
    ) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(group, "group");

        this.groups.put(name, group);

        return this;
    }

    /**
     * Adds the disruptor group with the given {@code name} to the disruptor instance,
     * after letting the {@code decorator} decorate the group builder.
     *
     * @param name      group name
     * @param decorator group decorator
     * @return {@code this}
     */
    public DisruptorBuilder group(
            final String name,
            final Consumer<DisruptorGroupBuilder> decorator
    ) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(decorator, "decorator");

        final DisruptorGroupBuilder builder = DisruptorGroup.builder();
        decorator.accept(builder);

        return this.group(name, builder.build());
    }

    /**
     * Build a new {@link Disruptor} instance using {@code this} builder.
     *
     * @return the disruptor instance
     */
    public Disruptor build() {
        return new DisruptorImpl(
                Map.copyOf(this.groups)
        );
    }
}
