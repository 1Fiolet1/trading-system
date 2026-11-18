package ru.seleznev.persistence.repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.seleznev.domain.enums.OrderSide;
import ru.seleznev.domain.enums.OrderStatus;
import ru.seleznev.persistence.entity.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

	@Query("""
			SELECT o
			FROM OrderEntity o
			WHERE o.instrument.id = :instrumentId
			  AND o.account.id <> :accountId
			  AND o.side = :side
			  AND o.status IN :statuses
			  AND o.price <= :maxPrice
			ORDER BY o.price ASC, o.createdAt ASC, o.id ASC
			""")
	List<OrderEntity> findBestSellOrder(
			@Param("instrumentId") Long instrumentId,
			@Param("accountId") Long accountId,
			@Param("side") OrderSide side,
			@Param("statuses") Collection<OrderStatus> statuses,
			@Param("maxPrice") BigDecimal maxPrice,
			Pageable pageable
	);

	@Query("""
			SELECT o
			FROM OrderEntity o
			WHERE o.instrument.id = :instrumentId
			  AND o.account.id <> :accountId
			  AND o.side = :side
			  AND o.status IN :statuses
			  AND o.price >= :minPrice
			ORDER BY o.price DESC, o.createdAt ASC, o.id ASC
			""")
	List<OrderEntity> findBestBuyOrder(
			@Param("instrumentId") Long instrumentId,
			@Param("accountId") Long accountId,
			@Param("side") OrderSide side,
			@Param("statuses") Collection<OrderStatus> statuses,
			@Param("minPrice") BigDecimal minPrice,
			Pageable pageable
	);

	List<OrderEntity> findAllByInstrument_IdAndStatusIn(
			Long instrumentId,
			Collection<OrderStatus> statuses
	);
}
