package org.rankun.learn.spring.models;

/**
 * Created with IntelliJ IDEA.
 * User: 坤
 * Date: 13-10-26
 * Time: 下午3:52
 */
public class Greeting {
    private final long id;
    private final String greeting;

    public Greeting(long id, String greeting) {
        this.id = id;
        this.greeting = greeting;
    }

    public long getId() {
        return id;
    }

    public String getGreeting() {
        return greeting;
    }
}
