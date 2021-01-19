package com.usian.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig extends ElasticsearchProperties {

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        String[] hosts = getClusterNodes().split(",");
        HttpHost[] httpHosts = new HttpHost[hosts.length];
        for (int i = 0; i < hosts.length; i++) {
            httpHosts[i] = new HttpHost(hosts[i].split(":")[0], Integer.parseInt(hosts[i].split(":")[1]));
        }
        return new RestHighLevelClient(RestClient.builder(httpHosts));
    }
}