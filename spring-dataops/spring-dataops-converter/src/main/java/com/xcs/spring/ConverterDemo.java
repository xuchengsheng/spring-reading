package com.xcs.spring;

import com.xcs.spring.converter.StringToBooleanConverter;
import com.xcs.spring.converter.StringToLocalDateConverter;
import org.springframework.core.convert.support.DefaultConversionService;

import java.time.LocalDate;

/**
 * @author xcs
 * @date 2023年12月07日 16时53分
 **/
public class ConverterDemo {

    public static void main(String[] args) {
        // 创建一个默认的转换服务
        DefaultConversionService service = new DefaultConversionService();

        // 向服务中添加自定义的转换器
        service.addConverter(new StringToLocalDateConverter());
        service.addConverter(new StringToBooleanConverter());

        // 检查是否可以将字符串转换为 LocalDate
        if (service.canConvert(String.class, LocalDate.class)) {
            LocalDate localDate = service.convert("2023-12-07", LocalDate.class);
            System.out.println("LocalDate = " + localDate);
        }

        // 检查是否可以将字符串 "yes" 转换为 Boolean
        if (service.canConvert(String.class, Boolean.class)) {
            Boolean boolValue = service.convert("yes", Boolean.class);
            System.out.println("yes = " + boolValue);
        }

        // 检查是否可以将字符串 "no" 转换为 Boolean
        if (service.canConvert(String.class, Boolean.class)) {
            Boolean boolValue = service.convert("no", Boolean.class);
            System.out.println("no = " + boolValue);
        }
    }
}
