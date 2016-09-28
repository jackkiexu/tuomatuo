package com.lami.tuomatuo.manage.controller;

import com.lami.tuomatuo.base.PageBean;
import com.lami.tuomatuo.model.base.Result;
import com.lami.tuomatuo.model.manage.User;
import com.lami.tuomatuo.service.manage.UserService;
import com.lami.tuomatuo.utils.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("user")
public class UserController extends BaseAction<User,Integer>{
	private static final Logger logger = Logger.getLogger(UserController.class);

	private UserService userService;

	@Override
	protected User getNewObject() {
		return null;
	}

	@Qualifier("manageUserService")
	@Resource(name="manageUserService")
	public void setUserService(UserService userService) {
		this.userService = userService;
		super.setBaseService(userService, "user");
	}

	@Override
	public Result validate(User form, HttpServletRequest request) {
		return new Result(Result.SUCCESS);
	}
}