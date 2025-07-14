package io.xquti.mdb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "io.xquti.mdb.search")
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Value("${elasticsearch.uris:http://localhost:9200}")
    private String elasticsearchUris;

    @Value("${elasticsearch.username:}")
    private String username;

    @Value("${elasticsearch.password:}")
    private String password;

    @Override
    public ClientConfiguration clientConfiguration() {
        // Parse the URI to extract host and port
        String hostAndPort = elasticsearchUris.replace("http://", "").replace("https://", "");
        
        var builder = ClientConfiguration.builder()
                .connectedTo(hostAndPort)
                .withConnectTimeout(java.time.Duration.ofSeconds(10))
                .withSocketTimeout(java.time.Duration.ofSeconds(60));

        if (!username.isEmpty() && !password.isEmpty()) {
            builder.withBasicAuth(username, password);
        }

        return builder.build();
    }
}