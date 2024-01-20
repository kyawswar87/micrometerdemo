package com.micrometer.test.interceptor;


import com.micrometer.test.service.Book;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
@Slf4j
public class MetricAspect {

    @Autowired
    private MeterRegistry meterRegistry;

    private Counter.Builder restfulMethodCounterBuilder;
    private Counter.Builder restfulMethodErrorCounterBuilder;

    private Timer.Sample timer;
    private static final String HTTP_METHOD_COUNTER = "http_method_counter";

    private static final String HTTP_METHOD_ERROR_COUNTER = "http_method_error_counter";

    private static final String HTTP_METHOD_TIMER = "http_method_timer";


    @PostConstruct
    public void init() {
        restfulMethodCounterBuilder = getCounterBuilder(HTTP_METHOD_COUNTER);
        restfulMethodErrorCounterBuilder = getCounterBuilder(HTTP_METHOD_ERROR_COUNTER);
    }

    @Around("@annotation(MetricAspectAnnotation)")
    public Object processTimer(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("Method args values:");
        Arrays.stream(joinPoint.getArgs()).forEach(o -> log.info("arg value: {}", o.toString()));
        Object retVal = null;
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        try {
            timer = Timer.start(meterRegistry);


            System.out.println("full method description: " + signature.getMethod());

            retVal = joinPoint.proceed();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            timer.stop(Timer.builder(HTTP_METHOD_TIMER)
                    .description("http method processing timer")
                    .tags(List.of(
                            Tag.of("method", signature.getName())
                    ))
                    .register(meterRegistry));
        }

        return retVal;
    }

    @Before("@annotation(MetricAspectAnnotation)")
    public void counter(JoinPoint joinPoint) {
        Arrays.stream(joinPoint.getArgs()).forEach(o -> log.info("arg value: {}", o.toString()));
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        restfulMethodCounterBuilder
                .tag("method", signature.getName())
                .register(meterRegistry).increment();
    }

    @AfterThrowing(value = "@annotation(MetricAspectAnnotation)", throwing = "ex")
    public void catchException(Exception ex) {
        restfulMethodErrorCounterBuilder
                .tag("method", ex.toString())
                .register(meterRegistry).increment();
    }

    private Counter.Builder getCounterBuilder(String metricName) {
        return Counter
                .builder(metricName)
                .description("indicates count of http methods");
    }
}
