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
package org.incendo.disruptor.openfeign;

import feign.Capability;
import feign.Client;
import feign.Request;
import feign.Response;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Supplier;
import org.apiguardian.api.API;
import org.incendo.disruptor.DisruptionMode;
import org.incendo.disruptor.Disruptor;

/**
 * Feign capability which runs {@link Disruptor#disrupt(String, Supplier)} before and after the Feign client has
 * executed a request.
 *
 * <p>This should be registered using {@link feign.Feign.Builder#addCapability(Capability)}, or
 * <pre>{@code
 * @Bean
 * Capability disruptorCapability() {
 *   return DisruptorCapability.of(disruptor, group);
 * }}</pre> when using spring-cloud-openfeign.
 *
 * @since 1.0.0
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public final class DisruptorCapability implements Capability {

    /**
     * Creates a new {@link DisruptorCapability}.
     *
     * @param disruptor disruptor instance
     * @param group disruptor group to use for the Feign clients
     * @return the capability
     */
    public static DisruptorCapability of(final Disruptor disruptor, final String group) {
        return new DisruptorCapability(disruptor, group);
    }

    private final Disruptor disruptor;
    private final String group;

    private DisruptorCapability(final Disruptor disruptor, final String group) {
        this.disruptor = Objects.requireNonNull(disruptor, "disruptor");
        this.group = Objects.requireNonNull(group, "group");
    }

    @Override
    public Client enrich(final Client client) {
        return new DisruptorClient(client);
    }

    private final class DisruptorClient implements Client {

        private final Client client;

        private DisruptorClient(final Client client) {
            this.client = Objects.requireNonNull(client, "client");
        }

        @Override
        public Response execute(final Request request, final Request.Options options) throws IOException {
            DisruptorCapability.this.disruptor.disrupt(DisruptorCapability.this.group, DisruptionMode.BEFORE);
            final Response result = this.client.execute(request, options);
            DisruptorCapability.this.disruptor.disrupt(DisruptorCapability.this.group, DisruptionMode.AFTER);
            return result;
        }
    }
}
