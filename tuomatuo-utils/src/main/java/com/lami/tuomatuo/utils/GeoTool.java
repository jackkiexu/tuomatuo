package com.lami.tuomatuo.utils;

/**
 * Created by xujiankang on 2016/1/11.
 * http://iamzhongyong.iteye.com/blog/1399333
 * http://www.cnblogs.com/dengxinglin/archive/2012/12/14/2817761.html#d
 * http://www.wubiao.info/401
 *
 * 在纬度相等的情况下：
 * 经度每隔0.00001度，距离相差约1米；
 * 每隔0.0001度，距离相差约10米；
 * 每隔0.001度，距离相差约100米；
 * 每隔0.01度，距离相差约1000米；
 * 每隔0.1度，距离相差约10000米。
 * 在经度相等的情况下：
 * 纬度每隔0.00001度，距离相差约1.1米；
 * 每隔0.0001度，距离相差约11米；
 * 每隔0.001度，距离相差约111米；
 * 每隔0.01度，距离相差约1113米；
 * 每隔0.1度，距离相差约11132米。
 */
public class GeoTool {

    private static final double EARTH_RADIUS = 6378.137;

    /**
     * <pre>两点计算距离，传入两点的经纬度,</pre>
     */
    public static double getPointDistance(double lat1,double lng1,double lat2,double lng2){
        double result = 0 ;

        double radLat1 = radian(lat1);

        double ratlat2 = radian(lat2);
        double a = radian(lat1) - radian(lat2);
        double b = radian(lng1) - radian(lng2);

        result = 2*Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2)+Math.cos(radLat1)*Math.cos(ratlat2)*Math.pow(Math.sin(b/2), 2)));
        result = result*EARTH_RADIUS;

        result = Math.round(result*1000);   //返回的单位是米，四舍五入

        return result;
    }

    /**由角度转换为弧度*/
    private static double radian(double d){
        return (d*Math.PI)/180.00;
    }

    public static void main(String[] args) {
        GeoTool tool = new GeoTool();

        System.out.println(tool.getPointDistance(30.27872, 120.12161, 30.27911, 120.12161));

    }
}
