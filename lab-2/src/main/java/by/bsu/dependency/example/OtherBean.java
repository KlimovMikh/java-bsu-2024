package by.bsu.dependency.example;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;

@Bean(name = "otherBean")
public class OtherBean {

    @SuppressWarnings("unused")
    @Inject
    private FirstBean firstBean;

    public FirstBean getFirstBean() {
        return firstBean;
    }

    void doSomething() {
        System.out.println("Hi, I'm other bean");
    }

    public void doSomethingWithFirst() {
        System.out.println("Trying to shake first bean...");
        firstBean.doSomething();
    }

    @SuppressWarnings("unused")
    @PostConstruct
    void postConstruct() {
        System.out.println("PostConstruct bean running, dependency on firstBean");
        firstBean.printSomething();
    }
}
