package com.xcs.spring;

import com.xcs.spring.printer.CurrencyPrinter;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.support.FormattingConversionService;

import java.util.Locale;

public class PrinterDemo {

    public static void main(String[] args) {
        // 创建一个 FormattingConversionService 实例
        FormattingConversionService conversionService = new FormattingConversionService();
        // 将自定义的 CurrencyPrinter 注册到 conversionService
        conversionService.addPrinter(new CurrencyPrinter());

        // 设置货币金额
        double amount = 1234.56;

        // 设置当前线程的 Locale 为美国
        LocaleContextHolder.setLocale(Locale.US);
        // 使用 conversionService 将金额转换为字符串，并应用美国的货币格式
        String formattedAmountForUS = conversionService.convert(amount, String.class);
        System.out.println("Formatted Currency (US): " + formattedAmountForUS);

        // 设置当前线程的 Locale 为中国
        LocaleContextHolder.setLocale(Locale.CHINA);
        // 使用 conversionService 将金额转换为字符串，并应用中国的货币格式
        String formattedAmountForCHINA = conversionService.convert(amount, String.class);
        System.out.println("Formatted Currency (CHINA): " + formattedAmountForCHINA);
    }
}
