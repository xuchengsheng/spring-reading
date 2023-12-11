package com.xcs.spring.parser;

import org.springframework.format.Parser;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class CurrencyParser implements Parser<Number> {
    @Override
    public Number parse(String text, Locale locale) throws ParseException {
        // 创建适用于特定区域的货币格式化器
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        // 解析字符串为数字
        return format.parse(text);
    }
}
