package by.bsu.dependency.usage;

import by.bsu.dependency.context.ApplicationContext;
import by.bsu.dependency.context.AutoScanApplicationContext;
import by.bsu.dependency.context.AbstractApplicationContext;

import by.bsu.dependency.context.AutoScanApplicationContext;
import by.bsu.dependency.example.FirstBean;
import by.bsu.dependency.example.OtherBean;
import by.bsu.dependency.example.PrototypeBean;

public class Main {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new AutoScanApplicationContext("by.bsu.dependency.example");
        applicationContext.start();

        // Singleton Bean Injection
        FirstBean firstBean = applicationContext.getBean(FirstBean.class);
        OtherBean otherBean = applicationContext.getBean(OtherBean.class);

        System.out.println("OtherBean's FirstBean: " + (otherBean.getFirstBean() == firstBean));

        // Prototype Bean Creation
        PrototypeBean prototypeBean1 = applicationContext.getBean(PrototypeBean.class);
        PrototypeBean prototypeBean2 = applicationContext.getBean(PrototypeBean.class);

        System.out.println("PrototypeBean instances are different: " + (prototypeBean1 != prototypeBean2));
    }
}
