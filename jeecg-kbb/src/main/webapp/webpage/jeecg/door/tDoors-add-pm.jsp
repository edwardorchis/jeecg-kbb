<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<!DOCTYPE html>
<html>
 <head>
  <title>门型维护</title>
  <t:base type="jquery,easyui,tools,DatePicker"></t:base> 
 </head>
 <body style="overflow-x: hidden;">
  <t:formvalid formid="formobj" dialog="true" usePlugin="password" layout="table" tiptype="1" action="tDoorsController.do?doAdd" >
			<input id="id" name="id" type="hidden" value="${tDoorsPage.id }">
			<table cellpadding="0" cellspacing="1" class="formtable">
				<tr>
					<td align="right">
						<label class="Validform_label">代码:</label>
					</td>
					<td class="value">
				     	 <input id="fnumber" name="fnumber" type="text" style="width: 150px" class="inputxt" 
						
				     	 >
						<span class="Validform_checktip"></span>
						<label class="Validform_label" style="display: none;">代码</label>
					</td>
					<td align="right">
						<label class="Validform_label">名称:</label>
					</td>
					<td class="value">
				     	 <input id="fname" name="fname" type="text" style="width: 150px" class="inputxt"datatype="*" 
				     	 >
						<span class="Validform_checktip"></span>
						<label class="Validform_label" style="display: none;">名称</label>
					</td>
				</tr>
				<tr>
					<td align="right">
						<label class="Validform_label">类型:</label>
					</td>
					<td class="value">
						<input name="fdoortype" type="hidden" value="PM" /> 
						<t:dictSelect id="selectDoorType" field="fdoortype" type="list"
						typeGroupCode="doorType"  hasLabel="false"  title="类型" datatype="*"
						defaultVal="PM" readonly="readonly"
						></t:dictSelect>   

						<span class="Validform_checktip"></span>
						<label class="Validform_label" style="display: none;">类型</label>
					</td>
				</tr>
			</table>
			<div style="width: auto;height: 200px;">
				<%-- 增加一个div，用于调节页面大小，否则默认太小 --%>
				<div style="width:800px;height:1px;"></div>
				<t:tabs id="tt" iframe="false" tabPosition="top" fit="false">
				 <t:tab href="tDoorsController.do?tDoorParamsList&id=${tDoorsPage.id}" icon="icon-search" title="一般参数" id="tabDoorParams"></t:tab>
				 <t:tab href="tDoorsController.do?tDoorsModelList&id=${tDoorsPage.id}" icon="icon-search" title="规格型号" id="tabDoorsModel"></t:tab> 
				 <t:tab href="tDoorsController.do?tDoorStandardList&id=${tDoorsPage.id}&doortype=PM" icon="icon-search" title="标准配件" id="tabDoorStandard"></t:tab>			
				 <t:tab href="tDoorsController.do?tDoorOptionsList&id=${tDoorsPage.id}&doortype=PM" icon="icon-search" title="可选配件" id="tabDoorOptions"></t:tab>
				 <t:tab href="tDoorsController.do?tDoorSurfaceList&id=${tDoorsPage.id}" icon="icon-search" title="表面处理" id="tabDoorSurface"></t:tab>			 
				 
				</t:tabs>
			</div>
	</t:formvalid>
	<!-- 添加 附表明细 模版 -->
	<table style="display:none">
	<tbody id="add_tDoorsModel_table_template">
		<tr>
			 <td align="center"><div style="width: 25px;" name="xh"></div></td>
			 <td align="center"><input style="width:20px;" type="checkbox" name="ck"/></td>			 
			   <c:forEach items="${tDoorModelListCaption}" var="pVal">	
			   		<td align="left" id="tdDoorModelExMap[#index#][${pVal.fkey}]" > 
			   			<c:set value="${pVal.fkey}" var="key"/>  
					    <input name="tDoorModelExMap[#index#][${pVal.fkey}]" maxlength="20" 
					  		type="${pVal.finputtype}" class="inputxt"  style="width:120px;" >
					    <label class="Validform_label" style="display: none;">${pVal.fcaption} }</label>
				     </td>
			   </c:forEach>			 
		</tr>
	</tbody>
	<tbody id="add_tDoorStandard_table_template">
		<tr>
			 <td align="center"><div style="width: 25px;" name="xh"></div></td>			 
			 <td align="center"><input style="width:20px;" type="checkbox" name="ck"/></td>
			 <input name="tDoorStandardList[#index#].id" id="tDoorStandardList[#index#].id" type="hidden"/>
				<!--   <td align="left">
					  	<input name="tDoorStandardList[#index#].fnumber" maxlength="50" 
					  		type="text" class="inputxt"  style="width:120px;" datatype="*"
							id="tDoorStandardList[#index#].fnumber"		
					  		>
					  <label class="Validform_label" style="display: none;">代码</label>
				  </td> -->
				  <td align="left">
					  	<input name="tDoorStandardList[#index#].fname" maxlength="50" 
					  		type="text" class="inputxt"  style="width:120px;" 
							id="tDoorStandardList[#index#].fname"	
					  		>
					  <label class="Validform_label" style="display: none;">名称</label>
				  </td>
				  <td align="left">
					  	<input name="tDoorStandardList[#index#].fmodel" maxlength="50" 
					  		type="text" class="inputxt"  style="width:120px;" 
							id="tDoorStandardList[#index#].fmodel"		
					  		>
					  <label class="Validform_label" style="display: none;">型号</label>
				  </td>
				  <td align="left">
					  	<input name="tDoorStandardList[#index#].fbrand" maxlength="50" 
					  		type="text" class="inputxt"  style="width:120px;" 
							id="tDoorStandardList[#index#].fbrand"		
					  		>
					  <label class="Validform_label" style="display: none;">品牌</label>
				  </td>
				   <td align="left">
					  	<input name="tDoorStandardList[#index#].funit" maxlength="50" 
					  		type="text" class="inputxt"  style="width:120px;" 
							id="tDoorStandardList[#index#].funit"		
					  		>
					  <label class="Validform_label" style="display: none;">单位</label>
				  </td>
				  <td align="left">
					  	<input name="tDoorStandardList[#index#].fqty" maxlength="50" 
					  		type="number" class="inputxt"  style="width:120px;"  datatype="/^-?[1-9]+(\.\d+)?$|^-?0(\.\d+)?$|^-?[1-9]+[0-9]*(\.\d+)?$/"
							id="tDoorStandardList[#index#].fqty"	
							onChange="calAmount('tDoorStandardList[#index#]')" 	
					  		>
					  <label class="Validform_label" style="display: none;">数量</label>
				  </td>
				  <td align="left">
					  	<input name="tDoorStandardList[#index#].fprice" maxlength="50" 
					  		type="number" class="inputxt"  style="width:120px;"  datatype="/^-?[1-9]+(\.\d+)?$|^-?0(\.\d+)?$|^-?[1-9]+[0-9]*(\.\d+)?$/"
							id="tDoorStandardList[#index#].fprice"	
							onChange="calAmount('tDoorStandardList[#index#]')" 
					  		>
					  <label class="Validform_label" style="display: none;">价格</label>
				  </td>
				  <td align="left">
					  	<input name="tDoorStandardList[#index#].famount" maxlength="32" 
					  		type="text" class="inputxt"  style="width:120px;"  datatype="/^-?[1-9]+(\.\d+)?$|^-?0(\.\d+)?$|^-?[1-9]+[0-9]*(\.\d+)?$/"
							readonly="readonly"	id="tDoorStandardList[#index#].famount"	
					  		>
					  <label class="Validform_label" style="display: none;">金额</label>
				  </td>
				  <td align="left">
					  	<input name="tDoorStandardList[#index#].fremark" maxlength="200" 
					  		type="text" class="inputxt"  style="width:120px;" 
							id="tDoorStandardList[#index#].fremark"		
					  		>
					  <label class="Validform_label" style="display: none;">备注</label>
				  </td>
			</tr>
		 </tbody>
	<tbody id="add_tDoorSurface_table_template">
		<tr>
			 <td align="center"><div style="width: 25px;" name="xh"></div></td>
			 <td align="center"><input style="width:20px;" type="checkbox" name="ck"/></td>
			  <input name="tDoorSurfaceList[#index#].id" id="tDoorSurfaceList[#index#].id" type="hidden"/>
				<!--   <td align="left">
					  	<input name="tDoorSurfaceList[#index#].fnumber" maxlength="50" 
					  		type="text" class="inputxt"  style="width:120px;" datatype="*"
							id="tDoorSurfaceList[#index#].fnumber"		
					  		>
					  <label class="Validform_label" style="display: none;">代码</label>
				  </td> -->
				  <td align="left">
					  	<input name="tDoorSurfaceList[#index#].fname" maxlength="50" 
					  		type="text" class="inputxt"  style="width:120px;" 
							id="tDoorSurfaceList[#index#].fname"	
					  		>
					  <label class="Validform_label" style="display: none;">名称</label>
				  </td>
				  <td align="left">
					  	<input name="tDoorSurfaceList[#index#].fratio" maxlength="20" 
					  		type="number" class="inputxt"  style="width:120px;"  datatype="/^-?[1-9]+(\.\d+)?$|^-?0(\.\d+)?$|^-?[1-9]+[0-9]*(\.\d+)?$/"
							id="tDoorSurfaceList[#index#].fratio"		
					  		>
					  <label class="Validform_label" style="display: none;">系数</label>
				  </td>
				  <td align="left">
					  	<input name="tDoorSurfaceList[#index#].fremark" maxlength="200" 
					  		type="text" class="inputxt"  style="width:120px;" 
							id="tDoorSurfaceList[#index#].fremark"		
					  		>
					  <label class="Validform_label" style="display: none;">备注</label>
				  </td>
			</tr>
		 </tbody>
	<tbody id="add_tDoorOptions_table_template">
		<tr>
			 <td align="center"><div style="width: 25px;" name="xh"></div></td>
			 <td align="center"><input style="width:20px;" type="checkbox" name="ck"/></td>
			 <input name="tDoorOptionsList[#index#].id" id="tDoorOptionsList[#index#].id" type="hidden"/>
				  <!-- <td align="left">
					  	<input name="tDoorOptionsList[#index#].fnumber" maxlength="50" 
					  		type="text" class="inputxt"  style="width:120px;" datatype="*"
							id="tDoorOptionsList[#index#].fnumber"		
					  		>
					  <label class="Validform_label" style="display: none;">代码</label>
				  </td> -->
				  <td align="left">
					  	<input name="tDoorOptionsList[#index#].fname" maxlength="50" 
					  		type="text" class="inputxt"  style="width:120px;" 
							id="tDoorOptionsList[#index#].fname"	
					  		>
					  <label class="Validform_label" style="display: none;">名称</label>
				  </td>
				  <td align="left">
					  	<input name="tDoorOptionsList[#index#].fmodel" maxlength="50" 
					  		type="text" class="inputxt"  style="width:120px;" 
							id="tDoorOptionsList[#index#].fmodel"		
					  		>
					  <label class="Validform_label" style="display: none;">型号</label>
				  </td>
				  <td align="left">
					  	<input name="tDoorOptionsList[#index#].fbrand" maxlength="50" 
					  		type="text" class="inputxt"  style="width:120px;" 
							id="tDoorOptionsList[#index#].fbrand"		
					  		>
					  <label class="Validform_label" style="display: none;">品牌</label>
				  </td>
				   <td align="left">
					  	<input name="tDoorOptionsList[#index#].funit" maxlength="50" 
					  		type="text" class="inputxt"  style="width:120px;" 
							id="tDoorOptionsList[#index#].funit"		
					  		>
					  <label class="Validform_label" style="display: none;">单位</label>
				  </td>
				  <td align="left">
					  	<input name="tDoorOptionsList[#index#].fqty" maxlength="50" 
					  		type="number" class="inputxt"  style="width:120px;"  datatype="/^-?[1-9]+(\.\d+)?$|^-?0(\.\d+)?$|^-?[1-9]+[0-9]*(\.\d+)?$/"
							id="tDoorOptionsList[#index#].fqty"	
							onChange="calAmount('tDoorOptionsList[#index#]')" 	
					  		>
					  <label class="Validform_label" style="display: none;">数量</label>
				  </td>
				  <td align="left">
					  	<input name="tDoorOptionsList[#index#].fprice" maxlength="50" 
					  		type="number" class="inputxt"  style="width:120px;"  datatype="/^-?[1-9]+(\.\d+)?$|^-?0(\.\d+)?$|^-?[1-9]+[0-9]*(\.\d+)?$/"
							id="tDoorOptionsList[#index#].fprice"	
							onChange="calAmount('tDoorOptionsList[#index#]')" 
					  		>
					  <label class="Validform_label" style="display: none;">价格</label>
				  </td>
				  <td align="left">
					  	<input name="tDoorOptionsList[#index#].famount" maxlength="32" 
					  		type="text" class="inputxt"  style="width:120px;"  datatype="/^-?[1-9]+(\.\d+)?$|^-?0(\.\d+)?$|^-?[1-9]+[0-9]*(\.\d+)?$/"
							readonly="readonly"	id="tDoorOptionsList[#index#].famount"	
					  		>
					  <label class="Validform_label" style="display: none;">金额</label>
				  </td>
				  <td align="left">
					  	<input name="tDoorOptionsList[#index#].fremark" maxlength="200" 
					  		type="text" class="inputxt"  style="width:120px;" 
							id="tDoorOptionsList[#index#].fremark"		
					  		>
					  <label class="Validform_label" style="display: none;">备注</label>
				  </td>
			</tr>
		 </tbody>
		</table>
 </body>
 <script src = "webpage/jeecg/door/tDoors.js"></script>
	