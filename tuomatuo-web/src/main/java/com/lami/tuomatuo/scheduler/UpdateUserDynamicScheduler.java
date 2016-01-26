package com.lami.tuomatuo.scheduler;

import com.lami.tuomatuo.model.UserDynamic;
import com.lami.tuomatuo.service.DynamicCommentService;
import com.lami.tuomatuo.service.UserDynamicService;
import com.lami.tuomatuo.utils.DateUtils;
import com.lami.tuomatuo.utils.help.HotAlgorithmHelper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

/** 更新所用用户动态的 hot 值
 * Created by xujiankang on 2016/1/21.
 */
public class UpdateUserDynamicScheduler{

    @Autowired
    private UserDynamicService userDynamicService;
    @Autowired
    private DynamicCommentService dynamicCommentService;

    private static final Logger logger = Logger.getLogger(UpdateUserDynamicScheduler.class);
    private static final Double i = 0.6d;

    public void run() {
        try {
            updateUserDynamicHotValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private  void updateUserDynamicHotValue(){
        Date now = new Date();
        logger.info("执行订单定时任务开始"+ DateUtils.formatDate(now, "yyyy-MM-dd HH:mm:ss"));
        Long total = userDynamicService.getCount();
        for (int i=0; i <= total; i+=100){
            List<UserDynamic> userDynamicList = userDynamicService.getUserDynamic(i, 100);
            calculateHotValue(userDynamicList);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
        logger.info("执行订单定时任务结束"+ DateUtils.formatDate(now, "yyyy-MM-dd HH:mm:ss"));
    }

    private void calculateHotValue(List<UserDynamic> userDynamicList){
        for (UserDynamic userDynamic : userDynamicList){
            try {
                Long dyComCount = dynamicCommentService.getUserDynamicCommentCount(userDynamic.getId());
                long hotValue = HotAlgorithmHelper.hotAlgorithm(userDynamic.getDynamicSeeSum(), userDynamic.getDynamicRecommend(), userDynamic.getLove(), dyComCount, DateUtils.getDifferHour(userDynamic.getCreateTime()), DateUtils.getDifferHour(userDynamic.getCreateTime()), i);
                userDynamic.setHotValue(hotValue);
                userDynamicService.update(userDynamic);
                logger.info("UserDynamic id:"+userDynamic.getId() +", hotValue:"+hotValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
