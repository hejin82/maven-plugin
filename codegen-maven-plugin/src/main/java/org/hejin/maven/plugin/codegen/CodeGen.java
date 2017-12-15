package org.hejin.maven.plugin.codegen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.codehaus.plexus.util.StringUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class CodeGen {

    private static int JAVA_COMMENT_INDEX = 0;
    private static int JAVA_NAME_INDEX = 6;
    private static int JSON_NAME_INDEX = 7;
    private static int JAVA_TYPE_INDEX = 8;
    private static int LEVEL_INDEX = 9;
    private static int SEARCH_INDEX = 10;
    private static int INSERT_INDEX = 11;
    private static int UPDATE_INDEX = 12;
    private static int MULTIPLE_INDEX = 13;

    public static void main(String[] args) throws IOException, TemplateException {

        String fileName = "E:\\hejin\\apiInput\\POJOフォーマット.xlsx";
        // new ExcelReader().read(fileName);

        String template_dir =
                "C:\\Users\\hejin\\eclipse-workspace\\codegen-maven-plugin\\src\\main\\resources\\template";
        Configuration cfg = new Configuration();
        cfg.setDirectoryForTemplateLoading(new File(template_dir));
        Template template = cfg.getTemplate("Entity.ftl", "UTF-8");

        Workbook workbook = ExcelUtils.open(fileName);

        List<List<Field>> allList = new ArrayList<>();
        for (Sheet sheet : workbook) {
            System.out.println(sheet.getLastRowNum());
            System.out.println(sheet.getSheetName());

            List<Field> fieldList = new ArrayList<>();

            String className1 = sheet.getRow(1).getCell(JAVA_NAME_INDEX).getStringCellValue();
            String classComment1 = sheet.getRow(1).getCell(JAVA_COMMENT_INDEX).getStringCellValue();

            for (Row currentRow : sheet) {
                if (currentRow.getRowNum() == 0) {
                    continue;
                }
                if (currentRow.getRowNum() == 1) {
                    continue;
                }
                Field field = getField(currentRow);

                fieldList.add(field);
            }

            List<Field> searchList1 = new ArrayList<>();
            List<Field> insertList1 = new ArrayList<>();
            List<Field> updateList1 = new ArrayList<>();

            for (int i = 0; i < fieldList.size(); i++) {
                Field field = fieldList.get(i);
                if (field.isSearch()) {
                    if (field.getLevel() == 1) {
                        searchList1.add(field);
                    }
                }
                if (field.isInsert()) {
                    if (field.getLevel() == 1) {
                        insertList1.add(field);
                    }
                }
                if (field.isUpdate()) {
                    if (field.getLevel() == 1) {
                        updateList1.add(field);
                    }
                }
            }

            outputClass(template, searchList1, className1 + "SearchDto", classComment1 + "閲覧DTO");
            outputClass(template, insertList1, className1 + "InsertDto", classComment1 + "登録DTO");
            outputClass(template, updateList1, className1 + "UpdateDto", classComment1 + "更新DTO");

            List<Field> searchList2 = new ArrayList<>();
            String searchClassName2 = "";
            String searchClassComment2 = "";

            List<Field> insertList2 = new ArrayList<>();
            String insertClassName2 = "";
            String insertClassComment2 = "";

            List<Field> updateList2 = new ArrayList<>();
            String updateClassName2 = "";
            String updateClassComment2 = "";

            List<Field> fieldList2 = new ArrayList<>();
            for (int i = 0; i < fieldList.size(); i++) {
                Field field = fieldList.get(i);
                if (field.getLevel() <= 2) {
                    fieldList2.add(field);
                }
            }

            for (int i = 0; i < fieldList2.size(); i++) {
                Field preSearhField = null;
                Field preInsertField = null;
                Field preUpdateField = null;
                Field field = fieldList2.get(i);

                if (field.getLevel() == 2) {
                    if (field.isSearch()) {
                        // preSearhField = fieldList2.get(i - 1);
                        preSearhField = getPreField(fieldList2, i, 1);
                        if (preSearhField.getLevel() == 1) {
                            if (StringUtils.isEmpty(searchClassName2)) {
                                searchClassName2 = preSearhField.getJavaName();
                                searchClassComment2 = preSearhField.getComment();
                            }
                        }
                        searchList2.add(field);
                    }
                } else {
                    if (searchList2 != null && searchList2.size() > 0) {
                        outputClass(template, searchList2, searchClassName2 + "SearchDto",
                                classComment1 + "閲覧-" + searchClassComment2 + "DTO");
                        searchClassName2 = "";
                        searchClassComment2 = "";
                        searchList2 = new ArrayList<>();
                    }
                }

                if (field.getLevel() == 2) {
                    if (field.isInsert()) {
                        // preInsertField = fieldList.get(i - 1);
                        preInsertField = getPreField(fieldList2, i, 1);
                        if (preInsertField.getLevel() == 1) {
                            if (StringUtils.isEmpty(insertClassName2)) {
                                insertClassName2 = preInsertField.getJavaName();
                                insertClassComment2 = preInsertField.getComment();
                            }
                        }
                        insertList2.add(field);
                    }
                } else {
                    if (insertList2 != null && insertList2.size() > 0) {
                        outputClass(template, insertList2, insertClassName2 + "InsertDto",
                                classComment1 + "登録-" + insertClassComment2 + "DTO");
                        insertClassName2 = "";
                        insertClassComment2 = "";
                        insertList2 = new ArrayList<>();
                    }
                }

                if (field.getLevel() == 2) {
                    if (field.isUpdate()) {
                        // preUpdateField = fieldList.get(i - 1);
                        preUpdateField = getPreField(fieldList2, i, 1);
                        if (preUpdateField.getLevel() == 1) {
                            if (StringUtils.isEmpty(updateClassName2)) {
                                updateClassName2 = preUpdateField.getJavaName();
                                updateClassComment2 = preUpdateField.getComment();
                            }
                        }
                        updateList2.add(field);
                    }
                } else {
                    if (updateList2 != null && updateList2.size() > 0) {
                        outputClass(template, updateList2, updateClassName2 + "UpdateDto",
                                classComment1 + "更新-" + updateClassComment2 + "DTO");
                        updateClassName2 = "";
                        updateClassComment2 = "";
                        updateList2 = new ArrayList<>();
                    }
                }
            }

            List<Field> searchList3 = new ArrayList<>();
            String searchClassName3 = "";
            String searchClassComment3 = "";

            List<Field> insertList3 = new ArrayList<>();
            String insertClassName3 = "";
            String insertClassComment3 = "";

            List<Field> updateList3 = new ArrayList<>();
            String updateClassName3 = "";
            String updateClassComment3 = "";

            List<Field> fieldList3 = new ArrayList<>();
            for (int i = 0; i < fieldList.size(); i++) {
                Field field = fieldList.get(i);
                if (field.getLevel() <= 3) {
                    fieldList3.add(field);
                }
            }

            for (int i = 0; i < fieldList3.size(); i++) {
                Field preSearhField = null;
                Field preInsertField = null;
                Field preUpdateField = null;
                Field field = fieldList3.get(i);

                if (field.getLevel() == 3) {
                    if (field.isSearch()) {
                        // preSearhField = fieldList3.get(i - 1);
                        preSearhField = getPreField(fieldList3, i, 2);
                        if (preSearhField.getLevel() == 2) {
                            if (StringUtils.isEmpty(searchClassName3)) {
                                searchClassName3 = preSearhField.getJavaName();
                                searchClassComment3 = preSearhField.getComment();
                            }
                        }
                        searchList3.add(field);
                    }
                } else {
                    if (searchList3 != null && searchList3.size() > 0) {
                        outputClass(template, searchList3, searchClassName3 + "SearchDto",
                                classComment1 + "閲覧-" + searchClassComment3 + "DTO");
                    }
                    searchClassName3 = "";
                    searchClassComment3 = "";
                    searchList3 = new ArrayList<>();
                }

                if (field.getLevel() == 3) {
                    if (field.isInsert()) {
                        // preInsertField = fieldList3.get(i - 1);
                        preInsertField = getPreField(fieldList3, i, 2);
                        if (preInsertField.getLevel() == 2) {
                            if (StringUtils.isEmpty(insertClassName3)) {
                                insertClassName3 = preInsertField.getJavaName();
                                insertClassComment3 = preInsertField.getComment();
                            }
                        }
                        insertList3.add(field);
                    }
                } else {
                    if (insertList3 != null && insertList3.size() > 0) {
                        outputClass(template, insertList3, insertClassName3 + "InsertDto",
                                classComment1 + "登録-" + insertClassComment3 + "DTO");
                    }
                    insertClassName3 = "";
                    insertClassComment3 = "";
                    insertList3 = new ArrayList<>();
                }

                if (field.getLevel() == 3) {
                    if (field.isUpdate()) {
                        // preUpdateField = fieldList3.get(i - 1);
                        preUpdateField = getPreField(fieldList3, i, 2);
                        if (preUpdateField.getLevel() == 2) {
                            if (StringUtils.isEmpty(updateClassName3)) {
                                updateClassName3 = preUpdateField.getJavaName();
                                updateClassComment3 = preUpdateField.getComment();
                            }
                        }
                        updateList3.add(field);
                    }
                } else {
                    if (updateList3 != null && updateList3.size() > 0) {
                        outputClass(template, updateList3, updateClassName3 + "UpdateDto",
                                classComment1 + "更新-" + updateClassComment3 + "DTO");
                    }
                    updateClassName3 = "";
                    updateClassComment3 = "";
                    updateList3 = new ArrayList<>();
                }
            }

            List<Field> searchList4 = new ArrayList<>();
            String searchClassName4 = "";
            String searchClassComment4 = "";

            List<Field> insertList4 = new ArrayList<>();
            String insertClassName4 = "";
            String insertClassComment4 = "";

            List<Field> updateList4 = new ArrayList<>();
            String updateClassName4 = "";
            String updateClassComment4 = "";

            List<Field> fieldList4 = new ArrayList<>();
            for (int i = 0; i < fieldList.size(); i++) {
                Field field = fieldList.get(i);
                if (field.getLevel() <= 4) {
                    fieldList4.add(field);
                }
            }
            for (int i = 0; i < fieldList4.size(); i++) {
                Field preSearhField = null;
                Field preInsertField = null;
                Field preUpdateField = null;
                Field field = fieldList.get(i);

                if (field.getLevel() == 4) {
                    if (field.isSearch()) {
                        // preSearhField = fieldList.get(i - 1);
                        preSearhField = getPreField(fieldList, i, 3);
                        if (preSearhField.getLevel() == 3) {
                            if (StringUtils.isEmpty(searchClassName4)) {
                                searchClassName4 = preSearhField.getJavaName();
                                searchClassComment4 = preSearhField.getComment();
                            }
                        }
                        searchList4.add(field);
                    }
                } else {
                    if (searchList4 != null && searchList4.size() > 0) {
                        outputClass(template, searchList4, searchClassName4 + "SearchDto",
                                classComment1 + "閲覧-" + searchClassComment4 + "DTO");
                    }
                    searchClassName4 = "";
                    searchClassComment4 = "";
                    searchList4 = new ArrayList<>();
                }

                if (field.getLevel() == 4) {
                    if (field.isInsert()) {
                        // preInsertField = fieldList.get(i - 1);
                        preInsertField = getPreField(fieldList, i, 3);
                        if (preInsertField.getLevel() == 3) {
                            if (StringUtils.isEmpty(insertClassName4)) {
                                insertClassName4 = preInsertField.getJavaName();
                                insertClassComment4 = preInsertField.getComment();
                            }
                        }
                        insertList4.add(field);
                    }
                } else {
                    if (insertList4 != null && insertList4.size() > 0) {
                        outputClass(template, insertList4, insertClassName4 + "InsertDto",
                                classComment1 + "登録-" + insertClassComment4 + "DTO");
                    }
                    insertClassName4 = "";
                    insertClassComment4 = "";
                    insertList4 = new ArrayList<>();
                }

                if (field.getLevel() == 4) {
                    if (field.isUpdate()) {
                        // preUpdateField = fieldList.get(i - 1);
                        preUpdateField = getPreField(fieldList, i, 3);
                        if (preUpdateField.getLevel() == 3) {
                            if (StringUtils.isEmpty(updateClassName4)) {
                                updateClassName4 = preUpdateField.getJavaName();
                                updateClassComment4 = preUpdateField.getComment();
                            }
                        }
                        updateList4.add(field);
                    }
                } else {
                    if (updateList4 != null && updateList4.size() > 0) {
                        outputClass(template, updateList4, updateClassName4 + "UpdateDto",
                                classComment1 + "更新-" + updateClassComment4 + "DTO");
                    }
                    updateClassName4 = "";
                    updateClassComment4 = "";
                    updateList4 = new ArrayList<>();
                }
            }

            List<Field> searchList5 = new ArrayList<>();
            String searchClassName5 = "";
            String searchClassComment5 = "";

            List<Field> insertList5 = new ArrayList<>();
            String insertClassName5 = "";
            String insertClassComment5 = "";

            List<Field> updateList5 = new ArrayList<>();
            String updateClassName5 = "";
            String updateClassComment5 = "";

            List<Field> fieldList5 = new ArrayList<>();
            for (int i = 0; i < fieldList.size(); i++) {
                Field field = fieldList.get(i);
                if (field.getLevel() <= 5) {
                    fieldList5.add(field);
                }
            }
            for (int i = 0; i < fieldList5.size(); i++) {
                Field preSearhField = null;
                Field preInsertField = null;
                Field preUpdateField = null;
                Field field = fieldList.get(i);

                if (field.getLevel() == 5) {
                    if (field.isSearch()) {
                        // preSearhField = fieldList.get(i - 1);
                        preSearhField = getPreField(fieldList, i, 4);
                        if (preSearhField.getLevel() == 4) {
                            if (StringUtils.isEmpty(searchClassName5)) {
                                searchClassName5 = preSearhField.getJavaName();
                                searchClassComment5 = preSearhField.getComment();
                            }
                        }
                        searchList5.add(field);
                    }
                } else {
                    if (searchList5 != null && searchList5.size() > 0) {
                        outputClass(template, searchList5, searchClassName5 + "SearchDto",
                                classComment1 + "閲覧-" + searchClassComment5 + "DTO");
                    }
                    searchClassName5 = "";
                    searchClassComment5 = "";
                    searchList5 = new ArrayList<>();
                }

                if (field.getLevel() == 5) {
                    if (field.isInsert()) {
                        // preInsertField = fieldList.get(i - 1);
                        preInsertField = getPreField(fieldList, i, 4);
                        if (preInsertField.getLevel() == 4) {
                            if (StringUtils.isEmpty(insertClassName5)) {
                                insertClassName5 = preInsertField.getJavaName();
                                insertClassComment5 = preInsertField.getComment();
                            }
                        }
                        insertList5.add(field);
                    }
                } else {
                    if (insertList5 != null && insertList5.size() > 0) {
                        outputClass(template, insertList5, insertClassName5 + "InsertDto",
                                classComment1 + "登録-" + insertClassComment5 + "DTO");
                    }
                    insertClassName5 = "";
                    insertClassComment5 = "";
                    insertList5 = new ArrayList<>();
                }

                if (field.getLevel() == 5) {
                    if (field.isUpdate()) {
                        // preUpdateField = fieldList.get(i - 1);
                        preUpdateField = getPreField(fieldList, i, 4);
                        if (preUpdateField.getLevel() == 4) {
                            if (StringUtils.isEmpty(updateClassName5)) {
                                updateClassName5 = preUpdateField.getJavaName();
                                updateClassComment5 = preUpdateField.getComment();
                            }
                        }
                        updateList5.add(field);
                    }
                } else {
                    if (updateList5 != null && updateList5.size() > 0) {
                        outputClass(template, updateList5, updateClassName5 + "UpdateDto",
                                classComment1 + "更新-" + updateClassComment5 + "DTO");
                    }
                    updateClassName5 = "";
                    updateClassComment5 = "";
                    updateList5 = new ArrayList<>();
                }
            }


            List<Field> searchList6 = new ArrayList<>();
            String searchClassName6 = "";
            String searchClassComment6 = "";

            List<Field> insertList6 = new ArrayList<>();
            String insertClassName6 = "";
            String insertClassComment6 = "";

            List<Field> updateList6 = new ArrayList<>();
            String updateClassName6 = "";
            String updateClassComment6 = "";

            List<Field> fieldList6 = new ArrayList<>();
            for (int i = 0; i < fieldList.size(); i++) {
                Field field = fieldList.get(i);
                if (field.getLevel() <= 6) {
                    fieldList6.add(field);
                }
            }
            for (int i = 0; i < fieldList6.size(); i++) {
                Field preSearhField = null;
                Field preInsertField = null;
                Field preUpdateField = null;
                Field field = fieldList.get(i);

                if (field.getLevel() == 6) {
                    if (field.isSearch()) {
                        // preSearhField = fieldList.get(i - 1);
                        preSearhField = getPreField(fieldList, i, 5);
                        if (preSearhField.getLevel() == 5) {
                            if (StringUtils.isEmpty(searchClassName6)) {
                                searchClassName6 = preSearhField.getJavaName();
                                searchClassComment6 = preSearhField.getComment();
                            }
                        }
                        searchList6.add(field);
                    }
                } else {
                    if (searchList6 != null && searchList6.size() > 0) {
                        outputClass(template, searchList6, searchClassName6 + "SearchDto",
                                classComment1 + "閲覧-" + searchClassComment6 + "DTO");
                    }
                    searchClassName6 = "";
                    searchClassComment6 = "";
                    searchList6 = new ArrayList<>();
                }

                if (field.getLevel() == 6) {
                    if (field.isInsert()) {
                        // preInsertField = fieldList.get(i - 1);
                        preInsertField = getPreField(fieldList, i, 5);
                        if (preInsertField.getLevel() == 5) {
                            if (StringUtils.isEmpty(insertClassName6)) {
                                insertClassName6 = preInsertField.getJavaName();
                                insertClassComment6 = preInsertField.getComment();
                            }
                        }
                        insertList6.add(field);
                    }
                } else {
                    if (insertList6 != null && insertList6.size() > 0) {
                        outputClass(template, insertList6, insertClassName6 + "InsertDto",
                                classComment1 + "登録-" + insertClassComment6 + "DTO");
                    }
                    insertClassName6 = "";
                    insertClassComment6 = "";
                    insertList6 = new ArrayList<>();
                }

                if (field.getLevel() == 6) {
                    if (field.isUpdate()) {
                        // preUpdateField = fieldList.get(i - 1);
                        preUpdateField = getPreField(fieldList, i, 5);
                        if (preUpdateField.getLevel() == 5) {
                            if (StringUtils.isEmpty(updateClassName6)) {
                                updateClassName6 = preUpdateField.getJavaName();
                                updateClassComment6 = preUpdateField.getComment();
                            }
                        }
                        updateList6.add(field);
                    }
                } else {
                    if (updateList6 != null && updateList6.size() > 0) {
                        outputClass(template, updateList6, updateClassName6 + "UpdateDto",
                                classComment1 + "更新-" + updateClassComment6 + "DTO");
                    }
                    updateClassName6 = "";
                    updateClassComment6 = "";
                    updateList6 = new ArrayList<>();
                }
            }

            allList.add(fieldList);
        }

    }

    private static Field getPreField(List<Field> fieldList, int i, int level) {
        int j = 0;
        do {
            j++;
        } while (!(fieldList.get(i - j).getLevel() == level));
        return fieldList.get(i - j);


    }

    private static void outputClass(Template template, List<Field> fieldList, String className,
            String classComment) throws UnsupportedEncodingException, FileNotFoundException,
            TemplateException, IOException {

        if (fieldList == null || fieldList.size() == 0) {
            return;
        }

        Writer out = new OutputStreamWriter(new FileOutputStream("E:\\hejin\\apiInput\\"
                + className.substring(0, 1).toUpperCase() + className.substring(1) + ".java"),
                "UTF-8");
        Map<String, Object> data = new HashMap<>();
        data.put("classComment", classComment);
        data.put("className", className);
        data.put("fields", fieldList);
        template.process(data, out);
    }

    private static Field getField(Row currentRow) {
        Field field = new Field();
        int level = (int) currentRow.getCell(LEVEL_INDEX).getNumericCellValue();
        String javaComment =
                currentRow.getCell(JAVA_COMMENT_INDEX - 1 + level).getStringCellValue();
        String javaName = currentRow.getCell(JAVA_NAME_INDEX).getStringCellValue();
        String jsonName = currentRow.getCell(JSON_NAME_INDEX).getStringCellValue();
        String javaType = null;
        if ("○".equals(currentRow.getCell(MULTIPLE_INDEX).getStringCellValue())) {
            String value = currentRow.getCell(JAVA_TYPE_INDEX).getStringCellValue();
            javaType = "List<" + value.substring(0, 1).toUpperCase() + value.substring(1) + ">";
        } else {
            javaType = currentRow.getCell(JAVA_TYPE_INDEX).getStringCellValue();
        }

        String search = currentRow.getCell(SEARCH_INDEX).getStringCellValue();
        String insert = currentRow.getCell(INSERT_INDEX).getStringCellValue();
        String update = currentRow.getCell(UPDATE_INDEX).getStringCellValue();

        field.setComment(javaComment);
        field.setJavaName(javaName);
        field.setJavaType(javaType);
        field.setJsonName(jsonName);
        field.setLevel(level);
        field.setSearch("-".equals(search) ? false : true);
        field.setInsert("-".equals(insert) ? false : true);
        field.setUpdate("-".equals(update) ? false : true);
        return field;
    }
}
