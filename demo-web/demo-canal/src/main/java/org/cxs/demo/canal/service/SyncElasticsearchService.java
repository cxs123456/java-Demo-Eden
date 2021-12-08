package org.cxs.demo.canal.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.CanalEntry.EntryType;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.alibaba.otter.canal.protocol.CanalEntry.RowData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.cxs.demo.canal.entity.Demo01;
import org.cxs.demo.canal.es.dao.Demo01Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * note:
 *
 * @author cxs
 * @date 2021/12/5 15:32
 **/
@Slf4j
@Service
public class SyncElasticsearchService {


    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired
    private Demo01Repository demo01Repository;

    /**
     * 同步demo01 到ES 中
     *
     * @param entrys
     */
    public void syncDemo01(List<CanalEntry.Entry> entrys) {
        for (CanalEntry.Entry entry : entrys) {
            if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN || entry.getEntryType() == EntryType.TRANSACTIONEND) {
                continue;
            }
            //
            String schemaName = entry.getHeader().getSchemaName();
            String tableName = entry.getHeader().getTableName();
            if (!StringUtils.equalsIgnoreCase("test", schemaName)) {
                continue;
            }
            if (!StringUtils.equalsIgnoreCase("demo01", tableName)) {
                continue;
            }

            RowChange rowChage = null;
            try {
                rowChage = RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(),
                        e);
            }
            CanalEntry.EventType eventType = rowChage.getEventType();

            for (RowData rowData : rowChage.getRowDatasList()) {
                if (eventType == EventType.DELETE) { // 删除
                    deleteRow(rowData);
                } else if (eventType == CanalEntry.EventType.INSERT) { // 新增
                    addRow(rowData);
                } else { // 更新
                    addRow(rowData);
                }
            }

        }
    }

    private void deleteRow(RowData rowData) {
        List<CanalEntry.Column> columns = rowData.getBeforeColumnsList();
        for (CanalEntry.Column column : columns) {
            log.info(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
            if ("id".equals(column.getName())) {
                demo01Repository.deleteById(Long.parseLong(column.getValue()));
                break;
            }
        }
    }

    private void addRow(RowData rowData) {
        List<CanalEntry.Column> columns = rowData.getAfterColumnsList();
        JSONObject jsonObject = new JSONObject();
        for (CanalEntry.Column column : columns) {
            log.info(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
            String name = column.getName();
            String value = column.getValue();
            jsonObject.put(name, value);
        }

        Demo01 demo = JSON.toJavaObject(jsonObject, Demo01.class);
        demo01Repository.save(demo);
        // elasticsearchRestTemplate.
    }
}
