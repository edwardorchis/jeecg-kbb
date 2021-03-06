package org.jeecgframework.web.activiti.entity;

import java.io.File;
import java.util.Map;

public class WorkflowBean {

	private File file;		//流程定义部署文件
	private String filename;//流程定义名称
	
	private String billType;//单据类型，和流程Key保持一致
	private String id;//单据ID
	
	private String deploymentId;//部署对象ID
	private String imageName;	//资源文件名称
	private String taskId;		//任务ID
	private String breanch;		//连线名称
	private String message;		//备注
	
	private String nextprocessor;//流程下一节点人
	
	private Map<String,Object> variables;
	
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDeploymentId() {
		return deploymentId;
	}
	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	public String getBillType() {
		return billType;
	}
	public void setBillType(String billType) {
		this.billType = billType;
	}
	
	public String getNextprocessor() {
		return nextprocessor;
	}
	public void setNextprocessor(String nextprocessor) {
		this.nextprocessor = nextprocessor;
	}
	
	public Map<String,Object> getVariables() {
		return variables;
	}
	public void setVariables(Map<String,Object> variables) {
		this.variables = variables;
	}
	public String getBreanch() {
		return breanch;
	}
	public void setBreanch(String breanch) {
		this.breanch = breanch;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
