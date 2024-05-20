package sbu.cs.CalculatePi;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PiCalculator {

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

        private BigDecimal bigDecimalPow(BigDecimal base, BigDecimal exponent) {
            BigDecimal logBase = new BigDecimal(Math.log(base.doubleValue()), mc);
            BigDecimal logResult = logBase.multiply(exponent, mc);
            return new BigDecimal(Math.exp(logResult.doubleValue()), mc);
        }

        @Override
        public void run() {
            BigDecimal numerator = factorial(6 * n)
                    .multiply(new BigDecimal(545140134, mc)
                            .multiply(new BigDecimal(n), mc)
                            .add(new BigDecimal(13591409, mc)));
            if (n % 2 == 1) {
                numerator = numerator.negate();
            }
            BigDecimal exponent = new BigDecimal(3).multiply(new BigDecimal(n)).add(new BigDecimal(1.5));
            BigDecimal basePow = bigDecimalPow(new BigDecimal("640320"), exponent);
            BigDecimal denominator = factorial(3 * n).multiply(factorial(n).pow(3).multiply(basePow, mc));
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
        BigDecimal C = new BigDecimal("426880").multiply(new BigDecimal("10005").sqrt(mc));
        ExecutorService threadPool = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 100; i++) {
            CalculatePi calculatePi = new CalculatePi(i);
            threadPool.execute(calculatePi);
        }
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sum = sum.multiply(new BigDecimal(12), mc);
        String pi = C.divide(sum, mc).toPlainString();
        return pi.substring(0, floatingPoint + 2);
    }

    public static void main(String[] args) {
        PiCalculator piCalculator = new PiCalculator();
        System.out.println(piCalculator.calculate(10));
    }
}
