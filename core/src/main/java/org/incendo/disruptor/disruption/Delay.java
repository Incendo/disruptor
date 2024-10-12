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
package org.incendo.disruptor.disruption;

import java.time.Duration;
import org.apiguardian.api.API;
import org.incendo.disruptor.DisruptionException;
import org.incendo.disruptor.DisruptorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@API(status = API.Status.INTERNAL, since = "1.0.0")
record Delay(Duration duration) implements Disruption {

    private static final Logger LOGGER = LoggerFactory.getLogger(Delay.class);

    @Override
    public void trigger(final DisruptorContext context) {
        LOGGER.debug("Starting delay of {}ms for group {}", this.duration.toMillis(), context.group());
        try {
            Thread.sleep(this.duration);
        } catch (final InterruptedException e) {
            throw new DisruptionException(e);
        }
    }
}
