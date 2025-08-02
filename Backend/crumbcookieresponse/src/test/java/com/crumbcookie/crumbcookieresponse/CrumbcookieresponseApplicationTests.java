package com.crumbcookie.crumbcookieresponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.crumbcookie.crumbcookieresponse.Service.YahooAPIService;
import com.crumbcookie.crumbcookieresponse.Service.YahooOHAPIService;
import com.crumbcookie.crumbcookieresponse.dto.YahooStockDTO;
import com.crumbcookie.crumbcookieresponse.lib.DateTimeManager;

@SpringBootTest
class CrumbcookieresponseApplicationTests {

	@Autowired
	private YahooAPIService yahooAPIService;

	@Autowired
	private YahooOHAPIService yahooOHAPIService;

	@Autowired
	private DateTimeManager dateTimeManager;

	@Test
	void contextLoads() {
	}

	/* @Test
	public void testgetAPIData() throws Exception {
		YahooStockDTO quoteResponse =
        new Getsource().geStockDTO(List.of("AAPL"));
    assertNotNull(quoteResponse.getBody().getResults().get(0));
	} */

	@Test
	public void testServiceGetData() throws Exception {
		List<YahooStockDTO> response = yahooAPIService.getStockDTO(List.of("AAPL"));
		//assertNotNull(response.getBody().getResults().get(0));
		assertNotNull(response.stream().map(e -> e.getBody().getResults().get(0)));

	}

	/* @Test
	public void testServiceGetOHDate() throws Exception {
		LocalDate toDate = LocalDate.now();
		LocalDateTime fromDate = toDate.plusMonths(3).atStartOfDay();
		
		YahooStockOHDTO response = yahooOHAPIService.getOHStockDTO(List.of("AAPL"), this.dateTimeManager.convert(fromDate),
			this.dateTimeManager.convert(toDate), "3mo" );
		assertNotNull(response.getChart().getResult());
		assertNotNull(response.getChart().getResult());
	} */



}
