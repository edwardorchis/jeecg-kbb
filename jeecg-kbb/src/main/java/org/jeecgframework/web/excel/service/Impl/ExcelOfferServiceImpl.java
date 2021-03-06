package org.jeecgframework.web.excel.service.Impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.RegionUtil;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.jeecgframework.core.util.SystemPath;
import org.jeecgframework.p3.core.common.utils.StringUtil;
import org.jeecgframework.web.excel.entity.*;
import org.jeecgframework.web.excel.service.IExcelOfferService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;



@Service("excelOfferService")
@Transactional
public class ExcelOfferServiceImpl extends CommonServiceImpl  implements IExcelOfferService {
	
	HSSFWorkbook workbook;
	HSSFSheet sheet;
	ExcelOfferEntity bill;
	String billId;
	Integer rowIndex;

	
	@Override
	public String getWorkbook(String id,HSSFWorkbook workbook)throws Exception{
		billId=id;
		this.workbook=workbook;
	
		Init();
		buildTitle();
		buildEmptyRow();
		
		List<Map<String,Object>> mapList=this.commonDao.findForJdbc(
				"select DISTINCT t2.fnumber,t2.fdoortype from t_offers_entry t1 inner join t_doors t2 on t1.item_id=t2.id where t1.id=? ", billId);
		mapList.stream()
			.forEach(map->{
				buildMainDoor(map.get("fnumber").toString(),map.get("fdoortype").toString());
				buildEmptyRow();
			});

		buildGroupList(OfferGroup.FRAMEWORK);
		buildEmptyRow();
		buildGroupList(OfferGroup.SIDEDOOR);
		buildEmptyRow();
		buildGroupList(OfferGroup.OTHER);
		buildFoot();
		createPicture();
		
		return bill.getBillno();
	}

	void Init() throws Exception{
		rowIndex=0;
		InitData();
		InitWorkbook();
	}

	void InitData() throws Exception{
		bill=new ExcelOfferEntity();
		Map<String,Object> map=this.commonDao.findOneForJdbc(
		"select fbillno,famount,fprojectid,fapplicant,fapplicant_date,fdiscountrate,fafteramount ,t1.fname as fcust,t0.fapplicant_date "
		+" from t_offers t0 inner join t_base_customer t1 on t0.fcustid=t1.id where t0.id=?", billId);
		if(map==null){
			throw new Exception("单据未找到");
		}
		bill.setBillno(map.get("fbillno").toString());
		bill.setCreatedate(readDate(map.get("fapplicant_date")));
		bill.setCustname(map.get("fcust").toString());
		bill.setProjectname(map.get("fprojectid").toString());
		bill.setSaleman(map.get("fapplicant").toString());
		bill.setTotalamount(Double.parseDouble(map.get("famount").toString()));
		bill.setEngineer("");
		bill.setDiscountrate(0.00);
		bill.setAfteramount(0.00);
		//旋转门和平门
		bill.setEntryList(getOfferEntryList());
		//选项
		bill.setGroupList(getGroupInfos());
	}
	List<ExcelOfferEntry> getOfferEntryList(){
		List<Map<String,Object>> mapList=this.commonDao.findForJdbc("select * from t_offers_entry where id=?",billId);
		List<ExcelOfferEntry> entryList=new ArrayList<ExcelOfferEntry>();
		Iterator<Map<String,Object>>  m =mapList.iterator();
		while (m.hasNext()) {  
			 Map<String,Object> dr = m.next();  
			 String json= dr.get("detail2json").toString();
			 if(StringUtil.isEmpty(json)){
				 continue;
			 }
			 JSONObject obj = new JSONObject().fromObject(json);
			 String doorModelId=obj.get("p1").toString();
			 if(StringUtil.isEmptyNull(doorModelId)){
				 continue;
			 }
			 ExcelOfferEntry entry=new ExcelOfferEntry();
			 if(dr.get("doortype").toString().equals("XZM")){
				 entry.setOffergroup(OfferGroup.REVOLUTION);
			 }else{
				 entry.setOffergroup(OfferGroup.SMOOTH);
			 }
			 entry.setOffersubgroup(OfferSubGroup.NONE);
			 entry.setIndex(-1);// 门型型号
			 entry.setPrice(readDouble(dr.get("price")));
			 entry.setQuatity(readDouble(dr.get("quantity")));
			 entry.setAmount(readDouble(dr.get("amount")));
			 entry.setRemark(dr.get("remark").toString());
			
			 //门型			
			 Map<String,Object> mapDoor=this.commonDao.findOneForJdbc("select  * from t_doors where id=?", dr.get("item_id"));
			 Map<String,Object> mapModel=this.commonDao.findOneForJdbc("select  * from t_doors_model where id=?", doorModelId);
			 entry.setNumber(readString(mapDoor.get("fnumber")));
	 		 entry.setName(readString(mapDoor.get("fname")));
			 entry.setModel(readString(mapModel.get("fmodel")));
			
			 entryList.add(entry);
			 entryList.addAll(getDoorModelPlugInfos(
					 readString(dr.get("item_id")),
					 readString(mapDoor.get("fnumber")),
					 entry.getOffergroup(),
					 obj));
		}
		return entryList;
	}
	List<ExcelOfferEntry> getDoorModelPlugInfos(String itemId,String itemNumber,OfferGroup offergroup,JSONObject detail2json) {
		List<ExcelOfferEntry> resultList=new ArrayList<ExcelOfferEntry>();
		 //一般参数
		 List<Map<String,Object>> paramList=this.commonDao.findForJdbc(
		 		"select t2.ffeildname,t2.fcaption from t_door_params t1 inner join t_base_params t2 on t1.fparamsid=t2.id \n" + 
		 		"where t1.fshow='Y' and t1.foreignid=?", itemId);
		if(paramList!=null && paramList.size()>0) {
			//fname save fcaption	fmodel save fvalue
			List<ExcelOfferEntry> params=new ArrayList<ExcelOfferEntry>();
			String doorModelId=detail2json.get("p1").toString();
			Map<String,Object> paramValueList=this.commonDao.findOneForJdbc(
			 		"select * from t_doors_model_ex where foreignid=? and id=?", itemId,doorModelId);
			Iterator<Map<String,Object>>  m =paramList.iterator();
			while (m.hasNext()) {  
				 Map<String,Object> dr = m.next(); 
				 ExcelOfferEntry entity=new ExcelOfferEntry();
				 entity.setOffergroup(offergroup);
				 entity.setOffersubgroup(OfferSubGroup.NONE);
				 entity.setIndex(-2);
				 entity.setNumber(itemNumber);
				 entity.setName(readString(dr.get("fcaption")));
				 entity.setModel(readString(paramValueList.get(readString(dr.get("ffeildname")))));
				 resultList.add(entity);
			}
		}
		
		 JSONArray standardArray=JSONArray.fromObject(detail2json.getString("p2"));
		 JSONArray optionsArray=JSONArray.fromObject(detail2json.getString("p3"));
		 resultList.addAll(getPartyList(itemId,itemNumber,offergroup,OfferSubGroup.STANDARD,standardArray));
		 resultList.addAll(getPartyList(itemId,itemNumber,offergroup,OfferSubGroup.OPTIONS,optionsArray));
		 String surface =detail2json.getString("p4").toString();
		 if(StringUtil.notEmptyNull(surface)){
			 //表面处理   name:fname		price:fratio
			 Map<String,Object> mapSurface=this.commonDao.findOneForJdbc("select * from t_door_surface where foreignid=? and id=?", itemId,surface);
			 ExcelOfferEntry entity=new ExcelOfferEntry();
			 entity.setOffergroup(offergroup);
			 entity.setOffersubgroup(OfferSubGroup.NONE);
			 entity.setIndex(-3);
			 entity.setNumber(itemNumber);
			 entity.setName(readString(mapSurface.get("fname")));
			 entity.setPrice(readDouble(mapSurface.get("fratio")));
			 resultList.add(entity);
		 }
		 return resultList;
	}
	List<ExcelOfferEntry> getPartyList(String itemId,String itemNumber,OfferGroup offergroup,OfferSubGroup offersubgroup,JSONArray array){
		List<ExcelOfferEntry> resultList=new ArrayList<ExcelOfferEntry>();
		if(array==null || array.size()==0) {
			return resultList;
		}
		String tablename="t_door_standard";
		if(offersubgroup.equals(OfferSubGroup.OPTIONS)) {
			tablename="t_door_options";
		}
		List<Map<String,Object>> mapList=this.commonDao.findForJdbc("select * from "+ tablename + " where foreignid=?", itemId);
		Iterator<Map<String,Object>>  m =mapList.iterator();
		while (m.hasNext()) {  
			 Map<String,Object> dr = m.next(); 
			 if(array.contains(dr.get("id").toString())) {
				 ExcelOfferEntry entity=new ExcelOfferEntry();
				 entity.setOffergroup(offergroup);
				 entity.setOffersubgroup(offersubgroup);
				 entity.setIndex(readInteger(dr.get("findex")));
				 entity.setNumber(itemNumber);
				 entity.setName(dr.get("fname").toString());
				 entity.setModel(dr.get("fmodel").toString());
				 entity.setQuatity(readDouble(dr.get("fqty")));
				 entity.setPrice(readDouble(dr.get("fprice")));
				 entity.setAmount(readDouble(dr.get("famount")));
				 entity.setRemark(dr.get("fremark").toString());
				 resultList.add(entity);
			 }
		}
		 return resultList;
	}
	
	List<ExcelOfferEntry> getGroupInfos(){
		List<ExcelOfferEntry> resultList=new ArrayList<ExcelOfferEntry>();
		List<Map<String,Object>> mapList=this.commonDao.findForJdbc(
				"select t0.group_id,t0.findex,t0.fname,t0.unit,t1.model,t1.quantity,t1.price,t1.amount,t1.remark\n" + 
				" from t_offer_group_option t0 left join t_offer_options t1 \n" + 
				"on t0.group_id=t1.group_id and t0.findex=t1.findex and t1.id=?\n" + 
				"order by t0.group_id,t0.findex", billId);
		Iterator<Map<String,Object>>  m =mapList.iterator();
		while (m.hasNext()) {  
			 Map<String,Object> dr = m.next();  
			 ExcelOfferEntry entry=new ExcelOfferEntry();
			 Integer groupId=readInteger(dr.get("group_id"));
			 switch(groupId) {
			 case 3:// 边框
				 entry.setOffergroup(OfferGroup.FRAMEWORK);
				 break;
			 case 4://边门
				 entry.setOffergroup(OfferGroup.SIDEDOOR);
				 break;
			 case 5://费用
				 entry.setOffergroup(OfferGroup.OTHER);
				 break;
			 }
			 entry.setOffersubgroup(OfferSubGroup.NONE);
			 entry.setIndex(readInteger(dr.get("findex")));
			 entry.setName(readString(dr.get("fname")));
			 entry.setModel(readString(dr.get("model")));
			 entry.setUnit(readString(dr.get("unit")));
			 entry.setQuatity(readDouble(dr.get("quantity")));
			 entry.setPrice(readDouble(dr.get("price")));
			 entry.setAmount(readDouble(dr.get("amount")));
			 entry.setRemark(readString(dr.get("remark")));
			 resultList.add(entry);
		}
		return resultList;
	}
	void createPicture() throws IOException{
        BufferedImage bufferImg = null;     
        //先把读进来的图片放到一个ByteArrayOutputStream中，以便产生ByteArray
        ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();     
        
        String path = SystemPath.getSysPath() + "images\\Kbb.jpg";
        bufferImg = ImageIO.read(new File(path));     
        ImageIO.write(bufferImg, "jpg", byteArrayOut);
     
        //画图的顶级管理器，一个sheet只能获取一个（一定要注意这点）  
        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();     
        //anchor主要用于设置图片的属性  
        HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 255, 255,(short) 0, 0, (short) 1, 0);     
        anchor.setAnchorType(3);     
        //插入图片    
        patriarch.createPicture(anchor, workbook.addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG));   
	}
	
	
	void InitWorkbook(){		
		sheet = workbook.createSheet();
		// 设置列宽   
	    sheet.setColumnWidth(0, 3500);   
	    sheet.setColumnWidth(1, 1500);   
	    sheet.setColumnWidth(2, 1000);   
	    sheet.setColumnWidth(3, 3500);   
	    sheet.setColumnWidth(4, 4000);   
	    sheet.setColumnWidth(5, 3500);   
	    sheet.setColumnWidth(6, 3500);   
	    sheet.setColumnWidth(7, 4000);   
	    sheet.setColumnWidth(8, 4500);   
	    sheet.setColumnWidth(9, 4500);   
	    // Sheet样式   
	    HSSFCellStyle sheetStyle = workbook.createCellStyle();   
	    // 背景色的设定   
	    sheetStyle.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);   
	    // 前景色的设定   
	    sheetStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);   
	    // 填充模式   
	    sheetStyle.setFillPattern(HSSFCellStyle.FINE_DOTS);   
	    // 设置列的样式   
	    for (int i = 0; i <= 14; i++) {   
	      sheet.setDefaultColumnStyle((short) i, sheetStyle);   
	    }
	}
	
	HSSFCellStyle getTitleStyle(){
		 // 设置字体   
	    HSSFFont headfont = workbook.createFont();   
	    headfont.setFontName("黑体");   
	    headfont.setFontHeightInPoints((short) 22);// 字体大小   
	    headfont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗   
	    // 另一个样式   
	    HSSFCellStyle headstyle = workbook.createCellStyle();   
	    headstyle.setFont(headfont);   
	    headstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中   
	    headstyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中   
	    headstyle.setLocked(true);   
	    headstyle.setWrapText(true);// 自动换行   
	    return headstyle;
	}
	HSSFCellStyle getColumnHeadStyle(){
		 // 另一个字体样式   
	    HSSFFont columnHeadFont = workbook.createFont();   
	    columnHeadFont.setFontName("宋体");   
	    columnHeadFont.setFontHeightInPoints((short) 10);   
	    columnHeadFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);   
	    // 列头的样式   
	    HSSFCellStyle columnHeadStyle = workbook.createCellStyle();   
	    columnHeadStyle.setFont(columnHeadFont);   
	    columnHeadStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中   
	    columnHeadStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中   
	    columnHeadStyle.setLocked(true);   
	    columnHeadStyle.setWrapText(true);   
	    columnHeadStyle.setLeftBorderColor(HSSFColor.BLACK.index);// 左边框的颜色   
	    columnHeadStyle.setBorderLeft((short) 1);// 边框的大小   
	    columnHeadStyle.setRightBorderColor(HSSFColor.BLACK.index);// 右边框的颜色   
	    columnHeadStyle.setBorderRight((short) 1);// 边框的大小   
	    columnHeadStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 设置单元格的边框为粗体   
	    columnHeadStyle.setBottomBorderColor(HSSFColor.BLACK.index); // 设置单元格的边框颜色   
	    // 设置单元格的背景颜色（单元格的样式会覆盖列或行的样式）   
	    columnHeadStyle.setFillForegroundColor(HSSFColor.WHITE.index);   
		return columnHeadStyle;
	}
	HSSFCellStyle getNormalStyle(){
		HSSFFont font = workbook.createFont();   
	    font.setFontName("宋体");   
	    font.setFontHeightInPoints((short) 10);   
	    // 普通单元格样式   
	    HSSFCellStyle style = workbook.createCellStyle();   
	    style.setFont(font);   
	    style.setAlignment(HSSFCellStyle.ALIGN_LEFT);// 左右居中   
	    style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中   
	    style.setWrapText(true);   
	    style.setLeftBorderColor(HSSFColor.BLACK.index);   
	    style.setBorderLeft((short) 1);   
	    style.setRightBorderColor(HSSFColor.BLACK.index);   
	    style.setBorderRight((short) 1);   
	    style.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 设置单元格的边框为粗体   
	    style.setBottomBorderColor(HSSFColor.BLACK.index); // 设置单元格的边框颜色．   
	    style.setFillForegroundColor(HSSFColor.WHITE.index);// 设置单元格的背景颜色．
	    return style;
	}
	HSSFCellStyle getCenterStyle(){
		HSSFFont font = workbook.createFont();   
	    font.setFontName("宋体");   
	    font.setFontHeightInPoints((short) 10);   
		 // 另一个样式   
	    HSSFCellStyle centerstyle = workbook.createCellStyle();   
	    centerstyle.setFont(font);   
	    centerstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中   
	    centerstyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中   
	    centerstyle.setWrapText(true);   
	    centerstyle.setLeftBorderColor(HSSFColor.BLACK.index);   
	    centerstyle.setBorderLeft((short) 1);   
	    centerstyle.setRightBorderColor(HSSFColor.BLACK.index);   
	    centerstyle.setBorderRight((short) 1);   
	    centerstyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 设置单元格的边框为粗体   
	    centerstyle.setBottomBorderColor(HSSFColor.BLACK.index); // 设置单元格的边框颜色．   
	    centerstyle.setFillForegroundColor(HSSFColor.WHITE.index);// 设置单元格的背景颜色．   
	    return centerstyle;
	}
	HSSFCellStyle getAmountStyle(){
		HSSFFont font = workbook.createFont();   
	    font.setFontName("宋体");   
	    font.setFontHeightInPoints((short) 10);   
		 // 另一个样式   
	    HSSFCellStyle style = workbook.createCellStyle();   
	    style.setFont(font);   
	    style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);// 左右居中   
	    style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中   
	    style.setWrapText(true);   
	    style.setLeftBorderColor(HSSFColor.BLACK.index);   
	    style.setBorderLeft((short) 1);   
	    style.setRightBorderColor(HSSFColor.BLACK.index);   
	    style.setBorderRight((short) 1);   
	    style.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 设置单元格的边框为粗体   
	    style.setBottomBorderColor(HSSFColor.BLACK.index); // 设置单元格的边框颜色．   
	    style.setFillForegroundColor(HSSFColor.WHITE.index);// 设置单元格的背景颜色．	    
	    HSSFDataFormat format= workbook.createDataFormat();  
	    style.setDataFormat(format.getFormat("#,##0.00"));         
	    return style;
	}
	String readDate(Object obj){ 
		if(obj==null){
			return "";
		}else{
		    SimpleDateFormat formatter; 
		    formatter = new SimpleDateFormat ("yyyy-MM-dd"); 
		    String ctime = formatter.format(obj); 
		    return ctime; 
		}
	} 
	Double readDouble(Object obj) {
		if(obj==null) {
			return 0.00;
		}else {
			return Double.parseDouble(obj.toString());
		}
	}
	Integer readInteger(Object obj) {
		if(obj==null) {
			return 0;
		}else {
			return Integer.parseInt(obj.toString());
		}
	}
	String readString(Object obj) {
		if(obj==null) {
			return "";
		}else {
			return obj.toString();
		}
	}
	
	void buildTitle(){
		// 创建第一行   
	      HSSFRow row0 = sheet.createRow(rowIndex);   
	      // 设置行高   
	      row0.setHeight((short) 900);
	      setCellValue(row0,0,getTitleStyle(),"报价单",new Integer[]{0,0,0,9});
	      HSSFRow row1 = sheet.createRow(++rowIndex); 
	      
	      HSSFCellStyle normalStyle=getNormalStyle();
	      HSSFCellStyle amountStyle=getAmountStyle();	      
	      setCellValue(row1,0,normalStyle,"单据编号：",null);
	      setCellValue(row1,1,normalStyle,bill.getBillno(),new Integer[]{rowIndex,rowIndex,1,4});
	      setCellValue(row1,5,normalStyle,"客户名称：",new Integer[]{rowIndex,rowIndex,5,6});
	      setCellValue(row1,7,normalStyle,bill.getCustname(),new Integer[]{rowIndex,rowIndex,7,9});
	      HSSFRow row2 = sheet.createRow(++rowIndex); 
	      setCellValue(row2,0,normalStyle,"项目名称：",null);
	      setCellValue(row2,1,normalStyle,bill.getProjectname(),new Integer[]{rowIndex,rowIndex,1,4});
	      setCellValue(row2,5,normalStyle,"项目总额：",new Integer[]{rowIndex,rowIndex,5,6});
	      setCellValue(row2,7,amountStyle,bill.getTotalamount(),new Integer[]{rowIndex,rowIndex,7,8});
	      if(bill.getAfteramount()!=0){
	    	  HSSFRow row3 = sheet.createRow(++rowIndex); 
	    	  setCellValue(row3,0,normalStyle,"折扣：",null);
		      setCellValue(row3,1,normalStyle,bill.getDiscountrate(),new Integer[]{rowIndex,rowIndex,1,4});
		      setCellValue(row3,5,normalStyle,"折后金额：",new Integer[]{rowIndex,rowIndex,5,6});
		      setCellValue(row3,7,amountStyle,bill.getAfteramount(),new Integer[]{rowIndex,rowIndex,7,8});
	      }
	}
	  @SuppressWarnings("deprecation")
	void setCellValue(HSSFRow row,Integer colIndex,HSSFCellStyle style,String text,Integer[] rangeAddress){		
		 HSSFCell cell1=row.createCell(colIndex);
	      cell1.setCellValue(new HSSFRichTextString(text));   
	      cell1.setCellStyle(style);  
	      if(rangeAddress!=null && rangeAddress.length==4){
	    	
			CellRangeAddress region=new CellRangeAddress(
	    			  rangeAddress[0],
	    			  rangeAddress[1],
	    			  rangeAddress[2],
	    			  rangeAddress[3]);
		      sheet.addMergedRegion(region);
		      int border=1;
		      RegionUtil.setBorderTop(border, region, sheet, workbook);
		      RegionUtil.setBorderBottom(border, region, sheet, workbook);
		      RegionUtil.setBorderRight(border, region, sheet, workbook);
		      RegionUtil.setBorderLeft(border, region, sheet, workbook);
	      }
	}
	  @SuppressWarnings("deprecation")
	void setCellValue(HSSFRow row,Integer colIndex,HSSFCellStyle style,Double amount,Integer[] rangeAddress){	
	      HSSFCell cell1=row.createCell(colIndex);
	      cell1.setCellValue(amount);   
	      cell1.setCellStyle(style);  
		  if(rangeAddress!=null && rangeAddress.length==4){
	    	  CellRangeAddress region=new CellRangeAddress(
	    			  rangeAddress[0],
	    			  rangeAddress[1],
	    			  rangeAddress[2],
	    			  rangeAddress[3]);
	    	  
		      sheet.addMergedRegion(region);		  
		      int border=1;
		      RegionUtil.setBorderTop(border, region, sheet, workbook);
		      RegionUtil.setBorderBottom(border, region, sheet, workbook);
		      RegionUtil.setBorderRight(border, region, sheet, workbook);
		      RegionUtil.setBorderLeft(border, region, sheet, workbook);
	      }
	}
	  @SuppressWarnings("deprecation")
	void setCellValue(HSSFRow row,Integer colIndex,HSSFCellStyle style,Integer intValue,Integer[] rangeAddress){	
		 
	      HSSFCell cell1=row.createCell(colIndex);
	      cell1.setCellValue(intValue);   
	      cell1.setCellStyle(style);   
	      if(rangeAddress!=null && rangeAddress.length==4){
	    	  CellRangeAddress region=new CellRangeAddress(
	    			  rangeAddress[0],
	    			  rangeAddress[1],
	    			  rangeAddress[2],
	    			  rangeAddress[3]);
		      sheet.addMergedRegion(region);
		      int border=1;
		      RegionUtil.setBorderTop(border, region, sheet, workbook);
		      RegionUtil.setBorderBottom(border, region, sheet, workbook);
		      RegionUtil.setBorderRight(border, region, sheet, workbook);
		      RegionUtil.setBorderLeft(border, region, sheet, workbook);
	      }
	     
	}

	
	void buildEmptyRow(){
		HSSFRow row = sheet.createRow(++rowIndex);	   
	    row.setHeight((short) 100);
	}
	
	HSSFRow buildColumnHead(){
		HSSFRow row = sheet.createRow(++rowIndex);	
		setCellValue(row,3,getColumnHeadStyle(),"名称",null);
		setCellValue(row,4,getColumnHeadStyle(),"规格型号",new Integer[]{rowIndex,rowIndex,4,5});
		setCellValue(row,6,getColumnHeadStyle(),"数量",null);
		setCellValue(row,7,getColumnHeadStyle(),"价格",null);
		setCellValue(row,8,getColumnHeadStyle(),"金额",null);
		setCellValue(row,9,getColumnHeadStyle(),"备注",null);
		return row;
	}
	
	void buildMainDoor(String number,String doortype){
		HSSFRow rowColumnHead=buildColumnHead();
		
		HSSFRow row1 = sheet.createRow(++rowIndex);	
		List<ExcelOfferEntry> currentDoorEntrys=	bill.getEntryList().stream()
				.filter(entry->entry.getNumber()!=null && entry.getNumber().equals(number))
				.collect(Collectors.toList());
		ExcelOfferEntry door=currentDoorEntrys.stream()
				.filter(entry->entry.getIndex()==-1)
				.findAny()
				.get();
		
		setCellValue(row1,3,getNormalStyle(),door.getName(),null);
		setCellValue(row1,4,getNormalStyle(),door.getModel(),new Integer[]{rowIndex,rowIndex,4,5});
		setCellValue(row1,6,getCenterStyle(),door.getQuatity(),null);
		setCellValue(row1,7,getAmountStyle(),door.getPrice(),null);
		setCellValue(row1,8,getAmountStyle(),door.getAmount(),null);
		setCellValue(row1,9,getNormalStyle(),door.getRemark(),null);
		//一般参数
		Integer paramStart=++rowIndex;	
		HSSFRow row2 = sheet.createRow(rowIndex);		
		setCellValue(row2,4,getColumnHeadStyle(),"参数项",new Integer[]{rowIndex,rowIndex,4,5});
		setCellValue(row2,6,getColumnHeadStyle(),"参数值",new Integer[]{rowIndex,rowIndex,6,9});
		List<ExcelOfferEntry> paramList=currentDoorEntrys.stream()
				.filter(entry->entry.getIndex()==-2)
				.collect(Collectors.toList());
		Iterator<ExcelOfferEntry>  m =paramList.iterator();
		while (m.hasNext()) {  
			 ExcelOfferEntry dr = m.next();  
			 HSSFRow row3 = sheet.createRow(++rowIndex);	
			setCellValue(row3,4,getCenterStyle(),dr.getName(),new Integer[]{rowIndex,rowIndex,4,5});
			setCellValue(row3,6,getCenterStyle(),dr.getModel(),new Integer[]{rowIndex,rowIndex,6,9});
		}
		setCellValue(row2,3,getCenterStyle(),"一般参数",new Integer[]{paramStart,rowIndex,3,3});
		buildEmptyRow();
		buildColumnHead();
		
		setCellValue(rowColumnHead,1,getColumnHeadStyle(),number,new Integer[]{paramStart-2,rowIndex,1,2});		
		//标准配件
		Integer standardStart=rowIndex;
		List<ExcelOfferEntry> standardList=currentDoorEntrys.stream()
				.filter(entry->entry.getOffersubgroup()==OfferSubGroup.STANDARD)
				.collect(Collectors.toList());
		Iterator<ExcelOfferEntry>  ms =standardList.iterator();
		Integer index=1;
		while (ms.hasNext()) {  
			 ExcelOfferEntry dr = ms.next();  
			 HSSFRow row = sheet.createRow(++rowIndex);	
			 setCellValue(row,2,getCenterStyle(),index,null);//序号
			setCellValue(row,3,getNormalStyle(),dr.getName(),null);//名称
			setCellValue(row,4,getCenterStyle(),dr.getModel(),new Integer[]{rowIndex,rowIndex,4,5});//规格型号
			setCellValue(row,6,getCenterStyle(),dr.getQuatity(),null);//数量
			setCellValue(row,7,getAmountStyle(),dr.getPrice(),null);//单价
			setCellValue(row,8,getAmountStyle(),dr.getAmount(),null);//总价
			setCellValue(row,9,getNormalStyle(),dr.getRemark(),null);//备注
			index++;
		}
		setCellValue(sheet.getRow(standardStart+1),1,getCenterStyle(),"标准配件",new Integer[]{standardStart+1,rowIndex,1,1});
		//可选配件
		Integer optionsStart=rowIndex;
		List<ExcelOfferEntry> optionsList=currentDoorEntrys.stream()
				.filter(entry->entry.getOffersubgroup()==OfferSubGroup.OPTIONS)
				.collect(Collectors.toList());
		Iterator<ExcelOfferEntry>  mo =optionsList.iterator();
		index=1;
		while (mo.hasNext()) {  
			 ExcelOfferEntry dr = mo.next();  
			 HSSFRow row = sheet.createRow(++rowIndex);	
			 setCellValue(row,2,getCenterStyle(),index,null);//序号
			setCellValue(row,3,getNormalStyle(),dr.getName(),null);//名称
			setCellValue(row,4,getCenterStyle(),dr.getModel(),new Integer[]{rowIndex,rowIndex,4,5});//规格型号
			setCellValue(row,6,getCenterStyle(),dr.getQuatity(),null);//数量
			setCellValue(row,7,getAmountStyle(),dr.getPrice(),null);//单价
			setCellValue(row,8,getAmountStyle(),dr.getAmount(),null);//总价
			setCellValue(row,9,getNormalStyle(),dr.getRemark(),null);//备注
			index++;
		}
		//如果有可选
		if(rowIndex>optionsStart){
			setCellValue(sheet.getRow(optionsStart+1),1,getCenterStyle(),"可选配件",new Integer[]{optionsStart+1,rowIndex,1,1});
		}
		//表面处理
		List<ExcelOfferEntry> surfaceList=currentDoorEntrys.stream()
				.filter(entry->entry.getIndex()==-3)
				.collect(Collectors.toList());
		if(surfaceList.size()>0)
		{
			ExcelOfferEntry surface=surfaceList.stream().findAny().get();
			HSSFRow row5 = sheet.createRow(++rowIndex);			
			setCellValue(row5,2,getColumnHeadStyle(),"名称",new Integer[]{rowIndex,rowIndex,2,5});//名称
			setCellValue(row5,6,getColumnHeadStyle(),"系数",new Integer[]{rowIndex,rowIndex,6,8});
			setCellValue(row5,9,getColumnHeadStyle(),"备注",null);
			
			
			HSSFRow row6 = sheet.createRow(++rowIndex);			
			setCellValue(row6,2,getCenterStyle(),surface.getName(),new Integer[]{rowIndex,rowIndex,2,5});//名称
			setCellValue(row6,6,getAmountStyle(),surface.getPrice(),new Integer[]{rowIndex,rowIndex,6,8});
			setCellValue(row6,9,getNormalStyle(),surface.getModel(),null);
			
			setCellValue(sheet.getRow(rowIndex-1),1,getCenterStyle(),"表面处理",new Integer[]{rowIndex-1,rowIndex,1,1});
		}
		setCellValue(rowColumnHead,0,getCenterStyle(),
				doortype.toLowerCase().equals("xzm")?"旋转门及其选项":"平门系列及选项",
				new Integer[]{rowColumnHead.getRowNum(),rowIndex,0,0});
		
	}
	
	void buildGroupList(OfferGroup offergroup){
		List<ExcelOfferEntry> current=null;
		Integer index=1;
		String groupname="";
		switch(offergroup){
		case FRAMEWORK:
			current=bill.getGroupList().stream()
				.filter(info->info.getOffergroup()==OfferGroup.FRAMEWORK)
				.collect(Collectors.toList());
			groupname="边门门体及门区框架部分";			
			break;
		case SIDEDOOR:
			current=bill.getGroupList().stream()
				.filter(info->info.getOffergroup()==OfferGroup.SIDEDOOR)
				.collect(Collectors.toList());
			groupname="边门选项";
			break;
		case OTHER:
			HSSFRow row5 = sheet.createRow(++rowIndex);		
			setCellValue(row5,2,getColumnHeadStyle(),"序号",null);
			setCellValue(row5,3,getColumnHeadStyle(),"项目",new Integer[]{rowIndex,rowIndex,3,7});//名称
			setCellValue(row5,8,getColumnHeadStyle(),"总价",null);
			setCellValue(row5,9,getColumnHeadStyle(),"备注",null);
			current=bill.getGroupList().stream()
				.filter(info->info.getOffergroup()==OfferGroup.OTHER).collect(Collectors.toList());
			for(ExcelOfferEntry info:current){
				HSSFRow row6 = sheet.createRow(++rowIndex);		
				setCellValue(row6,2,getCenterStyle(),index,null);
				setCellValue(row6,3,getNormalStyle(),info.getName(),new Integer[]{rowIndex,rowIndex,3,7});//名称
				setCellValue(row6,8,getAmountStyle(),info.getAmount(),null);
				setCellValue(row6,9,getNormalStyle(),info.getRemark(),null);
				index++;
			}
			setCellValue(row5,0,getCenterStyle(),"其他费用",new Integer[]{row5.getRowNum(),rowIndex,0,1});			
			return;
		}
		if(current==null){
			return;
		}
		HSSFRow row = sheet.createRow(++rowIndex);	
		setCellValue(row,2,getColumnHeadStyle(),"序号",null);
		setCellValue(row,3,getColumnHeadStyle(),"名称",null);
		setCellValue(row,4,getColumnHeadStyle(),"规格型号",null);
		setCellValue(row,5,getColumnHeadStyle(),"单位",null);
		setCellValue(row,6,getColumnHeadStyle(),"数量",null);
		setCellValue(row,7,getColumnHeadStyle(),"单价",null);
		setCellValue(row,8,getColumnHeadStyle(),"总价",null);
		setCellValue(row,9,getColumnHeadStyle(),"备注",null);
		for(ExcelOfferEntry info:current){
			HSSFRow row1 = sheet.createRow(++rowIndex);		
			setCellValue(row1,2,getCenterStyle(),index,null);
			setCellValue(row1,3,getNormalStyle(),info.getName(),null);//名称
			setCellValue(row1,4,getNormalStyle(),info.getModel(),null);//规格
			setCellValue(row1,5,getCenterStyle(),info.getUnit(),null);//单位
			setCellValue(row1,6,getCenterStyle(),info.getQuatity(),null);//数量
			setCellValue(row1,7,getAmountStyle(),info.getPrice(),null);//单价
			setCellValue(row1,8,getAmountStyle(),info.getAmount(),null);//总价		
			setCellValue(row1,9,getNormalStyle(),info.getRemark(),null);
			index++;
		}
		setCellValue(row,0,getCenterStyle(),groupname,new Integer[]{row.getRowNum(),rowIndex,0,1});		
	}
	
	void buildFoot(){
		HSSFRow row = sheet.createRow(++rowIndex);	
		setCellValue(row,5,getNormalStyle(),"编制人：",null);
		setCellValue(row,6,getNormalStyle(),bill.getSaleman(),null);
		setCellValue(row,8,getNormalStyle(),"编制日期：",null);
		setCellValue(row,9,getNormalStyle(),bill.getCreatedate(),null);
	}
}
