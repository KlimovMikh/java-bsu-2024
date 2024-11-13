package by.bsu.dependency.usage;

import by.bsu.dependency.context.*;

import by.bsu.dependency.context.AutoScanApplicationContext;
import by.bsu.dependency.example.FirstBean;
import by.bsu.dependency.example.OtherBean;
import by.bsu.dependency.example.PrototypeBean;
import by.bsu.dependency.example.TestBean;

public class Main {
    static void AutoScannerUse() {
        ApplicationContext applicationContext = new AutoScanApplicationContext("by.bsu.dependency.example");
        System.out.println('\n' + "Context initialized and not running: " + applicationContext.isRunning() + '\n');
        applicationContext.start();
        System.out.println('\n' + "Context initialized and started: " + applicationContext.isRunning() + '\n');

        FirstBean firstBean = applicationContext.getBean(FirstBean.class);
        OtherBean otherBean = applicationContext.getBean(OtherBean.class);

        System.out.println("OtherBean's FirstBean: " + (otherBean.getFirstBean() == firstBean));

        PrototypeBean prototypeBean1 = applicationContext.getBean(PrototypeBean.class);
        PrototypeBean prototypeBean2 = applicationContext.getBean(PrototypeBean.class);

        System.out.println("PrototypeBean instances are different: " + (prototypeBean1 != prototypeBean2));
    }

    static void SimpleUse() {
        ApplicationContext applicationContext = new SimpleApplicationContext(FirstBean.class, OtherBean.class,
                PrototypeBean.class, TestBean.class);

        System.out.println('\n' + "Context initialized and not running: " + applicationContext.isRunning() + '\n');
        applicationContext.start();
        System.out.println('\n' + "Context initialized and started: " + applicationContext.isRunning() + '\n');

        System.out.println( "Contains FirstBean: " + applicationContext.containsBean("firstBean")  + '\n' +
                "Contains OtherBean: " + applicationContext.containsBean("otherBean")   + '\n' +
                "Contains PrototypeBean: " + applicationContext.containsBean("prototypeBean")   + '\n' +
                "Contains TestBean: " + applicationContext.containsBean("testBean"));

        FirstBean firstBean = applicationContext.getBean(FirstBean.class);
        OtherBean otherBean = applicationContext.getBean(OtherBean.class);
        TestBean testBean = applicationContext.getBean(TestBean.class);

        System.out.println("OtherBean's FirstBean: " + (otherBean.getFirstBean() == firstBean));
        System.out.println("Test Bean's Other Bean / Other Bean's First Bean: " +
                (testBean.getOtherBean() == otherBean) + " / " + (testBean.getOtherBean().getFirstBean() == firstBean));

        PrototypeBean prototypeBean1 = applicationContext.getBean(PrototypeBean.class);
        PrototypeBean prototypeBean2 = applicationContext.getBean(PrototypeBean.class);

        System.out.println("PrototypeBean instances are different: " + (prototypeBean1 != prototypeBean2));
    }

    static void GeneralUse() {
        ApplicationContext simpleContext = new SimpleApplicationContext(FirstBean.class, OtherBean.class, PrototypeBean.class, TestBean.class);
        simpleContext.start();
        ApplicationContext scannerContext = new AutoScanApplicationContext("by.bsu.dependency.example");
        scannerContext.start();
        System.out.println("Working with simpleContext...");
        System.out.println('\n' + "Check that otherBean is Singleton: " + simpleContext.isSingleton("otherBean") + '\n');
        System.out.println("Check that otherBean is Prototype: " + simpleContext.isPrototype("otherBean") + '\n');
        System.out.println("Check that prototypeBean is Singleton: " + simpleContext.isSingleton("prototypeBean") + '\n');
        System.out.println("Check that prototypeBean is Prototype: " + simpleContext.isPrototype("prototypeBean") + '\n');

        System.out.println("Working with autoScanContext...");
        System.out.println('\n' + "Check that otherBean is Singleton: " + scannerContext.isSingleton("otherBean") + '\n');
        System.out.println("Check that otherBean is Prototype: " + scannerContext.isPrototype("otherBean") + '\n');
        System.out.println("Check that prototypeBean is Singleton: " + scannerContext.isSingleton("prototypeBean") + '\n');
        System.out.println("Check that prototypeBean is Prototype: " + scannerContext.isPrototype("prototypeBean") + '\n');
    }

    // Examples for HardCodedSingletonApplicationContext are located in "example" directory

    public static void main(String[] args) {
        System.out.println("""
                
                ______________Example1: Auto Scanner Use______________
                """);
        AutoScannerUse();
        System.out.println("""
                
                ______________Example2: Simple Context Use____________
                """);
        SimpleUse();
        System.out.println("""
                
                ______________Example3: General Context Use___________
                """);
        GeneralUse();
        System.out.println("EXAMPLES DONE!");
    }
}
