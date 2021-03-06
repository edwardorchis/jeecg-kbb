package org.jeecgframework.web.offer.entity;

import java.io.Serializable;
import java.util.Date;

import java.math.BigDecimal;

/**
 * 描述：</b>WxActCommoninvite:活动表<br>
 * 实体定义规则
 * 字段不允许存在基本类型，必须都是包装类型(因为基本类型有默认值)
 * 基本数据类型  包装类 byte Byte boolean Boolean short Short char Character int Integer long Long float Float double  Double 
 * @author Administrator
 *
 */
public class WxOffer implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 *ID
	 */
	private String id;
	
	/**单号*/

	private String fbillno;
	/**项目*/
	private String fprojectid;
	private String fprojectname;
	/**客户*/
	private String fcustid;
	private String fcust_name;
	/**金额*/

	private Double famount;
	private String fdiscountrate;
	private String fafteramount;
	/**状态*/

	private String fstatus;
	/**当前审批人*/

	private String fcurrent_approver;
	/**申请人*/

	private String fapplicant;
	/**申请时间*/

	private Date fapplicant_date;
	private Date fapplicant_date_begin;
	private Date fapplicant_date_end;
	/**备注*/

	private String fremark;

	private String fouterprice;
	private String fisoutsource;
	/**
	 * 是否当前用户新增的单据
	 * */
	private boolean fisself;

	public boolean isFisself() {
		return fisself;
	}
	public void setFisself(boolean fisself) {
		this.fisself = fisself;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  单号
	 */
	public java.lang.String getFbillno(){
		return this.fbillno;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  单号
	 */
	public void setFbillno(java.lang.String fbillno){
		this.fbillno = fbillno;
	}
	
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  项目
	 */
	public java.lang.String getFprojectid(){
		return this.fprojectid;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  项目
	 */
	public void setFprojectid(java.lang.String fprojectid){
		this.fprojectid = fprojectid;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  项目
	 */
	public java.lang.String getFprojectname(){
		return this.fprojectname;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  项目
	 */
	public void setFprojectname(java.lang.String fprojectname){
		this.fprojectname = fprojectname;
	}
	
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  客户
	 */	
	public java.lang.String getFcustid(){
		return this.fcustid;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  客户
	 */
	public void setFcustid(java.lang.String fcustid){
		this.fcustid = fcustid;
	}
	
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  客户
	 */	
	public java.lang.String getFcust_name(){
		return this.fcust_name;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  客户
	 */
	public void setFcust_name(java.lang.String fcust_name){
		this.fcust_name = fcust_name;
	}
	
	/**
	 *方法: 取得java.lang.Double
	 *@return: java.lang.Double  金额
	 */	
	public java.lang.Double getFamount(){
		return this.famount;
	}

	/**
	 *方法: 设置java.lang.Double
	 *@param: java.lang.Double  金额
	 */
	public void setFamount(java.lang.Double famount){
		this.famount = famount;
	}
	
	public String getFdiscountrate(){
		return this.fdiscountrate;
	}	
	public void setFdiscountrate(String fdiscountrate){
		this.fdiscountrate = fdiscountrate;
	}
	
	public String getFafteramount(){
		return this.fafteramount;
	}	
	public void setFafteramount(String fafteramount){
		this.fafteramount = fafteramount;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  状态
	 */
	public java.lang.String getFstatus(){
		return this.fstatus;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  状态
	 */
	public void setFstatus(java.lang.String fstatus){
		this.fstatus = fstatus;
	}
	
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  当前审批人
	 */
	public String getFcurrent_approver(){
		return this.fcurrent_approver;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  当前审批人
	 */
	public void setFcurrent_approver(String fcurrent_approver){
		this.fcurrent_approver = fcurrent_approver;
	}
	
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  申请人
	 */
	
	public String getFapplicant(){
		return this.fapplicant;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  申请人
	 */
	public void setFapplicant(String fapplicant){
		this.fapplicant = fapplicant;
	}
	
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  申请时间
	 */
	
	public Date getFapplicant_date(){
		return this.fapplicant_date;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  申请时间
	 */
	public void setFapplicant_date(Date fapplicant_date){
		this.fapplicant_date = fapplicant_date;
	}
	
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  备注
	 */
	public java.lang.String getFremark(){
		return this.fremark;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  备注
	 */
	public void setFremark(java.lang.String fremark){
		this.fremark = fremark;
	}
	
	public Date getFapplicant_date_begin(){
		return this.fapplicant_date_begin;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  申请时间
	 */
	public void setFapplicant_date_begin(Date fapplicant_date_begin){
		this.fapplicant_date_begin = fapplicant_date_begin;
	}
	public Date getFapplicant_date_end(){
		return this.fapplicant_date_end;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  申请时间
	 */
	public void setFapplicant_date_end(Date fapplicant_date_end){
		this.fapplicant_date_end = fapplicant_date_end;
	}
	public String getFouterprice() {
		return fouterprice;
	}
	public String getFisoutsource() {
		return fisoutsource;
	}
	public void setFouterprice(String fouterprice) {
		this.fouterprice = fouterprice;
	}
	public void setFisoutsource(String fisoutsource) {
		this.fisoutsource = fisoutsource;
	}
	
	
}

