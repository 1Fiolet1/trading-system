package ru.seleznev.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.seleznev.persistence.entity.PositionEntity;

public interface PositionRepository extends JpaRepository<PositionEntity, Long> {

	Optional<PositionEntity> findByAccount_IdAndInstrument_Id(Long accountId, Long instrumentId);
}
