package com.lami.tuomatuo.service;

import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.dao.SmsDaoInterface;
import com.lami.tuomatuo.dao.WeiXinAccountDaoInterface;
import com.lami.tuomatuo.model.Sms;
import com.lami.tuomatuo.model.WeiXinAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by xujiankang on 2016/1/25.
 */
@Service("smsService")
public class SmsService extends BaseService<Sms, Long> {

    @Autowired
    private SmsDaoInterface smsDaoInterface;

    public void saveSms(String mobile, String content, Integer type, Long userId){
        Sms sms = new Sms();
        sms.setContent(content);
        sms.setCreateTime(new Date());
        sms.setMobile(mobile);
        sms.setType(type);
        sms.setUpdateTime(new Date());
        sms.setUserId(userId);
        smsDaoInterface.save(sms);
    }

}