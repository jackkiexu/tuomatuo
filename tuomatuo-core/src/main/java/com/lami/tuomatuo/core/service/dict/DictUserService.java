package com.lami.tuomatuo.core.service.dict;

import com.lami.tuomatuo.core.base.BaseService;
import com.lami.tuomatuo.core.dao.dict.DictUserDaoInterface;
import com.lami.tuomatuo.core.model.dict.DictUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by xjk on 2016/8/23.
 */
@Service("dictUserService")
public class DictUserService extends BaseService<DictUser, Long> {

    @Autowired
    private DictUserDaoInterface dictUserDaoInterface;

    public DictUser getDictUserByMobile(String mobile){
        DictUser dictUser = new DictUser();
        dictUser.setMobile(mobile);
        List<DictUser> dictUserList = dictUserDaoInterface.search(dictUser);
        if(dictUserList != null && dictUserList.size() != 0)
            return dictUserList.get(0);
        return null;
    }

}

