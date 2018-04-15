package com.example.demo.healthendpoint;

import com.example.demo.RestDocsTests;
import org.junit.Test;
import org.springframework.http.MediaType;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StatusIsOkTest extends RestDocsTests {

	@Test
	public void whenTheHealthEndpointIsAvailableThenDocumentIt() throws Exception {
		this.mockMvc.perform(get("/actuator/health").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("health"));
	}

}
