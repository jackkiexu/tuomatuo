package com.lami.tuomatuo.service;

import com.lami.tuomatuo.base.AbstractBaseTest;
import com.lami.tuomatuo.model.UserDynamic;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by xjk on 2016/1/21.
 */
public class UserDynamicTest extends AbstractBaseTest {

    @Autowired
    private UserDynamicService userDynamicService;

    @Test
    public void getUserDynamic(){
        List<UserDynamic> userDynamicList = userDynamicService.getUserDynamicByUserId(16l);
        System.out.println("userDynamicList:"+userDynamicList);
    }
}
