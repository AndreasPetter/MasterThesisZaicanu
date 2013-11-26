package com.seeburger.research.securityframework.configuration;

import static org.junit.Assert.*;

import org.junit.Test;

public class ConfigurationReaderTest {

	@Test
	public void testConfigureFramework() {
		try {
			ConfigurationReader.configureFramework("/META-INF");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
