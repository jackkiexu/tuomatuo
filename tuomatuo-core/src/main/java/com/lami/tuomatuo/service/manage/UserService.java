package com.lami.tuomatuo.service.manage;

import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.dao.manage.UserDaoInterface;
import com.lami.tuomatuo.model.manage.User;
import com.lami.tuomatuo.utils.constant.Constant;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.*;

@Service("manageUserService")
public class UserService extends BaseService<User, Integer> {
	
	private UserDaoInterface userDaoInterface;

	@Qualifier("manageUserDaoInterface")
	@Resource(name = "manageUserDaoInterface")
	public void setUserDaoInterface(UserDaoInterface userDaoInterface) {
		this.userDaoInterface = userDaoInterface;
		this.baseDao = userDaoInterface;
	}

}