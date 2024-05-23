package sbu.cs.CalculatePi;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PiCalculator {
    //The calculation is done with the Ramanujan's series

    public static class CalculatePi implements Runnable {
        BigDecimal x;
        int n;

        public CalculatePi(int n) {
            this.x = BigDecimal.ZERO;
            this.n = n;
        }

        private BigDecimal factorial(int n) {
            BigDecimal result = BigDecimal.ONE;
            for (int i = 2; i <= n; i++) {
                result = result.multiply(new BigDecimal(i), mc);
            }
            return result;
        }

        @Override
        public void run() {
            BigDecimal nValue=new BigDecimal(n);
            BigDecimal numerator=factorial(4*n).multiply(new BigDecimal(1103).add(new BigDecimal(26390).multiply(nValue),mc),mc);
            BigDecimal  denominator=factorial(n).pow(4).multiply(new BigDecimal(396).pow(4*n),mc);
            x=numerator.divide(denominator,mc);
            addToSum(x);
        }
    }

    public static final MathContext mc = new MathContext(1020, RoundingMode.HALF_UP);
    public static BigDecimal sum = BigDecimal.ZERO;

    public static synchronized void addToSum(BigDecimal value) {
        sum = sum.add(value, mc);
    }

    public String calculate(int floatingPoint) {
        BigDecimal C = new BigDecimal(2).multiply(new BigDecimal(2).sqrt(mc),mc).divide(new BigDecimal(9801),mc);
        ExecutorService threadPool = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 126; i++) {
            CalculatePi calculatePi = new CalculatePi(i);
            threadPool.execute(calculatePi);
        }
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sum = sum.multiply(C, mc);
        String pi = BigDecimal.ONE.divide(sum,mc).toPlainString();
        return pi.substring(0, floatingPoint + 2);
    }

    public static void main(String[] args) {
        PiCalculator piCalculator = new PiCalculator();
        System.out.println(piCalculator.calculate(10));
    }
}

