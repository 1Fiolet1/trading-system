package ru.seleznev.app.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.seleznev.domain.enums.OrderSide;
import ru.seleznev.domain.enums.OrderStatus;

@Getter
@AllArgsConstructor
public class OrderResponse {

	private Long id;
	private Long accountId;
	private String seccode;
	private OrderSide side;
	private BigDecimal price;
	private Long initialQuantity;
	private Long remainingQuantity;
	private OrderStatus status;
	private Instant createdAt;
	private Instant updatedAt;
}
