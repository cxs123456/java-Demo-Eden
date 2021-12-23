package org.cxs.demo.drools.service;

import org.cxs.demo.drools.entity.Order;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * note:
 *
 * @author cxs
 * @date 2021/12/15 18:34
 **/
@Service
public class RuleService {

    @Autowired
    private KieBase kieBase;

    public Order execRule(Order order) {
        // 1.获取 KieSession
        KieSession kieSession = kieBase.newKieSession();

        // 2.插入事实对象
        kieSession.insert(order);
        // 3.执行规则，执行的是所有规则
        kieSession.fireAllRules();

        // 4.关闭会话
        kieSession.dispose();
        return order;
    }
}
