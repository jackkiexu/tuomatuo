package com.lami.tuomatuo.service;

import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.dao.MobileAccountDaoInterface;
import com.lami.tuomatuo.dao.SmsDaoInterface;
import com.lami.tuomatuo.model.MobileAccount;
import com.lami.tuomatuo.model.Sms;
import com.lami.tuomatuo.model.UserProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xujiankang on 2016/1/25.
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