package com.lami.tuomatuo.utils.help;

/**
 * {ln(views) + recommendScore + dynamicLoves + ln(articleComments) +ln(dynamicLoves) } / (age/2+update/2+1)i
 * http://segmentfault.com/a/1190000004253816?_ea=546109
 * Created by xujiankang on 2016/1/20.
 */
public class HotAlgorithmHelper {

    /**
     *  tuomatuo 计算用户动态的排名
     * @param view 用户动态查看的次数
     * @param recommendScore 转发的次数
     * @param dynamicLoves 点赞的次数
     * @param dynamicComments 评论的次数
     * @param age 从发布 到现在一共经过了多少小时
     * @param update 从最后一次更新到现在一共经历的小时
     * @param i: x 重力因子
     * @return
     */
    public static long hotAlgorithm(Long view, Long recommendScore, Long dynamicLoves, Long dynamicComments, Long age, Long update, double i){
        double molecule = Math.log1p(view) * 4 + recommendScore + dynamicLoves + dynamicComments;
        double denominator = Math.pow((age/2 + update/2 + 2), i);
        return (long)((molecule/denominator)*10000); // 放大1k倍进行存储
    }

    public static void main(String[] args) {

        double a0 = HotAlgorithmHelper.hotAlgorithm(1000l, 2l, 23l, 3l, 0l, 0l, 0.6);
        double aa = HotAlgorithmHelper.hotAlgorithm(1000l, 2l, 23l, 3l, 24l, 24l, 0.6);
        double ee = HotAlgorithmHelper.hotAlgorithm(10800l, 2l, 23l, 3l, 48l, 48l, 0.6);
        double bb = HotAlgorithmHelper.hotAlgorithm(1000l, 2l, 23l, 3l, 48l, 48l, 0.6);
        double cc = HotAlgorithmHelper.hotAlgorithm(1000l, 2l, 23l, 3l, 240l, 240l, 0.6);
        double dd = HotAlgorithmHelper.hotAlgorithm(1000l, 2l, 23l, 3l, 1000l, 1000l, 0.6);

        System.out.println("---------------------------");
        System.out.println("a0:" +a0);
        System.out.println("aa:" +aa);
        System.out.println("bb:"+bb);
        System.out.println("cc:"+cc);
        System.out.println("dd:"+dd);
        System.out.println("ee:"+ee);

        System.out.println("---------------------------");
        System.out.println(Math.log1p(100));
        System.out.println(Math.log1p(1000));
        System.out.println(Math.log1p(5000));
        System.out.println(Math.log1p(10000));
        System.out.println(Math.log1p(20000));
        System.out.println(Math.log1p(40000));
        System.out.println(Math.log1p(70000));
        System.out.println(Math.log1p(100000));
        System.out.println(Math.log1p(1000000));

        System.out.println("---------------------------");
        double a = 24.0;
        StringBuilder xcStr = new StringBuilder("");
        StringBuilder resultStr = new StringBuilder("");
        int xi = 0;
        for (double i = 0.5; i < 10; i+=0.5){
            long xc = (long)(a*i);
            xi+=1;
            long result = HotAlgorithmHelper.hotAlgorithm((long)( 1000l + 50*xi), ((long)(xi*0.5)*2l), (long)((xi*0.8)*24l), (long)((xi*0.5)*4l), (long)xc, (long)xc, 1.6);
            xcStr.append(xc+", ");
            resultStr.append(result+", ");
            System.out.println("xc:"+xc+", result:"+result);
        }

        System.out.println("xcStr:"+xcStr);
        System.out.println("resultStr:"+resultStr);
    }
}
