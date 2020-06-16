package com.easyframework.excel.manager;

import com.alibaba.fastjson.JSON;
import com.easydictionary.core.annotation.table.ModelField;
import com.easydictionary.core.annotation.table.TableModel;
import com.easydictionary.core.model.ExcelHeadModel;
import com.easydictionary.core.util.NumberUtil;
import com.easydictionary.core.util.StringUtil;
import com.easydictionary.core.util.TableFieldUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HeaderFooter;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class ExcelManager {

    private static final String TOTAL_TEXT = "汇总：";
    private static final int startRow = 0;
    private static final int maxRow = 100000;// 每行最大条数
    private static final int startCol = 0;
    private static final String FILE_TYPE = ".xlsx";
    private static String excelTitle = "";
    public static SimpleDateFormat defaultformat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
    public static SimpleDateFormat localDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 标题样式
     *
     * @return
     */
    private static XSSFCellStyle makeTitleStyle(SXSSFWorkbook wb) {

        XSSFCellStyle titleStyle = (XSSFCellStyle) wb.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        // titleStyle.setFillBackgroundColor(HSSFColor.WHITE.index);
        Font font = wb.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setFontHeight((short) 400);
        titleStyle.setFont(font);
        return titleStyle;
    }

    /**
     * 表头样式
     *
     * @return
     */
    private static XSSFCellStyle makeHeadStyle(SXSSFWorkbook wb) {
        XSSFCellStyle headStyle = (XSSFCellStyle) wb.createCellStyle();
        headStyle.setAlignment(HorizontalAlignment.CENTER);

        Font headFont = wb.createFont();
        headFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        headFont.setFontHeight((short) 240);
        headStyle.setFont(headFont);
        // headStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        // headStyle.setFillBackgroundColor(HSSFColor.WHITE.index);
        headStyle.setBorderBottom(BorderStyle.THIN); // 下边框
        headStyle.setBorderLeft(BorderStyle.THIN);// 左边框
        headStyle.setBorderTop(BorderStyle.THIN);// 上边框
        headStyle.setBorderRight(BorderStyle.THIN);// 右边框
        return headStyle;
    }

    /**
     * 备注行样式
     *
     * @return
     */
    private static XSSFCellStyle makeDescStyle(SXSSFWorkbook wb) {
        XSSFCellStyle headStyle = (XSSFCellStyle) wb.createCellStyle();
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        Font headFont = wb.createFont();
        headFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        headFont.setFontHeight((short) 240);
        headStyle.setFont(headFont);
        return headStyle;
    }

    private static XSSFCellStyle makeDataStyle(SXSSFWorkbook wb) {
        XSSFCellStyle dataStyle = (XSSFCellStyle) wb.createCellStyle();
        dataStyle.setFillBackgroundColor(HSSFColor.WHITE.index);
        Font dataFont = wb.createFont();
        dataFont.setFontHeight((short) 180);
        dataStyle.setFont(dataFont);
        // dataStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        // dataStyle.setFillBackgroundColor(HSSFColor.WHITE.index);
        dataStyle.setBorderBottom(BorderStyle.THIN); // 下边框
        dataStyle.setBorderLeft(BorderStyle.THIN);// 左边框
        dataStyle.setBorderTop(BorderStyle.THIN);// 上边框
        dataStyle.setBorderRight(BorderStyle.THIN);// 右边框

        dataStyle.setAlignment(HorizontalAlignment.CENTER);
        return dataStyle;
    }

    /**
     * 设置打印样式
     *
     * @return
     */
    private static void printSettings(Sheet sheet) {
        // 打印设置
        PrintSetup hps = sheet.getPrintSetup();
        hps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
        sheet.setMargin(Sheet.BottomMargin, 0.5);// 页边距（下）
        sheet.setMargin(Sheet.LeftMargin, 0.1);// 页边距（左）
        sheet.setMargin(Sheet.RightMargin, 0.1);// 页边距（右）
        sheet.setMargin(Sheet.TopMargin, 0.5);// 页边距（上）
        sheet.setHorizontallyCenter(true);// 设置打印页面为水平居中
        sheet.setFitToPage(false);
        Footer footer = sheet.getFooter();
        footer.setCenter("第" + HeaderFooter.page() + "页，共 " + HeaderFooter.numPages() + "页");
    }

    /**
     * 设置标题行
     *
     * @param sheet
     * @param title
     * @param style
     * @param rowIndex
     * @param colSpan
     */
    private static Cell fillTitleCell(Sheet sheet, String title, XSSFCellStyle style, int rowIndex, int colSpan) {
        Cell titleCell = sheet.createRow(rowIndex).createCell(startCol); // 创建第一行，并在该行创建单元格，设置内容，做为标题行
        titleCell.setCellValue(new HSSFRichTextString(title));
        // 合并表标题行
        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, startCol, startCol + colSpan - 1));
        titleCell.setCellStyle(style);
        return titleCell;
    }

    /**
     * 设置备注行
     *
     * @return
     */
    private static Cell fillDescCell(Sheet sheet, String desc, XSSFCellStyle style, int rowIndex, int colSpan) {

        Row descRow = sheet.createRow(rowIndex);
        Cell descCell = descRow.createCell(startCol); // 创建第二行，并在该行创建单元格，设置内容，做为标题行
        descCell.setCellValue(new HSSFRichTextString(desc));
        descCell.setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, startCol, startCol + colSpan - 1));
        return descCell;
    }

    /**
     * 设置打印备注
     *
     * @return
     */
    private static Cell setPrintDescCell(Sheet sheet, String desc,XSSFCellStyle style,int rowIndex, int colSpan) {
        XSSFCellStyle style2 =(XSSFCellStyle) sheet.getWorkbook().createCellStyle();
        style2.setAlignment(HorizontalAlignment.CENTER);

        Row descRow = sheet.createRow(rowIndex);
        Cell descCell = descRow.createCell(startCol); // 创建第二行，并在该行创建单元格，设置内容，做为标题行
        descCell.setCellValue(new XSSFRichTextString (desc));
        descCell.setCellStyle(style2);
        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, startCol, startCol + colSpan - 1));
        return descCell;
    }

    /**
     * 填充数据
     *
     * @return 行坐标
     */
    private static int fillDataCell(Sheet sheet, LinkedHashMap<Field, ExcelHeadModel> leafHeadModel, List<?> dataList,
                                    XSSFCellStyle style, SXSSFWorkbook wb, int rowIndex)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, ParseException {
        long startTime = System.currentTimeMillis();

        // 这里要注意下，本来格式转换代码是写在下面循环里的，有残留，这里拿出来单独处理，也可以按照自己想法做
        XSSFDataFormat df = (XSSFDataFormat) wb.createDataFormat();
        XSSFCellStyle contentStyleInteger = makeDataStyle(wb);

        contentStyleInteger.setDataFormat(df.getFormat("#0"));
        // 数据格式只显示整数
        XSSFCellStyle contentStyleDecimal = makeDataStyle(wb);

        contentStyleDecimal.setDataFormat(df.getFormat("##0.00"));// 保留两位小数点

        for (int i = 0, L = dataList.size(); i < L; i++) {
            if (i % 100 == 0) {
                long endTime = System.currentTimeMillis();
                log.info("生成数据  行数:{},耗时：{}", String.valueOf(i), String.valueOf(endTime - startTime));
                startTime = endTime;
            }
            /*
             * if (rowIndex > maxRow) { break; }
             */
            Row row = sheet.createRow(rowIndex++);
            Iterator<Field> iterator = leafHeadModel.keySet().iterator();
            while (iterator.hasNext()) {
                Field field = iterator.next();
                ExcelHeadModel headModel = leafHeadModel.get(field);
                Cell cell = row.createCell(headModel.getColIndex());
                // cell.setCellStyle(style);
                field.setAccessible(true);

                // String value =
                // ModelTableManager.convertPropertyValue(headModel.getTableHeader(), field,
                // field.get(dataList.get(i)));
                String value = fillExcelPropertyValue(headModel, field, field.get(dataList.get(i)));

                if (value != null && ("id".equals(field.getName()) || "vehicleId".equals(field.getName())
                        || "unitCode".equals(field.getName()) || value.length() >= 12)) {
                    cell.setCellStyle(style);
                    cell.setCellValue(new HSSFRichTextString(value));
                } else {
                    Boolean isNum = false;// data是否为数值型
                    Boolean isInteger = false;// data是否为整数
                    Boolean isPercent = false;// data是否为百分数

                    if (value != null || "".equals(value)) { // 判断data是否为数值型
                        isNum = value.toString().matches("^(-?\\d+)(\\.\\d+)?$"); // 判断data是否为整数（小数部分是否为0）
                        isInteger = value.toString().matches("^[-\\+]?[\\d]*$"); // 判断data是否为百分数（是否包含“%”）
                        isPercent = value.toString().contains("%");
                    }

                    // 如果单元格内容是数值类型，涉及到金钱（金额、本、利），则设置cell的类型为数值型，设置data的类型为数值类型
                    if (isNum && !isPercent) {
                        if (isInteger) {
                            cell.setCellStyle(contentStyleInteger);
                            cell.setCellValue(NumberUtils.toDouble(value));

                            if (value.length() > 1 && value.startsWith("0")) {
                                cell.setCellStyle(style);
                                cell.setCellValue(new HSSFRichTextString(value));
                            } else {
                                cell.setCellStyle(contentStyleInteger);
                                cell.setCellValue(NumberUtils.toDouble(value));
                            }

                        } else {
                            cell.setCellStyle(contentStyleDecimal); // 设置单元格内容为double类型
                            // cell.setCellValue(Double.parseDouble(value));
                            cell.setCellValue(NumberUtils.toDouble(value));
                        }

                    } else {
                        cell.setCellStyle(style);
                        cell.setCellValue(new HSSFRichTextString(value));
                    }
                }

                // cell.setCellStyle(style);
                /// cell.setCellValue(new HSSFRichTextString(value));
                calcCellWidth(sheet, headModel, style.getFont(), value);
            }
        }
        return rowIndex;
    }

    /**
     * 设置列宽
     *
     * @return 行坐标
     */
    private static void fillCellWidth(Sheet sheet, LinkedHashMap<Field, ExcelHeadModel> leafHeadModel) {
        Iterator<Field> iterator = leafHeadModel.keySet().iterator();
        while (iterator.hasNext()) {
            Field field = iterator.next();
            ExcelHeadModel headModel = leafHeadModel.get(field);
            sheet.setColumnWidth(headModel.getColIndex(), headModel.getWidth() < 20000 ? headModel.getWidth() : 20000);
        }
    }

    /**
     * 填充汇总行
     *
     * @return 行坐标
     */
    private static int fillTotalCell(Sheet sheet, LinkedHashMap<Field, ExcelHeadModel> leafHeadModel,
                                     Map<String, Object> totals, XSSFCellStyle style, SXSSFWorkbook wb, int rowIndex) throws ParseException {
        Row totalRow = sheet.createRow(rowIndex++);
        Iterator<Field> iterator = leafHeadModel.keySet().iterator();
        boolean showTotalText = false;
        boolean hasSpace = true;

        // 这里要注意下，本来格式转换代码是写在下面循环里的，有残留，这里拿出来单独处理，也可以按照自己想法做
        XSSFDataFormat df = (XSSFDataFormat) wb.createDataFormat();
        XSSFCellStyle contentStyleInteger = makeDataStyle(wb);

        contentStyleInteger.setDataFormat(df.getFormat("#0"));
        // 数据格式只显示整数
        XSSFCellStyle contentStyleDecimal = makeDataStyle(wb);

        contentStyleDecimal.setDataFormat(df.getFormat("##0.00"));// 保留两位小数点

        while (iterator.hasNext()) {
            Field field = iterator.next();
            ExcelHeadModel headModel = leafHeadModel.get(field);
            String value = null;
            if (totals != null && totals.containsKey(headModel.getName())) {
                value = totals.get(headModel.getName()).toString();
                showTotalText = true;
            } else if (headModel.isMakeTotal()) {
                value = headModel.getTotal().toString();
                showTotalText = true;
            }

            Cell cell = totalRow.createCell(headModel.getColIndex());

            Boolean isNum = false;// data是否为数值型
            Boolean isInteger = false;// data是否为整数
            Boolean isPercent = false;// data是否为百分数

            if (value != null || "".equals(value)) { // 判断data是否为数值型
                isNum = value.toString().matches("^(-?\\d+)(\\.\\d+)?$"); // 判断data是否为整数（小数部分是否为0）
                isInteger = value.toString().matches("^[-\\+]?[\\d]*$"); // 判断data是否为百分数（是否包含“%”）
                isPercent = value.toString().contains("%");
            }

            // 如果单元格内容是数值类型，涉及到金钱（金额、本、利），则设置cell的类型为数值型，设置data的类型为数值类型
            if (isNum && !isPercent) {
                if (isInteger) {
                    if (value.length() > 1 && value.startsWith("0")) {
                        cell.setCellStyle(style);
                        cell.setCellValue(new HSSFRichTextString(value));
                    } else {
                        cell.setCellStyle(contentStyleInteger);
                        cell.setCellValue(NumberUtils.toDouble(value));
                    }

                } else {
                    cell.setCellStyle(contentStyleDecimal); // 设置单元格内容为double类型
                    // cell.setCellValue(Double.parseDouble(value));
                    cell.setCellValue(NumberUtils.toDouble(value));
                }

            } else {
                cell.setCellStyle(style);
                cell.setCellValue(new HSSFRichTextString(value));
            }

            // cell.setCellStyle(style);
            // cell.setCellValue(new HSSFRichTextString(value));
            calcCellWidth(sheet, headModel, style.getFont(), value);
            if (showTotalText && headModel.getColIndex() == startCol) {
                hasSpace = false;
            }
        }

        if (showTotalText && hasSpace) {
            Cell cell = totalRow.createCell(startCol);
            cell.setCellStyle(style);
            cell.setCellValue(new HSSFRichTextString(TOTAL_TEXT));
        }

        if (!showTotalText) {
            sheet.removeRow(totalRow);
        }
        return rowIndex;
    }

    /**
     * 根据表头模型生成表头
     *
     * @return 行坐标
     */
    private static int fillHeadCell(Sheet sheet, LinkedList<LinkedHashMap<Field, ExcelHeadModel>> headModels,
                                    XSSFCellStyle style, int rowIndex) {
        for (int i = 0; i < headModels.size(); i++) {
            Row headRow = sheet.createRow(rowIndex);
            LinkedHashMap<Field, ExcelHeadModel> heads = headModels.get(i);
            Iterator<ExcelHeadModel> iterator = heads.values().iterator();
            while (iterator.hasNext()) {
                ExcelHeadModel headModel = iterator.next();
                if (headModel.isHidden()) {
                    continue;
                }
                Cell cell = headRow.createCell(headModel.getColIndex());
                cell.setCellValue(new HSSFRichTextString(headModel.getText()));

                if (headModel.getColSpan() != 1 || headModel.getRowSpan() != 1) {
                    int lastCol = headModel.getColIndex() + headModel.getColSpan() - 1;
                    int lastRow = rowIndex + headModel.getRowSpan() - 1;
                    sheet.addMergedRegion(new CellRangeAddress(rowIndex, lastRow, headModel.getColIndex(), lastCol));
                }
                cell.setCellStyle(style);
                if (i == headModels.size() - 1) {
                    if (headModel.getWidth() != 0) {
                        sheet.setColumnWidth(headModel.getColIndex(), headModel.getWidth());
                    } else {
                        calcCellWidth(sheet, headModel, style.getFont(), headModel.getText());
                    }
                }
                // 如果有列合并和行合并 同时设置它们的样式
                for (int j = 0; j < headModel.getColSpan(); j++) {
                    for (int k = 0; k < headModel.getRowSpan(); k++) {
                        if (j > 0 || k > 0) {
                            Row hiddenRow = sheet.getRow(rowIndex + k);
                            if (hiddenRow == null) {
                                hiddenRow = sheet.createRow(rowIndex + k);
                            }
                            Cell hiddenCell = hiddenRow.getCell(headModel.getColIndex() + j);
                            if (hiddenCell == null) {
                                hiddenCell = hiddenRow.createCell(headModel.getColIndex() + j);
                            }
                            hiddenCell.setCellStyle(style);
                        }
                    }
                }

            }
            rowIndex++;
        }
        return rowIndex;
    }

    /**
     * 数据列表头（最底层一行的表头）表头模型
     *
     * @return
     */
    private static LinkedHashMap<Field, ExcelHeadModel> makeDataColumnHeadModels(Class<?> clazz, List<String> columns)
            throws Exception {
        // 先获得最底层级的表头
        LinkedHashMap<Field, ExcelHeadModel> leafHeads = new LinkedHashMap<>();
        for (int i = 0; i < columns.size(); i++) {
            Field field = clazz.getDeclaredField(columns.get(i));
            if (field == null) {
                throw new Exception(clazz.getName() + "类没有" + columns.get(i) + "属性");
            }
            if (!field.isAnnotationPresent(ModelField.class)) {
                throw new Exception(clazz.getName() + "类的" + columns.get(i) + "属性没有加上ExcelProp注解");
            }
            ModelField excelProp = field.getAnnotation(ModelField.class);
            ExcelHeadModel headModel = new ExcelHeadModel(field.getName(), excelProp.name(), startCol + i,
                    excelProp.width());
            headModel.setTableField(excelProp);
            headModel.setAmountFormat(excelProp.amountFormat());
            headModel.setDateFormat(excelProp.dateFormat());
            headModel.setRadixPoint(excelProp.radixPoint());
            headModel.setDict(excelProp.dict());
            headModel.setCh(excelProp.ch());
            if (excelProp.makeTotal()) {
                headModel.setMakeTotal(excelProp.makeTotal());
                headModel.setTotal(new BigDecimal(0));
            }
            leafHeads.put(field, headModel);
        }
        return leafHeads;
    }

    /**
     * 加载多层级表头中的父级表头
     *
     * @param plies
     * @param modelClass
     * @return Map : key表头名称 value合并列数 list.size()= 父级层数
     * @throws Exception
     */
    public static void makeParentHeadModels(LinkedList<LinkedHashMap<Field, ExcelHeadModel>> plies, Class<?> modelClass)
            throws Exception {
        LinkedHashMap<Field, ExcelHeadModel> parentHeads = new LinkedHashMap<>();
        LinkedHashMap<Field, ExcelHeadModel> heads = plies.get(0);

        List<ExcelHeadModel> hiddenHeads = new ArrayList();
        List<ExcelHeadModel> rowSpanHeads = new ArrayList();
        List<ExcelHeadModel> changePliHeads = new ArrayList();
        Map<Field, ExcelHeadModel> addHeads = new HashMap<>();
        List<Field> deleteHeads = new ArrayList<>();
        Iterator<Field> iterator = heads.keySet().iterator();
        boolean hasParent = false;
        while (iterator.hasNext()) {
            Field field = iterator.next();
            if (deleteHeads.contains(field)) {// 已经在删除列表中
                continue;
            }
            ModelField modelProp = field.getAnnotation(ModelField.class);
            ExcelHeadModel headModel = heads.get(field);
            if (!StringUtils.isBlank(modelProp.parent())) {
                Field parentField = modelClass.getDeclaredField(modelProp.parent());
                if (parentField == null) {
                    throw new Exception(modelClass.getName() + "类没有" + parentField.getName() + "属性");
                }
                if (!parentField.isAnnotationPresent(ModelField.class)) {
                    throw new Exception(modelClass.getName() + "类的" + parentField.getName() + "属性没有加上ExcelProp注解");
                }
                if (parentHeads.containsKey(parentField)) {// 父级头层已经有此属性得父级表头
                    ExcelHeadModel parentHeadModel = parentHeads.get(parentField);
                    parentHeadModel.addColSpan(headModel.getColSpan());
                    parentHeadModel.getChildren().add(headModel);
                    headModel.setParent(parentHeadModel);
                } else if (heads.containsKey(parentField)) {// 同层级已经有此属性得父级表头
                    ExcelHeadModel excelHeadModel = heads.get(parentField);// 获得同层得此表头
                    // 将其添加到父级层中
                    int rowSpan = excelHeadModel.getRowSpan();
                    excelHeadModel.setRowSpan(1);
                    excelHeadModel.setColIndex(headModel.getColIndex());
                    excelHeadModel.addColSpan(headModel.getColSpan());
                    parentHeads.put(parentField, excelHeadModel);

                    // 将其子集表头增加行合并 并添加到同层列表 将其从同层列表删除
                    for (ExcelHeadModel sonModel : excelHeadModel.getChildren()) {
                        ExcelHeadModel addHeadModel = (ExcelHeadModel) BeanUtils.cloneBean(sonModel);
                        addHeadModel.addRowSpan(rowSpan);
                        // System.out.println("增加rowspan1 : " + headModel.toString());
                        addHeads.put(modelClass.getDeclaredField(addHeadModel.getName()), addHeadModel);
                    }
                    deleteHeads.add(parentField);

                    // 处理父子级关系
                    excelHeadModel.getChildren().add(headModel);
                    headModel.setParent(excelHeadModel);
                    changePliHeads.add(excelHeadModel);
                    // System.out.println("改变层级 : " + excelHeadModel.toString());
                } else {
                    ModelField parentModelProp = parentField.getAnnotation(ModelField.class);
                    ExcelHeadModel parentHeadModel = new ExcelHeadModel(parentField.getName(), parentModelProp.name(),
                            headModel.getColIndex(), 0);
                    parentHeadModel.setColSpan(headModel.getColSpan());
                    parentHeadModel.getChildren().add(headModel);
                    headModel.setParent(parentHeadModel);
                    parentHeads.put(parentField, parentHeadModel);
                }
                hasParent = true;
            } else {
                ExcelHeadModel parentHeadModel = (ExcelHeadModel) BeanUtils.cloneBean(headModel);
                parentHeadModel.getChildren().add(headModel);
                headModel.setParent(parentHeadModel);
                parentHeads.put(field, parentHeadModel);
                hiddenHeads.add(headModel);
                rowSpanHeads.add(parentHeadModel);
                // System.out.println("层级" + plies.size() +"增加hiddenHeads : " +
                // headModel.toString());
            }
        }
        if (addHeads.size() > 0) {
            Iterator<Field> addIterator = addHeads.keySet().iterator();
            while (addIterator.hasNext()) {
                Field field = addIterator.next();
                heads.put(field, addHeads.get(field));
            }
        }

        if (deleteHeads.size() > 0) {
            for (Field field : deleteHeads) {
                heads.remove(field);
            }
        }
        if (hasParent) {
            for (ExcelHeadModel headModel : rowSpanHeads) {
                if (!changePliHeads.contains(headModel)) {
                    headModel.addRowSpan();
                    // System.out.println("层级" + plies.size() + " 增加rowspan2 : " +
                    // headModel.toString());
                }
            }
            plies.addFirst(parentHeads);
            makeParentHeadModels(plies, modelClass);
        } else {
            for (ExcelHeadModel headModel : hiddenHeads) {
                headModel.setParent(null);
            }
        }
    }

    static List<String> fillColumns(Class<?> clazz){
        List<String> list = new ArrayList<>();
        Field[] fields =  clazz.getDeclaredFields();
        for (Field field: fields) {
            if(field.isAnnotationPresent(ModelField.class)){
                ModelField modelField = field.getAnnotation(ModelField.class);
                if(modelField.show()){
                    list.add(field.getName());
                }
            }
        }
        return list;
    }

    /**
     * @param list    数据
     * @param columns 导出的列和排序
     * @param totals
     * @return
     * @throws Exception
     */
    public static SXSSFWorkbook makeExcel(Class<?> clazz, List<?> list, List<String> columns, String desc, String title,
                                          Map<String, Object> totals, List<String> remarkList, boolean flag) throws Exception {
        System.out.println("生成Excel开始");
        if(columns == null || columns.size() == 0){
            columns = fillColumns(clazz);
        }

        long beginTime = System.currentTimeMillis();
        SXSSFWorkbook wb = null;
        Sheet sheet = null;
        if (flag) {
            String template = "template.xlsx";
            if(clazz.getName().contains("HlhtSettleReportOneDTO")){
                template = "hlhtSettleReportOne.xlsx";
            }
            else if(clazz.getName().contains("HlhtSettleReportTwoDTO")){
                template = "hlhtSettleReportTwo.xlsx";
            }
            else if(clazz.getName().contains("HlhtChannelAccSettleFinanceDTO")){
                template = "hlhtChannelAccSettleFinanceReport.xlsx";
            }
            template = "excelTemplate/" + template;
            //FileInputStream file = new FileInputStream(new ClassPathResource(template).getFile());
            //XSSFWorkbook xssfWorkbook = new XSSFWorkbook(file);
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(template);
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);
            wb = new SXSSFWorkbook(xssfWorkbook, 100);
            sheet = wb.getSheet("Sheet0");
        } else {
            wb = new SXSSFWorkbook(100);
            sheet = wb.createSheet();
        }
        int rowIndex = startRow;// 行坐标
        int colNum = columns.size();// 列宽
        TableModel excelModel = clazz.getAnnotation(TableModel.class);// 类模型注解
        LinkedList<LinkedHashMap<Field, ExcelHeadModel>> headModels = new LinkedList<>();// 表头模型列表

        /**
         * 定义各级样式
         */
        XSSFCellStyle titleStyle = makeTitleStyle(wb);
        XSSFCellStyle headStyle = makeHeadStyle(wb);
        XSSFCellStyle descStyle = makeDescStyle(wb);
        XSSFCellStyle dataStyle = makeDataStyle(wb);

        /**
         * 设置标题
         */
        if (title == null) {
            title = excelModel.name();
        }
        excelTitle = title;
        fillTitleCell(sheet, title, titleStyle, rowIndex++, colNum);

        /**
         * 设置备注(第二列)
         */
        if (!StringUtils.isBlank(desc)) {
            fillDescCell(sheet, desc, descStyle, rowIndex++, colNum);
        }

        /**
         * 生成表头
         */
        // 记录重复打印区开始行
        int rePrintRow = rowIndex;
        // 先根据类型 和 传入的要导出的列，生成最底下的那一层表头模型
        LinkedHashMap<Field, ExcelHeadModel> leafHeadModel = makeDataColumnHeadModels(clazz, columns);
        headModels.addFirst(leafHeadModel);
        // 然后用最底层的表头模型 一层一层往上生成 就像造房子一层一层往上建
        makeParentHeadModels(headModels, clazz);
        // 用表头模型生成表头
        rowIndex = fillHeadCell(sheet, headModels, headStyle, rowIndex);
        if (!flag) {
            // 设置重复打印区域
            sheet.setRepeatingRows(new CellRangeAddress(rePrintRow, rowIndex - 1, startCol, startCol + columns.size() - 1));
        }
        // 其它打印设置
        printSettings(sheet);
        System.out.println("生成表头完成 耗时" + (System.currentTimeMillis() - beginTime));

        /**
         * 塞数据
         */
        rowIndex = fillDataCell(sheet, leafHeadModel, list, dataStyle, wb, rowIndex);
        System.out.println("生成数据完成 耗时:" + (System.currentTimeMillis() - beginTime));

        /**
         * 汇总行
         */
        rowIndex = fillTotalCell(sheet, leafHeadModel, totals, dataStyle, wb, rowIndex);
        System.out.println("生成汇总完成 总耗时:" + (System.currentTimeMillis() - beginTime));

        /**
         * 最后一行备注
         */
        if (null != remarkList && remarkList.size() >= 0) {
            for (String remark : remarkList) {
                if (flag) {
                    rowIndex  = rowIndex + 2;
                    setPrintDescCell(sheet, remark,descStyle, rowIndex, colNum);
                }
                else{
                    fillDescCell(sheet, remark, descStyle, rowIndex++, colNum);
                }

            }
        }


        /**
         * 根据内容设置列宽
         */
        fillCellWidth(sheet, leafHeadModel);
        System.out.println("总行数" + rowIndex + " 耗时:" + (System.currentTimeMillis() - beginTime));
        if (flag) {
            wb.setPrintArea(0, startCol, startCol + columns.size() - 1, 0, rowIndex);
            sheet.protectSheet("123456");
        }
        return wb;
    }

    /**
     * 计算列宽
     *
     * @param sheet
     * @param headModel
     * @param value
     * @return
     */
    private static int calcCellWidth(Sheet sheet, ExcelHeadModel headModel, Font hssfFont, String value) {
        if (StringUtils.isBlank(value)) {
            return 0;
        }
        hssfFont.getFontHeight();

        int chCount = StringUtil.chineseCount(value);
        int count = value.length();
        int width = (hssfFont.getFontHeight() * chCount * 17 / 10 + hssfFont.getFontHeight() * (count - chCount)) * 13
                / 9 + 400;
        if (width > headModel.getWidth()) {
            headModel.setWidth(width);
        }
        return headModel.getWidth();
    }

    /**
     * 根据类型处理实体类的属性值
     *
     * @param headModel
     * @param field
     * @param value
     * @return
     * @throws ParseException
     */
    /**
     * 根据类型处理实体类的属性值
     *
     * @param headModel
     * @param field
     * @param
     * @return
     * @throws ParseException
     */
    private static String fillExcelPropertyValue(ExcelHeadModel headModel, Field field, Object value) {
        if (value == null) {
            return null;
        }
        String result = null;

        result = TableFieldUtil.convertExcelProperty(headModel.getTableField(), field, value);
        boolean isNumber = NumberUtil.isNumber(result);
        if (headModel.isMakeTotal() && isNumber) {
            BigDecimal bigDecimal = new BigDecimal(result);
            headModel.setTotal(headModel.getTotal().add(bigDecimal));
        }
//        if (headModel.isAmountFormat() && isNumber) {
//            result = NumberUtil.fixAmount(result);
//        } else if (headModel.getRadixPoint() != 0 && isNumber){
//            result = NumberUtil.fixRadixPoint(result, headModel.getRadixPoint());
//        } else if (!StringUtils.isBlank(headModel.getDateFormat())) {
//            try {
//                Date date = null;
//                if(field.getType().equals(Date.class)){
//                    date = defaultformat.parse(result);
//                }else if(field.getType().equals(LocalDateTime.class)){
//                    date = localDateTimeFormat.parse(result.replace("T", " "));
//                }
//                result = new SimpleDateFormat(headModel.getDateFormat()).format(date);
//            }catch(Exception e){
//                System.out.println(field.toString() + "字段：" + result + "值日期转换失败， 需要格式" + defaultformat.toPattern());
//            }
//        } else if(!StringUtils.isBlank(headModel.getDict())){
//            String[] dirts = headModel.getDict().split("&");
//            for (String dirt : dirts ) {
//                String[] kv = dirt.split("=");
//                if(result.equals(kv[0])){
//                    result = kv[1];
//                    break;
//                }
//            }
//        }

        return result;

    }

    public static void exportExcel(HttpServletRequest request, HttpServletResponse response, SXSSFWorkbook wb)
            throws Exception {
        // 告诉浏览器用什么软件可以打开此文件
        response.setHeader("content-Type", "application/vnd.ms-excel");

        if ("firefox".equals(getExplorerType(request))) {
            // 火狐浏览器自己会对URL进行一次URL转码所以区别处理
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + new String((excelTitle + FILE_TYPE).getBytes("GB2312"), "ISO-8859-1"));
        } else {
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(excelTitle + FILE_TYPE, "utf-8"));
        }

        wb.write(response.getOutputStream());
    }

    public static SXSSFWorkbook makeExcel(HttpServletRequest request, Class<?> modelClass, List<?> list, String desc,
                                          String title, Map<String, Object> totals, List<String> remark, boolean flag) throws Exception {
        List<String> columns = JSON.parseArray(request.getParameter("columns"), String.class);
        return makeExcel(modelClass, list, columns, desc, title, totals, remark, flag);
    }

    /**
     * @param response
     * @param list     导出的数据 List中泛型实体类必须有ExcelModel 和 ExcelProp注解
     * @param desc
     * @throws Exception
     */
    public static void exportExcel(HttpServletRequest request, HttpServletResponse response, Class<?> modelClass,
                                   List<?> list, String desc) throws Exception {
        SXSSFWorkbook wb = makeExcel(request, modelClass, list, desc, null, null, null, false);
        exportExcel(request, response, wb);
    }

    /**
     * @param response
     * @param list     导出的数据 List中泛型实体类必须有ExcelModel 和 ExcelProp注解
     * @param desc
     * @param title
     * @throws Exception
     */
    public static void exportExcel(HttpServletRequest request, HttpServletResponse response, Class<?> modelClass,
                                   List<?> list, String desc, String title) throws Exception {
        SXSSFWorkbook wb = makeExcel(request, modelClass, list, desc, title, null, null, false);
        exportExcel(request, response, wb);
    }

    /**
     * @param response
     * @param list     导出的数据 List中泛型实体类必须有ExcelModel 和 ExcelProp注解
     * @param desc
     * @param title
     * @throws Exception
     */
    public static void exportExcel(HttpServletRequest request, HttpServletResponse response, Class<?> modelClass,
                                   List<?> list, String desc, String title, List<String> remark) throws Exception {
        SXSSFWorkbook wb = makeExcel(request, modelClass, list, desc, title, null, remark, false);
        exportExcel(request, response, wb);
    }


    /**
     * @param response
     * @param list     导出的数据 List中泛型实体类必须有ExcelModel 和 ExcelProp注解
     * @param desc
     * @param totals
     * @throws Exception
     */
    public static void exportExcel(HttpServletRequest request, HttpServletResponse response, Class<?> modelClass,
                                   List<?> list, String desc, Map<String, Object> totals) throws Exception {
        SXSSFWorkbook wb = makeExcel(request, modelClass, list, desc, null, totals, null, false);
        exportExcel(request, response, wb);
    }

    /**
     * @param response
     * @param list     导出的数据 List中泛型实体类必须有ExcelModel 和 ExcelProp注解
     * @param desc
     * @param title
     * @param totals
     * @throws Exception
     */
    public static void exportExcel(HttpServletRequest request, HttpServletResponse response, Class<?> modelClass,
                                   List<?> list, String desc, String title, Map<String, Object> totals) throws Exception {
        SXSSFWorkbook wb = makeExcel(request, modelClass, list, desc, title, totals, null, false);
        exportExcel(request, response, wb);
    }

    public static void exportExcel2(HttpServletRequest request, HttpServletResponse response, Class<?> modelClass,
                                    List<?> list, String desc, String title, List<String> remark) throws Exception {
        SXSSFWorkbook wb = makeExcel(request, modelClass, list, desc, title, null, remark, true);
        exportExcel(request, response, wb);
    }

    /**
     * 生成excel
     *
     * @param columns
     * @param modelClass
     * @param list
     * @param desc
     * @param title
     * @param index
     * @throws Exception
     */
    public static File saveExcel(List<String> columns, Class<?> modelClass, List<?> list, String desc, String title,
                                 int index) throws Exception {
        SXSSFWorkbook wb = makeExcel(modelClass, list, columns, desc, title, null, null, false);
        return saveExcel(wb, title, index);
    }

    /**
     * 生成excel
     *
     * @param columns
     * @param modelClass
     * @param list
     * @param desc
     * @param title
     * @param totals
     * @param index
     * @throws Exception
     */
    public static File saveExcel(List<String> columns, Class<?> modelClass, List<?> list, String desc, String title,
                                 Map<String, Object> totals, int index) throws Exception {
        SXSSFWorkbook wb = makeExcel(modelClass, list, columns, desc, title, totals, null, false);
        return saveExcel(wb, title, index);
    }

    /**
     * 生成excel文件
     *
     * @param wb
     * @param index
     * @throws Exception
     */
    public static File saveExcel(SXSSFWorkbook wb, String title, int index) throws Exception {
        File file = null;
        if (index == 1) {
            file = File.createTempFile(title + "_", FILE_TYPE);
        } else {
            file = File.createTempFile(title + index + "_", FILE_TYPE);
        }
        FileOutputStream out = new FileOutputStream(file);
        wb.write(out);// 将数据写到指定文件
        out.close();
        return file;
    }

    /**
     * 压缩excel
     *
     * @throws IOException
     */
    public static File excelToZip(List<File> fileList, String title) throws IOException {
        String dir = "";
        File zipFile = File.createTempFile(title + "_", ".zip"); // 定义压缩文件名称
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));
        if (null != fileList && !fileList.isEmpty()) {
            byte[] buf = new byte[1024];
            for (File file : fileList) {
                FileInputStream in = new FileInputStream(file);
                BufferedReader bu = new BufferedReader(new FileReader(file));
                zipOutputStream.putNextEntry(
                        new ZipEntry(file.getName().substring(0, file.getName().indexOf("_")) + FILE_TYPE));
                int len;
                while ((len = in.read(buf)) > 0) {
                    zipOutputStream.write(buf, 0, len);
                }
                zipOutputStream.closeEntry();
                in.close();
                file.delete();// 清除临时文档
            }
            zipOutputStream.flush();
            zipOutputStream.close();
        }
        fileList.clear();
        return zipFile;
    }

    public static void exportExcel(HttpServletRequest request, HttpServletResponse response, byte[] filebyte,
                                   String fileName) throws IOException {
        ServletOutputStream out = response.getOutputStream();
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Type", "application/zip");

        if ("firefox".equals(getExplorerType(request))) {
            // 火狐浏览器自己会对URL进行一次URL转码所以区别处理
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + new String((fileName + ".zip").getBytes("GB2312"), "ISO-8859-1"));
        } else {
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(fileName + ".zip", "utf-8"));
        }
        out.write(filebyte);
        out.flush();
        out.close();
    }

    public static String getExplorerType(HttpServletRequest request) {
        String agent = request.getHeader("USER-AGENT");
        if (agent != null && agent.toLowerCase().indexOf("firefox") > 0) {
            return "firefox";
        } else if (agent != null && agent.toLowerCase().indexOf("msie") > 0) {
            return "ie";
        } else if (agent != null && agent.toLowerCase().indexOf("chrome") > 0) {
            return "chrome";
        } else if (agent != null && agent.toLowerCase().indexOf("opera") > 0) {
            return "opera";
        } else if (agent != null && agent.toLowerCase().indexOf("safari") > 0) {
            return "safari";
        }
        return "others";
    }

}
