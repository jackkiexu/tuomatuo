package com.lami.tuomatuo.service;

import com.lami.tuomatuo.base.AbstractBaseTest;
import com.lami.tuomatuo.model.UserDynamic;
import com.lami.tuomatuo.service.crawler.ChanYouJiAccountService;
import com.lami.tuomatuo.service.crawler.HuPuAccountService;
import com.lami.tuomatuo.service.crawler.UIAccountService;
import lombok.Data;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by xjk on 2016/3/21.
 */
public class UIAccountTest extends AbstractBaseTest {

    @Autowired
    private UIAccountService uiAccountService;
    @Autowired
    private HuPuAccountService huPuAccountService;
    @Autowired
    private ChanYouJiAccountService chanYouJiAccountService;

    @Test
    public void getUserDynamic(){uiAccountService.crawlerUIAccount(1l, 10l);}

    @Test
    public void getHuPuAccount(){
        huPuAccountService.crawlerHuPuAccount(50l, 60l);
    }

    @Test
    public void getChanYouJiAccount(){
        chanYouJiAccountService.crawlerChanYouJi(10l, 15l);
    }

    @Test
    public void testClassPropagate(){
        Person p1 = new Person(1);
        Person p2 = new Person(2);
        Person p3 = new Person(3);
        Person p4 = new Person(4);

        p1.next = p2;
        p2.next = p3;
        p3.next = p4;

        System.out.println("**************** begin **************");
        p2.next = p3 = p4.next;

        System.out.println("p1:"+p1);
        System.out.println("p2:"+p2);
        System.out.println("p3:"+p3);
        System.out.println("p4:"+p4);
    }

    @Data
    class Person{
        Person next;
        int identify = 1;
        public Person(int identify){
            this.identify = identify;
        }
    }
}
