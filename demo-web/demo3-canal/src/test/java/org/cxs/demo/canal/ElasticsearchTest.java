package org.cxs.demo.canal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.cxs.demo.canal.entity.Demo01;
import org.cxs.demo.canal.es.dao.Demo01Repository;
import org.cxs.demo.canal.es.dao.ItemRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * note:
 *
 * @author cxs
 * @date 2021/12/5 16:05
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class ElasticsearchTest {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    Demo01Repository demo01Repository;

    @Test
    public void testCreateIndex() {
        boolean index = elasticsearchRestTemplate.createIndex(Demo01.class);
//        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(Item.class);
//        Document mapping = indexOperations.createMapping();
//        indexOperations.putMapping(mapping);
    }


    @Test
    public void testDeleteIndex() {
        boolean b = elasticsearchRestTemplate.deleteIndex(Demo01.class);
    }

    @Test
    public void testCreate() {
//        Item item = new Item(1L, "phone0123", " 手机", "小米", 3499.00, "http://image.leyou.com/13123.jpg");
//        itemRepository.save(item);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", "123456");
        jsonObject.put("name", "你 hello");
        jsonObject.put("create_time", "2021-12-05 17:50:30");

        //Demo01 demo01 = new Demo01(129L, "SDSD", new Date());
        Demo01 demo = JSON.toJavaObject(jsonObject, Demo01.class);
        demo01Repository.save(demo);
//        new UpdateQueryBuilder().build().setUpdateRequest();
//        elasticsearchRestTemplate.update()
    }

    @Test
    public void testDelete() {
        //itemRepository.deleteById(1L);
    }
}
