package com.crumbcookie.crumbcookieresponse;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import com.crumbcookie.crumbcookieresponse.Service.YahooAPIService;
import com.crumbcookie.crumbcookieresponse.dto.YahooStockDTO;
import com.crumbcookie.crumbcookieresponse.lib.CrumbManager;
import com.crumbcookie.crumbcookieresponse.lib.Getsource;

@SpringBootTest
class CrumbcookieresponseApplicationTests {

	@Autowired
	private YahooAPIService yahooAPIService;

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
		YahooStockDTO response = yahooAPIService.getStockDTO(List.of("AAPL"));
		assertNotNull(response.getBody().getResults().get(0));

	}

}
