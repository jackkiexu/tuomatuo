package com.lami.tuomatuo.search.client;

import com.lami.tuomatuo.search.entity.Item;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by xjk on 2016/4/12.
 */
public class HuPuClient {

    private static final Logger logger = Logger.getLogger(HuPuClient.class);

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
        // remove all data
        // ------------------------------------------------------
        core.deleteByQuery("*:*");

        // ------------------------------------------------------
        // add item
        // ------------------------------------------------------
        Item item = new Item();
        item.setId("1");
        item.setAvatarURL("http://www.baidu.com");
        item.setAddress("上海");
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());
        item.setName("test01");
        core.addBean(item);

        // ------------------------------------------------------
        // add unicode item
        // ------------------------------------------------------
        Item item_cn = new Item();
        item_cn.setId("2");
        item_cn.setAvatarURL("http://www.baidu02.com");
        item_cn.setAddress("上海");
        item_cn.setCreateTime(new Date());
        item_cn.setUpdateTime(new Date());
        item_cn.setName("test02");
        core.addBean(item_cn);

        // commit
        core.commit();

        // ------------------------------------------------------
        // search
        // ------------------------------------------------------
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.addSort(new SolrQuery.SortClause("id", SolrQuery.ORDER.desc));
        QueryResponse response = core.query(query);
        List<Item> items = response.getBeans(Item.class);
        for (Item i : items) {
            logger.info(i);
        }
    }

}
