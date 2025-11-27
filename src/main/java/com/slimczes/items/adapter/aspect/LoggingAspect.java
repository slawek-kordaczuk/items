package com.slimczes.items.adapter.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* com.slimczes.items.service.reservation.*.*(..))")
    public Object logMethodTimeExecution(org.aspectj.lang.ProceedingJoinPoint joinPoint) throws Throwable {
        long now = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();
        log.info("Entering method: {}", methodName);
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - now;
        log.info("Method {} executed in {} ms", methodName, duration);
        return result;
    }
}
