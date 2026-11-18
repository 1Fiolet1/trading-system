package ru.seleznev.app.mapper;

import org.springframework.stereotype.Component;
import ru.seleznev.app.dto.response.TradeResponse;
import ru.seleznev.persistence.entity.TradeEntity;

@Component
public class TradeResponseMapper {

	public TradeResponse toResponse(TradeEntity trade) {
		return new TradeResponse(
				trade.getId(),
				trade.getInstrument().getSeccode(),
				trade.getBuyOrder().getId(),
				trade.getSellOrder().getId(),
				trade.getPrice(),
				trade.getQuantity(),
				trade.getExecutedAt()
		);
	}
}
