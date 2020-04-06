package com.devebot.service;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by myasus on 4/6/20.
 */
public class ShutdownHookListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try{
            FibonacciService fibonacciService = FibonacciService.getInstance();
            fibonacciService.close();
        }catch (Exception e){
            System.out.println("============== FibonacciService.commander close ERROR: " + e.getMessage());
        }
    }
}
