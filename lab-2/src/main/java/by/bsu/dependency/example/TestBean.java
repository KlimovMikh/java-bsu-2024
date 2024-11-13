package by.bsu.dependency.example;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;

public class TestBean {

    @Inject
    private OtherBean otherBean;

    public OtherBean getOtherBean() {
        return otherBean;
    }

    void doSomething() {
        System.out.println("Hi, I'm test bean");
    }

    void doSomethingWithFirst() {
        System.out.println("Trying to shake other bean...");
        otherBean.doSomething();
    }

    @PostConstruct
    void postConstruct() {
        System.out.println("PostConstruct bean running, dependency on otherBean");
        otherBean.doSomethingWithFirst();
    }
}
