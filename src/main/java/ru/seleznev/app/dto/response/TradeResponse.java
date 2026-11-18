package ru.seleznev.app.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TradeResponse {

	private Long id;
	private String seccode;
	private Long buyOrderId;
	private Long sellOrderId;
	private BigDecimal price;
	private Long quantity;
	private Instant executedAt;
}
