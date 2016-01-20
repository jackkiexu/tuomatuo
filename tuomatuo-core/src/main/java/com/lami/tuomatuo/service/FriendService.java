package com.lami.tuomatuo.service;

import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.cache.impl.UserCacheImpl;
import com.lami.tuomatuo.dao.FriendDaoInterface;
import com.lami.tuomatuo.dao.UserDaoInterface;
import com.lami.tuomatuo.model.*;
import com.lami.tuomatuo.model.co.UserCo;
import com.lami.tuomatuo.model.vo.FriendVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by xujiankang on 2016/1/20.
 */
@Service("friendService")
public class FriendService extends BaseService<Friend, Long> {

    @Autowired
    private FriendDaoInterface friendDaoInterface;
    @Autowired
    private UserService userService;
    @Autowired
    private WeiXinAccountService weiXinAccountService;
    @Autowired
    private QQAccountService qqAccountService;
    @Autowired
    private UserPropertyService userPropertyService;
    @Autowired
    private UserPositionService userPositionService;
    @Autowired
    private UserCacheImpl userCache;

    /**
     *  查询一个用户的朋友
     * @param userId
     * @return
     */
    public List<FriendVo> getFriendsByUserId(Long userId){
        List<FriendVo> friendVoList = new ArrayList<FriendVo>();
        Friend friend = new Friend();
        friend.setUserId(userId);
        List<Friend> friendList = friendDaoInterface.search(friend);
        for (Friend friend1 : friendList){
            User user = userService.get(friend1.getUserId());
            UserProperty userProperty = userPropertyService.getUserPropertyByUserId(friend1.getUserId());
            if(user.getAccountType() == 1){
                WeiXinAccount weiXinAccount = weiXinAccountService.get(user.getThirdAccountId());
            }
            if(user.getAccountType() == 1){
                QQAccount qqAccount = qqAccountService.get(user.getThirdAccountId());
            }
            friendVoList.add(new FriendVo(friend1, userProperty, user));
        }
        return friendVoList;
    }

    /**
     *  用户获取系统推荐信息
     * @param userId
     * @return
     */
    public List<FriendVo> getRecommendFriendsByUserId(Long userId){
        List<FriendVo> friendVoList = new ArrayList<FriendVo>();
        Friend friend = new Friend();
        friend.setUserId(userId);
        List<Friend> friendList = friendDaoInterface.search(friend);
       // TODO
        return friendVoList;
    }

    /**
     * 等到附近的朋友
     * @param userId
     * @return
     */
    public List<FriendVo> getNearbyFriends(Long userId){
        UserCo userCo =  userCache.getUserCo(userId);
        Set<String> userList = userCache.getGeoHashCollection(userCo.getGeoHash().substring(0,6));
        return null;
    }
}
