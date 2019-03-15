package com.myapp.makemyfood.apiagateway.orders;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.myapp.makemyfood.apiagateway.proxies.AccountingService;
import com.myapp.makemyfood.apiagateway.proxies.DeliveryService;
import com.myapp.makemyfood.apiagateway.proxies.KitchenService;
import com.myapp.makemyfood.apiagateway.proxies.OrderServiceProxy;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
//@EnableConfigurationProperties(OrderDestinations.class)
public class OrderConfiguration {

//  @Bean
  public RouteLocator orderProxyRouting(RouteLocatorBuilder builder, OrderDestinations orderDestinations) {
    return builder.routes()
            .route(r -> r.path("/orders").and().method("POST").uri(orderDestinations.getOrderServiceUrl()))
            .route(r -> r.path("/orders").and().method("PUT").uri(orderDestinations.getOrderServiceUrl()))
            .route(r -> r.path("/orders/**").and().method("POST").uri(orderDestinations.getOrderServiceUrl()))
            .route(r -> r.path("/orders/**").and().method("PUT").uri(orderDestinations.getOrderServiceUrl()))
            .route(r -> r.path("/orders").and().method("GET").uri(orderDestinations.getOrderHistoryServiceUrl()))
            .build();
  }

  @Bean
  public RouterFunction<ServerResponse> orderHandlerRouting(OrderHandlers orderHandlers) {
    return RouterFunctions.route(GET("/orders/{orderId}"), orderHandlers::getOrderDetails);
  }

  @Bean
  public OrderHandlers orderHandlers(OrderServiceProxy orderService, KitchenService kitchenService,
                                     DeliveryService deliveryService, AccountingService accountingService) {
    return new OrderHandlers(orderService, kitchenService, deliveryService, accountingService);
  }

  @Bean
  public WebClient webClient() {
    return WebClient.create();
  }

}
