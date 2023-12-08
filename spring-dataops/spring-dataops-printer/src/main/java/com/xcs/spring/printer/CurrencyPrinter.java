package com.xcs.spring.printer;

import org.springframework.format.Printer;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyPrinter implements Printer<Number> {

    @Override
    public String print(Number number, Locale locale) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);
        return currencyFormat.format(number);
    }
}
