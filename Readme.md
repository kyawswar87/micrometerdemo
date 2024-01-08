# Getting Started

This is micrometer demo project with spring actuator. Prometheus server will pull this spring actuator Restful endpoint.

### Requirement
These following requirements need to setup.
- prometheus server
- grafana server

`prometheus.yml` file is configuration file for prometheus server running.
```dtd
scrape_configs:
  - job_name: 'spring-boot-app'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8080']
```
There are two metric for Restful endpoints.
- `http_method_counter`: how many access for each specific endpoint?
- `http_method_timer`: how much processing time for each endpoint?

I used micrometer Timer and Counter builder. This builder generates as following.

### metric format
> metric_name{label:label_value,...} metric_value

#### http_method_counter metric
```dtd
# HELP http_method_counter_total indicates count of http methods
# TYPE http_method_counter_total counter
http_method_counter_total{application="spring-boot-metrics",method="POST",path="/",} 4.0
```
There is total keyword ending.

#### http_method_timer metric
```dtd
# HELP http_method_timer_seconds http method processing timer
# TYPE http_method_timer_seconds summary
http_method_timer_seconds_count{application="spring-boot-metrics",method="POST",path="/",} 4.0
http_method_timer_seconds_sum{application="spring-boot-metrics",method="POST",path="/",} 1.224771527
# HELP http_method_timer_seconds_max http method processing timer
# TYPE http_method_timer_seconds_max gauge
http_method_timer_seconds_max{application="spring-boot-metrics",method="POST",path="/",} 0.397616006
```
There are three metric is generated for timer metric.

There are other useful metrics. You can see by using http://localhost:8080/actuator/prometheus endpoint.

We can find by using those metric name in Grafana http://localhost:3000 or Prometheus http://localhost:9090 Dashboard.# micrometerdemo

## Micrometer with Spring AOP

I upgraded micrometer with spring AOP.

Every method with this annotation `@MetricAspectAnnotation` will intercept by spring AOP.

I removed HTTP method tags from controller method metric because Spring Actuator already listen those method as follows.
```dtd
# HELP http_server_requests_seconds
# TYPE http_server_requests_seconds summary
http_server_requests_seconds_count{application="spring-boot-metrics",error="none",exception="none",method="GET",outcome="SUCCESS",status="200",uri="/actuator/prometheus",} 1.0
http_server_requests_seconds_sum{application="spring-boot-metrics",error="none",exception="none",method="GET",outcome="SUCCESS",status="200",uri="/actuator/prometheus",} 0.119050624
http_server_requests_seconds_count{application="spring-boot-metrics",error="none",exception="none",method="GET",outcome="SUCCESS",status="200",uri="/api/book/",} 1.0
http_server_requests_seconds_sum{application="spring-boot-metrics",error="none",exception="none",method="GET",outcome="SUCCESS",status="200",uri="/api/book/",} 0.305244145
# HELP http_server_requests_seconds_max
# TYPE http_server_requests_seconds_max gauge
http_server_requests_seconds_max{application="spring-boot-metrics",error="none",exception="none",method="GET",outcome="SUCCESS",status="200",uri="/actuator/prometheus",} 0.119050624
http_server_requests_seconds_max{application="spring-boot-metrics",error="none",exception="none",method="GET",outcome="SUCCESS",status="200",uri="/api/book/",} 0.305244145
```