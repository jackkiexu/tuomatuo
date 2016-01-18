package com.lami.tuomatuo.mapper.write;

import com.lami.tuomatuo.model.mybatis.User;

/**
 * Created by xjk on 10/21/15.
 */
public interface UserMapper {
    User getUserById(Integer id);
}
