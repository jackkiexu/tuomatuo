package com.lami.tuomatuo.service.crawler;

import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.dao.crawler.UIAccountDaoInterface;
import com.lami.tuomatuo.model.WeiXinAccount;
import com.lami.tuomatuo.model.crawler.UIAccount;
import com.lami.tuomatuo.model.crawler.vo.UIAccountVO;
import com.lami.tuomatuo.utils.help.crawler.CrawlerHelper;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xujiankang on 2016/3/21.
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
                account =  CrawlerHelper.getInstance().crawlerUIAccount(URL, referer, iInit);
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
                    Thread.sleep(5100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
