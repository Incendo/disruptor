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

import java.util.concurrent.atomic.AtomicBoolean;
import org.apiguardian.api.API;
import org.incendo.disruptor.Disruptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

@ConditionalOnBean(Disruptor.class)
@Component
@API(status = API.Status.INTERNAL, since = "1.0.0")
public class DisruptorBeanPostProcessor implements BeanPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DisruptorAdvice.class);

    private final DisruptorAdvice disruptorAdvice;

    /**
     * Creates a new post processor.
     *
     * @param disruptorAdvice disruptor advice
     */
    public DisruptorBeanPostProcessor(final DisruptorAdvice disruptorAdvice) {
        this.disruptorAdvice = disruptorAdvice;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        // If there are no @Disrupt annotations then we're not interested.
        if (!this.hasDisruptorAnnotation(bean)) {
            return bean;
        }

        LOGGER.debug("Creating disruptor proxy for bean {}, class {}", bean, bean.getClass().getCanonicalName());

        // If there are, however, then we want to create a new proxy which invokes the disruptor.
        final ProxyFactory proxyFactory = new ProxyFactory(bean);
        proxyFactory.addAdvice(this.disruptorAdvice);
        return proxyFactory.getProxy(getClass().getClassLoader());
    }

    private boolean hasDisruptorAnnotation(final Object bean) {
        if (AnnotationUtils.findAnnotation(bean.getClass(), Disrupt.class) != null) {
            return true;
        }
        final AtomicBoolean annotationPresent = new AtomicBoolean(false);
        ReflectionUtils.doWithMethods(bean.getClass(), method -> {
            if (AnnotationUtils.findAnnotation(method, Disrupt.class) != null) {
                annotationPresent.set(true);
            }
        });
        return annotationPresent.get();
    }
}
