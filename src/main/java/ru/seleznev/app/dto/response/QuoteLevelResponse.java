package ru.seleznev.app.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class QuoteLevelResponse {

	private BigDecimal price;
	private Long quantity;
	private Long orderCount;
}
