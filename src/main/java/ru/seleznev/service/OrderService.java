package ru.seleznev.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.seleznev.app.dto.request.CreateOrderRequest;
import ru.seleznev.app.dto.request.ReplaceOrderRequest;
import ru.seleznev.app.dto.response.OrderResponse;
import ru.seleznev.app.mapper.OrderResponseMapper;
import ru.seleznev.domain.enums.OrderSide;
import ru.seleznev.domain.enums.OrderStatus;
import ru.seleznev.domain.exception.EntityNotFoundException;
import ru.seleznev.domain.exception.InsufficientFundsException;
import ru.seleznev.domain.exception.InsufficientPositionException;
import ru.seleznev.persistence.entity.AccountEntity;
import ru.seleznev.persistence.entity.OrderEntity;
import ru.seleznev.persistence.entity.PositionEntity;
import ru.seleznev.persistence.entity.TradingInstrumentEntity;
import ru.seleznev.persistence.repository.AccountRepository;
import ru.seleznev.persistence.repository.OrderRepository;
import ru.seleznev.persistence.repository.PositionRepository;
import ru.seleznev.persistence.repository.TradingInstrumentRepository;

@Service
public class OrderService {

	private final AccountRepository accountRepository;
	private final TradingInstrumentRepository instrumentRepository;
	private final PositionRepository positionRepository;
	private final OrderRepository orderRepository;
	private final OrderResponseMapper orderResponseMapper;
	private final MatchingService matchingService;

	@Autowired
	public OrderService(
			AccountRepository accountRepository,
			TradingInstrumentRepository instrumentRepository,
			PositionRepository positionRepository,
			OrderRepository orderRepository,
			OrderResponseMapper orderResponseMapper,
			MatchingService matchingService
	) {
		this.accountRepository = accountRepository;
		this.instrumentRepository = instrumentRepository;
		this.positionRepository = positionRepository;
		this.orderRepository = orderRepository;
		this.orderResponseMapper = orderResponseMapper;
		this.matchingService = matchingService;
	}

	@Transactional
	public OrderResponse createOrder(CreateOrderRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Данные заявки не переданы");
		}

		return createOrder(
				request.getAccountId(),
				request.getSeccode(),
				request.getSide(),
				request.getPrice(),
				request.getQuantity()
		);
	}

	@Transactional
	public OrderResponse replaceOrder(Long orderId, ReplaceOrderRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Новые параметры заявки не переданы");
		}

		OrderEntity oldOrder = getOrderEntity(orderId);
		Long accountId = oldOrder.getAccount().getId();
		String seccode = oldOrder.getInstrument().getSeccode();
		OrderSide side = oldOrder.getSide();

		cancelOrder(oldOrder);

		return createOrder(accountId, seccode, side, request.getPrice(), request.getQuantity());
	}

	@Transactional
	public OrderResponse cancelOrder(Long orderId) {
		OrderEntity order = getOrderEntity(orderId);
		cancelOrder(order);
		return orderResponseMapper.toResponse(order);
	}

	@Transactional(readOnly = true)
	public OrderResponse getOrder(Long orderId) {
		return orderResponseMapper.toResponse(getOrderEntity(orderId));
	}

	private OrderResponse createOrder(
			Long accountId,
			String seccode,
			OrderSide side,
			BigDecimal price,
			Long quantity
	) {
		validateRequiredFields(accountId, seccode, side, price, quantity);

		AccountEntity account = accountRepository.findById(accountId)
				.orElseThrow(() -> new EntityNotFoundException("Счёт не найден: " + accountId));

		String normalizedSeccode = seccode.trim().toUpperCase(Locale.ROOT);
		TradingInstrumentEntity instrument = instrumentRepository.findBySeccode(normalizedSeccode)
				.orElseThrow(() -> new EntityNotFoundException("Инструмент не найден: " + normalizedSeccode));

		validateOrderParameters(account, instrument, price, quantity);
		reserveAssets(account, instrument, side, price, quantity);

		Instant now = Instant.now();
		OrderEntity order = new OrderEntity(
				null,
				account,
				instrument,
				side,
				price,
				quantity,
				quantity,
				OrderStatus.OPEN,
				now,
				now
		);

		OrderEntity savedOrder = orderRepository.save(order);
		matchingService.match(savedOrder);
		return orderResponseMapper.toResponse(savedOrder);
	}

	private void reserveAssets(
			AccountEntity account,
			TradingInstrumentEntity instrument,
			OrderSide side,
			BigDecimal price,
			Long quantity
	) {
		if (side == OrderSide.BUY) {
			BigDecimal requiredAmount = price.multiply(BigDecimal.valueOf(quantity));
			BigDecimal availableBalance = account.getBalance().subtract(account.getReservedBalance());

			if (availableBalance.compareTo(requiredAmount) < 0) {
				throw new InsufficientFundsException("Недостаточно денежных средств");
			}

			account.setReservedBalance(account.getReservedBalance().add(requiredAmount));
			return;
		}

		PositionEntity position = positionRepository
				.findByAccount_IdAndInstrument_Id(account.getId(), instrument.getId())
				.orElseThrow(() -> new InsufficientPositionException(
						"Нет позиции по инструменту для продажи"
				));

		Long availableQuantity = position.getQuantity() - position.getReservedQuantity();
		if (availableQuantity < quantity) {
			throw new InsufficientPositionException("Недостаточно бумаг для продажи");
		}

		position.setReservedQuantity(position.getReservedQuantity() + quantity);
	}

	private void cancelOrder(OrderEntity order) {
		if (order.getStatus() != OrderStatus.OPEN
				&& order.getStatus() != OrderStatus.PARTIALLY_FILLED) {
			throw new IllegalArgumentException(
					"Заявку нельзя отменить в статусе " + order.getStatus()
			);
		}

		if (order.getSide() == OrderSide.BUY) {
			BigDecimal releasedAmount = order.getPrice()
					.multiply(BigDecimal.valueOf(order.getRemainingQuantity()));
			AccountEntity account = order.getAccount();
			account.setReservedBalance(account.getReservedBalance().subtract(releasedAmount));
		} else {
			PositionEntity position = positionRepository
					.findByAccount_IdAndInstrument_Id(
							order.getAccount().getId(),
							order.getInstrument().getId()
					)
					.orElseThrow(() -> new InsufficientPositionException(
							"Позиция по инструменту не найдена"
					));
			position.setReservedQuantity(
					position.getReservedQuantity() - order.getRemainingQuantity()
			);
		}

		order.setStatus(OrderStatus.CANCELLED);
		order.setUpdatedAt(Instant.now());
	}

	private void validateRequiredFields(
			Long accountId,
			String seccode,
			OrderSide side,
			BigDecimal price,
			Long quantity
	) {
		if (accountId == null || seccode == null || seccode.isBlank()
				|| side == null || price == null || quantity == null) {
			throw new IllegalArgumentException("Все параметры заявки обязательны");
		}
		if (accountId <= 0 || price.signum() <= 0 || quantity <= 0) {
			throw new IllegalArgumentException(
					"Идентификатор, цена и количество должны быть положительными"
			);
		}
	}

	private void validateOrderParameters(
			AccountEntity account,
			TradingInstrumentEntity instrument,
			BigDecimal price,
			Long quantity
	) {
		if (!Boolean.TRUE.equals(instrument.getActive())) {
			throw new IllegalArgumentException("Торговый инструмент неактивен");
		}
		if (!account.getCurrency().equals(instrument.getCurrency())) {
			throw new IllegalArgumentException("Валюта счёта не совпадает с валютой инструмента");
		}
		if (quantity % instrument.getLotSize() != 0) {
			throw new IllegalArgumentException("Количество должно быть кратно размеру лота");
		}
		if (price.remainder(instrument.getMinPriceStep()).compareTo(BigDecimal.ZERO) != 0) {
			throw new IllegalArgumentException("Цена должна быть кратна минимальному шагу цены");
		}
	}

	private OrderEntity getOrderEntity(Long orderId) {
		if (orderId == null || orderId <= 0) {
			throw new IllegalArgumentException("Некорректный идентификатор заявки");
		}
		return orderRepository.findById(orderId)
				.orElseThrow(() -> new EntityNotFoundException("Заявка не найдена: " + orderId));
	}
}
