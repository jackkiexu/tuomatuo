package com.lami.tuomatuo.service;

import com.lami.tuomatuo.mapper.write.UserMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xjk on 10/21/15.
 */
@Service
public class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class);

    @Autowired
    private UserMapper userMapper;


}
