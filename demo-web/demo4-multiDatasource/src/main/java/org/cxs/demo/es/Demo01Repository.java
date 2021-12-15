package org.cxs.demo.es;

import org.cxs.demo.entity.Demo01;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * note:
 *
 * @author cxs
 * @date 2021/12/5 16:45
 **/
public interface Demo01Repository extends ElasticsearchRepository<Demo01, Long> {


}
