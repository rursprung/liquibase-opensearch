package liquibase.ext.opensearch.integration.spring;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration
@EnableConfigurationProperties(SpringLiquibaseOpenSearchProperties.class)
public class LiquibaseOpenSearchAutoConfiguration {
}
