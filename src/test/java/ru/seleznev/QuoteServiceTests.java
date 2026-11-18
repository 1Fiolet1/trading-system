package ru.seleznev;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.seleznev.app.dto.response.OrderBookResponse;
import ru.seleznev.domain.enums.OrderSide;
import ru.seleznev.domain.enums.OrderStatus;
import ru.seleznev.domain.exception.EntityNotFoundException;
import ru.seleznev.persistence.entity.OrderEntity;
import ru.seleznev.persistence.entity.TradingInstrumentEntity;
import ru.seleznev.persistence.repository.OrderRepository;
import ru.seleznev.persistence.repository.TradingInstrumentRepository;
import ru.seleznev.service.QuoteService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class QuoteServiceTests {

	private TradingInstrumentRepository instrumentRepository;
	private OrderRepository orderRepository;
	private QuoteService quoteService;

	@BeforeEach
	void setUp() {
		instrumentRepository = mock(TradingInstrumentRepository.class);
		orderRepository = mock(OrderRepository.class);
		quoteService = new QuoteService(instrumentRepository, orderRepository);
	}

	@Test
	void shouldCombineOrdersWithSamePrice() {
		TradingInstrumentEntity instrument = new TradingInstrumentEntity();
		instrument.setId(1L);
		instrument.setSeccode("SBER");

		OrderEntity firstOrder = new OrderEntity();
		firstOrder.setSide(OrderSide.BUY);
		firstOrder.setPrice(new BigDecimal("250.00"));
		firstOrder.setRemainingQuantity(10L);

		OrderEntity secondOrder = new OrderEntity();
		secondOrder.setSide(OrderSide.BUY);
		secondOrder.setPrice(new BigDecimal("250.00"));
		secondOrder.setRemainingQuantity(20L);

		when(instrumentRepository.findBySeccode("SBER"))
				.thenReturn(Optional.of(instrument));
		when(orderRepository.findAllByInstrument_IdAndStatusIn(
				1L,
				List.of(OrderStatus.OPEN, OrderStatus.PARTIALLY_FILLED)
		))
				.thenReturn(List.of(firstOrder, secondOrder));

		OrderBookResponse result = quoteService.getOrderBook("SBER", 10);

		assertEquals(1, result.getBids().size());
		assertEquals(30L, result.getBids().get(0).getQuantity());
		assertEquals(2L, result.getBids().get(0).getOrderCount());
	}

	@Test
	void shouldThrowExceptionWhenInstrumentNotFound() {
		when(instrumentRepository.findBySeccode("UNKNOWN"))
				.thenReturn(Optional.empty());

		EntityNotFoundException exception = assertThrows(
				EntityNotFoundException.class,
				() -> quoteService.getOrderBook("UNKNOWN", 10)
		);

		assertEquals("Инструмент не найден: UNKNOWN", exception.getMessage());
	}
}
