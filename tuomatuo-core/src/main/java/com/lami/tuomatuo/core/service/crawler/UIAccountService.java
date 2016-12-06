package com.lami.tuomatuo.core.service.crawler;

import com.lami.tuomatuo.core.base.BaseService;
import com.lami.tuomatuo.core.utils.help.crawler.CrawlerUIAccountHelper;
import com.lami.tuomatuo.core.dao.crawler.UIAccountDaoInterface;
import com.lami.tuomatuo.core.model.crawler.UIAccount;
import com.lami.tuomatuo.core.model.crawler.vo.UIAccountVO;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xjk on 2016/3/21.
 */
@Service("uiAccountService")
public class UIAccountService extends BaseService<UIAccount, Long> {

    @Autowired
    private UIAccountDaoInterface uiAccountDaoInterface;

    /**
     *  crawler ui account and storage them
     * @param minId
     * @param maxId
     */
    public void crawlerUIAccount(Long minId, Long maxId){

        Long iInit = 1l;
        Long iMax = 10l;
        if(minId != null) iInit = minId;
        if(maxId != null) iMax = maxId;

        UIAccountVO account = new UIAccountVO();

        Long nullCount = 0l;
        String URL = null;
        String referer = null;
        for(;nullCount <= 1000 && iInit <= iMax;iInit++){
            try {
                URL = "http://i.ui.cn/ucenter/"+iInit+".html";
                referer = "http://i.ui.cn/ucenter/"+iInit+".html";
                account =  CrawlerUIAccountHelper.getInstance().crawlerUIAccount(URL, referer, iInit);
                logger.info("index = " + iInit +", account:"+account);
                if(account == null){
                    nullCount += 1l;
                    continue;
                }else {
                    nullCount = 0l;
                }

                UIAccount uiAccount = new UIAccount();
                try {
                    BeanUtils.copyProperties(uiAccount, account);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                List<UIAccount> accountList = new ArrayList<UIAccount>();
                accountList.add(uiAccount);
                uiAccountDaoInterface.batchSave(accountList);

                try {
                    Thread.sleep(1300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
