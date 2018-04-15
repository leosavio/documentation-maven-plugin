package com.example.demo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

/**
 * RestDocsTests
 * <p>
 * Created on 22.03.18
 * <p>
 * Copyright (C) 2018 Tobias Hochgürtel, All rights reserved.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public abstract class RestDocsTests {

	private final static Logger LOGGER = LoggerFactory.getLogger(RestDocsTests.class);

	@Rule
	public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

	protected MockMvc mockMvc;
	@Autowired
	private WebApplicationContext context;

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation))
				.build();
	}

}
