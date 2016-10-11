package com.lami.tuomatuo.service;

import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.dao.QQAccountDaoInterface;
import com.lami.tuomatuo.dao.UserDynamicDaoInterface;
import com.lami.tuomatuo.model.*;
import com.lami.tuomatuo.model.vo.UserDynamicVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xjk on 2016/1/18.
 */
@Service("userDynamicService")
public class UserDynamicService extends BaseService<UserDynamic, Long> {

    @Autowired
    private UserDynamicDaoInterface userDynamicDaoInterface;
    @Autowired
    private MobileAccountService mobileAccountService;
    @Autowired
    private UserPropertyService userPropertyService;
    @Autowired
    private UserService userService;


    public List<UserDynamicVo> getUserDynamicVoByUserDynamic(List<UserDynamic> userDynamicList){
        List<UserDynamicVo> userDynamicVoList = new ArrayList<UserDynamicVo>();
        for(UserDynamic userDynamic : userDynamicList){
            User user = userService.get(userDynamic.getUserId());
            UserProperty userProperty = userPropertyService.getUserPropertyByUserId(userDynamic.getUserId());
            MobileAccount mobileAccount = mobileAccountService.get(user.getThirdAccountId());
            userDynamicVoList.add(new UserDynamicVo(userDynamic, userProperty, mobileAccount));
        }
        return userDynamicVoList;
    }

    public List<UserDynamic> getUserDynamicByUserId(Long userId){
        UserDynamic userDynamic = new UserDynamic();
        userDynamic.setUserId(userId);
        return userDynamicDaoInterface.search(userDynamic);
    }

    /**
     * 得到当前所有动态的总数
     * @return
     */
    public Long getCount(){
        String sql = "select count(*) from user_dynamic";
        return userDynamicDaoInterface.getLong(sql, new ArrayList<Object>());
    }

    public List<UserDynamic> getUserDynamic(Integer offset, Integer row){
        List<Object> parameters = new ArrayList<Object>();
        parameters.add(offset);
        parameters.add(row);
        String sql = "select * from user_dynamic order by id desc limit ?, ?";
        return userDynamicDaoInterface.search(sql, parameters);
    }

    public List<UserDynamic> getHotDynamic(Integer offset, Integer row){
        List<Object> parameters = new ArrayList<Object>();
        parameters.add(offset);
        parameters.add(row);
        String sql = "select * from user_dynamic order by hotValue desc limit ?, ?";
        return userDynamicDaoInterface.search(sql, parameters);
    }

    public List<UserDynamic> getUserNearbyDynamic(String geoHash, Integer offset, Integer row ){
        List<Object> parameters = new ArrayList<Object>();
        parameters.add(geoHash.substring(0, 6));
        parameters.add(offset);
        parameters.add(row);
        String sql = "select * from user_dynamic where geoHash like \'%?\' order by id desc limit ?, ?";
        return userDynamicDaoInterface.search(sql, parameters);
    }


    public List<UserDynamic> searchDynamic(String keyWord, Integer offset, Integer row){
        List<Object> parameters = new ArrayList<Object>();
        parameters.add(keyWord);
        parameters.add(offset);
        parameters.add(row);
        String sql = "select * from user_dynamic where title like \'%"+keyWord+"%\' order by id desc limit ?, ?";
        return userDynamicDaoInterface.search(sql, parameters);
    }
}