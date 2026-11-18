package ru.seleznev.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.seleznev.app.dto.response.TradeResponse;
import ru.seleznev.service.RegistryService;

@RestController
@RequestMapping("/api/v1/trades")
public class TradeController {

	private final RegistryService registryService;

	@Autowired
	public TradeController(RegistryService registryService) {
		this.registryService = registryService;
	}

	@GetMapping
	public List<TradeResponse> getTrades() {
		return registryService.getTrades();
	}
}
