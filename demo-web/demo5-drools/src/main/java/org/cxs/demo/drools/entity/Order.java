package org.cxs.demo.drools.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * note:
 *
 * @author cxs
 * @date 2021/12/15 18:21
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable {

    private int amount;
    private int score;

}
