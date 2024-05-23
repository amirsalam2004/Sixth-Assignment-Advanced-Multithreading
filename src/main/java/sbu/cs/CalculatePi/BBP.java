package sbu.cs.CalculatePi;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BBP {
    //The calculation is done with the BBP series
    public static final MathContext mc = new MathContext(1005, RoundingMode.HALF_UP);
    public static BigDecimal sum = BigDecimal.ZERO;

    public static class CalculatePi implements Runnable {
        BigDecimal x;
        int n;
        BigDecimal C;

        public CalculatePi(int n) {
            this.x = BigDecimal.ZERO;
            this.n = n;
            C=new BigDecimal(1).divide(new BigDecimal(16).pow(n,mc),mc);
        }
        public static synchronized void addToSum(BigDecimal value) {
            sum = sum.add(value, mc);
        }
        @Override
        public void run(){
            BigDecimal valeu_n=new BigDecimal(n);
            BigDecimal valeu_8=new BigDecimal(8);
            BigDecimal num1=new BigDecimal(4).divide(valeu_8.multiply(valeu_n,mc).add(new BigDecimal(1),mc),mc);
            BigDecimal num2=new BigDecimal(2).divide(valeu_8.multiply(valeu_n,mc).add(new BigDecimal(4),mc),mc);
            BigDecimal num3=new BigDecimal(1).divide(valeu_8.multiply(valeu_n,mc).add(new BigDecimal(5),mc),mc);
            BigDecimal num4=new BigDecimal(1).divide(valeu_8.multiply(valeu_n,mc).add(new BigDecimal(6),mc),mc);
            BigDecimal num=num1.subtract(num2,mc).subtract(num3,mc).subtract(num4,mc);
            x=C.multiply(num,mc);
            addToSum(x);
        }
    }
    public String calculate(int floatingPoint) {
        ExecutorService threadPool = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 850; i++) {
            CalculatePi calculatePi = new CalculatePi(i);
            threadPool.execute(calculatePi);
        }
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(4, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String pi =sum.toPlainString();
        return pi.substring(0, floatingPoint + 2);
    }
}
