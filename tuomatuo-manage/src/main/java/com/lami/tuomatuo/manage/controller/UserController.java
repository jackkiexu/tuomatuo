package com.lami.tuomatuo.manage.controller;

import com.lami.tuomatuo.model.User;
import com.lami.tuomatuo.model.base.Result;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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


	@Override
	protected Result validate(User form, HttpServletRequest request) {
		return null;
	}

	@Override
	protected User getNewObject() {
		return null;
	}
}