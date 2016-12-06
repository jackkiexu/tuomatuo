package com.lami.tuomatuo.core.service;

import com.lami.tuomatuo.core.base.BaseService;
import com.lami.tuomatuo.core.dao.MobileAccountDaoInterface;
import com.lami.tuomatuo.core.model.UserProperty;
import com.lami.tuomatuo.core.model.MobileAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xjk on 2016/1/25.
 */
@Service("mobileAccountService")
public class MobileAccountService extends BaseService<MobileAccount, Long> {

    @Autowired
    private MobileAccountDaoInterface mobileAccountDaoInterface;
    @Autowired
    private UserPropertyService userPropertyService;

    public void updateMobileAccount(Long thirdAccountId, String nick,String imgUrl, Integer age, Integer sex, Long userId){
        MobileAccount mobileAccount = get(thirdAccountId);
        mobileAccount.setNick(nick);
        mobileAccount.setImgUrl(imgUrl);
        update(mobileAccount);

        UserProperty userProperty = userPropertyService.getUserPropertyByUserId(userId);
        userProperty.setAge(age);
        userProperty.setSex(sex);
        userPropertyService.update(userProperty);
    }
}