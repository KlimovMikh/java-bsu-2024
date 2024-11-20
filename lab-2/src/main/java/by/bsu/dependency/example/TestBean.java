package by.bsu.dependency.example;

import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;

public class TestBean {

    @SuppressWarnings("unused")
    @Inject
    private OtherBean otherBean;

    @SuppressWarnings("unused")
    @Inject
    private PrototypeBean prototypeBean;

    private int x = 0;

    public int getX() {
        return x;
    }

    public OtherBean getOtherBean() {
        return otherBean;
    }

    public PrototypeBean getPrototypeBean() {
        return prototypeBean;
    }

    @SuppressWarnings("unused")
    void doSomething() {
        System.out.println("Hi, I'm test bean");
    }

    public void doSomethingWithFirst() {
        System.out.println("Trying to shake other bean...");
        otherBean.doSomethingWithFirst();
    }

    public void doSomethingWithPrototype() {
        System.out.println("Trying to shake prototype bean...");
        prototypeBean.doSomething();
    }

    @SuppressWarnings("unused")
    @PostConstruct
    void postConstruct() {
        System.out.println("PostConstruct bean running, dependency on otherBean");
        otherBean.doSomethingWithFirst();
        this.x = 42;
    }
}
