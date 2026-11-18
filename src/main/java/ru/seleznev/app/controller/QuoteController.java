package ru.seleznev.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.seleznev.app.dto.response.OrderBookResponse;
import ru.seleznev.service.QuoteService;

@RestController
@RequestMapping("/api/v1/instruments")
public class QuoteController {

	private final QuoteService quoteService;

	@Autowired
	public QuoteController(QuoteService quoteService) {
		this.quoteService = quoteService;
	}

	@GetMapping("/{seccode}/order-book")
	public OrderBookResponse getOrderBook(
			@PathVariable String seccode,
			@RequestParam(defaultValue = "10") Integer depth
	) {
		return quoteService.getOrderBook(seccode, depth);
	}
}
