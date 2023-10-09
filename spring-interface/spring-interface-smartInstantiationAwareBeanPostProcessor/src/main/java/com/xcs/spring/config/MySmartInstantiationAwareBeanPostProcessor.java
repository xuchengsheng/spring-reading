package com.xcs.spring.config;

import com.xcs.spring.annotation.MyAutowired;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xcs
 * @date 2023年09月19日 16时42分
 **/
public class MySmartInstantiationAwareBeanPostProcessor implements SmartInstantiationAwareBeanPostProcessor {

    @Override
    public Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName) throws BeansException {
        // 首先，查找@MyAutowired带注释的构造函数
        List<Constructor<?>> myAutowiredConstructors = Arrays.stream(beanClass.getConstructors())
                .filter(constructor -> constructor.isAnnotationPresent(MyAutowired.class))
                .collect(Collectors.toList());

        if (!myAutowiredConstructors.isEmpty()) {
            return myAutowiredConstructors.toArray(new Constructor<?>[0]);
        }

        // 其次，检查默认构造函数
        try {
            Constructor<?> defaultConstructor = beanClass.getDeclaredConstructor();
            return new Constructor<?>[]{defaultConstructor};
        } catch (NoSuchMethodException e) {
            // 找不到默认构造函数，请继续选择合适的构造函数
        }

        // 返回所有构造函数，让Spring将选择最具体的构造函数
        return beanClass.getConstructors();
    }
}
