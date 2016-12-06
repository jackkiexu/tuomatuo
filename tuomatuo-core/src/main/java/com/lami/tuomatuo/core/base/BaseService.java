package com.lami.tuomatuo.core.base;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public abstract class BaseService<T,ID extends Serializable> {
	protected final  Log logger=LogFactory.getLog(this.getClass());
	
	public String getTableName(){
		return baseDao.getTableName();
	}
	
	protected BaseDao<T,ID> baseDao;
	
	/**
	 * 适用于update delete等批量操作
	 * @param queryStr
	 * @param param
	 * @return
	 */
	public int execute(String queryStr,List<Object> param){
		
		return baseDao.execute(queryStr, param);
	}
	/**
	 *  添加资源记录,并返回新增记录的主键<br/>
	 *  如果sql执行失败则返回NULL
	 * @param t
	 * @return
	 */
	public T save(T t){
		return baseDao.save(t);
	}
	
	public ID save(Map<String, Object> map){
		return baseDao.save(map);
	}
	/**
	 * 根据资源id删除
	 * 本操作为<b>真实</b>删除
	 * @param id
	 * @return
	 */
	public int del(ID id){
		return baseDao.del(id);
	}
	
	
	/**
	 * 根据ids删除多个资源
	 * 本操作为<b>真实</b>删除
	 * @param ids
	 * @return
	 */
	public int delByIds(List<ID> ids){
		return baseDao.delByIds(ids);
	}
	
	/**
	 * 根据id更新资源记录
	 * @param t
	 * @return
	 */
	public T update(T t){
		return baseDao.update(t);
	}
	
	public int updateById(Map<String, Object> map){
		return baseDao.updateById(map);
	}
	
	
	/**
	 * 查询本资源一条记录
	 * @param id
	 * @return
	 */
	public T get(ID id){
		return baseDao.get(id);
	}
	
	public T get(String tableName,ID id){
		return baseDao.get(tableName,id);
	}
	
	public PageBean<T> search(T t,PageBean<T> pageBean){
		return baseDao.search(t, pageBean);
	}
	
	public PageBean<T> search(T t,PageBean<T> pageBean,boolean isCount){
		return baseDao.search(t, pageBean,isCount);
	}
	
	public List<T> search(T t){
		return baseDao.search(t);
	}
	public T searchOne(T t){
		return baseDao.searchOne(t);
	}

	
	/**
	 * 根据ids查询本资源多条记录，忽略is_delete标记
	 * @param ids
	 * @return
	 */
	public List<T> getByIds(List<ID> ids){
		return baseDao.getByIds(ids);
	}
	/**
	 * 查询本资源的所有记录
	 * @return
	 */
	public List<T> getAll(){
		return baseDao.getAll();
	}
	
	
	/**
	 * 分页查询本资源的所有记录
	 * @return
	 */
	public PageBean<T> getAll(PageBean<T> pageBean,boolean isCount){
		StringBuilder sb=new StringBuilder("select * from ");
		sb.append(baseDao.getTableName());
		return baseDao.search(sb.toString(), null, pageBean,isCount);
	}
	
	public PageBean<T> getAll(PageBean<T> pageBean){
		return getAll(pageBean,true);
	}
	
	/**
	 * 分页查询<br/>
	 * 如果queryStr不包含select * from table,本方法将自动补全
	 * @param queryStr
	 * @param param
	 * @param pageBean
	 * @return
	 */
	public PageBean<T> search(String queryStr,List<Object> param,PageBean<T> pageBean,boolean isCount){
		StringBuilder sb=new StringBuilder();
		if(!StringUtils.startsWith(queryStr, "select")&&!StringUtils.startsWith(queryStr, "SELECT")){
			sb.append("select * from ");
			sb.append(baseDao.getTableName());
		}
		if(!StringUtils.isEmpty(queryStr)){
			if(sb.length()!=0&&!StringUtils.containsIgnoreCase(sb.toString(), "where")){
				sb.append(" where ");
			}
			sb.append(queryStr);
		}
		return baseDao.search(sb.toString(), param, pageBean,isCount);
	}
	public PageBean<T> search(String queryStr,List<Object> param,PageBean<T> pageBean){
		return search(queryStr,param,pageBean,true);
	}
	/**
	 * 查询<br/>
	 * 如果queryStr不包含select * from table,本方法将自动补全
	 * @param queryStr
	 * @param param
	 * @param pageBean
	 * @return
	 */
	public List<T> search(String queryStr,List<Object> param){ 
		StringBuilder sb=new StringBuilder();
		if(!StringUtils.startsWith(queryStr, "select")&&!StringUtils.startsWith(queryStr, "SELECT")){
			sb.append("select * from ");
			sb.append(baseDao.getTableName());
		}
		if(!StringUtils.isEmpty(queryStr)){
			if(!StringUtils.containsIgnoreCase(queryStr, "where")){
				sb.append(" where ");
			}
			sb.append(queryStr);
		}
		return baseDao.search(sb.toString(), param);
	}
}
