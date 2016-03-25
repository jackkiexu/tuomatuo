package com.lami.tuomatuo.utils.help.crawler;

import com.lami.tuomatuo.model.crawler.ChanYouJiAccount;
import com.lami.tuomatuo.model.crawler.vo.ChanYouJiAccountVO;
import com.lami.tuomatuo.model.crawler.vo.ChanYouJiVO;
import com.lami.tuomatuo.model.crawler.vo.ChanYoujiDynamicVO;
import com.lami.tuomatuo.model.crawler.vo.HuPuAccountVO;
import com.lami.tuomatuo.utils.CrawlerUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xujiankang on 2016/3/22.
 */
public class CrawlerChanYouJiAccountHelper {
    private static final Logger logger = Logger.getLogger(CrawlerChanYouJiAccountHelper.class);

    private CrawlerChanYouJiAccountHelper() {}
    static class  InnerClass{
        private static CrawlerChanYouJiAccountHelper instance = new CrawlerChanYouJiAccountHelper();
    }
    public static CrawlerChanYouJiAccountHelper getInstance(){
        return InnerClass.instance;
    }


    public static void main(String[] args) {
        CrawlerChanYouJiAccountHelper.getInstance().crawlerChanYouJi(1l, 20l);
    }

    public List<ChanYouJiVO> crawlerChanYouJi(Long minId, Long maxId){

        Long iInit = 1l;
        Long iMax = 10l;
        if(minId != null) iInit = minId;
        if(maxId != null) iMax = maxId;

        ChanYouJiVO chanYouJiVO = new ChanYouJiVO();
        ChanYouJiAccountVO chanYouJiAccountVO = new ChanYouJiAccountVO();
        List<ChanYouJiVO> chanYouJiVOList = new ArrayList<ChanYouJiVO>();

        Long nullCount = 0l;
        String URL = null;
        String referer = null;
        for(;nullCount <= 1000 && iInit <= iMax;iInit++){
            URL = "http://chanyouji.com/users/"+iInit;
            referer = "http://chanyouji.com/users/"+iInit;
            chanYouJiVO = crawlerChanYouJi(URL, referer, iInit);
            if(chanYouJiVO == null || chanYouJiVO.getChanYouJiAccountVO() == null){
                logger.info("account == null, and i = " + iInit);
                nullCount += 1l;
                continue;
            }else {
                nullCount = 0l;
            }
            logger.info("chanYouJiVO:"+chanYouJiVO);
            chanYouJiVOList.add(chanYouJiVO);
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return chanYouJiVOList;
    }

    public ChanYouJiVO crawlerChanYouJi(String URL, String referer, Long index) {
        logger.info("URL:"+URL+", referer:"+referer+", index:"+index);
        ChanYouJiVO chanYouJiVO = new ChanYouJiVO();
        ChanYouJiAccountVO chanYouJiAccountVO = new ChanYouJiAccountVO(index);
        List<ChanYoujiDynamicVO> chanYoujiDynamicVOList = new ArrayList<ChanYoujiDynamicVO>();

        try {
            Document doc = CrawlerUtils.buildConnection(URL, CrawlerUtils.getCookies(URL), referer).get();
            if(doc == null) return null;

            try {
                Elements elements = doc.getElementsByAttributeValue("class", "avatar");
                String avatar =  elements.get(0).attr("src");
                chanYouJiAccountVO.setAvatarURL(avatar);
            } catch (Exception e) {
                e.printStackTrace();
                chanYouJiVO.setChanYouJiAccountVO(null);
                return chanYouJiVO;
            }

            try {
                Elements elementInfo = doc.getElementsByAttributeValue("class", "users-info");
                Elements elementsNames =  elementInfo.get(0).getElementsByTag("h1");
                String name = elementsNames.get(0).text();
                chanYouJiAccountVO.setName(name);
            } catch (Exception e) {
                e.printStackTrace();
                chanYouJiVO.setChanYouJiAccountVO(null);
                return chanYouJiVO;
            }

            try {
                Elements elementSina = doc.getElementsByAttributeValue("class", "weibo weibo-selected");
                String sina = elementSina.get(0).getElementsByTag("a").get(0).attr("href");
                chanYouJiAccountVO.setSina(sina);
            } catch (Exception e) {
                e.printStackTrace();
            }

            logger.info("chanYouJiAccountVO:"+chanYouJiAccountVO);

            chanYouJiVO.setChanYouJiAccountVO(chanYouJiAccountVO);

            // iterator current_Page_Dynamic
            Elements elementDynamicS = doc.getElementsByAttributeValue("class", "trip-list-item");
            if(elementDynamicS != null && elementDynamicS.size() != 0){
                for(Element element : elementDynamicS){

                    ChanYoujiDynamicVO chanYoujiDynamicVO = new ChanYoujiDynamicVO(index);
                    try {
                        String dynamicURL = element.getElementsByTag("a").get(0).attr("href");
                        chanYoujiDynamicVO.setDynaWebURL("http://chanyouji.com" + dynamicURL);
                        String dynamicCoverURL = element.getElementsByTag("img").get(0).attr("src");
                        chanYoujiDynamicVO.setDynaCoverImgURL(dynamicCoverURL);
                        String dynamicTiTle = element.getElementsByTag("h1").get(0).text();
                        chanYoujiDynamicVO.setDynaTitle(dynamicTiTle);

                        String seeSum = element.getElementsByAttributeValue("class", "ii v").get(0).text();
                        chanYoujiDynamicVO.setSeeSum(Long.parseLong(seeSum));
                        String msgSum = element.getElementsByAttributeValue("class", "ii c").get(0).text();
                        chanYoujiDynamicVO.setMsgSum(Long.parseLong(msgSum));
                        String loveSum = element.getElementsByAttributeValue("class", "ii l").get(0).text();
                        chanYoujiDynamicVO.setLoveSum(Long.parseLong(loveSum));
                        String collectSum = element.getElementsByAttributeValue("class", "ii s").get(0).text();
                        chanYoujiDynamicVO.setForwardSum(Long.parseLong(collectSum));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        chanYoujiDynamicVO = null;
                    }
                    if(chanYoujiDynamicVO != null) chanYoujiDynamicVOList.add(chanYoujiDynamicVO);
                }
                chanYouJiVO.setChanYoujiDynamicVOList(chanYoujiDynamicVOList);
            }


            // iterator page pagination
            try {
                Elements elementDynamicPagination = doc.getElementsByAttributeValue("class", "pagination");
                Elements elementDynamicPaginationA = elementDynamicPagination.get(0).getElementsByTag("a");
                for(Element elementA  : elementDynamicPaginationA){
                    try {
                        String pagination = elementA.attr("href");
                        String pageURL = "http://chanyouji.com" + pagination;
                        chanYouJiVO.getChanYoujiDynamicVOList().addAll(collectionChanYouJiDynamic(pageURL, pageURL, index));
                        Thread.sleep(1300);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(1300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
            chanYouJiVO.setChanYouJiAccountVO(null);
            chanYouJiVO.setChanYoujiDynamicVOList(null);
        }
        return chanYouJiVO;
    }


    private List<ChanYoujiDynamicVO> collectionChanYouJiDynamic(String URL, String referer, Long index){
        List<ChanYoujiDynamicVO> chanYoujiDynamicVOList = new ArrayList<ChanYoujiDynamicVO>();

        try {
            Document doc = CrawlerUtils.buildConnection(URL, CrawlerUtils.getCookies(URL), referer).get();
            if(doc == null) return null;

            Elements elementDynamicS = doc.getElementsByAttributeValue("class", "trip-list-item");
            for(Element element : elementDynamicS){

                ChanYoujiDynamicVO chanYoujiDynamicVO = new ChanYoujiDynamicVO(index);
                try {
                    String dynamicURL = element.getElementsByTag("a").get(0).attr("href");
                    chanYoujiDynamicVO.setDynaWebURL("http://chanyouji.com" + dynamicURL);
                    String dynamicCoverURL = element.getElementsByTag("img").get(0).attr("src");
                    chanYoujiDynamicVO.setDynaCoverImgURL(dynamicCoverURL);
                    String dynamicTiTle = element.getElementsByTag("h1").get(0).text();
                    chanYoujiDynamicVO.setDynaTitle(dynamicTiTle);

                    String seeSum = element.getElementsByAttributeValue("class", "ii v").get(0).text();
                    chanYoujiDynamicVO.setSeeSum(Long.parseLong(seeSum));
                    String msgSum = element.getElementsByAttributeValue("class", "ii c").get(0).text();
                    chanYoujiDynamicVO.setMsgSum(Long.parseLong(msgSum));
                    String loveSum = element.getElementsByAttributeValue("class", "ii l").get(0).text();
                    chanYoujiDynamicVO.setLoveSum(Long.parseLong(loveSum));
                    String collectSum = element.getElementsByAttributeValue("class", "ii s").get(0).text();
                    chanYoujiDynamicVO.setForwardSum(Long.parseLong(collectSum));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    chanYoujiDynamicVO = null;
                }
                if(chanYoujiDynamicVO != null) chanYoujiDynamicVOList.add(chanYoujiDynamicVO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return chanYoujiDynamicVOList;
    }
}
