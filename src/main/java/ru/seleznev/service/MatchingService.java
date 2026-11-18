package ru.seleznev.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.seleznev.domain.enums.OrderSide;
import ru.seleznev.domain.enums.OrderStatus;
import ru.seleznev.domain.exception.InsufficientPositionException;
import ru.seleznev.persistence.entity.AccountEntity;
import ru.seleznev.persistence.entity.OrderEntity;
import ru.seleznev.persistence.entity.PositionEntity;
import ru.seleznev.persistence.entity.TradeEntity;
import ru.seleznev.persistence.repository.OrderRepository;
import ru.seleznev.persistence.repository.PositionRepository;
import ru.seleznev.persistence.repository.TradeRepository;

@Service
public class MatchingService {

	private static final List<OrderStatus> ACTIVE_STATUSES = List.of(
			OrderStatus.OPEN,
			OrderStatus.PARTIALLY_FILLED
	);

	private final OrderRepository orderRepository;
	private final TradeRepository tradeRepository;
	private final PositionRepository positionRepository;

	@Autowired
	public MatchingService(
			OrderRepository orderRepository,
			TradeRepository tradeRepository,
			PositionRepository positionRepository
	) {
		this.orderRepository = orderRepository;
		this.tradeRepository = tradeRepository;
		this.positionRepository = positionRepository;
	}

	@Transactional
	public void match(OrderEntity incomingOrder) {
		while (incomingOrder.getRemainingQuantity() > 0) {
			OrderEntity oppositeOrder = findBestOppositeOrder(incomingOrder);
			if (oppositeOrder == null) {
				return;
			}

			executeTrade(incomingOrder, oppositeOrder);
		}
	}

	private OrderEntity findBestOppositeOrder(OrderEntity incomingOrder) {
		if (incomingOrder.getSide() == OrderSide.BUY) {
			return orderRepository.findBestSellOrder(
					incomingOrder.getInstrument().getId(),
					incomingOrder.getAccount().getId(),
					OrderSide.SELL,
					ACTIVE_STATUSES,
					incomingOrder.getPrice(),
					PageRequest.of(0, 1)
			).stream().findFirst().orElse(null);
		}

		return orderRepository.findBestBuyOrder(
				incomingOrder.getInstrument().getId(),
				incomingOrder.getAccount().getId(),
				OrderSide.BUY,
				ACTIVE_STATUSES,
				incomingOrder.getPrice(),
				PageRequest.of(0, 1)
		).stream().findFirst().orElse(null);
	}

	private void executeTrade(OrderEntity incomingOrder, OrderEntity oppositeOrder) {
		OrderEntity buyOrder = incomingOrder.getSide() == OrderSide.BUY
				? incomingOrder
				: oppositeOrder;
		OrderEntity sellOrder = incomingOrder.getSide() == OrderSide.SELL
				? incomingOrder
				: oppositeOrder;

		Long tradeQuantity = Math.min(
				incomingOrder.getRemainingQuantity(),
				oppositeOrder.getRemainingQuantity()
		);
		BigDecimal executionPrice = oppositeOrder.getPrice();
		Instant executionTime = Instant.now();

		settleTrade(buyOrder, sellOrder, executionPrice, tradeQuantity);
		updateOrderAfterTrade(buyOrder, tradeQuantity, executionTime);
		updateOrderAfterTrade(sellOrder, tradeQuantity, executionTime);

		tradeRepository.save(new TradeEntity(
				null,
				incomingOrder.getInstrument(),
				buyOrder,
				sellOrder,
				executionPrice,
				tradeQuantity,
				executionTime
		));
	}

	private void settleTrade(
			OrderEntity buyOrder,
			OrderEntity sellOrder,
			BigDecimal executionPrice,
			Long quantity
	) {
		AccountEntity buyerAccount = buyOrder.getAccount();
		AccountEntity sellerAccount = sellOrder.getAccount();
		BigDecimal executedAmount = executionPrice.multiply(BigDecimal.valueOf(quantity));
		BigDecimal reservedAmount = buyOrder.getPrice().multiply(BigDecimal.valueOf(quantity));

		buyerAccount.setBalance(buyerAccount.getBalance().subtract(executedAmount));
		buyerAccount.setReservedBalance(
				buyerAccount.getReservedBalance().subtract(reservedAmount)
		);
		sellerAccount.setBalance(sellerAccount.getBalance().add(executedAmount));

		PositionEntity buyerPosition = positionRepository
				.findByAccount_IdAndInstrument_Id(
						buyerAccount.getId(),
						buyOrder.getInstrument().getId()
				).orElseGet(() -> new PositionEntity(
						null,
						buyerAccount,
						buyOrder.getInstrument(),
						0L,
						0L)
				);

		PositionEntity sellerPosition = positionRepository
				.findByAccount_IdAndInstrument_Id(
						sellerAccount.getId(),
						sellOrder.getInstrument().getId()
				).orElseThrow(() -> new InsufficientPositionException(
						"Позиция продавца не найдена"
				));

		buyerPosition.setQuantity(buyerPosition.getQuantity() + quantity);
		sellerPosition.setQuantity(sellerPosition.getQuantity() - quantity);
		sellerPosition.setReservedQuantity(sellerPosition.getReservedQuantity() - quantity);

		positionRepository.save(buyerPosition);
	}

	private void updateOrderAfterTrade(OrderEntity order, Long quantity, Instant updatedAt) {
		Long remainingQuantity = order.getRemainingQuantity() - quantity;
		order.setRemainingQuantity(remainingQuantity);
		order.setUpdatedAt(updatedAt);
		order.setStatus(remainingQuantity == 0 ? OrderStatus.FILLED : OrderStatus.PARTIALLY_FILLED);
	}
}
