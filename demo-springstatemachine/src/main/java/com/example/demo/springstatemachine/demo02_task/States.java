package com.example.demo.springstatemachine.demo02_task;

/**
 * @author chenxinsui
 * @date 2020/11/30 9:43
 * @description:
 **/
public enum States {
    READY,
    FORK, JOIN, CHOICE,
    TASKS, T1, T1E, T2, T2E, T3, T3E,
    ERROR, AUTOMATIC, MANUAL

}
