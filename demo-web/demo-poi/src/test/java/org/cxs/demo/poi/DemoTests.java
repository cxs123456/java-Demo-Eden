package org.cxs.demo.poi;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * note:
 *
 * @author cxs
 * @date 2021/12/3 12:19
 **/
//@RunWith(SpringRunner.class)
//@SpringBootTest
@Slf4j
public class DemoTests {

    private static final String JS_ENGINE_NAME = "JavaScript";
    private final ScriptEngineManager sem = new ScriptEngineManager();
    private final ScriptEngine engine = sem.getEngineByName(JS_ENGINE_NAME);

    @Before
    public void setup() {

    }

    /**
     * 连续替换多个字符串对应的值，例如  给定字符串 IF(A1=0,A2,2),数组[1,2] 结果是IF(1=0,2,2)
     * <p>
     * 给定 数组a[1,2,3,4 ]将A1,A2,..AN替换成数组a[0],a[1]...a[n-1]
     */
    @Test
    public void test() {
        // 方法1 正则表达式
        List<String> param = new ArrayList<>();
        param.add("13");
        param.add("2");
        String formula = "IF(A1=0,A2,2)";
        String express = "";
        Pattern compile = Pattern.compile("A\\d+");
        Matcher matcher = compile.matcher(formula);
        StringBuffer sb = new StringBuffer();
        int count = 0;
        while (matcher.find()) {
            System.out.println(matcher.group());
            matcher.appendReplacement(sb, param.get(count++));
        }
        matcher.appendTail(sb);
        System.out.println(sb.toString());

        // 方法2
        StringBuffer sb1 = new StringBuffer();
        int count1 = 0;
        for (int i = 0; i < formula.length(); i++) {
            char c = formula.charAt(i);
            if ('A' == c && i < formula.length() - 1) {
                int j = i;
                // 获取A后面的数字
                while (i < formula.length() - 1 && Character.isDigit(formula.charAt(i + 1))) {
                    i++;
                }
                if (i > j) {
                    sb1.append(param.get(count1++));
                }
            } else {
                sb1.append(c);
            }
        }
        System.out.println(sb1.toString());

    }


    /**
     * 测试 java 调用 js 引擎执行js代码
     *
     * @throws IOException
     */
    @Test
    public void test1() throws IOException {
        //
        List<String> param = new ArrayList<>();
        param.add("13");
        param.add("2");
        String formula = "A1+A2";
        String express = "";
        Pattern compile = Pattern.compile("A\\d");
        Matcher matcher = compile.matcher(formula);
        if (matcher.find()) {
            System.out.println(matcher.group());

        }

        ClassPathResource resource1 = new ClassPathResource("formula.min.js");
        ClassPathResource resource2 = new ClassPathResource("jstat.min.js");
        BufferedReader reader1 = new BufferedReader(new InputStreamReader(resource1.getInputStream()));
        BufferedReader reader2 = new BufferedReader(new InputStreamReader(resource2.getInputStream()));
        try {
            Object eval = engine.eval(reader1);
            Object eval1 = engine.eval(reader2);
            System.out.println(eval);
            System.out.println(eval1);
            Object res = engine.eval("formulajs.DATE(2008, 7, 8)");

            //            Invocable jsInvoke = (Invocable)engine;
            //            Object res = jsInvoke.invokeFunction("eval", "SUM(3,2)");
            System.out.println(res);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            reader1.close();
            reader2.close();
        }

    }

    /**
     * 测试 excel 执行公式
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        List<String> param = new ArrayList<>();
        String a1 = "13";
        String a2 = "2";
        String formula = "A1+A2";
        String formula1 = "IF(A1=0,A2,2)";
        String formula2 = "AND(A1,A2)";
        String formula3 = "POWER(A1,A2)";
        // 1.内存中创建xls 文档
        //HSSFWorkbook workbook = new HSSFWorkbook();
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("test");
        // 2.创建单元格并设值
        XSSFRow row1 = sheet.createRow(0);
        XSSFRow row2 = sheet.createRow(1);
        XSSFRow row3 = sheet.createRow(2);
        row1.createCell(0).setCellValue(a1);
        row2.createCell(0).setCellValue(a2);

        XSSFCell cell = row3.createCell(0, CellType.FORMULA);
        cell.setCellFormula(formula3);
        System.out.println(cell.getCellFormula());

        // 3.运行公式
        FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
        CellType cellType = evaluator.evaluateFormulaCell(cell);
        System.out.println(cell.getRawValue());
        //        System.out.println(evaluate);
        //        System.out.println(evaluate.getStringValue());
        //        System.out.println(evaluate.formatAsString());
        //        System.out.println(evaluate.getNumberValue());
        //        System.out.println(evaluate.getBooleanValue());

        wb.close();
    }


}
