package com.example.demo.springstatemachine.demo01;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.config.configurers.StateConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author chenxinsui
 * @date 2020/11/26 18:05
 * @description:
 **/
@Component
public class MyCommandLineRunner implements CommandLineRunner, ApplicationRunner {
    @Override
    public void run(String... args) throws Exception {
        // System.out.println(" ---> CommandLineRunner ");

    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println(" ---> ApplicationRunner ");

        StateMachine<Integer, String> machine = buildMachine();

        machine.start();

        System.out.println(Thread.currentThread().getName() + " start id = " + machine.getState().getId());
        // machine.sendEvent("NEXT");
    }

    StateMachine<Integer, String> buildMachine() throws Exception {
        final int DONE_STATE = 100;
        Integer[] states = {1, 2, 3};
        LinkedHashSet<Integer> stateSet = new LinkedHashSet<>(Arrays.asList(states));
        // 1.配置 状态
        StateMachineBuilder.Builder<Integer, String> builder = StateMachineBuilder.builder();
        StateConfigurer<Integer, String> configurer = builder.configureStates()
                .withStates();
        configurer.initial(0)
                .end(DONE_STATE)
                .state(DONE_STATE, context -> {
                    String event = context.getEvent();
                    Integer stateId = context.getSource().getId();
                    System.out.println(stateId + " into DONE_STATE --->" + Thread.currentThread().getName());

//                    if (stateId < 3) {
//                        // 睡2秒，重新开始
//                        try {
//                            TimeUnit.SECONDS.sleep(2);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        System.out.println(" sleep statemachime 2 second");
//                        StateMachine<Integer, String> stateMachine = context.getStateMachine();
//                        stateMachine.start();
//                    }


                })
                .states(stateSet).and();
        // 状态退出action
        /*for (Integer s : states) {
            configurer.state(s,
                    context -> {
                        Integer stateId = context.getSource().getId();
                        Integer id = context.getTarget().getId();
                        System.out.println(id + " into self --->");
                    },
                    context -> {
                        Integer stateId = context.getSource().getId();
                        System.out.println(stateId + " exit --->");
                    }).and();
        }*/

        // 2.配置 迁移
        // 2.1 开始状态之后直接进入第一个执行节点
        builder.configureTransitions().withExternal().source(0).target(1).and();
        for (int i = 0; i < states.length; i++) {
            // 当前节点
            int curState = states[i];
            if (i != states.length - 1) {
                // 下一个节点
                int nextState = states[i + 1];
                builder.configureTransitions()
                        .withExternal().source(curState).target(nextState).event("NEXT").and();
            } else {
                builder.configureTransitions()
                        .withExternal().source(curState).target(DONE_STATE).event("NEXT").and();
            }
            builder.configureTransitions()
                    .withExternal().source(curState).target(DONE_STATE).event("PASS").and()
                    .withExternal().source(curState).target(DONE_STATE).event("FORWARD_MAN").and()
                    .withExternal().source(curState).target(DONE_STATE).event("REJECT").and()
                    .withInternal()
                    .source(curState)
                    .action(context -> {
                        Integer stateId = context.getSource().getId();
                        StateMachine<Integer, String> stateMachine = context.getStateMachine();

                        System.out.println(stateId + " into Internal timerAction --->" + Thread.currentThread().getName());
                        int r = new Random().nextInt(10);
                        int n = r;
                        if (n >= 0 && n <= 5) {
                            System.out.println(stateId + " into event NEXT");
                            stateMachine.sendEvent("NEXT");
//                        } else if (n == 6) {
//                            System.out.println(stateId + " into event PASS");
//                            stateMachine.sendEvent("PASS");
//                        } else if (n == 7) {
//                            System.out.println(stateId + " into event FORWARD_MAN");
//                            stateMachine.sendEvent("FORWARD_MAN");
//                        } else if (n == 8) {
//                            System.out.println(stateId + " into event REJECT");
//                            stateMachine.sendEvent("REJECT");
//                        } else {
                            // throw new RuntimeException(" 重新执行 ");
                        }

                        System.out.println(stateId + " ---> into Internal timerAction out");

                    })
                    .timer(TimeUnit.SECONDS.toMillis(1))
                    .and();

        }


        return builder.build();
    }

    @Bean
    public Action<Integer, String> action1() {
        return context -> {
            Integer stateId = context.getSource().getId();
            StateMachine<Integer, String> stateMachine = context.getStateMachine();

            System.out.println(stateId + " into Internal timerAction --->");
            int r = new Random().nextInt(10);
            int n = r % 4;
            switch (n) {
                case 0:
                    System.out.println(stateId + " into event NEXT");
                    stateMachine.sendEvent("NEXT");
                    break;
                case 1:
                    System.out.println(stateId + " into event PASS");
                    stateMachine.sendEvent("PASS");
                    break;
                case 2:
                    System.out.println(stateId + " into event FORWARD_MAN");
                    stateMachine.sendEvent("FORWARD_MAN");
                    break;
                case 3:
                    System.out.println(stateId + " into event REJECT");
                    stateMachine.sendEvent("REJECT");
                    break;
                default:
                    throw new RuntimeException(" 重新执行 ");
            }

        };
    }
}

