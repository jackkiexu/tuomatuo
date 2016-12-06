package com.lami.tuomatuo.core.service;

import com.lami.tuomatuo.core.base.BaseService;
import com.lami.tuomatuo.core.dao.FriendDaoInterface;
import com.lami.tuomatuo.core.model.Friend;
import com.lami.tuomatuo.core.model.MobileAccount;
import com.lami.tuomatuo.core.model.User;
import com.lami.tuomatuo.core.model.UserProperty;
import com.lami.tuomatuo.core.model.co.UserCo;
import com.lami.tuomatuo.core.model.vo.FriendVo;
import com.lami.tuomatuo.core.cache.impl.UserCacheImpl;
import com.lami.tuomatuo.core.utils.constant.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by xjk on 2016/1/20.
 */
@Service("friendService")
public class FriendService extends BaseService<Friend, Long> {

    @Autowired
    private FriendDaoInterface friendDaoInterface;
    @Autowired
    private UserService userService;
    @Autowired
    private UserPropertyService userPropertyService;
    @Autowired
    private UserCacheImpl userCache;
    @Autowired
    private MobileAccountService mobileAccountService;


    private List<FriendVo>  getFriendVoByFriend(List<Friend> friendList){
        List<FriendVo> friendVoList = new ArrayList<FriendVo>();
        for (Friend friend1 : friendList){
            User user = userService.get(friend1.getUserId());
            UserProperty userProperty = userPropertyService.getUserPropertyByUserId(friend1.getUserId());
            MobileAccount mobileAccount = mobileAccountService.get(user.getThirdAccountId());
            friendVoList.add(new FriendVo(friend1, userProperty, user, mobileAccount));
        }
        return friendVoList;
    }

    public List<FriendVo> followFriend(Long userId, List<Long> friendList){
        List<FriendVo> friendVoList = new ArrayList<FriendVo>();
        for(Long friendId : friendList){
            friendVoList.add(followFriend(userId, friendId));
        }
        return friendVoList;
    }

    /**
     * 添加用户
     * @param userId
     * @param friendId
     * @return
     */
    public FriendVo followFriend(Long userId, Long friendId){
        Friend friend = new Friend();
        friend.setFriendId(friendId);
        friend.setUserId(userId);
        friend.setCreateTime(new Date());
        friend.setStatus(Constant.TRUE);
        friend.setUpdateTime(new Date());
        friend = friendDaoInterface.save(friend);
        List<Friend> friendList = new ArrayList<Friend>();
        friendList.add(friend);
        List<FriendVo> friendVoList = getFriendVoByFriend(friendList);
        return friendVoList.get(0);
    }

    /**
     * 添加用户
     * @param userId
     * @param friendId
     * @return
     */
    public void unFollowFriend(Long userId, Long friendId){
        List<Object> parameters = new ArrayList<Object>();
        parameters.add(userId);
        parameters.add(friendId);
        String sql = "update friend set status = 0 where userId = ? and friendId = ?";
        friendDaoInterface.execute(sql, parameters);
    }

    /**
     *  查询一个用户的朋友
     * @param userId
     * @return
     */
    public List<FriendVo> getFriendsByUserId(Long userId){
        Friend friend = new Friend();
        friend.setUserId(userId);
        friend.setStatus(Constant.TRUE);
        List<Friend> friendList = friendDaoInterface.search(friend);
        return getFriendVoByFriend(friendList);
    }

    /**
     * 根据关键字进行查询朋友
     * @param content
     * @param offset
     * @param pageSize
     * @return
     */
    public List<FriendVo> searchFriend(String content, Integer offset, Integer pageSize){
        String[] keys = content.split(" ");
        List<Friend> friendList = new ArrayList<Friend>();
        for(String key : keys){
            String sql = "select friend.* from friend left join user on user.id=friend.userId left join mobile_account on mobile_account.id=user.thirdAccountId where mobile_account.nick like \'%"+key+"\'% order by user.id desc limit ?,?";
            List<Object> parameters = new ArrayList<Object>();
            parameters.add(offset);
            parameters.add(pageSize);
            List<Friend> friendListTemp = friendDaoInterface.search(sql, parameters);
            friendList.addAll(friendListTemp);
        }
        return getFriendVoByFriend(friendList);
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
