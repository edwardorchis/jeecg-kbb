
var whole_amount=0.00;
var stanard_amount=0.00;
var parts_amout=0.00;
var surfaceRatio=1.00;
var totalAmount=0.00;
var detail2_obj={};

function jParentOrign(id){
	return $('#'+id,window.parent.document);
}

function jParent(id){
	id=id.replace('[','\\[').replace(']','\\]');
	id=id.replace('.','\\.');
	return $('#'+id,window.parent.document);
}

function jObject(id){
	id=id.replace('[','\\[').replace(']','\\]');
	id=id.replace('.','\\.');
	return $('#'+id);
}

function initDialog(){	
	var tag=jParent('detailModal_tag').val();
	var action=jParent('detailModal_action').val();
	jParent("p1ModelHeadCaption").text(jObject(tag+".item_name").val());
	jParent("p5Amount.wholerate").val(jObject(tag+".wholerate").val());
	jParent("p5Amount.partsrate").val(jObject(tag+".partsrate").val());
	jParent("p5Amount.amount").val(jObject(tag+".price").val());
	
	var detail2_json=jObject(tag+".detail2json").val();
	if(detail2_json){
		var obj =JSON.parse(detail2_json);
		setRedioValue("p1ModelE",obj.p1);
		setCheckValue('p2Standard',obj.p2);
		setCheckValue('p3Option',obj.p3);
		setRedioValue('p4Surface',obj.p4);
	}
	jParent("p5Amount.remark").val(jObject(tag+".remark").val());
	if(action=="view"){
		jParentOrign("modal-dialog-body input").each(function(){
			$(this).attr("disabled","disabled");
		});
		jParentOrign("detailModal .btn-primary").hide();
	}else{
		jParentOrign("detailModal .btn-primary").show();
		jParentOrign("detailModal input[type='checkbox']").each(function(){
			$(this).bind("click",function(){
		    		this.value=this.checked;
		    		detail2getAmount();
		    	});
	    	});
		jParentOrign("detailModal input[type='radio']").each(function(){
			$(this).bind("click",function(){
				detail2getAmount();
	    	});
    	});
		
		jParentOrign("detailModal .btn-primary").bind("click",function(){
			jParent("detailModal").modal('hide');
			var tag=jParent('detailModal_tag').val();
			var $this=jObject(tag+".price");
			$this.val(totalAmount);				
			calSumDoorAmount($this);
			
			jObject(tag+".remark").val(jParent("p5Amount.remark").val());
			jObject(tag+".detail2json").val(JSON.stringify(detail2_obj));
			jObject(tag+".wholerate").val(jParent("p5Amount.wholerate").val());
			jObject(tag+".partsrate").val(jParent("p5Amount.partsrate").val());		
		});
		detail2getAmount();
	}
	
}
function setRedioValue(idprex,objval){
	if(objval){
		var i =0;
		var p1rowdata=jParent(idprex +'['+i+"].id");
		while(p1rowdata.length>0){
			if(p1rowdata.val()==objval){				
				jParent(idprex +'['+i+'].ck').attr('checked',true);
				break;
			}
			i=i+1;
			p1rowdata=jParent(idprex +'['+i+"].id");
		}
	}
}
function setCheckValue(idprex,arr){
	if(arr!=null && arr.length>0){
		var i =0;
		var p1rowdata=jParent(idprex+'['+i+"].id");
		while(p1rowdata.length>0){
			jParent(idprex +'['+i+'].ck').val('false');
			jParent(idprex +'['+i+'].ck').attr('checked',false);
//			$('#'+idprex+'['+i+'].ck').value="false";
//			document.getElementById(idprex +'['+i+'].ck').checked=false;
			for(var j=0;j<arr.length;j++){
				if(arr[j]==p1rowdata.val()){
					jParent(idprex +'['+i+'].ck').attr('checked',true);
					jParent(idprex +'['+i+'].ck').val('true');
//					document.getElementById(idprex +'['+i+'].ck').checked=true;
//					$('#'+idprex+'['+i+'].ck').value="true";
					break;
				}
			}				
			i=i+1;
			p1rowdata=jParent(idprex+'['+i+"].id");
		}
	}
}




function detail2getAmount(){
	//整门金额
	whole_amount=0.00;
	detail2_obj.p1="";
	jParentOrign("detailModal_door input[name='p1ModelE_radio']:checked").each(function(){
		var whole_id=$(this).attr("id");
		whole_amount=parseFloat(jParent(whole_id.replace('.ck','.fprice')).val());
		detail2_obj.p1=jParent(whole_id.replace('.ck','.id')).val();
	});
	//配件金额
	stanard_amount=0.00;
	var p2array=new Array();
	var i=0;
	jParentOrign("p2Standard input[type='checkbox']").each(function(){
	    	if (this.checked) {	    		
	    		 p2array[i]=jParent($(this).attr('id').replace('.ck','.id')).val();
	    		 i++;
	           }else{
	        	   var amount_id=$(this).attr('id').replace('.ck','.famount');
	        	   stanard_amount=stanard_amount+parseFloat(jParent(amount_id).val());
	           }
	    	});
	 detail2_obj.p2=p2array;
	 parts_amout=0.00;
	 i=0;
	 var p3array=new Array();
	 jParentOrign("p3Option input[type='checkbox']").each(function(){
	    	if (this.checked) {
	    		 var amount_id=$(this).attr('id').replace('.ck','.famount');
	    		 parts_amout=parts_amout+parseFloat(jParent(amount_id).val());
	    		 p3array[i]=jParent($(this).attr('id').replace('.ck','.id')).val();
	    		 i++;
	           }
	    	});
	 detail2_obj.p3=p3array;	
	 detail2_obj.p4="";
	 surfaceRatio=1.00;
	 jParentOrign("p4Surface input[name='p4Surface_radio']:checked").each(function(){
			var surface_id=$(this).attr("id");
			surfaceRatio=parseFloat(jParent(surface_id.replace('.ck','.fratio')).val());
			detail2_obj.p4=jParent($(this).attr('id').replace('.ck','.id')).val();
		});	
	 
	 var partsRate=parseFloat(jParent("p5Amount.partsrate").val());
	 var wholeRate=parseFloat(jParent("p5Amount.wholerate").val());
	 
	 totalAmount=((whole_amount-stanard_amount)*wholeRate+parts_amout*partsRate)*surfaceRatio;
	 jParent("p5Amount.amount").val(fmoney(totalAmount,2));
}
