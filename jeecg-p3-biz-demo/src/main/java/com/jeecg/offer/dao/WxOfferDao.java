package com.jeecg.offer.dao;

import java.util.List;
import java.util.Map;

import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;
import org.jeecgframework.minidao.pojo.MiniDaoPage;
import org.springframework.stereotype.Repository;

import com.jeecg.offer.entity.WxGroupInfos;
import com.jeecg.offer.entity.WxOffer;
import com.jeecg.offer.entity.WxRevolutionDoor;


@Repository
public interface WxOfferDao {

	/**
	 * 查询返回Java对象
	 * @param id
	 * @return
	 */
	@Sql("SELECT t0.id,t0.fbillno,t0.fcustid,t0.fprojectid,t1.fname as fproject_name,t2.fname as fcust_name,"
+" t0.fremark,t0.famount,t0.fstatus,t0.fapplicant,t0.fapplicant_date,t0.fcurrent_approver "
+" FROM t_offers t0 "
+" inner join t_base_project t1 on t0.fprojectid=t1.id" 
+" inner join t_base_customer t2 on t0.fcustid=t2.id"
+" where t0.id=:id")
	WxOffer get(@Param("id") String id);
	
	
	/**
	 * 修改数据
	 * @param act
	 * @return
	 */
	@Sql("update t_offers set fcustid=:act.fcustid,famount=:act.famount,fprojectid=:act.fprojectid where id=:act.id")
	int update(@Param("act") WxOffer act);

	/**
	 * 插入数据
	 * @param act
	 */
	void insert(@Param("act") WxOffer act);
	
	/**
	 * 通用分页方法，支持（oracle、mysql、SqlServer、postgresql）
	 * @param act
	 * @param page
	 * @param rows
	 * @return
	 */
	@ResultType(WxOffer.class)
	public MiniDaoPage<WxOffer> getAll(@Param("act") WxOffer act,@Param("page")  int page,@Param("rows") int rows);
	
	@Sql("DELETE from t_offers WHERE ID = :id")
	public void delete(@Param("id") String id);
	
	@Sql("select * from t_door_standard where foreignid= :item_id")
	List<Map> getDetail2StandardInfo(@Param("item_id") String item_id);
	@Sql("select * from t_door_options where foreignid= :item_id")
	List<Map> getDetail2OptionInfo(@Param("item_id") String item_id);
	@Sql("select * from t_door_surface where foreignid= :item_id")
	List<Map> getDetail2SurfaceInfo(@Param("item_id") String item_id);
}
