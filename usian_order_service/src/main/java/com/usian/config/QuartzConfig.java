package com.usian.config;

import com.usian.Job.OrderQuartz;
import com.usian.factory.MyAdaptableJobFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfig {

    //job
    @Bean
    public JobDetailFactoryBean jobDetailFactoryBean(){
        JobDetailFactoryBean factory = new JobDetailFactoryBean();
        //关联我们自己的Job类
        factory.setJobClass(OrderQuartz.class);
        return factory;
    }
    //trigger
    @Bean
    public CronTriggerFactoryBean cronTriggerFactoryBean(JobDetailFactoryBean
                                                                 jobDetailFactoryBean){
        CronTriggerFactoryBean factory = new CronTriggerFactoryBean();
        factory.setJobDetail(jobDetailFactoryBean.getObject());
        //设置触发时间
        factory.setCronExpression("0/2 * * * * ?");
        return factory;
    }

    //scheduler
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(CronTriggerFactoryBean
                                                             cronTriggerFactoryBean, MyAdaptableJobFactory myAdaptableJobFactory){
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        //关联trigger
        factory.setTriggers(cronTriggerFactoryBean.getObject());
        factory.setJobFactory(myAdaptableJobFactory);
        return factory;
    }
}
