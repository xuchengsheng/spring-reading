package com.xcs.spring;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * @author xcs
 * @date 2023年12月04日 14时13分
 **/
public class PersonValidator implements Validator {

    @Override
    public boolean supports(Class clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors e) {
        // 检查名称是否为空
        ValidationUtils.rejectIfEmpty(e, "name", "name.empty", "姓名不能为空");

        // 将对象转换为 Person 类型
        Person p = (Person) obj;

        // 检查年龄是否为负数
        if (p.getAge() < 0) {
            e.rejectValue("age", "negative.value", "年龄不能为负数");
        }
        // 检查年龄是否超过 120 岁
        else if (p.getAge() > 120) {
            e.rejectValue("age", "too.darn.old", "目前年龄最大的是120岁");
        }
    }
}
