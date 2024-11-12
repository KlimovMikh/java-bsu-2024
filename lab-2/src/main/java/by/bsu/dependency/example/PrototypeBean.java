package by.bsu.dependency.example;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;

@Bean(name = "prototypeBean", scope = BeanScope.PROTOTYPE)
public class PrototypeBean {
    public PrototypeBean() {
        System.out.println("PrototypeBean is created");
    }
    @Inject
    private FirstBean firstBean;

    void doSomething() {
        System.out.println("Hi, I'm Prototype bean");
    }

    void doSomethingWithFirst() {
        System.out.println("Trying to shake first bean...");
        firstBean.doSomething();
    }

    @PostConstruct
    void postConstruct() {
        System.out.println("PostConstruct bean running on Prototype, dependency on firstBean");
        firstBean.printSomething();
    }
}
