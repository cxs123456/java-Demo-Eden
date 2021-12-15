package org.cxs.demo.canal.es.dao;

import org.cxs.demo.canal.entity.Item;

import java.util.List;

/**
 * note:
 *
 * @author cxs
 * @date 2021/12/5 16:47
 **/
public interface ItemRepository {

    List<Item> findByTitle(String title);

    List<Item> findByPriceBetween(Double d1, Double d2);
}
