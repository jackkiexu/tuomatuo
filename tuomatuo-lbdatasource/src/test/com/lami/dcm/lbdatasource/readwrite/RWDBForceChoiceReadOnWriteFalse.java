package com.lami.dcm.lbdatasource.readwrite;

import com.lami.dcm.lbdatasource.model.Address;
import com.lami.dcm.lbdatasource.model.User;
import com.lami.dcm.lbdatasource.service.UserService;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by xjk on 9/11/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-context.xml"})
@TransactionConfiguration(transactionManager = "txManager", defaultRollback = false)
public class RWDBForceChoiceReadOnWriteFalse {

    private static final Logger logger = Logger.getLogger(RWDBForceChoiceReadOnWriteFalse.class);

    @Autowired
    private UserService userService;

    @Test
    public void testOnlyRead(){
        logger.info("test only read begin");
        userService.findById(1);
        logger.info("test only read end");
    }

    @Test
    public void testOnlyWrite(){
        logger.info("test only write begin");
        User user = getUser();
        userService.save(user);
        userService.delete(user.getId());

        User user2 = getUser();
        Address address2 = getAddress();
        userService.save(user2, address2);

        userService.delete(user2.getId());
        logger.info("test only write end");
    }

    @Test
    public void TestFirstReadNextWrite(){
        logger.info("test first read next write begin");
        User user = getUser();
        userService.save(user);

        user = userService.findById(user.getId());
        userService.delete(user.getId());

        logger.info("test first read next write end");
    }

    @Test
    public void testFirstWriteNextRead(){
        logger.info("test first write next read ==== begin");
        userService.testFirstWriteNextRead(getUser());
        logger.info("test firat write next read end");
    }

    private User getUser(){
        return new User("zhang" + System.currentTimeMillis());
    }

    private Address getAddress(){
        return new Address("city" + System.currentTimeMillis());
    }
}
