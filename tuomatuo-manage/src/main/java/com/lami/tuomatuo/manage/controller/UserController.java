package com.lami.tuomatuo.manage.controller;

import com.lami.tuomatuo.core.model.base.Result;
import com.lami.tuomatuo.core.model.manage.User;
import com.lami.tuomatuo.core.service.manage.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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