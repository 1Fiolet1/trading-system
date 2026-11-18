package ru.seleznev.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.seleznev.app.dto.response.OrderBookResponse;
import ru.seleznev.app.dto.response.QuoteLevelResponse;
import ru.seleznev.domain.enums.OrderSide;
import ru.seleznev.domain.enums.OrderStatus;
import ru.seleznev.domain.exception.EntityNotFoundException;
import ru.seleznev.persistence.entity.OrderEntity;
import ru.seleznev.persistence.entity.TradingInstrumentEntity;
import ru.seleznev.persistence.repository.OrderRepository;
import ru.seleznev.persistence.repository.TradingInstrumentRepository;

@Service
public class QuoteService {

	private static final List<OrderStatus> ACTIVE_STATUSES = List.of(
			OrderStatus.OPEN,
			OrderStatus.PARTIALLY_FILLED
	);

	private final TradingInstrumentRepository instrumentRepository;
	private final OrderRepository orderRepository;

	@Autowired
	public QuoteService(
			TradingInstrumentRepository instrumentRepository,
			OrderRepository orderRepository
	) {
		this.instrumentRepository = instrumentRepository;
		this.orderRepository = orderRepository;
	}

	@Transactional(readOnly = true)
	public OrderBookResponse getOrderBook(String seccode, Integer depth) {
		TradingInstrumentEntity instrument = instrumentRepository.findBySeccode(seccode)
				.orElseThrow(() -> new EntityNotFoundException(
						"Инструмент не найден: " + seccode
				));

		List<OrderEntity> orders = orderRepository.findAllByInstrument_IdAndStatusIn(
				instrument.getId(),
				ACTIVE_STATUSES
		);

		Map<BigDecimal, QuoteLevelResponse> bids = new TreeMap<>(Comparator.reverseOrder());
		Map<BigDecimal, QuoteLevelResponse> asks = new TreeMap<>();

		for (OrderEntity order : orders) {
			if (order.getSide() == OrderSide.BUY) {
				addToLevel(bids, order);
			} else {
				addToLevel(asks, order);
			}
		}

		return new OrderBookResponse(
				instrument.getSeccode(),
				limitDepth(bids, depth),
				limitDepth(asks, depth),
				Instant.now()
		);
	}

	private void addToLevel(Map<BigDecimal, QuoteLevelResponse> levels, OrderEntity order) {
		QuoteLevelResponse level = levels.get(order.getPrice());
		if (level == null) {
			levels.put(order.getPrice(), new QuoteLevelResponse(
					order.getPrice(),
					order.getRemainingQuantity(),
					1L
			));
			return;
		}

		level.setQuantity(level.getQuantity() + order.getRemainingQuantity());
		level.setOrderCount(level.getOrderCount() + 1);
	}

	private List<QuoteLevelResponse> limitDepth(
			Map<BigDecimal, QuoteLevelResponse> levels,
			Integer depth
	) {
		List<QuoteLevelResponse> result = new ArrayList<>(levels.values());
		if (depth == null || depth <= 0 || result.size() <= depth) {
			return result;
		}
		return new ArrayList<>(result.subList(0, depth));
	}
}
