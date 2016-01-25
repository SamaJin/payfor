package com.helphand.ccdms.thread;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MyListener implements ServletContextListener{
    private Timer timer = null;
                     
    //时间间隔
    private static final long PERSIOD_DAY = 24 * 60 * 60 * 1000;
                     
    public void contextDestroyed(ServletContextEvent sce) {
        timer.cancel();
    }
    public void contextInitialized(ServletContextEvent sce) {
        timer = new Timer(true);
        MyTask myTask = new MyTask();
        MyNewTask myNewTask=new MyNewTask();
        //7.定制每日夜里2:00执行方法
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 02);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 0);
        //第一次执行定时任务的时间
        Date date = calendar.getTime();
        if (date.before(new Date())) {
            date = this.addDate(date, 1);
        }
        timer.schedule(myNewTask, date,PERSIOD_DAY);
        timer.schedule(myTask, date,PERSIOD_DAY);
        //timer.schedule(myTask, 1,100000000);
        //timer.schedule(myNewTask, 1,100000000);
    }
   
    
    public Date addDate(Date date,int num) {
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(date);
        startDate.add(Calendar.DAY_OF_MONTH, num);
        return startDate.getTime();
    }
                     
}