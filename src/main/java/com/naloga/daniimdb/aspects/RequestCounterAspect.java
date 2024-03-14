package com.naloga.daniimdb.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

@Aspect
@Component
public class RequestCounterAspect {
    private static final Logger logger = LoggerFactory.getLogger(RequestCounterAspect.class);

    // I found out that this is more thread-safe with using AtomicInteger requestCounter for each request being handled by a separate thread
    private final AtomicInteger requestCounter = new AtomicInteger(0);

    // aspect will intercept all public methods within the REST controller classes (com.naloga.daniimdb.*.controller.*) and count the number of requests made at
    // all controller methods in actor movie etc.
    @Pointcut("execution(public * com.naloga.daniimdb.*.controller.*.*(..))")
    public void restControllerMethods() {
    }

    @Before("restControllerMethods()")
    public void countRequest() {
        int currentCount = requestCounter.incrementAndGet();
        logger.info("Total requests so far: {}", currentCount);
    }
}
