package ru.seleznev.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.seleznev.persistence.entity.TradingInstrumentEntity;

public interface TradingInstrumentRepository extends JpaRepository<TradingInstrumentEntity, Long> {

	Optional<TradingInstrumentEntity> findBySeccode(String seccode);
}
