package org.kcsup.gramersrankupcorev2.util;

public class Pair<A, B> {
    
    public A object1;
    public B object2;
    
    public Pair(A object1, B object2) {
        this.object1 = object1;
        this.object2 = object2;
    }
    
    public A getFirst() { return object1; }
    public B getSecond() { return object2; }
}
