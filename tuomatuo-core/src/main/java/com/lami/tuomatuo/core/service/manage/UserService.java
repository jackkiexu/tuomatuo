package com.lami.tuomatuo.core.service.manage;

import com.lami.tuomatuo.core.base.BaseService;
import com.lami.tuomatuo.core.dao.manage.UserDaoInterface;
import com.lami.tuomatuo.core.model.manage.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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