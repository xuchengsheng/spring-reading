package com.xcs.spring.convert;

import com.xcs.spring.annotation.DateFormat;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

public class AnnotatedStringToDateConverter implements GenericConverter {

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        // 定义可转换的类型对：从 String 到 Date
        return Collections.singleton(new ConvertiblePair(String.class, Date.class));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        // 如果源对象为空，直接返回 null
        if (source == null) {
            return null;
        }

        // 将源对象转换为字符串
        String dateString = (String) source;
        // 获取目标类型（Date类型字段）上的 DateFormat 注解
        DateFormat dateFormatAnnotation = targetType.getAnnotation(DateFormat.class);
        // 如果目标字段上没有 DateFormat 注解，则抛出异常
        if (dateFormatAnnotation == null) {
            throw new IllegalArgumentException("目标字段上缺少DateFormat注解");
        }

        try {
            // 根据注解中提供的日期格式创建 SimpleDateFormat
            SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatAnnotation.value());
            // 使用 SimpleDateFormat 将字符串解析为日期对象
            return dateFormat.parse(dateString);
        } catch (Exception e) {
            // 如果解析失败，抛出异常
            throw new IllegalArgumentException("无法解析日期", e);
        }
    }
}
