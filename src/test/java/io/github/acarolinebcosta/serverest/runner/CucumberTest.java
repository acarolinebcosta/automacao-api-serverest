package io.github.acarolinebcosta.serverest.runner;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.ConfigurationParametersResource;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("io/github/acarolinebcosta/serverest")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "io.github.acarolinebcosta.serverest")
@ConfigurationParameter(key = "cucumber.features", value = "classpath:features")
@ConfigurationParametersResource("cucumber.properties")
public class CucumberTest {
}