package ru.seleznev.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.seleznev.persistence.entity.AccountEntity;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
}
