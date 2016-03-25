package com.lami.tuomatuo.utils;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by xujiankang on 2016/3/18.
 */
public class CrawlerUtils {

    public static Logger logger = LoggerFactory.getLogger(CrawlerUtils.class);

    public static final String LOGIN_URL_PATH = "https://passport.lianjia.com/cas/login?service=http%3A%2F%2Ftj.lianjia.com%2F";
    public static final String LOGIN_HOST = "password.lianjia.com";


    public static String userName = "15900849265";
    public static String password = "68825528";

    public static final Integer WARN_LIMIT_NUM = 5;//预警条数

    private static final int LIMIT = 5;

    private static final BlockingQueue<Runnable> DEAL_TASK = new LinkedBlockingQueue<Runnable>(2 * LIMIT);

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 5, 150000, TimeUnit.SECONDS, DEAL_TASK);

    private static String[] cityAs = null; //链家城市简称
    private static String[] cityIds = null; //爱屋城市id




    /**
     *
     * @author: asenpan - panyahui1.sh@superjia.com
     * @Description: 开始爬取前，获取cookie,如果链家异常，则此次爬取失败，直接抛出异常，通知相关人员
     * @param url
     * @return
     * @throws Exception
     */
    public static Map<String, String> getCookies(String url) throws Exception{
        Connection.Response res;
        Map<String, String> cookies = null;
        try {
            res = Jsoup.connect(url).timeout(30000).execute();
            cookies = res.cookies();
        } catch (IOException e) {
            logger.error("crawel cookie faied",e);
        }
        if(cookies == null || cookies.isEmpty()){
            if(cookies == null)
                cookies = new HashMap<String, String>();
            cookies.put("select_city", "310000");
            cookies.put("logger_session", "dc826277efaa7b3c8d247682ce39968d");
            cookies.put("lianjia_uuid", "334f5e27-ebc5-4ed4-94c1-7eee36b2e591");
        }

        cookies.put("_jzqy", "1.1453086171.1453086171.1.jzqsr=baidu|jzqct=%E9%93%BE%E5%AE%B6%E7%BD%91.-");
        cookies.put("_jzqckmp", "1");
        cookies.put("_gat", "1");
        cookies.put("_gat_past", "1");
        cookies.put("_gat_global", "1");
        cookies.put("_gat_new_global", "1");
        cookies.put("_gat_dianpu_agent", "1");
        cookies.put("CNZZDATA1253492439", "1237967892-1453084058-null%7C1453084058");
        cookies.put("_ga", "GA1.2.1350580089.1453086171");
        cookies.put("CNZZDATA1254525948", "1161180129-1453082684-null%7C1453082684");
        cookies.put("CNZZDATA1255633284", "920437903-1453082479-null%7C1453082479");
        cookies.put("CNZZDATA1255604082", "490854014-1453084871-null%7C1453084871");
        cookies.put("_smt_uid", "569c55da.12aeb50c");
        cookies.put("_qzja", "1.1067545470.1453086170536.1453086170536.1453086170537.1453086170537.1453086172892.0.0.0.2.1");
        cookies.put("_qzjb", "1.1453086170537.2.0.0.0");
        cookies.put("_qzjc", "1");
        cookies.put("_qzjto", "2.1.0");
        cookies.put("_jzqa", "1.4214208852172226000.1453086171.1453086171.1453086171.1");
        cookies.put("_jzqc", "1");
        cookies.put("_jzqb", "1.2.10.1453086171.1");
        return cookies;
    }

    public static String url = "https://passport.lianjia.com/cas/login?service=http%3A%2F%2Ftj.lianjia.com%2F";

    /**
     * 獲取登錄cookie
     * @param userName 用户名
     * @param password 密码
     * @return
     */
    private Map<String, String> getLoginCookies(String userName,String password){
        Map<String,String> defaultCookie = new HashMap<String ,String >();
        defaultCookie.put("JSESSIONID","7AF07733DA447C27CA26EE7BFC96A84B-n2");
        defaultCookie.put("lianjia_uuid","524fec0c-5b46-4164-a9de-27d7e78de9ab");
        defaultCookie.put("_jzqckmp","1");
        defaultCookie.put("select_city","120000");
        defaultCookie.put("_smt_uid","56c6e5f6.62e202e");
        defaultCookie.put("_ga","GA1.2.1215414366.1455875575");
        defaultCookie.put("_jzqa","1.4028828644899910700.1455875575.1455875575.1455875575.1");
        defaultCookie.put("_jzqc","1");

        Connection.Response res;
        Document document;
        Map<String, String> cookies = null;
        Map<String,String> map = null;
        try {
            //首次獲取input信息
            org.jsoup.Connection connection = buildLoginConnection(LOGIN_URL_PATH, defaultCookie, " ",LOGIN_HOST);//登录连接
            document = connection.timeout(30000).get();
            map = new HashMap<String,String>();
            Elements elements = document.select("input");//獲取所有的input
            for (Element element : elements){
                String attribute = element.attr("name");
                if(attribute.equalsIgnoreCase("username")){
                    map.put("username",userName);break;
                }else if(attribute.equalsIgnoreCase("password")){
                    map.put("password",password);break;
                }else if(attribute.equalsIgnoreCase("verifyCode")){
                    map.put("verifyCode",element.attr("val"));break;
                }else{
                    map.put(element.attr("name"),element.attr("value"));
                }
            }
            //獲取實際登錄cookie
            res = connection.timeout(30000).execute();
            cookies = res.cookies();
            defaultCookie.put("JSESSIONID",cookies.get("JSESSIONID"));
        } catch (IOException e) {
            logger.error("crawel cookie faied",e);
        }
        return defaultCookie;
    }

    public org.jsoup.Connection buildLoginConnection(String url ,Map<String, String> cookies, String referer,String host){
        org.jsoup.Connection connection = Jsoup.connect(url)
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .header("Accept-Encoding", "gzip, deflate, sdch")
                .header("Accept-Language", "zh-CN,zh;q=0.8")
                .header("Cache-Control", "max-age=0")
                .header("Connection", "keep-alive")
                .header("Upgrade-Insecure-Requests", "1")
                .header("Host", host)
                .header("Referer", referer)
                .header("Upgrade-Insecure-Requests","1")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.111 Safari/537.36")
                .cookies(cookies).timeout(1000 * 60);
        return connection;
    }

    private Map<String, String> getCengjiaoCookies(String url) {
        Connection.Response res;
        Map<String, String> cookies = null;
        try {
            res = Jsoup.connect(url).timeout(30000).execute();
            cookies = res.cookies();

        } catch (IOException e) {
            logger.error("crawel cookie faied",e);
        }
        if(cookies == null || cookies.isEmpty()){
            if(cookies == null)
                cookies = new HashMap<String, String>();
            cookies.put("select_city", "310000");
            cookies.put("logger_session", "cc4b30501f989c230cae64da33783aa9");
            cookies.put("lianjia_uuid", "e88f8390-6423-477f-aba6-c2cb19d592a4");
        }
        cookies.put("_jzqx", "1.1453266333.1453266333.1.jzqsr=sh%2Elianjia%2Ecom|jzqct=/xiaoqu/chengjiao/.-");
        cookies.put("_jzqckmp", "1");
        cookies.put("looyu_id", "15bd9d26319e2688354b15f351103f5e13_32735%3A1");
        cookies.put("_jzqy", "1.1453260057.1453266838.2.jzqsr=baidu.jzqsr=baidu|jzqct=%E9%93%BE%E5%AE%B6%E4%BA%8C%E6%89%8B%E6%88%BF%E4%B8%8A%E6%B5%B7");
        cookies.put("_smt_uid", "569efd53.2fb83f0f");
        cookies.put("CNZZDATA1253492439", "2111986582-1453255310-http%253A%252F%252Fwww.lianjia.com%252F%7C1453266110");
        cookies.put("_gat", "1");
        cookies.put("_gat_past", "1");
        cookies.put("_gat_global", "1");
        cookies.put("_gat_new_global", "1");
        cookies.put("_gat_dianpu_agent", "1");
        cookies.put("CNZZDATA1254525948", "1237967892-1453084058-null%7C1453084058");
        cookies.put("_ga", "GA1.2.67415213.1453260058");
        cookies.put("CNZZDATA1254525948", "1054430819-1453257141-http%253A%252F%252Fwww.lianjia.com%252F%7C1453262542");
        cookies.put("CNZZDATA1255633284", "36596504-1453256573-http%253A%252F%252Fwww.lianjia.com%252F%7C1453263154");
        cookies.put("CNZZDATA1255604082", "1653364139-1453258621-http%253A%252F%252Fwww.lianjia.com%252F%7C1453265270");
        cookies.put("_qzja", "1.371230354.1453260115694.1453260115695.1453266332967.1453266900591.1453267008440.0.0.0.19.2");
        cookies.put("_qzjb", "1.1453266332967.15.0.0.0");
        cookies.put("_qzjc", "1");
        cookies.put("_qzjto", "19.2.0");
        cookies.put("_jzqa", "1.3102055523401765400.1453260057.1453260057.1453266333.2");
        cookies.put("_jzqc", "1");
        cookies.put("_jzqb", "1.15.10.1453266333.1");
        return cookies;
    }

    /**
     *
     * @author: asenpan - panyahui1.sh@superjia.com
     * @Description: 构建connection
     *
     */
    public static org.jsoup.Connection buildConnection(String url ,Map<String, String> cookies, String referer){

        org.jsoup.Connection connection = Jsoup.connect(url)
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .header("Accept-Encoding", "gzip, deflate, sdch")
                .header("Accept-Language", "zh-CN,zh;q=0.8")
                .header("Cache-Control", "max-age=0")
                .header("Connection", "keep-alive")
                .header("Upgrade-Insecure-Requests", "1")
                .header("Host", "sh.lianjia.com")
                .header("If-Modified-Since", DateUtils.getGMTDate())
                .header("Referer", referer)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.111 Safari/537.36")
                .cookies(cookies).timeout(1000 * 60 * 20)
                .timeout(1000 * 60 * 20);
        return connection;
    }


    public static void main(String[] args) {
        try {
            Document doc = buildConnection("http://i.ui.cn/ucenter/143208.html", getCookies("http://i.ui.cn/ucenter/143208.html"), "http://www.baidu.com").get();
            Element user_avatar_hideE = doc.getElementById("user-avatar-hide"); //avatar URL
            Elements us_nameE = doc.getElementsByClass("us-name"); // user Name and sign
            Elements us_infoE = doc.getElementsByClass("us-info"); // user property

            String user_avatar_hide = user_avatar_hideE.attr("src");

            for(Element element : us_nameE){
                Elements elementNames = element.getElementsByClass("n1");
                for(Element element2 : elementNames){
                    System.out.println("element2.data():"+element2.text());
                }

                Elements elementSignature = element.getElementsByClass("n2");
                for(Element element2 : elementSignature){
                    System.out.println("element2.data():"+element2.text());
                }
            }

            System.out.println("**********************************************************");
            for(Element element : us_infoE){
                Elements elements = element.getElementsByClass("ul");

                for(Element elementSub : us_infoE){
                    Elements elementNames = elementSub.getElementsByClass("us-i-l");
                    for(Element elementSubs: us_infoE){
                        Elements elements1 = elementSubs.getElementsByTag("span");
                        for(Element elementSubss: elements1){
                            System.out.println("elementSubss.text():"+elementSubss.text());
                        }
                    }
                }



            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @author: asenpan - panyahui1.sh@superjia.com
     * @Description: 获取行政区
     *
     */
    private Map<String,String> getAreas(String url,Map<String, String> cookies) throws Exception{
        Map<String,String> areas = new HashMap<String,String>();
        String referer = "http://sh.lianjia.com/ershoufang/?utm_source=baidu&utm_medium=ppc&utm_term=%E9%93%BE%E5%AE%B6%E7%BD%91&utm_content=%E9%93%BE%E5%AE%B6%E7%BD%91%E7%AB%99&utm_campaign=%E5%93%81%E7%89%8C%E8%AF%8D";
        try {
            Document doc = buildConnection(url,cookies,referer).get();// 开启连接，获取HTML文档DOM结构
            // 超时时间60s
            Elements lis = doc.select(".option-list");
            if (lis.size() > 0) {
                Element href = lis.get(0);
                Elements hrefs = href.select("a");
                for (Element element : hrefs) {
                    if("不限".equals(element.text()))
                        continue;
                    areas.put(element.absUrl("href"),element.text());
                }
            }
        } catch (IOException e) {
            logger.error("get dirstricts failed , crawler lj_second_hand housing error",e);
        }
        return areas;
    }



    /**
     *
     * @author: asenpan - panyahui1.sh@superjia.com
     * @Description: 获取区域下的板块
     * @param doc
     * @return
     *
     */
    private Map<String,String> getDistricts(Document doc){
        Map<String,String> districts = new HashMap<String,String>();
        try {
            Elements lis = doc.select(".option-list");
            if (lis.size() > 2) {
                Element href = lis.get(1);
                Elements hrefs = href.select("a");
                for (Element element : hrefs) {
                    if("不限".equals(element.text()))
                        continue;
                    districts.put(element.absUrl("href"),element.text());
                }
            }
        } catch (Exception e) {
            logger.error("get dirstricts failed , crawler lj_second_hand housing error",e);
        }
        return districts;
    }

    private String getFiveLastHouseUrl(String estateUrl, String currentUrl,Map<String, String> cookies){
        try {
            //sleep();
            Document doc = buildConnection(estateUrl + "esf/", cookies, currentUrl).get();// 开启连接，获取HTML文档DOM结构
            StringBuilder builder = new StringBuilder();
            Elements lis = doc.select(".house-lst li");
            Elements tmp = null;
            int count = 0;
            if (lis != null && lis.size() > 0) {
                for (Element element : lis) {
                    tmp = element.select(".pic-panel a");
                    if (tmp == null || tmp.isEmpty())
                        continue;
                    String houseUrl = tmp.get(0).absUrl("href");
                    if(StringUtils.isNotBlank(houseUrl))
                        builder.append(houseUrl.trim()).append(";");
                    if (++count > 5)
                        break;

                }
                String lastFiveHouseUrl = builder.toString();
                if (lastFiveHouseUrl != null && lastFiveHouseUrl.endsWith(";"))
                    lastFiveHouseUrl = lastFiveHouseUrl.substring(0, lastFiveHouseUrl.length() - 1);
                return StringUtils.isNotBlank(lastFiveHouseUrl) ? lastFiveHouseUrl.trim():"";
            }
        } catch (Exception e) {
            logger.error("get dirstricts failed , crawler lj_second_hand housing error", e);
        }
        return "";
    }

    private String parseUrlCode(String url){
        if(StringUtils.isBlank(url))
            return null;
        String[] tmp = url.split("/");
        if(tmp != null && tmp.length > 0){
            String code = tmp[tmp.length-1];
            if(StringUtils.isNotBlank(code))
                return code.trim();
        }
        return null;
    }


}
