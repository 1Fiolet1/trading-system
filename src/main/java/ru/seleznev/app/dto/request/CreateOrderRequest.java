package ru.seleznev.app.dto.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.seleznev.domain.enums.OrderSide;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

	private Long accountId;

	private String seccode;

	private OrderSide side;

	private BigDecimal price;

	private Long quantity;
}
