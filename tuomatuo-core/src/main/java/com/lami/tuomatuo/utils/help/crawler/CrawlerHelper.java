package com.lami.tuomatuo.utils.help.crawler;

import com.lami.tuomatuo.model.crawler.vo.UIAccountVO;
import com.lami.tuomatuo.utils.CrawlerUtils;
import com.lami.tuomatuo.utils.StringUtil;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xujiankang on 2016/3/18.
 */
public class CrawlerHelper {

    private static final Logger logger = Logger.getLogger(CrawlerHelper.class);

    private  CrawlerHelper() {}
    static class  InnerClass{
        private static CrawlerHelper instance = new CrawlerHelper();
    }
    public static CrawlerHelper getInstance(){
        return InnerClass.instance;
    }

    public static void main(String[] args) {
        List<UIAccountVO> accountList = CrawlerHelper.getInstance().crawlerUICN(1l, 100l);
        logger.info("accountList:"+accountList);
    }

    public List<UIAccountVO> crawlerUICN(Long minId, Long maxId){

        Long iInit = 1l;
        Long iMax = 10l;
        if(minId != null) iInit = minId;
        if(maxId != null) iMax = maxId;

        UIAccountVO account = new UIAccountVO();
        List<UIAccountVO> accountList = new ArrayList<UIAccountVO>();

        Long nullCount = 0l;
        String URL = null;
        String referer = null;
        for(;nullCount <= 100 && iInit <= iMax;iInit++){
            URL = "http://i.ui.cn/ucenter/"+iInit+".html";
            referer = "http://i.ui.cn/ucenter/"+iInit+".html";
            account = crawlerUIAccount(URL, referer, iInit);
            if(account == null){
                logger.info("account == null, and i = " + iInit);
                nullCount += 1l;
                continue;
            }else {
                nullCount = 0l;
            }

            accountList.add(account);
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return accountList;
    }

    public UIAccountVO crawlerUIAccount(String URL, String referer, Long index) {

        UIAccountVO account = new UIAccountVO(index);

        try {
            Document doc = CrawlerUtils.buildConnection(URL, CrawlerUtils.getCookies(URL), referer).get();
            if(doc == null) return null;
            Element user_avatar_hideE = null; //avatar URL
            Elements us_nameE = null; // user Name and sign
            Elements us_infoE = null; // user property
            try {
                user_avatar_hideE = doc.getElementById("user-avatar-hide");
                us_nameE = doc.getElementsByClass("us-name");
                us_infoE = doc.getElementsByClass("us-info");
                if(user_avatar_hideE == null || us_nameE == null || us_infoE == null) return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            account.setAvatarURL(user_avatar_hideE.attr("src"));

            for(Element element : us_nameE){
                Elements elementNames = element.getElementsByClass("n1");
                for(Element element2 : elementNames){
                    try {
                        String name = element2.text();
                        if(!StringUtil.isEmpty(name)) account.setName(name);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Elements elementSignature = element.getElementsByClass("n2");
                for(Element element2 : elementSignature){
                    try {
                        String signature = element2.text();
                        if(!StringUtil.isEmpty(signature)) account.setSignature(signature);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            int i = 0;
            for(Element elementSubs: us_infoE){

                Elements elements2 = elementSubs.getElementsByTag("li");
                for(Element elementSub2 : elements2){
                    String elementsStr = elementSub2.toString();

                    if(elementsStr.contains("icon-iconfans-round")){
                        Elements element2s = elementSub2.getElementsByTag("span");
                        try {
                            account.setAge(element2s.get(0).text());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if(elementsStr.contains("icon-qq-round")){
                        Elements element2s = elementSub2.getElementsByTag("span");
                        try {
                            account.setQq(element2s.get(0).text());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if(elementsStr.contains("icon-envelope-round")){
                        Elements element2s = elementSub2.getElementsByTag("span");
                        try {
                            account.setEmail(element2s.get(0).text());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if(elementsStr.contains("icon-ball")){
                        Elements element2s = elementSub2.getElementsByTag("span");
                        try {
                            account.setNet(element2s.get(0).text());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if(elementsStr.contains("icon-sina1")){
                        Elements element2s = elementSub2.getElementsByTag("span");
                        try {
                            account.setSina(element2s.get(0).text());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if(elementsStr.contains("icon-weixin")){
                        Elements element2s = elementSub2.getElementsByTag("span");
                        try {
                            account.setWeiChat(element2s.get(0).text());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            account = null;
        }
        return account;
    }

}