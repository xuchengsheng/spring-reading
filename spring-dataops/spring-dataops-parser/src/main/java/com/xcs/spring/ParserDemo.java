package com.xcs.spring;

import com.xcs.spring.parser.CurrencyParser;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.support.FormattingConversionService;

import java.util.Locale;

public class ParserDemo {

    public static void main(String[] args) {
        // 创建一个格式化转换服务
        FormattingConversionService conversionService = new FormattingConversionService();
        // 向转换服务中添加一个货币解析器
        conversionService.addParser(new CurrencyParser());

        // 设置当前线程的区域设置为美国
        LocaleContextHolder.setLocale(Locale.US);
        // 将美元格式的字符串转换为数值类型
        Number formattedAmountForUS = conversionService.convert("$1,234.56", Number.class);
        System.out.println("Parsed Currency (US): " + formattedAmountForUS);

        // 改变区域设置为中国
        LocaleContextHolder.setLocale(Locale.CHINA);
        // 将人民币格式的字符串转换为数值类型
        Number formattedAmountForCHINA = conversionService.convert("¥1,234.56", Number.class);
        System.out.println("Parsed Currency (CHINA): " + formattedAmountForCHINA);
    }
}
