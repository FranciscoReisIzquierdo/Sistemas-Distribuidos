package com.company;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Warehouse {
    private Map<String, Product> map =  new HashMap<String, Product>();
    Lock lock= new ReentrantLock();
    Condition warehouse=  lock.newCondition();

    private class Product {
        Condition c=  lock.newCondition();
        int quantity = 0;
    }

    private Product get(String item) {
        Product p = map.get(item);
        if(p!= null) return p;

        p = new Product();
        map.put(item, p);
        return p;
    }

    public void supply(String item, int quantity) {
        try {
            lock.lock();
            Product product = get(item);
            product.quantity += quantity;
            //Must signal the others threads
            product.c.signalAll();
        }
        finally{ lock.unlock(); }
    }

    public boolean isMissing(Set<String> items){
        for(String item: items) if(map.get(item).quantity== 0) return false;
        return true;
    }

    // Errado se faltar algum produto...
    public void consume(Set<String> items) throws InterruptedException {
        lock.lock();
        int i= 0;
        while(i< items.size()){
            String[] array= (String []) items.toArray();
            Product p= map.get(array[i]);
            while(p.quantity== 0){
                i= 0;
                p.c.await();
            }
            i++;
        }
        try {
            for (String s : items){
                Product product= map.get(s);
                get(s).quantity--;
            }
        } finally{ lock.unlock(); }
    }
}

public class Main {

    public static void main(String[] args) {
	// write your code here
    }
}
