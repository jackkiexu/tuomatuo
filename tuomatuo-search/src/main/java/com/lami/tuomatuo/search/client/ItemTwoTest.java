package com.lami.tuomatuo.search.client;

import com.lami.tuomatuo.search.entity.ItemTwo;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by xujiankang on 2016/4/12.
 */
public class ItemTwoTest {

    private static final Logger logger = Logger.getLogger(ItemTwoTest.class);

    public static void main02(String[] args) throws IOException, SolrServerException {
        String url = "http://192.168.1.105:8080/solr/mycore";
        HttpSolrServer core = new HttpSolrServer(url);
        core.setMaxRetries(1);
        core.setConnectionTimeout(5000);
        core.setParser(new XMLResponseParser()); // binary parser is used by default
        core.setSoTimeout(1000); // socket read timeout
        core.setDefaultMaxConnectionsPerHost(100);
        core.setMaxTotalConnections(100);
        core.setFollowRedirects(false); // defaults to false
        core.setAllowCompression(true);

        // ------------------------------------------------------
        // remove all data
        // ------------------------------------------------------
        core.deleteByQuery("*:*");
        List<ItemTwo> items = new ArrayList<ItemTwo>();
        items.add(makeItem(1, "cpu", "this is intel cpu", 1, "cpu-intel"));
        items.add(makeItem(2, "cpu AMD", "this is AMD cpu", 2, "cpu-AMD"));
        items.add(makeItem(3, "cpu intel", "this is intel-I7 cpu", 1, "cpu-intel"));
        items.add(makeItem(4, "cpu AMD", "this is AMD 5000x cpu", 2, "cpu-AMD"));
        items.add(makeItem(5, "cpu intel I6", "this is intel-I6 cpu", 1, "cpu-intel-I6"));
        core.addBeans(items);
        // commit
        core.commit();

        // ------------------------------------------------------
        // search
        // ------------------------------------------------------
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.setStart(0);    // query的开始行数(分页使用)
        query.setRows(0);    // query的返回行数(分页使用)
        query.setFacet(true);    // 设置使用facet
        query.setFacetMinCount(1);    // 设置facet最少的统计数量
        query.setFacetLimit(10);    // facet结果的返回行数
        query.addFacetField("category_name");    // facet的字段
        query.setFacetPrefix("cpu");    // facet字段值
        query.addSort(new SolrQuery.SortClause("id", SolrQuery.ORDER.asc));    // 排序
        QueryResponse response = core.query(query);
        List<ItemTwo> items_rep = response.getBeans(ItemTwo.class);
        List<FacetField> facetFields = response.getFacetFields();
        // 因为上面的start和rows均设置为0，所以这里不会有query结果输出
        logger.info("----------items_rep----------");
        for (ItemTwo i : items_rep) {
            logger.info("id=" + i.getId() + "\tcontent=" + i.getContent());
        }
        // 打印所有facet
        logger.info("----------facetFields----------");
        for(FacetField ff : facetFields) {
            logger.info("name=" + ff.getName() + "\tcount=" + ff.getValueCount());
            logger.info("--------------------");
            for(FacetField.Count count: ff.getValues() ) {
                logger.info("name=" + count.getName() + "\tvalue=" + count.getCount());
            }
        }
    }

    public static void main03(String[] args) throws IOException, SolrServerException {
        String url = "http://192.168.1.105:8080/solr/mycore";
        HttpSolrServer core = new HttpSolrServer(url);
        core.setMaxRetries(1);
        core.setConnectionTimeout(5000);
        core.setParser(new XMLResponseParser()); // binary parser is used by default
        core.setSoTimeout(1000); // socket read timeout
        core.setDefaultMaxConnectionsPerHost(100);
        core.setMaxTotalConnections(100);
        core.setFollowRedirects(false); // defaults to false
        core.setAllowCompression(true);

        // ------------------------------------------------------
        // remove all data
        // ------------------------------------------------------
        core.deleteByQuery("*:*");
        List<ItemTwo> items = new ArrayList<ItemTwo>();
        items.add(makeItem(1, "cpu", "this is intel cpu", 1, "cpu-intel"));
        items.add(makeItem(2, "cpu AMD", "this is AMD cpu", 2, "cpu-AMD"));
        items.add(makeItem(3, "cpu intel", "this is intel-I7 cpu", 1, "cpu-intel"));
        items.add(makeItem(4, "cpu AMD", "this is AMD 5000x cpu", 2, "cpu-AMD"));
        items.add(makeItem(5, "cpu intel I6", "this is intel-I6 cpu", 1, "cpu-intel-I6"));
        items.add(makeItem(6, "处理器", "中央处理器英特儿", 1, "cpu-intel"));
        items.add(makeItem(7, "处理器AMD", "中央处理器AMD", 2, "cpu-AMD"));
        items.add(makeItem(8, "中央处理器", "中央处理器Intel", 1, "cpu-intel"));
        items.add(makeItem(9, "中央空调格力", "格力中央空调", 3, "air"));
        items.add(makeItem(10, "中央空调海尔", "海尔中央空调", 3, "air"));
        items.add(makeItem(11, "中央空调美的", "美的中央空调", 3, "air"));
        core.addBeans(items);
        // commit
        core.commit();

        // ------------------------------------------------------
        // search
        // ------------------------------------------------------
        SolrQuery query = new SolrQuery();
        String token = "中央";
        query.set("qt", "/spellcheck");
        query.set("q", token);
        query.set("spellcheck", "on");
        query.set("spellcheck.build", "true");
        query.set("spellcheck.onlyMorePopular", "true");

        query.set("spellcheck.count", "100");
        query.set("spellcheck.alternativeTermCount", "4");
        query.set("spellcheck.onlyMorePopular", "true");

        query.set("spellcheck.extendedResults", "true");
        query.set("spellcheck.maxResultsForSuggest", "5");

        query.set("spellcheck.collate", "true");
        query.set("spellcheck.collateExtendedResults", "true");
        query.set("spellcheck.maxCollationTries", "5");
        query.set("spellcheck.maxCollations", "3");

        QueryResponse response = null;

        try {
            response = core.query(query);
            logger.info("查询耗时：" + response.getQTime());
        } catch (SolrServerException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } finally {
            core.shutdown();
        }

        SpellCheckResponse spellCheckResponse = response.getSpellCheckResponse();
        if (spellCheckResponse != null) {
            List<SpellCheckResponse.Suggestion> suggestionList = spellCheckResponse.getSuggestions();
            for (SpellCheckResponse.Suggestion suggestion : suggestionList) {
                logger.info("Suggestions NumFound: " + suggestion.getNumFound());
                logger.info("Token: " + suggestion.getToken());
                logger.info("Suggested: ");
                List<String> suggestedWordList = suggestion.getAlternatives();
                for (String word : suggestedWordList) {
                    logger.info(word + ", ");
                }
                System.out.println();
            }
            System.out.println();
            Map<String, SpellCheckResponse.Suggestion> suggestedMap = spellCheckResponse.getSuggestionMap();
            for (Map.Entry<String, SpellCheckResponse.Suggestion> entry : suggestedMap.entrySet()) {
                logger.info("suggestionName: " + entry.getKey());
                SpellCheckResponse.Suggestion suggestion = entry.getValue();
                logger.info("NumFound: " + suggestion.getNumFound());
                logger.info("Token: " + suggestion.getToken());
                logger.info("suggested: ");

                List<String> suggestedList = suggestion.getAlternatives();
                for (String suggestedWord : suggestedList) {
                    logger.info(suggestedWord + ", ");
                }
                logger.info("\n\n");
            }

            SpellCheckResponse.Suggestion suggestion = spellCheckResponse.getSuggestion(token);
            logger.info("NumFound: " + suggestion.getNumFound());
            logger.info("Token: " + suggestion.getToken());
            logger.info("suggested: ");
            List<String> suggestedList = suggestion.getAlternatives();
            for (String suggestedWord : suggestedList) {
                logger.info(suggestedWord + ", ");
            }
            logger.info("\n\n");

            logger.info("The First suggested word for solr is : " + spellCheckResponse.getFirstSuggestion(token));
            logger.info("\n\n");

            List<SpellCheckResponse.Collation> collatedList = spellCheckResponse.getCollatedResults();
            if (collatedList != null) {
                for (SpellCheckResponse.Collation collation : collatedList) {
                    logger.info("collated query String: " + collation.getCollationQueryString());
                    logger.info("collation Num: " + collation.getNumberOfHits());
                    List<SpellCheckResponse.Correction> correctionList = collation.getMisspellingsAndCorrections();
                    for (SpellCheckResponse.Correction correction : correctionList) {
                        logger.info("original: " + correction.getOriginal());
                        logger.info("correction: " + correction.getCorrection());
                    }
                    logger.info("");
                }
            }
            logger.info("");
            logger.info("The Collated word: " + spellCheckResponse.getCollatedResult());
            System.out.println();
        }

        logger.info("查询耗时：" + response.getQTime());
    }

    public static void main(String[] args) throws IOException, SolrServerException {
        String url = "http://192.168.1.105:8080/solr/mycore";
        HttpSolrServer core = new HttpSolrServer(url);
        core.setMaxRetries(1);
        core.setConnectionTimeout(5000);
        core.setParser(new XMLResponseParser()); // binary parser is used by default
        core.setSoTimeout(1000); // socket read timeout
        core.setDefaultMaxConnectionsPerHost(100);
        core.setMaxTotalConnections(100);
        core.setFollowRedirects(false); // defaults to false
        core.setAllowCompression(true);

        // ------------------------------------------------------
        // search
        // ------------------------------------------------------
        SolrQuery query = new SolrQuery();
        String token = "中央";
        query.set("qt", "/spellcheck");
        query.set("q", token);
        query.set("spellcheck", "on");
        query.set("spellcheck.build", "true");
        query.set("spellcheck.onlyMorePopular", "true");

        query.set("spellcheck.count", "100");
        query.set("spellcheck.alternativeTermCount", "4");
        query.set("spellcheck.onlyMorePopular", "true");

        query.set("spellcheck.extendedResults", "true");
        query.set("spellcheck.maxResultsForSuggest", "5");

        query.set("spellcheck.collate", "true");
        query.set("spellcheck.collateExtendedResults", "true");
        query.set("spellcheck.maxCollationTries", "5");
        query.set("spellcheck.maxCollations", "3");

        QueryResponse response = null;

        try {
            response = core.query(query);
            System.out.println("查询耗时：" + response.getQTime());
        } catch (SolrServerException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } finally {
            core.shutdown();
        }

        SpellCheckResponse spellCheckResponse = response.getSpellCheckResponse();
        if (spellCheckResponse != null) {
            List<SpellCheckResponse.Suggestion> suggestionList = spellCheckResponse.getSuggestions();
            for (SpellCheckResponse.Suggestion suggestion : suggestionList) {
                System.out.println("Suggestions NumFound: " + suggestion.getNumFound());
                System.out.println("Token: " + suggestion.getToken());
                System.out.print("Suggested: ");
                List<String> suggestedWordList = suggestion.getAlternatives();
                for (String word : suggestedWordList) {
                    System.out.println(word + ", ");
                }
                System.out.println();
            }
            System.out.println();
            Map<String, SpellCheckResponse.Suggestion> suggestedMap = spellCheckResponse.getSuggestionMap();
            for (Map.Entry<String, SpellCheckResponse.Suggestion> entry : suggestedMap.entrySet()) {
                System.out.println("suggestionName: " + entry.getKey());
                SpellCheckResponse.Suggestion suggestion = entry.getValue();
                System.out.println("NumFound: " + suggestion.getNumFound());
                System.out.println("Token: " + suggestion.getToken());
                System.out.print("suggested: ");

                List<String> suggestedList = suggestion.getAlternatives();
                for (String suggestedWord : suggestedList) {
                    System.out.print(suggestedWord + ", ");
                }
                System.out.println("\n\n");
            }

            SpellCheckResponse.Suggestion suggestion = spellCheckResponse.getSuggestion(token);
            System.out.println("NumFound: " + suggestion.getNumFound());
            System.out.println("Token: " + suggestion.getToken());
            System.out.print("suggested: ");
            List<String> suggestedList = suggestion.getAlternatives();
            for (String suggestedWord : suggestedList) {
                System.out.print(suggestedWord + ", ");
            }
            System.out.println("\n\n");

            System.out.println("The First suggested word for solr is : " + spellCheckResponse.getFirstSuggestion(token));
            System.out.println("\n\n");

            List<SpellCheckResponse.Collation> collatedList = spellCheckResponse.getCollatedResults();
            if (collatedList != null) {
                for (SpellCheckResponse.Collation collation : collatedList) {
                    System.out.println("collated query String: " + collation.getCollationQueryString());
                    System.out.println("collation Num: " + collation.getNumberOfHits());
                    List<SpellCheckResponse.Correction> correctionList = collation.getMisspellingsAndCorrections();
                    for (SpellCheckResponse.Correction correction : correctionList) {
                        System.out.println("original: " + correction.getOriginal());
                        System.out.println("correction: " + correction.getCorrection());
                    }
                    System.out.println();
                }
            }
            System.out.println();
            System.out.println("The Collated word: " + spellCheckResponse.getCollatedResult());
            System.out.println();
        }

        System.out.println("查询耗时：" + response.getQTime());
    }

    private static ItemTwo makeItem(long id, String subject, String content, long categoryId, String categoryName) {
        ItemTwo item = new ItemTwo();
        item.setId(id);
        item.setSubject(subject);
        item.setContent(content);
        item.setLastUpdateTime(new Date());
        item.setCategoryId(categoryId);
        item.setCategoryName(categoryName);
        return item;
    }

}
