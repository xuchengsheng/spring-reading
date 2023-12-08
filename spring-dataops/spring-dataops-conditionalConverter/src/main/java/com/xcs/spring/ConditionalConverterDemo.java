package com.xcs.spring;

import com.xcs.spring.converter.StringToIntegerConditionalConverter;
import org.springframework.core.convert.TypeDescriptor;

/**
 * @author xcs
 * @date 2023年12月08日 11时02分
 **/
public class ConditionalConverterDemo {

    public static void main(String[] args) {
        // 创建自定义的转换器实例
        StringToIntegerConditionalConverter converter = new StringToIntegerConditionalConverter();

        // 定义源类型（String）和目标类型（Integer）的描述符
        TypeDescriptor sourceType = TypeDescriptor.valueOf(String.class);
        TypeDescriptor targetType = TypeDescriptor.valueOf(Integer.class);

        // 测试转换器是否适用于从 String 到 Integer 的转换
        if (converter.matches(sourceType, targetType)) {
            // 如果转换条件匹配，则执行转换
            Integer result = converter.convert("8");
            System.out.println("Converted result: " + result);
        } else {
            // 如果条件不匹配，打印不适用的消息
            System.out.println("Conversion not applicable.");
        }
    }
}
