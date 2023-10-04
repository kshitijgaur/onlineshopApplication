package com.kshitij.orderservice.service;

import com.kshitij.orderservice.dto.InventoryResponse;
import com.kshitij.orderservice.dto.OrderLineItemDto;
import com.kshitij.orderservice.dto.OrderRequest;
import com.kshitij.orderservice.model.Order;
import com.kshitij.orderservice.model.OrderLineItem;
import com.kshitij.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;



@Service
@Transactional
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;


    @Autowired
    private WebClient.Builder webClientBuilder;

    public void placeOrder(OrderRequest orderRequest){
        List<OrderLineItem> orderLineItems = orderRequest.getOrderLineItemDtosList()
                .stream()
                .map(orderLineItemDto -> mapToDto(orderLineItemDto))
                .toList();

        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .orderLineItemList(orderLineItems)
                .build();


    List<String> skuCodes = order.getOrderLineItemList().stream().map(orderLineItem -> orderLineItem.getSkuCode()).toList();
          //call inventory service and place order if product in stock
      InventoryResponse[]  inventoryResponseArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
                        .retrieve()
                                .bodyToMono(InventoryResponse[].class)//define type of response returning from inventory service
                                        .block();//for sync call
      boolean allProductInStock=Arrays.stream(inventoryResponseArray).allMatch(inventoryResponse -> inventoryResponse.isInStock());//it will check all values are true in array of inventory response, then will return true;

        if(allProductInStock)
        orderRepository.save(order);
        else throw new IllegalArgumentException("product not in stock");


    }

    private OrderLineItem mapToDto(OrderLineItemDto orderLineItemDto) {
      OrderLineItem orderLineItem = OrderLineItem.builder()
              .price(orderLineItemDto.getPrice())
              .quantity(orderLineItemDto.getQuantity())
              .skuCode(orderLineItemDto.getSkuCode())
              .build();
   return orderLineItem;
    }
}
