package com.lami.dcm.lbdatasource.readwrite;

import com.lami.dcm.lbdatasource.model.Address;
import com.lami.dcm.lbdatasource.model.User;
import com.lami.dcm.lbdatasource.service.UserService;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by xjk on 2016/9/12.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-context.xml"})
public class ReadWriteDBTestWithNoForceChoiceReadOnWriteTrue {

    private static final Logger logger = Logger.getLogger(ReadWriteDBTestWithNoForceChoiceReadOnWriteTrue.class);

    @Autowired
    @Qualifier("lbUserService")
    private UserService userService;

    @Test
    public void testOnlyRead(){
        logger.info("test only read begin");
        userService.findById(1);
        logger.info("test only read end");
    }

    @Test
    public void testOnlyWrite() {
        logger.debug("test only write============begin");
        User user = genUser();
        userService.save(user); //1  choice write datasource
        userService.delete(user.getId()); //2  choice write datasource

        User user2 = genUser();
        Address address2 = genAddress();
        userService.save(user2, address2);//3  choice write datasource  此处内部会传播事务

        userService.delete(user2.getId());//4  choice write datasource

        logger.debug("test only write============end");
    }


    @Test
    public void testFirstReadNextWrite() {
        logger.debug("test first read next write============begin");
        User user = genUser();
        userService.save(user); //1  choice write datasource

        user = userService.findById(user.getId()); //2  choice read datasource

        userService.delete(user.getId());//3  choice write datasource

        logger.debug("test first read next write============end");
    }

    @Test
    public void testFirstWriteNextRead() {
        logger.debug("test first write next read============begin");

        userService.testFirstWriteNextRead(genUser());

        logger.debug("test first write next read============end");
    }



    private User genUser() {
        return new User("zhang" + System.currentTimeMillis());
    }


    private Address genAddress() {
        return new Address("city"+System.currentTimeMillis());
    }
}
