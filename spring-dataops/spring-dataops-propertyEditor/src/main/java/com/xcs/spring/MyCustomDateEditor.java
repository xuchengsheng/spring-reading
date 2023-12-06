package com.xcs.spring;

import org.springframework.beans.propertyeditors.CustomDateEditor;

import java.text.SimpleDateFormat;

public class MyCustomDateEditor extends CustomDateEditor {

    public MyCustomDateEditor(){
        super(new SimpleDateFormat("yyyy-MM-DD"),false);
    }
}
