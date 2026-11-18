package ru.seleznev.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.seleznev.persistence.entity.TradeEntity;

public interface TradeRepository extends JpaRepository<TradeEntity, Long> {
}
