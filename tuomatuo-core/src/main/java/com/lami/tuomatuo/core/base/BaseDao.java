package com.lami.tuomatuo.core.base;


import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;


public interface BaseDao<T,ID extends Serializable> {
	
	public void setJb(JdbcTemplate jb);
	
	public int execute(String sql, List<Object> values);
	/**
	 * 基础接口之一</br>
	 * 批量添加接口
	 * @param sql
	 * @param values
	 * @return
	 */
	public int[] batchAdd(String sql, List<List<Object>> values);
	/**
	 * 基础接口之一</br>
	 * 批量删除记录接口
	 * @param sql
	 * @param values
	 * @return
	 */
	public int del(String sql, List<Object> values);
	
	/**
	 * 基础接口之一</br>
	 * 更新接口
	 * @param sql
	 * @param values
	 * @return
	 */
	public int update(String sql, List<Object> values);
	
	public int updateById(Map<String, Object> map);
	
	/**
	 * 基础接口之一</br>
	 * 查询一个long值
	 * @param sql
	 * @param values
	 * @return
	 */
	public long getLong(String sql, List<Object> values);
	
	/**
	 * 基础接口之一</br>
	 * 查询一个int值
	 * @param sql
	 * @param values
	 * @return
	 */
	public int getInt(String sql, List<Object> values);
	
	/**
	 * 基础接口之一</br>
	 * sql查询通用接口
	 * @param sql
	 * @param values
	 * @return
	 */
	public List<T> search(String sql, List<Object> values);
	
	/**
	 * 基础接口之一</br>
	 * sql查询通用接口
	 * @param sql
	 * @param values
	 * @param e
	 * @return
	 */
	public <E> List<E> search(String sql, List<Object> values, Class<E> e);
	
	/**
	 * 分页查询
	 *
	 * @param sql
	 * @param values
	 * @param pageBean
	 * @return
	 */
	public PageBean<T> search(String sql, List<Object> values, PageBean<T> pageBean);
	public PageBean<T> search(String sql, List<Object> values, PageBean<T> pageBean, boolean isCount);
	/**
	 * 分页查询
	 *
	 * @param sql
	 * @param values
	 * @param pageBean
	 * @return
	 */
	public <E> PageBean<E> search(String sql, List<Object> values, PageBean<E> pageBean, Class<E> e);
	public <E> PageBean<E> search(String sql, List<Object> values, PageBean<E> pageBean, Class<E> e, boolean isCount);
	/**
	 * 根据主键修改数据
	 *
	 * @param t
	 * @return
	 */
	public T update(T t);
	/**
	 * 批量添加接口
	 * @param list
	 * @return
	 */
	public int[] batchSave(List<T> list);
	/**
	 * 获取一条sql执行后返回的记录数
	 * @param sql
	 * @param values
	 * @return
	 */
	public int getCount(String sql, List<Object> values);
	
	/**
	 * 根据id值删除多条数据
	 * @param ids
	 * @return
	 */
	public int delByIds(List<ID> ids);
	/**
	 * 根据id删除一条记录
	 * @param id
	 * @return
	 */
	public int del(ID id);

	/**
	 * 根据id值查询多条数据
	 * @param ids
	 * @return
	 */
	public List<T> getByIds(List<ID> ids);
	
	public T get(String tableName, ID id);
	/**
	 * 根据id值查询
	 * @param id
	 * @return
	 */
	public T get(ID id);
	/**
	 * 获取本表所有记录
	 * @return
	 */
	public List<T> getAll();
	/**
	 * 获取主键列名
	 * @return
	 */
	public String getPk();
	/**
	 * 获取表名
	 * @return
	 */
	public String getTableName();
	/**
	 * 添加记录,并返回新增记录的主键<br/>
	 * 注意:<br/>
	 * 当主键生成策略为IDENTITY（数据库自增）时，SQL执行成功返回新数据主键，执行不成功返回0<br/>
	 * 主键生成策略为非IDENTITY（数据库自增）时，SQL执行返回值无法确定<br/>
	 * @param sql
	 * @param values
	 * @return
	 */
	public Integer addReturnId(String sql, List<Object> values);
	/**
	 * 保存一个对象
	 * @param t
	 * @return
	 */
	public T save(T t);
	
	public ID save(Map<String, Object> map);

	/**
	 * sql查询通用接口
	 * @param t
	 * @return
	 */
	public List<T> search(T t);
	/**
	 * sql查询通用接口,忽略主键,仅返回查询结果集的第一个对象
	 * @param t
	 * @return
	 */
	public T searchOne(T t);
	/**
	 * 
	 * @param t
	 * @param pageBean
	 * @return
	 */
	public PageBean<T> search(T t, PageBean<T> pageBean);
	public PageBean<T> search(T t, PageBean<T> pageBean, boolean isCount);
	/**
	 * sql查询通用接口
	 * @param sql
	 * @param values
	 * @param e
	 * @return
	 */
	public List<Map<String,Object>> searchForMap(String sql, List<Object> values);
	/**
	 * map型分页查询接口
	 * @param sql
	 * @param values
	 * @param pageBean
	 * @return
	 */
	public PageBean<Map<String,Object>> searchForMap(String sql, List<Object> values, PageBean<Map<String, Object>> pageBean);
	public PageBean<Map<String,Object>> searchForMap(String sql, List<Object> values, PageBean<Map<String, Object>> pageBean, boolean isCoount);
}
