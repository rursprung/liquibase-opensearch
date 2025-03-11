package liquibase.ext.opensearch.integration.spring;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.testcontainers.OpensearchContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static liquibase.ext.opensearch.AbstractOpenSearchLiquibaseIT.OPENSEARCH_DOCKER_IMAGE_NAME;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest(properties = {
    "spring.liquibase.enabled=false"
})
class SpringLiquibaseOpenSearchIT {

    @Container
    @ServiceConnection
    protected static OpensearchContainer<?> container = new OpensearchContainer<>(DockerImageName.parse(OPENSEARCH_DOCKER_IMAGE_NAME));

    @Autowired
    private OpenSearchClient openSearchClient;

    @Test
    void contextLoads() {
    }

    @Test
    @SneakyThrows
    void itRanTheChangelog() {
        assertTrue(openSearchClient.indices().exists(e -> e.index("testindex")).value());
    }

}
