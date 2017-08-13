package org.jeecgframework.web.activiti.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.activiti.engine.impl.task.TaskDefinition;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.jeecgframework.web.activiti.entity.ProcessorEntity;
import org.jeecgframework.web.activiti.service.IBillService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("offerBillService")
@Transactional
public class OfferBillServiceImpl extends CommonServiceImpl  implements IBillService {

	@Override
	public void setBillStatus(String id, String status) {
		// TODO 自动生成的方法存根
		String sql="update t_offers set fstatus=? where id=?";
		this.commonDao.executeSql(sql,status,id);
	}
	@Override
	public List<Map<String,Object>> findForJdbc(String sql,Object...param){
		return commonDao.findForJdbc(sql,param);
	}
	
	@Override
	public List<ProcessorEntity> getNextprocessor(String userTask){
		if(userTask.equals("areamanager2")){
			userTask="areamanager";
		}
		List<Map<String,Object>> listMap=commonDao.findForJdbc(""
				+ "select t3.id,t3.realname from t_s_role t1 inner join t_s_role_user t2 on t1.id =t2.roleid "
				+" inner join t_s_base_user t3 on t2.userid=t3.ID "
				+" where t1.rolecode=?", userTask);
		List<ProcessorEntity> listProcessor=new ArrayList<ProcessorEntity>();
		Iterator<Map<String,Object>> it = listMap.iterator();		
		 while(it.hasNext()) {
			 Map<String,Object> map= it.next();
			 ProcessorEntity processor=new ProcessorEntity();
			 processor.setId(map.get("id").toString());
			 processor.setFname(map.get("realname").toString());
			 listProcessor.add(processor);
		 }
		 return listProcessor;
	}
}