package org.cxs.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

/**
 * note:
 *
 * @author cxs
 * @date 2021/12/5 16:29
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "demo01", shards = 1, replicas = 0)
public class Demo01 implements Serializable {
    @Id
    private Long id;
    @Field(type = FieldType.Text)
    private String name;
    @Field(type = FieldType.Date)
    private Date createTime;
}
