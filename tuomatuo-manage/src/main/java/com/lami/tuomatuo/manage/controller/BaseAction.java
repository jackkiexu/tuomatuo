package com.lami.tuomatuo.manage.controller;

import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.base.PageBean;
import com.lami.tuomatuo.manage.bean.Comment;
import com.lami.tuomatuo.manage.bean.ConstantMessage;
import com.lami.tuomatuo.manage.bean.ConstantModel;
import com.lami.tuomatuo.model.base.Result;
import com.lami.tuomatuo.utils.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;

public abstract class BaseAction<T, ID extends Serializable> {
	protected final Log logger = LogFactory.getLog(this.getClass());

	protected ConstantModel cmodel;
	protected Comment comment;
	protected ConstantMessage cmessage;
	
	
	public Comment getComment() {
		return comment;
	}
	@Resource(name = "comment")
	public void setComment(Comment comment) {
		this.comment = comment;
	}

	@Resource(name = "cmodel")
	protected void setCmodel(ConstantModel cmodel) {
		this.cmodel = cmodel;
	}

	@Resource(name = "cmessage")
	protected void setCmessage(ConstantMessage cmessage) {
		this.cmessage = cmessage;
	}
	
	private BaseService<T, ID> baseService;
	protected String viewName;

	protected void setBaseService(BaseService<T, ID> baseService, String viewName) {
		this.baseService = baseService;
		this.viewName = viewName;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/view.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> view(HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		String id = request.getParameter("id");
		logger.info("view : " + id);
		T obj = (T) baseService.get((ID) id);

		if (obj != null) {
			request.setAttribute("obj", obj);
		}
		result.put("result", true);
		result.put("row", obj);
		logger.info(result.toString());
		return result;
	}

	/**
	 * 处理 /blog/1 HTTP DELETE
	 */
	@RequestMapping(value = "/delete.do",method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> delete(
			@RequestParam(value = "id") List<Integer> id,
			HttpServletRequest request) {
		logger.info(request.getParameterMap().toString());
		Map<String, Object> result = new HashMap<String, Object>();
		logger.info("deleteList : " + id);
		baseService.delByIds((List<ID>) id);
		result.put("result", true);
		return result;
	}

	@RequestMapping(value = "/preSearch.do")
	public String preSearch(HttpServletRequest request) {
		return (viewName + "/" + viewName + "Search");
	}

	@SuppressWarnings({"rawtypes" })
	@RequestMapping(value = "/search.do")
	@ResponseBody
	public Map<String, Object> search(HttpServletRequest request,Integer totalCount) {
		Map map = request.getParameterMap();
		logger.info(map.toString());
		Map<String, Object> result = new HashMap<String, Object>();
		String pageNo = request.getParameter("page");
		String pageSize = request.getParameter("rows");
		PageBean<T> pageBean = new PageBean<T>();
		if (pageNo == null || !StringUtil.isNumeric(pageNo)
				|| Integer.parseInt(pageNo) <= 0) {
			pageBean.setPageNo(1);
		} else {
			pageBean.setPageNo(Integer.parseInt(pageNo));
		}
		if (pageSize == null || !StringUtil.isNumeric(pageSize)
				|| Integer.parseInt(pageSize) <= 0) {
			pageBean.setPageSize(10);
		} else {
			pageBean.setPageSize(Integer.parseInt(pageSize));
		}

		String orderBy = request.getParameter("sort");
		String orderType = request.getParameter("order");
		if (orderBy != null) {
			pageBean.setOrderBy(orderBy);
			if (orderType != null) {
				pageBean.setOrderType(orderType);
			}
		}
		logger.info(pageBean.toString());
		// pageBean = commonSearch(getNewObject(), pageBean);
		StringBuilder sb = new StringBuilder();
		List<Object> param = new ArrayList<Object>();
		Map.Entry entry;
		String key = "";
		String value = "";
		if (map != null && map.size() > 0) {
			Iterator entries = map.entrySet().iterator();
			while (entries.hasNext()) {
				entry = (Map.Entry) entries.next();
				key = (String) entry.getKey();
				Object valueObj = entry.getValue();
				if (null == valueObj) {
					value = "";
				} else if (valueObj instanceof String[]) {
					String[] values = (String[]) valueObj;
					for (int i = 0; i < values.length; i++) {
						value = values[i] + ",";
					}
					value = value.substring(0, value.length() - 1);
				} else {
					value = valueObj.toString();
				}

				if (key != null && key.startsWith("search.")) {
					String ktp = key.replaceFirst("search.", "");
					int iSize = ktp.indexOf("_");
					String con = ktp.substring(0, iSize - 1);
					String contype = ktp.substring(iSize - 1, iSize);
					String sqlValue = ktp.substring(iSize + 1, ktp.length());

					getSearchSql(con, contype, sqlValue,value,sb,param);
				}
			}
		}
		boolean isCount = true;
		if(totalCount==null||totalCount.intValue()<=0) 
			isCount = true;
		else{
			isCount = false;
			pageBean.setRowCount(totalCount);
		}
		
		pageBean = baseService.search(sb.toString(), param, pageBean,isCount);
		

		if (pageBean != null) {
			request.setAttribute("pb", pageBean);
		}
		result.put("total", pageBean.getRowCount());
		result.put("rows", pageBean.getList());
		logger.info("search result size = " + pageBean.getRowCount());
		return result;
	}

	/**
	 * 处理 /blog/1 HTTP POST
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/update.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> update(HttpServletRequest request) {
		Map<String, Object> param = getUpdateParam(request);
		logger.info(param.toString());
		Map<String, Object> result = new HashMap<String, Object>();
		int ri = baseService.updateById(param);
		if(ri==0){
			result.put("result", false);
			result.put("msg", "failure");
		}else{
			result.put("result", true);
			result.put("msg", "success");
		}
		logger.info(ri);		
		return result;
	}

	/**
	 * 处理 /blog HTTP POST
	 */
	@RequestMapping(value = "/save.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> save(HttpServletRequest request) {
		
		Map<String, Object> param = getSaveParam(request);
		logger.info(param.toString());
		Map<String, Object> result = new HashMap<String, Object>();
		ID ri = baseService.save(param);
		if(ri==null){
			result.put("result", false);
			result.put("msg", "failure");
		}else{
			result.put("result", true);
			result.put("msg", "success");
		}
		logger.info(ri);
		result.put("id", ri);
		return result;
	}
	protected Map<String, Object> getSaveParam(HttpServletRequest request){
		return getParam(request);
	}
	protected Map<String, Object> getUpdateParam(HttpServletRequest request){
		return getParam(request);
	}
	protected Map<String, Object> getParam(HttpServletRequest request){
		Map<String, Object> map = request.getParameterMap();
		
		Map<String, Object> param = new HashMap<String,Object>();
		Map.Entry entry;
		String key = "";
		String value = "";
		if (map != null && map.size() > 0) {
			Iterator entries = map.entrySet().iterator();
			while (entries.hasNext()) {
				entry = (Map.Entry) entries.next();
				key = (String) entry.getKey();
				Object valueObj = entry.getValue();
				if (null == valueObj) {
					value = "";
				} else if (valueObj instanceof String[]) {
					String[] values = (String[]) valueObj;
					for (int i = 0; i < values.length; i++) {
						value = values[i] + ",";
					}
					value = value.substring(0, value.length() - 1);
				} else {
					value = valueObj.toString();
				}
				param.put(key, value);
			}
		}
		return param;
	}

	protected PageBean<T> commonSearch(T form, PageBean<T> pageBean) {
		T obj = getObject(form, getNewObject());
		return baseService.search(obj, pageBean);
	}

	protected T getObject(T form, T obj) {
		BeanUtils.copyProperties(form, obj);
		return obj;
	}


	protected abstract Result validate(T form, HttpServletRequest request);

	protected abstract T getNewObject();

	protected void getSearchSql(String con, String contype, String name,String value,StringBuilder sb,List<Object> param) {
		String vt = getSearchSqlByType(contype);
		if (vt == null||"".equals(value.trim()))
			return;
		switch (MatchCon.valueOf(con)) {
		case QE:
		case EQ:
			if(sb.length()>0 ) sb.append(" and ");
			sb.append(name).append(" = ").append(vt);
			param.add(value);
			break;
		case EN:
		case NE:
			if(sb.length()>0 ) sb.append(" and ");
			sb.append(name).append(" <> ").append(vt);
			param.add(value);
			break;
		case LIKE:
			if(sb.length()>0 ) sb.append(" and ");
			sb.append(name).append(" like ").append("?");
			param.add("%"+value+"%");
			break;
		case LT:
			if(sb.length()>0 ) sb.append(" and ");
			sb.append(name).append(" < ").append(vt);
			param.add(value);
			break;
		case GT:
			if(sb.length()>0 ) sb.append(" and ");
			sb.append(name).append(" > ").append(vt);
			param.add(value);
			break;
		case LE:
			if(sb.length()>0 ) sb.append(" and ");
			sb.append(name).append(" <= ").append(vt);
			param.add(value);
			break;
		case GE:
			if(sb.length()>0 ) sb.append(" and ");
			sb.append(name).append(" >= ").append(vt);
			param.add(value);
			break;
		// case
		// IN:sb.append(" and ").append(name).append(" in ( ").append(vt).append(")");break;
		default:
			return;
		}
		return;
	}

	protected String getSearchSqlByType(String contype) {
		if (contype == null || "".equals(contype))
			return null;
		StringBuffer sb = new StringBuffer();
		switch (MatchTaye.valueOf(contype)) {
		case I:
			sb.append("?");
			break;
		case D:
			sb.append("?");
			break;
		case S:
			sb.append("?");
			break;// sb.append(" '").append(value).append("'");break;
		default:
			return null;
		}
		return sb.toString();
	}

	public enum MatchCon {
		QE,EQ, EN,NE, LIKE, LT, GT, LE, GE;// , IN

	}

	public enum MatchTaye {
		I, D, S;
	}

	public static final String OR_SEPARATOR = "_OR_";
}
