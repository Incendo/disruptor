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
package org.incendo.disruptor.spring;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apiguardian.api.API;
import org.incendo.disruptor.DisruptionMode;
import org.incendo.disruptor.Disruptor;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;

@Role(ROLE_INFRASTRUCTURE)
@ConditionalOnBean(Disruptor.class)
@Component
@API(status = API.Status.INTERNAL, since = "1.0.0")
public class DisruptorAdvice implements MethodInterceptor, ApplicationListener<ApplicationReadyEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DisruptorAdvice.class);

    private @Nullable Disruptor disruptor;

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        if (this.disruptor == null) {
            return invocation.proceed();
        }

        // The annotation may either be on the class, or method level.
        final String group;
        final Disrupt methodAnnotation = AnnotationUtils.findAnnotation(invocation.getMethod(), Disrupt.class);

        final Class<?> clazz;
        if (invocation.getThis() != null) {
            clazz = invocation.getThis().getClass();
        } else {
            clazz = invocation.getMethod().getDeclaringClass();
        }

        if (methodAnnotation == null) {
            final Disrupt classAnnotation = AnnotationUtils.findAnnotation(clazz, Disrupt.class);
            if (classAnnotation == null) {
                return invocation.proceed();
            }
            group = classAnnotation.group();
        } else {
            group = methodAnnotation.group();
        }

        LOGGER.trace(
                "Calling disruptor for method {} in class {} using group {}",
                invocation.getMethod().getName(),
                clazz.getCanonicalName(),
                group
        );

        this.disruptor.disrupt(group, DisruptionMode.BEFORE);
        final Object result = invocation.proceed();
        this.disruptor.disrupt(group, DisruptionMode.AFTER);
        return result;
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        this.disruptor = event.getApplicationContext().getBean(Disruptor.class);
    }
}
