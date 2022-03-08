package org.cxs.demo.poi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * note:
 *
 * @author cxs
 * @date 2022/1/4 14:50
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalcResponse implements Serializable {

    private String result;

}
