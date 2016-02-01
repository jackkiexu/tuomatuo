package com.lami.tuomatuo.utils;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import lombok.Data;
import org.apache.log4j.Logger;

/**
 * Created by xujiankang on 2016/1/7.
 */
public class QiuNiuUtils {
    private static final Logger log = Logger.getLogger(QiuNiuUtils.class);

    private static  Auth auth = Auth.create("", "");
    // 重用 uploadManager。一般地，只需要创建一个 uploadManager 对象
    private static UploadManager uploadManager = new UploadManager();

    public static void main(String[] args) {
        System.out.println("getUpToken0 :"+getUpToken0());
        BucketManager bucketManager = new BucketManager(auth);
        try {
            String[] buckets = bucketManager.buckets();
            System.out.println("buckets.length : " + buckets.length);
            for(String part : buckets){
                System.out.println("part:" + part);
            }
        } catch (QiniuException e) {
            e.printStackTrace();
        }
        log.info("*******************************");
        upload();
    }

    /******************************** 通过上传策略生成上传凭证 *********************************************/
    // 简单上传，使用默认策略
    private static String getUpToken0(){
        return auth.uploadToken("tuomatuo");
    }

    // 覆盖上传
    private String getUpToken1(){
        return auth.uploadToken("tuomatuo", "key");
    }

    /**
     * 生成上传token
     *
     * @param bucket  空间名
     * @param key     key，可为 null
     * @param expires 有效时长，单位秒。默认3600s
     * @param policy  上传策略的其它参数，如 new StringMap().put("endUser", "uid").putNotEmpty("returnBody", "") scope通过 bucket、key间接设置，deadline 通过 expires 间接设置
     * @param strict  是否去除非限定的策略字段，默认true
     * @return 生成的上传token
     */
    private String getUpToken2(){
        return auth.uploadToken("bucket", null, 3600, new StringMap()
                .put("callbackUrl", "call back url").putNotEmpty("callbackHost", "")
                .put("callbackBody", "key=$(key)&hash=$(etag)"));
    }

    // 设置预处理、去除非限定的策略字段
    private String getUpToken3(){
        return auth.uploadToken("bucket", null, 3600, new StringMap()
                .putNotEmpty("persistentOps", "").putNotEmpty("persistentNotifyUrl", "")
                .putNotEmpty("persistentPipeline", ""), true);
    }

    /**
     * 生成上传token
     *
     * @param bucket  空间名
     * @param key     key，可为 null
     * @param expires 有效时长，单位秒。默认3600s
     * @param policy  上传策略的其它参数，如 new StringMap().put("endUser", "uid").putNotEmpty("returnBody", "")。
     *        scope通过 bucket、key间接设置，deadline 通过 expires 间接设置
     * @param strict  是否去除非限定的策略字段，默认true
     * @return 生成的上传token
     */
    public String uploadToken(String bucket, String key, long expires, StringMap policy, boolean strict){
        return null;
    }


    /***************************** 上传 *********************************************/

    private static void upload() {
        try {
            Response res = uploadManager.put("C:\\Users\\Administrator\\Desktop\\down\\test.png", "test12.png", getUpToken());
            MyRet ret = res.jsonToObject(MyRet.class);
            log.info("res.toString():"+ret);
            log.info("res.bodyString():" + ret);

        } catch (QiniuException e) {
            Response r = e.response;
            // 请求失败时简单状态信息
            log.error(r.toString());
            try {
                // 响应的文本信息
                log.error(r.bodyString());
            } catch (QiniuException e1) {
                //ignore
            }
        }
    }

    @Data
    class MyRet {
        public long fsize;
        public String key;
        public String hash;
        public int width;
        public int height;
    }

    private static String getUpToken(){
        return auth.uploadToken("tuomatuo", null, 3600, new StringMap()
                .put("callbackUrl", "http://<IP>/app1/qiniu/callbackUrl.form").putNotEmpty("callbackHost", "<IP>")
                .put("callbackBody", "key=$(key)&hash=$(etag)&width=$(imageInfo.width)&height=$(imageInfo.height)&uuid=$(uuid)&imageInfo=$(imageInfo)&fsize=$(fsize)&bucket=$(bucket)&location=$(x:location)&price=$(x:price)"));
    }
}

class UploadDemo {
    String ACCESS_KEY = "";
    String SECRET_KEY = "";
    Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);

    UploadManager uploadManager = new UploadManager();

    // 覆盖上传
    private String getUpToken(){

        return auth.uploadToken("tuomatuo", "key", 3600, new StringMap().put("insertOnly","1"));
    }

    public void upload() throws QiniuException{

        Response res = uploadManager.put("file", "key", getUpToken());
        System.out.println(getUpToken());
        System.out.println(res.bodyString());

    }

    public static void main(String args[]) throws QiniuException{
        new UploadDemo().upload();
    }
}
