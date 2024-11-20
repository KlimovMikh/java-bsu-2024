package by.bsu.dependency.cyclical;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;

@Bean(name = "cyclicalBean2")
public class CyclicalBean2 {

    @SuppressWarnings("unused")
    @Inject
    private CyclicalBean1 cyclicalBean1;

    @SuppressWarnings("unused")
    public CyclicalBean1 getCyclicalBean1() {
        return cyclicalBean1;
    }

    void doSomething() {
        System.out.println("Hi, I'm cyclicalBean2 bean");
    }

    @SuppressWarnings("unused")
    public void doSomethingWithFirst() {
        System.out.println("Trying to shake cyclical bean 1...");
        cyclicalBean1.doSomething();
    }

    @SuppressWarnings("unused")
    @PostConstruct
    void postConstruct() {
        System.out.println("PostConstruct bean running, dependency on cyclicalBean1");
        cyclicalBean1.doSomething();
    }
}
