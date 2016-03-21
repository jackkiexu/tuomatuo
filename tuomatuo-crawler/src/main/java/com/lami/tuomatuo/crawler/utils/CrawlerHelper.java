package com.lami.tuomatuo.crawler.utils;

import com.lami.tuomatuo.crawler.model.po.uicn.Account;
import com.lami.tuomatuo.utils.CrawlerUtils;
import com.lami.tuomatuo.utils.StringUtil;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

/**
 * Created by xujiankang on 2016/3/18.
 */
public class CrawlerHelper {

    private static final Logger logger = Logger.getLogger(CrawlerHelper.class);


    public static List<Account> crawlerUICN(){
        return null;
    }



    public static void main(String[] args) {

        Account account = new Account();

        try {
            Document doc = CrawlerUtils.buildConnection("http://i.ui.cn/ucenter/84039.html", CrawlerUtils.getCookies("http://i.ui.cn/ucenter/143208.html"), "http://www.baidu.com").get();
            Element user_avatar_hideE = doc.getElementById("user-avatar-hide"); //avatar URL
            Elements us_nameE = doc.getElementsByClass("us-name"); // user Name and sign
            Elements us_infoE = doc.getElementsByClass("us-info"); // user property

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

            logger.info("account:"+account);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
