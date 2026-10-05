package com.fundatech.shareway.paymentsettlement;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Ejecuta todos los escenarios Gherkin de {@code src/test/resources/features} sobre JUnit Platform.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.fundatech.shareway.paymentsettlement.bdd")
public class CucumberRunnerTest {
}
