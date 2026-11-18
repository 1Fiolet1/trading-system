package ru.seleznev.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.seleznev.app.dto.request.CreateOrderRequest;
import ru.seleznev.app.dto.request.ReplaceOrderRequest;
import ru.seleznev.app.dto.response.OrderResponse;
import ru.seleznev.service.OrderService;
import ru.seleznev.service.RegistryService;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

	private final OrderService orderService;
	private final RegistryService registryService;

	@Autowired
	public OrderController(OrderService orderService, RegistryService registryService) {
		this.orderService = orderService;
		this.registryService = registryService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public OrderResponse createOrder(@RequestBody CreateOrderRequest request) {
		return orderService.createOrder(request);
	}

	@GetMapping
	public List<OrderResponse> getOrders() {
		return registryService.getOrders();
	}

	@GetMapping("/{orderId}")
	public OrderResponse getOrder(@PathVariable Long orderId) {
		return orderService.getOrder(orderId);
	}

	@PostMapping("/{orderId}/replace")
	public OrderResponse replaceOrder(
			@PathVariable Long orderId,
			@RequestBody ReplaceOrderRequest request
	) {
		return orderService.replaceOrder(orderId, request);
	}

	@DeleteMapping("/{orderId}")
	public OrderResponse cancelOrder(@PathVariable Long orderId) {
		return orderService.cancelOrder(orderId);
	}
}
