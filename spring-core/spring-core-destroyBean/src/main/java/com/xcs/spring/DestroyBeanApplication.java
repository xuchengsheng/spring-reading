package com.xcs.spring;

import com.xcs.spring.bean.MyBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author xcs
 * @date 2023年11月22日 14时27分
 **/
public class DestroyBeanApplication {

    public static void main(String[] args) {
        // 创建一个基于注解的应用程序上下文对象
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        // 注册配置类 MyBean，告诉 Spring 在容器中管理这个配置类所定义的 bean
        context.register(MyBean.class);
        // 刷新应用程序上下文，初始化并启动 Spring 容器
        context.refresh();
        // 关闭应用程序上下文，销毁 Spring 容器并释放资源
        context.close();
    }
}
