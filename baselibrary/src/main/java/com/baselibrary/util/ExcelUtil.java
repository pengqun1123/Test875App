package com.baselibrary.util;

import android.content.Context;
import android.widget.Toast;

import com.baselibrary.entitys.FaceTestEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * 生成Excel文件工具类
 * <p>
 * 版权声明：本文为CSDN博主「ai-exception」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
 * 原文链接：https://blog.csdn.net/qq_36982160/article/details/82421940
 */
public class ExcelUtil {

    private static WritableFont arial14font = null;

    private static WritableCellFormat arial14format = null;
    private static WritableFont arial10font = null;
    private static WritableCellFormat arial10format = null;
    private static WritableFont arial12font = null;
    private static WritableCellFormat arial12format = null;
    private final static String UTF8_ENCODING = "UTF-8";

    /**
     * 单元格的格式设置 字体大小 颜色 对齐方式、背景颜色等...
     */
    private static void format() {
        try {
            arial14font = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD);
            arial14font.setColour(jxl.format.Colour.LIGHT_BLUE);
            arial14format = new WritableCellFormat(arial14font);
            arial14format.setAlignment(jxl.format.Alignment.CENTRE);
            arial14format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
            arial14format.setBackground(jxl.format.Colour.VERY_LIGHT_YELLOW);

            arial10font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            arial10font.setColour(Colour.DARK_BLUE);
            arial10format = new WritableCellFormat(arial10font);
            arial10format.setAlignment(jxl.format.Alignment.CENTRE);
            arial10format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
            arial10format.setBackground(Colour.GRAY_25);

            arial12font = new WritableFont(WritableFont.ARIAL, 10);
            //设置字体颜色

            arial12format = new WritableCellFormat(arial12font);
            //对齐格式
            arial12font.setColour(Colour.BLACK);
            arial12font.setPointSize(10);
            //设置边框
            arial12format.setBorder(Border.ALL, BorderLineStyle.THIN);
            arial12format.setAlignment(jxl.format.Alignment.CENTRE);
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    public static WritableCellFormat getHeader() {
        WritableFont font = new WritableFont(WritableFont.TIMES, 12,
                WritableFont.BOLD);// 定义字体
        try {
            font.setColour(Colour.BLUE);// 蓝色字体
        } catch (WriteException e1) {
            e1.printStackTrace();
        }
        WritableCellFormat format = new WritableCellFormat(font);
        try {
            format.setAlignment(jxl.format.Alignment.CENTRE);// 左右居中
            format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);// 上下居中
            format.setBorder(Border.ALL, BorderLineStyle.THIN,
                    Colour.BLACK);// 黑色边框
            format.setBackground(Colour.GREEN);// 黄色背景
        } catch (WriteException e) {
            e.printStackTrace();
        }
        return format;
    }

    public static <T> void setSheetHeader(Context context, String fileName, String[] colName,
                                          String sheetName, List<T> objList) {
        WritableWorkbook workbook = null;
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            WorkbookSettings workbookSettings = new WorkbookSettings();
            workbookSettings.setEncoding("UTF-8");
            workbookSettings.setFormulaAdjust(true);
            workbookSettings.setRefreshAll(true);
            // 创建Excel工作表
            OutputStream os = new FileOutputStream(file);
            workbook = Workbook.createWorkbook(os);
            //设置表格的名字
            WritableSheet sheet = workbook.createSheet(sheetName, 0);
            Label label;
            for (int i = 0; i < colName.length; i++) {
                // Label(x,y,z) 代表单元格的第x+1列，第y+1行, 内容z
                // 在Label对象的子对象中指明单元格的位置和内容
                label = new Label(i, 0, colName[i], getHeader());
                // 将定义好的单元格添加到工作表中
                sheet.addCell(label);
            }
            for (int i = 0; i < objList.size(); i++) {
                FaceTestEntity faceTestEntity = (FaceTestEntity) objList.get(i);
                Label name = new Label(0, i + 1, faceTestEntity.getName());
                Label trackId = new Label(1, i + 1, String.valueOf(faceTestEntity.getTrackId()));
                Label liveNess = new Label(2, i + 1, String.valueOf(faceTestEntity.isLiveness()));
                Label rgbTime = new Label(3, i + 1, String.valueOf(faceTestEntity.getRGB_Tiem()));
                Label rgbIrTime = new Label(4, i + 1, String.valueOf(faceTestEntity.getRGB_IR_Time()));
                Label compareTime = new Label(5, i + 1, String.valueOf(faceTestEntity.getCompare_time()));
                Label compareSimilar = new Label(6, i + 1, String.valueOf(faceTestEntity.getCompareSimilar()));
                sheet.addCell(name);
                sheet.addCell(trackId);
                sheet.addCell(liveNess);
                sheet.addCell(rgbTime);
                sheet.addCell(rgbIrTime);
                sheet.addCell(compareTime);
                sheet.addCell(compareSimilar);
            }
            ToastUtils.showShortToast(context, "写入成功");
            sheet.setColumnView(0, 350);
            workbook.write();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化Excel
     *
     * @param fileName 导出excel存放的地址（目录）
     * @param colName  excel中包含的列名（可以有多个）
     */
    public static void initExcel(String fileName, String sheetName, String[] colName) {
        format();
        WritableWorkbook workbook = null;
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            WorkbookSettings workbookSettings = new WorkbookSettings();
            workbookSettings.setEncoding("UTF-8");
            workbookSettings.setFormulaAdjust(true);
            workbookSettings.setRefreshAll(true);
            FileOutputStream os = new FileOutputStream(file);
            workbook = Workbook.createWorkbook(os);
            //设置表格的名字
            WritableSheet sheet = workbook.createSheet(sheetName, 0);
            //创建标题栏
            sheet.addCell((WritableCell) new Label(0, 0, "FR测试数据", arial14format));
            sheet.setColumnView(0, 24);
            for (int col = 0; col < colName.length; col++) {
                // Label(x,y,z) 代表单元格的第x+1列，第y+1行, 内容z
                // 在Label对象的子对象中指明单元格的位置和内容
                sheet.addCell(new Label(col, 1, colName[col], /*getHeader()*/arial10format));
                sheet.setColumnView(0, colName[col].length() * 8);
            }
            //设置行高
            sheet.setRowView(0, 340);
            workbook.write();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 写入实体类数据到Excel文件
     */
    @SuppressWarnings("unchecked")
    public static <T> void writeObjListToExcel(List<T> objList, String fileName, Context c) {
        if (objList != null && objList.size() > 0) {
            WritableWorkbook writebook = null;
            InputStream in = null;
            try {
                in = new FileInputStream(new File(fileName));
                Workbook workbook = Workbook.getWorkbook(in);
                writebook = Workbook.createWorkbook(new File(fileName), workbook);
                WritableSheet sheet = writebook.getSheet(0);
                if (objList.size() > 0) {
                    for (int j = 0; j < objList.size(); j++) {
                        //要写入表格的数据对象
                        FaceTestEntity faceTestEntity = (FaceTestEntity) objList.get(j);
                        List<String> list = new ArrayList<>();
                        list.add(faceTestEntity.getName());
                        list.add(String.valueOf(faceTestEntity.getTrackId()));
                        list.add(String.valueOf(faceTestEntity.isLiveness()));
                        list.add(String.valueOf(faceTestEntity.getRGB_Tiem()));
                        list.add(String.valueOf(faceTestEntity.getRGB_IR_Time()));
                        list.add(String.valueOf(faceTestEntity.getCompare_time()));
                        list.add(String.valueOf(faceTestEntity.getCompareSimilar()));
                        for (int i = 0; i < list.size(); i++) {
                            sheet.addCell(new Label(i, j + 2, list.get(i), arial12format));
                            if (list.get(i).length() <= 4) {
                                //设置列宽 ，第一个参数是列的索引，第二个参数是列宽
                                sheet.setColumnView(i, list.get(i).length() * 7);
                            } else {
                                //设置列宽
                                sheet.setColumnView(i, list.get(i).length() * 4);
                            }
                        }
                        //设置行高，第一个参数是行数，第二个参数是行高
                        sheet.setRowView(j, 340);
                    }
                }
                writebook.write();
                Toast.makeText(c, "导出Excel成功", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (writebook != null) {
                    try {
                        writebook.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
