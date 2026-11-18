package ru.seleznev;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class TradingSystemApplicationTests {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres =
			new PostgreSQLContainer<>("postgres:15.20");

	@Autowired
	private MockMvc mockMvc;

	@Test
	void shouldCreateOrder() throws Exception {
		String request = """
				{
				  "accountId": 1,
				  "seccode": "SBER",
				  "side": "BUY",
				  "price": 250.00,
				  "quantity": 10
				}
				""";

		mockMvc.perform(post("/api/v1/orders")
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.accountId").value(1))
				.andExpect(jsonPath("$.seccode").value("SBER"))
				.andExpect(jsonPath("$.side").value("BUY"))
				.andExpect(jsonPath("$.remainingQuantity").value(10))
				.andExpect(jsonPath("$.status").value("OPEN"));
	}
}
