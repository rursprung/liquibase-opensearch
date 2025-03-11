package liquibase.ext.opensearch.integration.spring;

import liquibase.Scope;
import liquibase.UpdateSummaryOutputEnum;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionArgumentsCommandStep;
import liquibase.command.core.helpers.ShowSummaryArgument;
import liquibase.database.ConnectionServiceFactory;
import liquibase.database.DatabaseFactory;
import liquibase.ext.opensearch.database.OpenSearchConnection;
import liquibase.ext.opensearch.database.OpenSearchLiquibaseDatabase;
import liquibase.ui.UIServiceEnum;
import lombok.Getter;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpringLiquibaseOpenSearch implements InitializingBean {

    @Autowired
    private OpenSearchClient openSearchClient;

    @Autowired
    private SpringLiquibaseOpenSearchProperties properties;

    @Getter
    protected UIServiceEnum uiService = UIServiceEnum.LOGGER;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!properties.enabled()) {
            return;
        }

        Scope.child(Scope.Attr.ui.name(), this.uiService.getUiServiceClass().getDeclaredConstructor().newInstance(),
                () -> {
                    // we want to re-use the connection which spring-data-opensearch (or somebody else) already constructed
                    // => do not rely on liquibase' standard mechanism of constructing a new connection, instead we force it to take our own.
                    final var connection = new OpenSearchConnection(openSearchClient);
                    ConnectionServiceFactory.getInstance().register(connection);
                    final var database = new OpenSearchLiquibaseDatabase();
                    database.setConnection(connection);
                    DatabaseFactory.getInstance().register(database);

                    new CommandScope(UpdateCommandStep.COMMAND_NAME)
                            .addArgumentValue(ShowSummaryArgument.SHOW_SUMMARY_OUTPUT, UpdateSummaryOutputEnum.LOG)
                            .addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, database)
                            .addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, properties.changelogFile())
                            .addArgumentValue(UpdateCommandStep.CONTEXTS_ARG, properties.contexts())
                            .addArgumentValue(UpdateCommandStep.LABEL_FILTER_ARG, properties.labelFilterArgs())
                            .execute();
                    connection.close();
                    database.close();
                });
    }

}
