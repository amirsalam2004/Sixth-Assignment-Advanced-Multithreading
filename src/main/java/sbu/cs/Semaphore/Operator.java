package sbu.cs.Semaphore;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Semaphore;

public class Operator extends Thread {
    Semaphore sem;
    public Operator(String name, Semaphore semaphore) {
        super(name);
        this.sem=semaphore;
    }

    @Override
    public void run() {
        try{
            sem.acquire();
            LocalTime now = LocalTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            System.out.println(currentThread().getName()+"  Time:"+now.format(formatter));
        for (int i = 0; i < 10; i++)
        {
            Resource.accessResource();         // critical section - a Maximum of 2 operators can access the resource concurrently
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        }catch (Exception e){

        }finally {
            sem.release();
        }
    }
}
