package com.xcs.spring;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.*;

public class BeanWrapperDemo {

    public static void main(String[] args) {
        // ok, let's create the director and tie it to the company:
        BeanWrapper employee = new BeanWrapperImpl(new Employee());
        employee.setPropertyValue("name", "Jim Stravinsky");
        employee.setPropertyValue("salary", "100");

        BeanWrapper company = new BeanWrapperImpl(new Company());
        company.setPropertyValue("name", "Some Company Inc.");
        company.setPropertyValue("managingDirector", employee.getWrappedInstance());

        // retrieving the salary of the managingDirector through the company
        Float salary = (Float) company.getPropertyValue("managingDirector.salary");

        System.out.println("salary = " + salary);

        System.out.println("company.getPropertyDescriptors() = " + company.getPropertyDescriptors());

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(Employee.class);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            System.out.println("beanInfo.getBeanDescriptor() = " + propertyDescriptors);
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
    }
}
