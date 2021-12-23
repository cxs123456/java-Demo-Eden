package org.cxs.demo.drools.controller;

import org.cxs.demo.drools.entity.Order;
import org.cxs.demo.drools.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * note:
 *
 * @author cxs
 * @date 2021/12/15 18:39
 **/
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private RuleService ruleService;

    @GetMapping("/demo")
    public Order saveOrder(Order order) {
        return ruleService.execRule(order);
    }
}
