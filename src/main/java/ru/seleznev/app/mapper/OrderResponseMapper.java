package ru.seleznev.app.mapper;

import org.springframework.stereotype.Component;
import ru.seleznev.app.dto.response.OrderResponse;
import ru.seleznev.persistence.entity.OrderEntity;

@Component
public class OrderResponseMapper {

	public OrderResponse toResponse(OrderEntity order) {
		return new OrderResponse(
				order.getId(),
				order.getAccount().getId(),
				order.getInstrument().getSeccode(),
				order.getSide(),
				order.getPrice(),
				order.getInitialQuantity(),
				order.getRemainingQuantity(),
				order.getStatus(),
				order.getCreatedAt(),
				order.getUpdatedAt()
		);
	}
}
