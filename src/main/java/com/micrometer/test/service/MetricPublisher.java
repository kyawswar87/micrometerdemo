package com.micrometer.test.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tag;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MetricPublisher {

    public void counter(List<Tag> tagList, String metricName) {

    }
}
