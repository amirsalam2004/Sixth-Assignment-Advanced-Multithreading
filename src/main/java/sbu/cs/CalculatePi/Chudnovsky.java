package sbu.cs.CalculatePi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Chudnovsky {
    //The calculation is done with the Chudnovsky series

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
            BigDecimal numerator = factorial(6 * n)
                    .multiply(new BigDecimal(545140134).multiply(new BigDecimal(n)).add(new BigDecimal(13591409)));
            if (n % 2 == 1) {
                numerator = numerator.negate();
            }
            BigDecimal exponent = new BigDecimal(3).multiply(new BigDecimal(n)).add(new BigDecimal("1.5"));
            BigDecimal base = new BigDecimal("640320");
            BigDecimal basePow = base.pow(exponent.intValue(), mc);
            BigDecimal fracPart = exponent.subtract(new BigDecimal(exponent.intValue()));
            if (fracPart.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal logBase = new BigDecimal(Math.log(base.doubleValue()), mc);
                BigDecimal logResult = logBase.multiply(fracPart, mc);
                basePow = basePow.multiply(new BigDecimal(Math.exp(logResult.doubleValue()), mc), mc);
            }
            BigDecimal denominator = factorial(3 * n).multiply(factorial(n).pow(3)).multiply(basePow, mc);
            x = numerator.divide(denominator, mc);
            addToSum(x);
        }
    }

    public static final MathContext mc = new MathContext(1020, RoundingMode.HALF_UP);
    public static BigDecimal sum = BigDecimal.ZERO;

    public static synchronized void addToSum(BigDecimal value) {
        sum = sum.add(value, mc);
    }

    public String calculate(int floatingPoint) {
        sum = BigDecimal.ZERO;
        ExecutorService threadPool = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 500; i++) {
            PiCalculator.CalculatePi calculatePi = new PiCalculator.CalculatePi(i);
            threadPool.execute(calculatePi);
        }
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        BigDecimal result = sum.multiply(new BigDecimal(12), mc);
        BigDecimal pi = new BigDecimal(1).divide(result, mc);
        return pi.toPlainString().substring(0, floatingPoint + 2);
    }
}
