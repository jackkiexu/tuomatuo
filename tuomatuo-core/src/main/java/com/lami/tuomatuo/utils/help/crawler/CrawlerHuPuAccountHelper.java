package com.lami.tuomatuo.utils.help.crawler;

import com.lami.tuomatuo.model.crawler.HuPuAccount;
import com.lami.tuomatuo.model.crawler.vo.HuPuAccountVO;
import com.lami.tuomatuo.model.crawler.vo.UIAccountVO;
import com.lami.tuomatuo.utils.CrawlerUtils;
import com.lami.tuomatuo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xjk on 2016/3/22.
 */
public class CrawlerHuPuAccountHelper {

    private static final Logger logger = Logger.getLogger(CrawlerUIAccountHelper.class);

    private CrawlerHuPuAccountHelper() {}
    static class  InnerClass{
        private static CrawlerHuPuAccountHelper instance = new CrawlerHuPuAccountHelper();
    }
    public static CrawlerHuPuAccountHelper getInstance(){
        return InnerClass.instance;
    }


    public static void main(String[] args) {
        CrawlerHuPuAccountHelper.getInstance().crawlerUICN(50l, 60l);
    }

    public List<HuPuAccountVO> crawlerUICN(Long minId, Long maxId){

        Long iInit = 1l;
        Long iMax = 10l;
        if(minId != null) iInit = minId;
        if(maxId != null) iMax = maxId;

        HuPuAccountVO huPuAccountVO = new HuPuAccountVO();
        List<HuPuAccountVO> huPuAccountVOList = new ArrayList<HuPuAccountVO>();

        Long nullCount = 0l;
        String URL = null;
        String referer = null;
        for(;nullCount <= 1000 && iInit <= iMax;iInit++){
            URL = "http://my.hupu.com/"+iInit;
            referer = "http://my.hupu.com/"+iInit;
            huPuAccountVO = crawlerUIAccount(URL, referer, iInit);
            if(huPuAccountVO == null){
                logger.info("account == null, and i = " + iInit);
                nullCount += 1l;
                continue;
            }else {
                nullCount = 0l;
            }

            huPuAccountVOList.add(huPuAccountVO);
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return huPuAccountVOList;
    }

    public HuPuAccountVO crawlerUIAccount(String URL, String referer, Long index) {

        HuPuAccountVO huPuAccountVO = new HuPuAccountVO(index);

        try {
            Document doc = CrawlerUtils.buildConnection(URL, CrawlerUtils.getCookies(URL), referer).get();
            if(doc == null) return null;

            String avatarURL = null;
            try {
                avatarURL = doc.getElementById("j_head").attr("src");
                huPuAccountVO.setAvatarURL(avatarURL);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            String name = null;
            try {
                name = doc.getElementsByAttributeValue("itemprop", "name").get(0).text();
                huPuAccountVO.setName(name);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            String sex = null;
            try {
                sex = doc.getElementsByAttributeValue("itemprop","gender").get(0).text();
                if(!StringUtils.isEmpty(sex)){
                    if("男".equalsIgnoreCase(sex.trim())){
                        huPuAccountVO.setSex(1);
                    } else if("女".equalsIgnoreCase(sex.trim())){
                        huPuAccountVO.setSex(2);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            huPuAccountVO = null;
        }
        return huPuAccountVO;
    }


}
