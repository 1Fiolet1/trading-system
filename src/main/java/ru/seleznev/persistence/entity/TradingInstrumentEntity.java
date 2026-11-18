package ru.seleznev.persistence.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.seleznev.domain.enums.InstrumentType;

@Entity
@Table(name = "trading_instruments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TradingInstrumentEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 32)
	private String seccode;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 16)
	private InstrumentType type;

	@Column(name = "lot_size", nullable = false)
	private Long lotSize;

	@Column(name = "min_price_step", nullable = false, precision = 19, scale = 8)
	private BigDecimal minPriceStep;

	@Column(nullable = false, length = 3)
	private String currency;

	@Column(nullable = false)
	private Boolean active;
}
