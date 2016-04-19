package com.lami.tuomatuo.search.base.curator.cache;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Created by xujiankang on 2016/4/18.
 */

public class NodeCacheExample {

    private static final Logger logger = Logger.getLogger(NodeCacheExample.class);

    private static final String PATH = "/example/nodeCache";

    public static void main(String[] args) {

        NodeCache nodeCache = null;
        CuratorFramework client = null;
        try {
            client =  CuratorFrameworkFactory.newClient("192.168.1.28:2181", new ExponentialBackoffRetry(1000, 3));
            client.start();

            nodeCache = new NodeCache(client, PATH);
            nodeCache.start();
            processCommands(client, nodeCache);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseableUtils.closeQuietly(client);
            CloseableUtils.closeQuietly(nodeCache);
        }
    }

    private static void addListener(final NodeCache cache){
        // a PathChildrenCacheListener is optional. Here, it's used just to log
        // changes
        NodeCacheListener listener = new NodeCacheListener() {
            public void nodeChanged() throws Exception {
                if(cache.getCurrentData() != null){
                    logger.info("Node changed : " + cache.getCurrentData().getPath() + ", value : " + new String(cache.getCurrentData().getData()));
                }
            }
        };
        cache.getListenable().addListener(listener);
    }



    private static void processCommands(CuratorFramework client, NodeCache nodeCache) throws Exception{
        printHelp();

        addListener(nodeCache);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        boolean done = false;
        while(!done){
            logger.info(">");
            String line = in.readLine();
            if (line == null) break;

            String command = line.trim();
            String[] parts = command.split("\\s");
            if(parts.length == 0){
                continue;
            }
            String operation = parts[0];

            String args[] = Arrays.copyOfRange(parts, 1, parts.length);
            if(operation.equalsIgnoreCase("help") || operation.equalsIgnoreCase("?")){
                printHelp();
            }else if(operation.equalsIgnoreCase("q") || operation.equalsIgnoreCase("quit")){
                done = true;
            }else if(operation.equals("set")){
                setValue(client, command, args);
            }else if(operation.equals("remove")){
                remove(client);
            }else if(operation.equals("show")){
                show(nodeCache);
            }
            Thread.sleep(1000);
            // just to allow the console ouput to catch up

        }
    }

    private static void show(NodeCache cache){
        if(cache.getCurrentData() != null){
            logger.info(cache.getCurrentData().getPath() + " = " + new String(cache.getCurrentData().getData()));
        }else{
            logger.info("cache don't set a value");
        }
    }

    private static void remove(CuratorFramework client) throws Exception{
        client.delete().forPath(PATH);
    }

    private static void setValue(CuratorFramework client, String command, String[] args) throws Exception{
        if(args.length != 1){
            logger.info("syntax error (excepted set <value>) : " + command);
            return;
        }
        byte[] bytes = args[0].getBytes();
        try {
            client.setData().forPath(PATH, bytes);
        } catch (Exception e) {
            client.create().creatingParentsIfNeeded().forPath(PATH, bytes);
            e.printStackTrace();
        }
    }

    private static void printHelp(){
        logger.info("An example of using PathChildrenCache, This is example is driven by entring commands at the prompt:");
        logger.info("set<value>: Adds or updates a node with the given name");
        logger.info("remove: Deletes the node with the given name");
        logger.info("show: Display the node's value in the cache");
        logger.info("quit: Quit the example");
    }

}
