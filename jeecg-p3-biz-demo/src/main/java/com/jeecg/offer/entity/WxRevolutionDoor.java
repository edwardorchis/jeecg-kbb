package com.jeecg.offer.entity;

import java.io.Serializable;
import java.util.Date;



public class WxRevolutionDoor implements Serializable {
	
	private String group_id;
	/**序号*/

	private Integer findex;
	
	private String item_id;
	
	private String item_number;

	private String item_name;

	private Double quantity;
	
	private Double price;
	
	private Double amount;	
	
	private String remark;
	public String getGroup_id() {
		return group_id;
	}
	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

    public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
    public Integer getFindex() {
		return findex;
	}
	public void setFindex(Integer findex) {
		this.findex = findex;
	}
    public String getItem_id() {
		return item_id;
	}
	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}
    public String getItem_number() {
		return item_number;
	}
	public void setItem_number(String item_number) {
		this.item_number = item_number;
	}
    public String getItem_name() {
		return item_name;
	}
	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}
    public Double getQuantity() {
		return quantity;
	}
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}
    public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
    public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
    public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
