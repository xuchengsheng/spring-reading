package com.xcs.spring;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;

/**
 * @author xcs
 * @date 2023年12月04日 14时06分
 **/
public class ValidatorDemo {

    public static void main(String[] args) {
        // 创建一个 Person 对象实例
        Person person = new Person();
        person.setName(null);
        person.setAge(130);

        // 创建一个 BeanPropertyBindingResult 对象，用于存储验证过程中的错误
        BeanPropertyBindingResult result = new BeanPropertyBindingResult(person, "person");

        // 创建一个 PersonValidator 实例，这是自定义的验证器
        PersonValidator validator = new PersonValidator();

        // 检查 PersonValidator 是否支持 Person 类的验证
        if (validator.supports(person.getClass())) {
            // 执行验证逻辑
            validator.validate(person, result);
        }

        // 检查是否存在验证错误，并打印出所有的字段错误
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                System.out.println(error.getField() + ":" + error.getDefaultMessage());
            }
        }
    }
}
