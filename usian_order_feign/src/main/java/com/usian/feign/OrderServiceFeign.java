package com.usian.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("usian-order-service")
public interface OrderServiceFeign {
}
