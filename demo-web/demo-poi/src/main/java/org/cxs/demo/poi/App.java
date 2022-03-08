package org.cxs.demo.poi;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.cxs.demo.poi.entity.CalcRequest;
import org.cxs.demo.poi.entity.CalcResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * note:
 *
 * @author cxs
 * @date 2022/1/4 13:28
 **/
@Slf4j
@SpringBootApplication
@RestController
@RequestMapping("/demo")
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    /**
     * @param request
     * @return
     */
    @PostMapping("/calc")
    public ResponseEntity<CalcResponse> calc(@RequestBody CalcRequest request) {
        String result = execlFormula(request.getParam(), request.getFormula());
        return ResponseEntity.ok(new CalcResponse(result));
    }

    public String execlFormula(List<Long> params, String formula) {
        System.out.println("参数：params = " + params);
        System.out.println("公式：formula = " + formula);
        String value = null;
        XSSFWorkbook wb = null;
        try {
            // 1.内存中创建xls 文档
            //HSSFWorkbook workbook = new HSSFWorkbook();
            wb = new XSSFWorkbook();
            XSSFSheet sheet = wb.createSheet("test");
            // 2.创建单元格并设值
            for (int i = 0; i < params.size(); i++) {
                sheet.createRow(i).createCell(0).setCellValue(params.get(i));
            }
            XSSFCell cell = sheet.createRow(params.size()).createCell(0, CellType.FORMULA);
            cell.setCellFormula(formula);

            // 3.运行公式
            FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
            evaluator.evaluateFormulaCell(cell);
            value = cell.getRawValue();
            System.out.println("计算结果：" + value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                wb.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return value;
    }
}
