package org.cxs.demo.canal.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;

/**
 * note:
 *
 * @author cxs
 * @date 2021/12/5 15:37
 **/
// @Configuration
public class ElasticSearchConfig {

    @Bean
    public RestHighLevelClient esRestClient() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(//在这里配置你的elasticsearch的情况
                        new HttpHost("139.196.220.34", 9890, "http"))
        );
        return client;
    }

}
