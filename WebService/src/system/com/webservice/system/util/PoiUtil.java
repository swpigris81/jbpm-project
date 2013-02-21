/**
 * 
 */
package com.webservice.system.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.directwebremoting.util.Logger;

/**
 * POI处理Excel公共类
 * 
 * @author Jason
 */
public class PoiUtil<T> {
    private static Logger logger = Logger.getLogger(PoiUtil.class);
    
	/**
	 * <p>Discription:[创建excel表格]</p>
	 * @param defaultLines 默认存储在内存中的数据行数
	 * @return
	 * @author:[创建者中文名字]
	 * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
	 */
	public static Workbook createWorkBook(int defaultLines) {
		return new SXSSFWorkbook(defaultLines);
	}

	/**
	 * <p>Discription:[创建excel表格]</p>
	 * @return
	 * @author:[创建者中文名字]
	 * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
	 */
	public static Workbook createWorkBook() {
		return new SXSSFWorkbook();
	}
	/**
	 * <p>Discription:解析Excel文件为Workbook</p>
	 * @param file Exce文件
	 * @return Workbook
	 * @throws Exception
	 * @author:大牙
	 * @update:2012-11-30
	 */
	public static Workbook getWorkBook(File file) throws Exception{
	    if(file == null){
	        return null;
	    }
	    Workbook wb = null;
	    try{
	        wb = new HSSFWorkbook(new FileInputStream(file));
	    }catch(Exception e){
	        try {
                wb = new XSSFWorkbook(new FileInputStream(file));
            } catch (FileNotFoundException e1) {
                logger.error("文件不存在", e1);
                throw e1;
            } catch (IOException e1) {
                logger.error("IO异常，" + e1.getMessage(), e1);
                throw e1;
            }
	    }
	    return wb;
	}

	/**
	 * <p>Discription:[创建excel页]</p>
	 * @param wb excel表格
	 * @return
	 * @author:[创建者中文名字]
	 * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
	 */
	public static Sheet createWorkSheet(Workbook wb) {
		return createWorkSheet(wb, "新建sheet页");
	}
	/**
	 * <p>Discription:[创建excel页]</p>
	 * @param wb excel表格
	 * @param sheetName sheet页名称, 名称长度必须大于等于1并且小于等于31
	 * @return
	 * @author:[创建者中文名字]
	 * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
	 */
	public static Sheet createWorkSheet(Workbook wb, String sheetName) {
	    if(sheetName == null || "".equals(sheetName.trim())){
            return createWorkSheet(wb);
        }
		return wb.createSheet(sheetName);
	}
	/**
     * <p>Discription:[导出数据到Excel文档]</p>
     * @param map 导出的字段及其字段名称
     * @param dataSet 导出的数据
     * @return
     * @throws Exception
     * @author:[代超]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public Workbook exportExcel2007(Map<String, String> map, Collection<T> dataSet) throws Exception{
        Workbook wb = createWorkBook(100);
        exportExcel2007(wb, null, map, null, dataSet);
        return wb;
    }
    /**
     * <p>Discription:[导出数据到Excel文档]</p>
     * @param sheetName 导出excel页的名称
     * @param map 导出的字段及其字段名称
     * @param dataSet 导出的数据
     * @return
     * @throws Exception
     * @author:[代超]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public Workbook exportExcel2007(String sheetName, Map<String, String> map, Collection<T> dataSet) throws Exception{
        Workbook wb = createWorkBook(100);
        exportExcel2007(wb, sheetName, map, null, dataSet);
        return wb;
    }
    
    /**
     * <p>Discription:[导出数据到Excel文档]</p>
     * @param sheetName 导出excel页的名称
     * @param map 导出的字段及其字段名称
     * @param dateParten 日期格式
     * @param dataSet 导出的数据
     * @return
     * @throws Exception
     * @author:[代超]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public Workbook exportExcel2007(String sheetName, Map<String, String> map, String dateParten, Collection<T> dataSet) throws Exception{
        Workbook wb = createWorkBook(100);
        exportExcel2007(wb, sheetName, map, dateParten, dataSet);
        return wb;
    }
	/**
     * <p>Discription:[将数据写入到Excel2007中]</p>
     * @param wb Excel2007
     * @param sheetName Excel中的sheet的名称
     * @param map<字段名, 对应名称> Excel导出的字段及其对应的名称。名称将以抬头的形式写入Excel的第一行
     * @param dateParten 如果到处数据是日期的话，给出一个日期格式。默认：yyyy-MM-dd
     * @param dataSet 要导出的数据。必须是符合javabean的集合
     * @throws Exception
     * @author:[代超]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void exportExcel2007(Workbook wb, String sheetName,
            Map<String, String> map, String dateParten, Collection<T> dataSet)
            throws Exception {
        if(dataSet != null && map != null){
            if(dateParten == null || "".equals(dateParten.trim())){
                dateParten = "yyyy-MM-dd";
            }
            Sheet sheet = createWorkSheet(wb, sheetName);
            //写入数据, 从第1行开始
            try{
                int rowNumber = 0;
                // 遍历集合数据，产生数据行
                Iterator<T> it = dataSet.iterator();
                while(it.hasNext()){
                    T t = (T) it.next();
                    // 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
                    Field[] fields = t.getClass().getDeclaredFields();
                    //从第0列开始
                    int cellNumber = 0;
                    Object obj[] = new Object[fields.length];
                    String [] header = new String[fields.length];;
                    for(Field field : fields){
                        String fieldName = field.getName();
                        //导出指定字段
                        String fn = map.get(fieldName);
                        if(fn == null){
                            continue;
                        }else{
                            if(rowNumber == 0){
                                header[cellNumber] = fn;
                            }
                        }
                        String getMethodName = "get"
                                + fieldName.substring(0, 1).toUpperCase()
                                + fieldName.substring(1);
                        Class tCls = t.getClass();
                        Method getMethod = tCls.getMethod(getMethodName,
                                new Class[] {});
                        Object value = getMethod.invoke(t, new Object[] {});
                        obj[cellNumber] = value;
                        cellNumber ++;
                    }
                    //写入第一行
                    if(rowNumber == 0){
                        //第一行要写入文件标题头
                        exportExcel2007(sheet, rowNumber, header);
                        rowNumber ++ ;
                    }
                    exportExcel2007(sheet, rowNumber, obj);
                    rowNumber ++ ;
                }
            }catch(Exception e){
                e.printStackTrace();
                throw e;
            }
        }
    }

	/**
	 * <p>Discription:[写数据到单元格(仅仅适用于Excel2007版本)]</p>
	 * @param sheet excel的sheet页
	 * @param rowNumber 当前写入的是sheet的第rowNumber行
	 * @param data 写入数据(Object数组)
	 * @throws Exception
	 * @author:[创建者中文名字]
	 * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
	 */
	public void exportExcel2007(Sheet sheet, int rowNumber, Object[] data)
			throws Exception {
		try {
			if (data != null) {
				Row row = sheet.createRow(rowNumber);
				for (int i = 0; i < data.length; i++) {
					Cell cell = row.createCell(i);
					if (data[i] == null) {
						cell.setCellValue("");
					} else if (data[i] instanceof Integer) {
						cell.setCellValue((Integer) data[i]);
					} else if (data[i] instanceof String) {
						cell.setCellValue((String) data[i]);
					} else if (data[i] instanceof Double) {
						cell.setCellValue((Double) data[i]);
					} else if (data[i] instanceof Float) {
						cell.setCellValue((Float) data[i]);
					} else if (data[i] instanceof Long) {
						cell.setCellValue((Long) data[i]);
					} else if (data[i] instanceof Boolean) {
						cell.setCellValue((Boolean) data[i]);
					} else if (data[i] instanceof Date) {
						cell.setCellValue(Tools.dateToString((Date) data[i]));
					} else if (data[i] instanceof BigDecimal) {
						cell.setCellValue(((BigDecimal) data[i]).doubleValue());
					} else {
						cell.setCellValue(String.valueOf(data[i]));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * <p>Discription:[写数据到单元格(仅仅适用于Excel2007版本)]</p>
	 * @param sheet excel的sheet页
	 * @param rowNumber 当前写入的是sheet的第rowNumber行
	 * @param data 写入数据(Object数组)
	 * @param cellStyle
	 * @throws Exception
	 * @author:[创建者中文名字]
	 * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
	 */
	public static void exportExcel2007(Sheet sheet, int rowNumber,
			Object[] data, CellStyle cellStyle) throws Exception {
		try {
			if (data != null) {
				Row row = sheet.createRow(rowNumber);
				for (int i = 0; i < data.length; i++) {
					Cell cell = row.createCell(i);
					cell.setCellStyle(cellStyle);
					if (data[i] == null) {
						cell.setCellValue("");
					} else if (data[i] instanceof Integer) {
						cell.setCellValue((Integer) data[i]);
					} else if (data[i] instanceof String) {
						cell.setCellValue((String) data[i]);
					} else if (data[i] instanceof Double) {
						cell.setCellValue((Double) data[i]);
					} else if (data[i] instanceof Float) {
						cell.setCellValue((Float) data[i]);
					} else if (data[i] instanceof Long) {
						cell.setCellValue((Long) data[i]);
					} else if (data[i] instanceof Boolean) {
						cell.setCellValue((Boolean) data[i]);
					} else if (data[i] instanceof Date) {
					    cell.setCellValue(Tools.dateToString((Date) data[i]));
					} else if (data[i] instanceof BigDecimal) {
						cell.setCellValue(((BigDecimal) data[i]).doubleValue());
					} else {
						cell.setCellValue(String.valueOf(data[i]));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * <p>Discription:[解析excel文档]</p>
	 * @param xlsPath 文档路径
	 * @param imgSavePath 图片保存路径
	 * @throws Exception
	 * @author:[创建者中文名字]
	 * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
	 */
	public static void xlsParse(String xlsPath, String imgSavePath)
			throws Exception {
		InputStream input = null;
		HSSFWorkbook workbook = null;
		try {
			// 加载文档
			input = new FileInputStream(xlsPath);
			workbook = new HSSFWorkbook(input);
			/** 获取文档属性 **/
			DocumentSummaryInformation docInfo = workbook
					.getDocumentSummaryInformation();
			SummaryInformation sumInfo = workbook.getSummaryInformation();
			showInfo(sumInfo, docInfo);

			/** 获取文档内容 因为excel采用的是单元格格式 所以采用循环取单元格的值 **/
			StringBuilder xlsContent = new StringBuilder();
			// 获取工作表数量
			int sheetTotal = workbook.getNumberOfSheets();
			// 获取工作表信息
			for (int i = 0; i < sheetTotal; i++) {
				HSSFSheet sheet = workbook.getSheetAt(i);
				if (null == sheet){
					continue;
				}
				int rowTotal = sheet.getLastRowNum();
				// 获取 行信息
				for (int j = 0; j < rowTotal; j++) {
					HSSFRow row = sheet.getRow(j);
					if (null == row){
						continue;
					}
					int cellTotal = row.getLastCellNum();
					// 获取单元格信息
					for (int k = 0; k < cellTotal; k++) {
						HSSFCell cell = row.getCell(k);
						if (null == cell){
							continue;
						}
						xlsContent.append(cell.toString());
					}
				}
			}
			System.out.println("内容：" + xlsContent.toString());
		} catch (Exception e) {
			throw e;
		} finally {
			if (null != input){
				input.close();
			}
		}
	}
	
	/**
	 * <p>Discription:读取Excel文件，支持03以及07及以上版本</p>
	 * @param file Excel文件
	 * @return 以map格式输出：<sheet名：sheet内容>。其中sheet内容是以列表存储，列表中存储的是String数组，sheet中的每一行则是一个String数组
	 * @throws Exception
	 * @author:大牙
	 * @update:2012-11-30
	 */
	public Map<String, Object> readExcel(File file) throws Exception{
	    return readExcel(getWorkBook(file));
	}
	
	/**
     * <p>Discription:读取Workbook文件，支持03以及07及以上版本</p>
     * @param file Workbook文件
     * @return 以map格式输出：<sheet名：sheet内容>。其中sheet内容是以列表存储，列表中存储的是String数组，sheet中的每一行则是一个String数组
     * @throws Exception
     * @author:大牙
     * @update:2012-11-30
     */
	public Map<String, Object> readExcel(Workbook wb) throws Exception{
	    if(wb == null){
	        return null;
	    }
	    DecimalFormat df = new DecimalFormat("#.##");
        int sheetNumber = wb.getNumberOfSheets();
        Map<String, Object> excelMap = new LinkedHashMap<String, Object>();
        for (int i = 0; i < sheetNumber; i++) {
            Sheet sheet = wb.getSheetAt(i);
            List<String[]> strs=new ArrayList<String[]>();
            // 注意得到的行数是基于0的索引 遍历所有的行
            for (int k = 0; k <= sheet.getLastRowNum(); k++) {
                Row rows = sheet.getRow(k);
                if(rows == null){
                    continue;
                }
                String[] str = new String[rows.getLastCellNum()];
                // 遍历每一列
                for (int l = 0; l < rows.getLastCellNum(); l++) {
                    Cell cell = rows.getCell(l);
                    // 单元格类型
                    if(cell == null){
                        continue;
                    }
                    int cellType = cell.getCellType();
                    switch (cellType) {
                    case 0:// 数字类型
                        str[l] = df.format(cell.getNumericCellValue());
                        break;
                    case 1:// String类型
                        str[l] = cell.getStringCellValue();
                        break;
                    case 2:// Formula Cell type 公式类型
                        FormulaEvaluator he = null;
                        try {
                            he = new HSSFFormulaEvaluator((HSSFWorkbook) wb);
                        }
                        catch (Exception e) {
                            he = new XSSFFormulaEvaluator((XSSFWorkbook) wb);
                        }
                        if (he != null && he.evaluateFormulaCell(cell) == 0) {
                            str[l] = df.format(he.evaluate(cell).getNumberValue());
                        }
                        else {
                            str[l] = he.evaluate(cell).getStringValue();
                        }
                        break;
                    case 3:// 空格
                        str[l] = "";
                        break;
                    case 4:// Boolean Cell type
                        str[l] = BooleanUtils.toStringTrueFalse(cell.getBooleanCellValue());
                        break;
                    case 5:// Errors
                        break;
                    default:// 其它格式的数据
                        break;
                    }
                }
                strs.add(str);
            }
            excelMap.put(sheet.getSheetName(), strs);
        }
	    return excelMap;
    }

	public static void showInfo(SummaryInformation sumInfo,
			DocumentSummaryInformation docInfo) throws Exception {
		/** 摘要信息 **/
		System.out.println("标题:" + sumInfo.getTitle());
		System.out.println("主题:" + sumInfo.getSubject());
		System.out.println("作者:" + sumInfo.getAuthor());
		System.out.println("关键字:" + sumInfo.getKeywords());

		System.out.println("备注:" + sumInfo.getComments());
		System.out.println("模板:" + sumInfo.getTemplate());
		System.out.println("上次保存用户:" + sumInfo.getLastAuthor());
		System.out.println("修订次数:" + sumInfo.getRevNumber());

		System.out.println("编辑文档的时间:" + sumInfo.getEditTime());
		System.out.println("打印时间:" + sumInfo.getLastPrinted());
		System.out.println("创建时间:" + sumInfo.getCreateDateTime());
		System.out.println("上一次保存时间:" + sumInfo.getLastSaveDateTime());

		System.out.println("页面数量:" + sumInfo.getPageCount());
		System.out.println("字数:" + sumInfo.getWordCount());
		System.out.println("字符数:" + sumInfo.getCharCount());
		System.out.println("应用软件名称:" + sumInfo.getApplicationName());

		/** 文档信息 部分属性属于个别office文档类型特有的属性 **/
		System.out.println("类别:" + docInfo.getCategory());
		System.out.println("显示的格式:" + docInfo.getPresentationFormat());
		System.out.println("字节数:" + docInfo.getByteCount());
		System.out.println("行数:" + docInfo.getLineCount());

		System.out.println("段落数:" + docInfo.getParCount());
		System.out.println("幻灯片的数量:" + docInfo.getSlideCount());
		System.out.println("备注数量:" + docInfo.getNoteCount());
		System.out.println("隐藏文件的数量:" + docInfo.getHiddenCount());

		System.out.println("多媒体剪辑数量:" + docInfo.getMMClipCount());
		System.out.println("经理:" + docInfo.getManager());
		System.out.println("单位:" + docInfo.getCompany());
		System.out.println("链接:" + docInfo.getLineCount());
	}
	
	/**
     * 测试使用
     * 
     * @param args
     */
    public static void main(String[] args) {
        //test1();
        test3();
    }
    
    /**
     * 仅仅用于测试
     */
    public static void test(){
        // 只在内存中保留100行记录,
        Workbook wb = new SXSSFWorkbook(100);
        Sheet sh = wb.createSheet();
        int rownum = 0;
        try{
            while(true){
                Row row = sh.createRow(rownum);
                for (int cellnum = 0; cellnum < 10; cellnum++) {
                    Cell cell = row.createCell(cellnum);
                    String address = new CellReference(cell).formatAsString();
                    cell.setCellValue(address);
                }
                System.out.println(rownum);
                rownum++;
                if (rownum >= 1000000)//最多写100w行—EXcel2007的记录上线大概是100w多点
                    break;
            }
            FileOutputStream out = new FileOutputStream("c:/test.xlsx");
            wb.write(out);
            out.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
     * <p>Discription:[基于数组集合数据导出Excel（适用于无法使用javabean的情况）]</p>
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public static void test1(){
        PoiUtil util = new PoiUtil();
        Workbook wb = util.createWorkBook(100);
        Sheet sheet = util.createWorkSheet(wb);
        Object[] data = new Object[]{"aaaaa", 1223, true, 23.13, "20090817"};
        FileOutputStream out = null;
        try {
            out = new FileOutputStream("c:/test.xlsx");
            for(int i=0; i< 100000; i++){
                util.exportExcel2007(sheet, i, data);
            }
            wb.write(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * <p>Discription:[基于javabean数据集合导出Excel]</p>
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public static void test2(){
        PoiUtil<Student> util = new PoiUtil<Student>();
        Workbook wb = util.createWorkBook(100);
        List<Student> list = new ArrayList<Student>(100000);
        for(int i=0; i< 100000; i++){
            Student s = new Student("1", "测试", 13, true);
            list.add(s);
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", "学号");
        map.put("name", "姓名");
        map.put("age", "年龄");
        map.put("sex", "性别");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream("c:/test1.xlsx");
            util.exportExcel2007(wb, "测试", map, null, list);
            wb.write(out);
        }
        catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * <p>Discription:[基于javabean的数据集合导出excel（优先使用这样的方式）]</p>
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public static void test3(){
        PoiUtil<Student> util = new PoiUtil<Student>();
        List<Student> list = new ArrayList<Student>(100000);
        for(int i=0; i< 100000; i++){
            Student s = new Student("1", "测试", 13, true);
            list.add(s);
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", "学号");
        map.put("name", "姓名");
        map.put("age", "年龄");
        map.put("sex", "性别");
        FileOutputStream out = null;
        Date start = null;
        try {
            start = new Date();
            System.out.println("开始导出Excel ：" + start);
            out = new FileOutputStream("c:/test2.xlsx");
            util.exportExcel2007("sheet名称", map, list).write(out);
        }
        catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Date end = new Date();
            System.out.println("导出Excel结束 : " + end);
            System.out.println("写入100w数据耗时：" + (end.getTime() - start.getTime()) + "毫秒");
        }
    }
}

/**
 * <p>Description: [用于测试]</p>
 * @author  <a href="mailto: swpigris81@126.com">代超</a>
 * @version $Revision$
 */
class Student{
    private String id;
    private String name;
    private int age;
    private boolean sex;
    
    public Student() {
    }
    public Student(String id, String name, int age, boolean sex) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.sex = sex;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public boolean getSex() {
        return sex;
    }
    public void setSex(boolean sex) {
        this.sex = sex;
    }
}
