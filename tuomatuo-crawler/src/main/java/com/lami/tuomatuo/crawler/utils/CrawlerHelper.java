package com.lami.tuomatuo.crawler.utils;

import com.lami.tuomatuo.crawler.model.po.uicn.Account;
import com.lami.tuomatuo.utils.CrawlerUtils;
import com.lami.tuomatuo.utils.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

/**
 * Created by xujiankang on 2016/3/18.
 */
public class CrawlerHelper {



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
                    String name = element2.text();
                    if(!StringUtil.isEmpty(name)) account.setName(name);
                }

                Elements elementSignature = element.getElementsByClass("n2");
                for(Element element2 : elementSignature){
                    String signature = element2.text();
                    if(!StringUtil.isEmpty(signature)) account.setSignature(signature);
                }
            }

            int i = 0;
            for(Element elementSubs: us_infoE){

                Elements elements2 = elementSubs.getElementsByTag("li");
                for(Element elementSub2 : elements2){
                    Elements elements = elementSub2.getElementsByClass("icon-iconfans-round");
                    System.out.println("elements:"+elements);
                }



               /* Elements elements1 = elementSubs.getElementsByTag("span");
                for(Element elementSubss: elements1){
                    String value = elementSubss.text();
                    if(StringUtil.isEmpty(value)) continue;
                    value = value.trim();
                    switch (i){
                        case 0: account.setAge(value);
                            break;
                        case 2: account.setQq(value);
                            break;
                        case 1: account.setEmail(value);
                            break;
                        case 3: account.setNet(value);
                            break;
                        case 4: account.setSina(value);
                            break;
                        case 6: account.setWeiChat(value);
                            break;
                        default :
                            System.out.println("value:"+value);
                            break;
                    }
                    i++;
                }*/
            }

            System.out.println("account:"+account);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
