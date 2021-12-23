package org.cxs.demo.drools.test;

import org.cxs.demo.drools.entity.Order;
import org.cxs.demo.drools.service.RuleService;
import org.drools.core.base.RuleNameEqualsAgendaFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * note:
 *
 * @author cxs
 * @date 2021/12/3 12:19
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoTests {
    @Autowired
    private RuleService ruleService;
    @Autowired
    private KieBase kieBase;

    @Before
    public void setup() {

    }

    @Test
    public void testRule() {
        Order order = new Order();
        ruleService.execRule(order);
        System.out.println(" ----> " + order);
    }

    @Test
    public void test1() {
        Order order = new Order();
        order.setAmount(1999);
        // 1.获取 KieSession
        KieSession kieSession = kieBase.newKieSession();

        // 2.插入事实对象
        kieSession.insert(order);
        // 3.执行规则，执行的是所有规则
        // kieSession.fireAllRules();
        // 3.1 执行指定规则
        kieSession.fireAllRules(new RuleNameEqualsAgendaFilter("score_4"));

        // 4.关闭会话
        kieSession.dispose();
    }

    @Test
    public void test2() {
        Order order = new Order();
        order.setAmount(1999);
        // 1.获取 KieSession
        KieSession kieSession = kieBase.newKieSession();

        // 2.插入事实对象
        kieSession.insert(order);
        // 3.执行规则，执行的是所有规则
        kieSession.fireAllRules();
        // 3.1 执行指定规则
        /// kieSession.fireAllRules(new RuleNameEqualsAgendaFilter("score_5"));

        // 4.关闭会话
        kieSession.dispose();
    }

}
