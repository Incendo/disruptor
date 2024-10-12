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

import java.util.Optional;
import java.util.function.Supplier;
import org.apiguardian.api.API;

/**
 * The disruptor contains the configuration used by the Incendo Disruptor library.
 * The configuration is immutable and may not be modified once it has been constructed.
 *
 * <p><b>Note:</b> This interface should not be implemented. An instance should be built using {@link #builder()}.</p>
 *
 * @since 1.0.0
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public interface Disruptor {

    /**
     * Creates a new {@link Disruptor} builder. The builder is mutable.
     *
     * @return a mutable builder
     */
    static DisruptorBuilder builder() {
        return new DisruptorBuilder();
    }

    /**
     * Return an empty disruptor instance with no groups configured.
     *
     * @return empty instance
     */
    static Disruptor empty() {
        return DisruptorImpl.empty();
    }

    /**
     * Returns the group identified by the given {@code name}, if it exists
     *
     * @param name group name
     * @return optional that contain the group if it exists
     */
    Optional<DisruptorGroup> group(String name);

    /**
     * Runs the given {@code supplier}, invoking any relevant disruptions before and after.
     * If a disruption throws an exception, it'll be propagated and the execution will terminate.
     *
     * @param group disruptor group
     * @param supplier result supplier
     * @return the result
     * @param <T> result type
     */
    default <T> T disrupt(final String group, Supplier<T> supplier) {
        final DisruptorGroup disruptorGroup = this.group(group).orElse(null);
        if (disruptorGroup == null) {
            return supplier.get();
        }

        final DisruptorContext context = DisruptorContext.of(group);

        this.triggerDisruptions(context, disruptorGroup, DisruptionMode.BEFORE);
        final T result = supplier.get();
        this.triggerDisruptions(context, disruptorGroup, DisruptionMode.AFTER);
        return result;
    }

    /**
     * Like {@link #disrupt(String, Supplier)} but without returning a result.
     *
     * @param group disruption group
     * @param runnable runnable to wrap
     */
    default void disruptWithoutResult(final String group, Runnable runnable) {
        this.disrupt(group, (Supplier<Object>) () -> {
            runnable.run();
            return runnable;
        });
    }

    /**
     * Triggers the disruptions for the given {@code group} and {@code mode}.
     *
     * @param group disruption group to trigger
     * @param mode mode to trigger
     */
    default void disrupt(final String group, final DisruptionMode mode) {
         this.group(group).ifPresent(disruptorGroup -> this.triggerDisruptions(
                 DisruptorContext.of(group),
                 disruptorGroup,
                 mode
         ));
    }

    private void triggerDisruptions(
            final DisruptorContext context,
            final DisruptorGroup group,
            final DisruptionMode mode
    ) {
        group.configurations()
                .stream()
                .filter(config -> config.mode() == mode)
                .filter(config -> config.trigger().shouldTrigger(context))
                .flatMap(config -> config.disruptions().stream())
                .forEach(disruption -> disruption.trigger(context));
    }

}
