package ru.seleznev.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.seleznev.app.dto.response.OrderResponse;
import ru.seleznev.app.dto.response.TradeResponse;
import ru.seleznev.app.mapper.OrderResponseMapper;
import ru.seleznev.app.mapper.TradeResponseMapper;
import ru.seleznev.persistence.repository.OrderRepository;
import ru.seleznev.persistence.repository.TradeRepository;

@Service
public class RegistryService {

	private final OrderRepository orderRepository;
	private final TradeRepository tradeRepository;
	private final OrderResponseMapper orderResponseMapper;
	private final TradeResponseMapper tradeResponseMapper;

	@Autowired
	public RegistryService(
			OrderRepository orderRepository,
			TradeRepository tradeRepository,
			OrderResponseMapper orderResponseMapper,
			TradeResponseMapper tradeResponseMapper
	) {
		this.orderRepository = orderRepository;
		this.tradeRepository = tradeRepository;
		this.orderResponseMapper = orderResponseMapper;
		this.tradeResponseMapper = tradeResponseMapper;
	}

	@Transactional(readOnly = true)
	public List<OrderResponse> getOrders() {
		return orderRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
				.stream()
				.map(orderResponseMapper::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<TradeResponse> getTrades() {
		return tradeRepository.findAll(Sort.by(Sort.Direction.DESC, "executedAt"))
				.stream()
				.map(tradeResponseMapper::toResponse)
				.toList();
	}
}
