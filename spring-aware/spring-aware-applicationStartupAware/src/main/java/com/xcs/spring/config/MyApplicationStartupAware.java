package com.xcs.spring.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationStartupAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.core.metrics.StartupStep;

public class MyApplicationStartupAware implements ApplicationStartupAware, InitializingBean {

    private ApplicationStartup applicationStartup;

    @Override
    public void setApplicationStartup(ApplicationStartup applicationStartup) {
        this.applicationStartup = applicationStartup;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        StartupStep step1 = applicationStartup.start("MyApplicationStartupAware Logic Step 1");
        // 自定义逻辑
        Thread.sleep(200);
        step1.tag("status", "done").end();

        StartupStep step2 = applicationStartup.start("MyApplicationStartupAware Logic Step 2");
        // 自定义逻辑
        Thread.sleep(300);
        step2.tag("status", "done").end();
    }
}
