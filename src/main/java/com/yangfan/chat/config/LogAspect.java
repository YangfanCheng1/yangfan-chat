package com.yangfan.chat.config;

import lombok.val;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@Aspect
public class LogAspect {

    private static final Map<Class<?>, Logger> LOGGERS = new ConcurrentHashMap<>();

    private Logger logger(JoinPoint joinPoint) {
        val clazz = joinPoint.getTarget().getClass();
        return LOGGERS.computeIfAbsent(clazz, LoggerFactory::getLogger);
    }

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    void restController() {
    }

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    void serviceBeans() {
    }

    @Pointcut("execution(* *.*(..))")
    void allMethods() {
    }

    @Before("restController() && allMethods()")
    void beforeController(JoinPoint joinPoint) {
        val log = logger(joinPoint);
        val attributes = RequestContextHolder.currentRequestAttributes();
        if (attributes instanceof ServletRequestAttributes) {
            val request = ((ServletRequestAttributes) attributes).getRequest();
            val name = joinPoint.getSignature().getName();
            log.info("{} {}{}",
                    request.getMethod(),
                    request.getServletPath(),
                    Optional.ofNullable(request.getQueryString()).map(q -> "?" + q).orElse(""));
            log.debug("{}() - args:  {}", name, joinPoint.getArgs());
        }
    }

    @AfterThrowing(value = "serviceBeans() && allMethods()", throwing = "exception")
    void afterThrowing(JoinPoint joinPoint, Throwable exception) {

        val log = logger(joinPoint);
        log.error("{}({}) threw an error (exception={})",
                joinPoint.getSignature().getName(),
                joinPoint.getArgs(),
                exception.toString());
    }
}
