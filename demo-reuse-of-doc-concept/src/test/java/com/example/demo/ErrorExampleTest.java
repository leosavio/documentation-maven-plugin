package com.example.demo;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ErrorExampleTest
 * <p>
 * Created on 22.03.18
 * <p>
 * Copyright (C) 2018 Tobias Hochgürtel, All rights reserved.
 */
public class ErrorExampleTest extends RestDocsTests {

	private final static Logger LOGGER = LoggerFactory.getLogger(ErrorExampleTest.class);
	private final String DOCUMENTATION_IDENTIFIER = "error-example";

	@Test
	public void errorExample() throws Exception {
		final ResultActions resultActions = this.mockMvc
				.perform(get("/error").contentType(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().is(500));
		final String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
		LOGGER.info(contentAsString);

		resultActions
				.andExpect(jsonPath("error", is("None")))
				.andExpect(jsonPath("timestamp", is(notNullValue())))
				.andExpect(jsonPath("status", is(999)))
				.andDo(document(DOCUMENTATION_IDENTIFIER,
				                responseFields(
						                fieldWithPath("error").description("The HTTP error that occurred, e.g. `Bad Request`"),
						                fieldWithPath("message").description("A description of the cause of the error"),
						                fieldWithPath("status").description("The HTTP status code, e.g. `999`, `400`"),
						                fieldWithPath("timestamp").description("The time, in milliseconds, at which the error occurred"))));
	}

}
