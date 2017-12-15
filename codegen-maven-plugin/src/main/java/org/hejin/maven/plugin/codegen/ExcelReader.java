/*
 * license.txt
 */
package org.hejin.maven.plugin.codegen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class ExcelReader {

    public static HSSFWorkbook createWorkbook(InputStream in) {
        try {
            return new HSSFWorkbook(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static HSSFCell getCell(HSSFSheet sheet, int row, int col) {
        return getCell(sheet.getRow(row), col);
    }

    public static HSSFCell getCell(HSSFRow row, int col) {
        if (row == null) {
            return null;
        }
        return row.getCell((short) col);
    }

    public static String getString(HSSFSheet sheet, int row, int col) {
        return getString(getCell(sheet, row, col));
    }

    public static String getString(HSSFRow row, int col) {
        return getString(getCell(row, col));
    }

    public static String getString(HSSFCell cell) {
        if (cell == null) {
            return null;
        }
        return trim(cell.getRichStringCellValue().getString());
    }

    private static String trim(String s) {
        return (s == null) ? s : s.trim();
    }



    public static List<Field> read(Sheet sheet) throws IOException {

        System.out.println(sheet.getLastRowNum());
        List<Field> result = new ArrayList<>();


        System.out.println(sheet.getSheetName());

        for (Row currentRow : sheet) {

            if (currentRow.getRowNum() < 6) {
                continue;
            }

            if (StringUtils.isEmpty(currentRow.getCell(Field.INDEX_COMMENT).getStringCellValue())) {
                break;
            }

            Field field = new Field();

            field.setComment(currentRow.getCell(Field.INDEX_COMMENT).getStringCellValue());
            field.setType(currentRow.getCell(Field.INDEX_TYPE).getStringCellValue());
            field.setLevel((int) currentRow.getCell(Field.INDEX_LEVEL).getNumericCellValue());
            field.setJavaName(
                    currentRow.getCell(Field.INDEX_JAVANAME).getStringCellValue().toLowerCase());

            result.add(field);
        }

        return result;
    }

    public static String upeer(String value) {
        String[] list = value.toLowerCase().split("_");
        String result = "";
        for (String str : list) {
            if (str.length() == 1) {

            } else {
                str = str.substring(0, 1).toUpperCase() + str.substring(1);
            }
            result += str;
        }
        return result.substring(0, 1).toLowerCase() + result.substring(1);
    }

    public static void main(String[] args) throws IOException, TemplateException {
        String fileName = "E:\\hejin\\Dao\\CCS スキーマ定義\\【CCS】MLスキーマ定義_Data_技能者情報.xlsm";
        String sheetName = "技能者情報";

        // String fileName =
        // "C:\\Users\\hejin\\ownCloud\\MCBG-FUJIFILM\\10_ノア作成\\80_参考資料\\Dao\\CCS
        // スキーマ定義\\【CCS】MLスキーマ定義_Data_現場契約情報.xlsm";
        // String sheetName = "現場契約情報";

        String template_dir = "src\\main\\resources\\template";
        Configuration cfg = new Configuration();
        cfg.setDirectoryForTemplateLoading(new File(template_dir));
        Template template = cfg.getTemplate("Entity.ftl", "UTF-8");

        Workbook workbook = ExcelUtils.open(fileName);

        Sheet sheet = workbook.getSheet(sheetName);
        String classComment = sheet.getRow(1).getCell(2).getStringCellValue();
        String className = sheet.getRow(2).getCell(2).getStringCellValue();

        Entity entityTop = new Entity();
        entityTop.setClassComment(classComment);
        entityTop.setClassName(changeClassName(className));
        List<Field> fieldListTop = new ArrayList<>();
        entityTop.setField(fieldListTop);

        List<Field> fieldList = read(sheet);

        for (int j = 0; j < fieldList.size(); j++) {
            Field field = fieldList.get(j);
            if (field.getLevel() == 2) {
                fieldListTop.add(field);
            }
        }
        generateClassFile(template, entityTop);

        int maxLevel = 0;
        for (Field field : fieldList) {
            if (field.getLevel() > maxLevel) {
                maxLevel = field.getLevel();
            }
        }

        // for (int i = 3; i <= maxLevel; i++) {
        // Entity entity = new Entity();
        // for (int j = 0; j < fieldList.size(); j++) {
        // Field field = fieldList.get(j);
        //
        // if (field.getLevel() == i) {
        // Field preField = fieldList.get(j - 1);
        // Field nextField = null;
        // if (fieldList.size() > (j + 1)) {
        // nextField = fieldList.get(j + 1);
        // } else {
        // nextField = null;
        // }
        // if (preField.getLevel() != i) {
        // entity.setClassComment(preField.getComment());
        // entity.setClassName(changeClassName(preField.getJavaName()));
        // }
        // entity.getField().add(field);
        //
        // if (nextField == null || nextField.getLevel() != i) {
        // generateClassFile(template, entity);
        // entity = new Entity();
        // }
        //
        // }
        // }
        // }

        for (int i = 2; i <= (maxLevel - 1); i++) {
            Entity entity = new Entity();
            for (int j = 0; j < fieldList.size(); j++) {
                Field preField = null;
                Field field = fieldList.get(j);
                Field nextField = null;
                int nextLevel = 0;
                if ((j - 1) > 0) {
                    preField = fieldList.get(j - 1);
                }
                if (fieldList.size() > (j + 1)) {
                    nextField = fieldList.get(j + 1);
                    nextLevel = nextField.getLevel();
                } else {
                    nextField = null;
                }

                if (field.getLevel() == i) {

                    if (!StringUtils.isEmpty(entity.getClassName())) {
                        generateClassFile(template, entity);
                        entity = new Entity();
                    }

                    if (nextLevel == i) {
                        continue;
                    }

                }
                if (field.getLevel() == (i + 1)) {
                    if (preField != null && preField.getLevel() == i) {
                        entity.setClassComment(preField.getComment());
                        entity.setClassName(changeClassName(preField.getJavaName()));
                    }
                    entity.getField().add(field);
                }

            }
            if (!StringUtils.isEmpty(entity.getClassName())) {
                generateClassFile(template, entity);
                entity = new Entity();
            }
        }
    }

    private static void generateClassFile(Template template, Entity entityTop)
            throws UnsupportedEncodingException, FileNotFoundException, TemplateException,
            IOException {
        Writer out = new OutputStreamWriter(
                new FileOutputStream("E:\\hejin\\apiInput\\" + entityTop.getClassName() + ".java"),
                "UTF-8");
        Map<String, Object> data = new HashMap<>();
        data.put("classComment", entityTop.getClassComment());
        data.put("className", entityTop.getClassName());
        data.put("fields", entityTop.getField());
        template.process(data, out);
    }

    private static String changeClassName(String oldName) {

        StringBuilder sb = new StringBuilder();
        sb.append(oldName.substring(0, 1).toUpperCase());

        for (int i = 1; i < oldName.length(); i++) {
            if ("_".equals(oldName.substring(i, i + 1))) {
                i++;
                sb.append(oldName.substring(i, i + 1).toUpperCase());
            } else {
                sb.append(oldName.substring(i, i + 1).toLowerCase());
            }
        }
        return sb.toString();
    }

}
