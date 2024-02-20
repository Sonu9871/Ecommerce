package com.sonu.orderservice.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.sonu.orderservice.dto.InventoryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sonu.orderservice.dto.OrderRequest;
import com.sonu.orderservice.dto.OrederLineItemsDto;
import com.sonu.orderservice.model.Order;
import com.sonu.orderservice.model.OrderLineItems;
import com.sonu.orderservice.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
	
	private final OrderRepository orderRepository;
	private final WebClient webClient;

	public void placeOrder(OrderRequest orderRequest) {
		Order order = new Order();
		order.setOrderNumber(UUID.randomUUID().toString());

		List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList().stream().map(this::mapToDto)
				.toList();
		order.setOrderLineItemsList(orderLineItems);

	List<String> skuCodes=order.getOrderLineItemsList().stream()
				.map(OrderLineItems::getSkuCode)
				.toList();

		//call inventory service ,and place order if product is in stock
	InventoryResponse[] inventoryResponsesArray=	webClient.get()
			.uri("http://localhost:8084/api/inventory",uriBuilder -> uriBuilder.queryParam(
					"skuCode",skuCodes).build())
			.retrieve()
			.bodyToMono(InventoryResponse[].class)
			.block();

        assert inventoryResponsesArray != null;
        boolean allProductsInStock=	Arrays.stream(inventoryResponsesArray).allMatch(InventoryResponse::isInStock);

		if(allProductsInStock){
			orderRepository.save(order);
		}else{
			throw new IllegalArgumentException("Product is not in stock, please try again later");
		}
	}

	private OrderLineItems mapToDto(OrederLineItemsDto orderLineItemsDto) {
		OrderLineItems orderLineItems = new OrderLineItems();
		orderLineItems.setPrice(orderLineItemsDto.getPrice());
		orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
		orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
		return orderLineItems;

	}

}
