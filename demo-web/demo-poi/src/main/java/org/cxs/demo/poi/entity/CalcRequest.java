package org.cxs.demo.poi.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * note:
 *
 * @author cxs
 * @date 2022/1/4 14:50
 **/
@Data
public class CalcRequest implements Serializable {

    private List<Long> param;
    private String formula;

}
