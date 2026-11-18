package ru.seleznev.app.dto.response;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderBookResponse {

	private String seccode;
	private List<QuoteLevelResponse> bids;
	private List<QuoteLevelResponse> asks;
	private Instant timestamp;
}
