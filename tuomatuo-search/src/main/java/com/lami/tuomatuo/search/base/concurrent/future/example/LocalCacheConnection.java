package com.lami.tuomatuo.search.base.concurrent.future.example;

/**
 * Created by xujiankang on 2016/12/16.
 */
public class LocalCacheConnection extends AbstractLocalCache<String , Connection> {
    @Override
    public Connection computeV(String s) {

        logger.info("创建connection开始");
        logger.info("睡觉开始");

        try {
            Thread.sleep(5*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.info("睡觉结束");
        logger.info("创建connection结束");

        return new Connection() {
            @Override
            public Connection getConnection() {
                return null;
            }
        };
    }
}
