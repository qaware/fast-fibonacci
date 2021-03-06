package hands.on.fibonacci;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public enum Algorithm {
    RECURSIVE {
        @Override
        public BigInteger calculate(int n) {
            return recursiveFibonacci(n);
        }

        @Override
        public String toString() {
            return "Textbook recursive (extremely slow)";
        }
    },
    CACHED {
        @Override
        public BigInteger calculate(int n) {
            return fibonacciRecursiveCached(new BigInteger[n+1], n);
        }

        @Override
        public String toString() {
            return "Cached recursive";
        }
    },
    DYNAMIC {
        @Override
        public BigInteger calculate(int n) {
            return dynamicFibonacci(n);
        }

        @Override
        public String toString() {
            return "Dynamic programming";
        }
    },
    BINET1 {
        private final BigDecimal PHI = BigDecimal.valueOf((1 + Math.sqrt(5)) / 2);
        private final BigDecimal PSI = BigDecimal.valueOf((1 - Math.sqrt(5)) / 2);
        private final BigDecimal SQRT5 = BigDecimal.valueOf(Math.sqrt(5));

        @Override
        public BigInteger calculate(int n) {
            return PHI.pow(n).subtract(PSI.pow(n)).divide(SQRT5, RoundingMode.HALF_UP).toBigInteger();
        }
        
        @Override
        public String toString() {
            return "binet closed formula";
        }
    },
    MATRIX {
        @Override
        public BigInteger calculate(int n) {
            return fastFibonacciMatrix(n);
        }

        @Override
        public String toString() {
            return "Matrix exponentiation";
        }
    },
    DOUBLING {
        @Override
        public BigInteger calculate(int n) {
            return fastFibonacciDoubling(n);
        }

        @Override
        public String toString() {
            return "Fast doubling";
        }
    };

    public abstract BigInteger calculate(int n);

    private static BigInteger recursiveFibonacci(int n) {
        if (n == 0) {
            return BigInteger.ZERO;
        } else if (n == 1) {
            return BigInteger.ONE;
        } else {
            return recursiveFibonacci(n - 1).add(recursiveFibonacci(n - 2));
        }
    }

    private static BigInteger fibonacciRecursiveCached(BigInteger[] cache, int n) {
        if (n == 0) {
            return BigInteger.ZERO;
        } else if (n == 1) {
            return BigInteger.ONE;
        }

        if (cache[n] == null) {
            cache[n] = fibonacciRecursiveCached(cache, n-1).add(fibonacciRecursiveCached(cache, n-2));
        } 

        return cache[n];
    }

    /*
     * Simple slow method, using dynamic programming.
     * F(n+2) = F(n+1) + F(n).
     */
    private static BigInteger dynamicFibonacci(int n) {
        BigInteger a = BigInteger.ZERO;
        BigInteger b = BigInteger.ONE;

        for (int i = 0; i < n; i++) {
            BigInteger c = a.add(b);
            a = b;
            b = c;
        }
        return a;
    }

    /*
     * Fast doubling method. Faster than the matrix method.
     * F(2n) = F(n) * (2*F(n+1) - F(n)).
     * F(2n+1) = F(n+1)^2 + F(n)^2.
     * This implementation is the non-recursive version. See the web page and
     * the other programming language implementations for the recursive version.
     */
    private static BigInteger fastFibonacciDoubling(int n) {
        BigInteger a = BigInteger.ZERO;
        BigInteger b = BigInteger.ONE;
        int m = 0;
        for (int bit = Integer.highestOneBit(n); bit != 0; bit >>>= 1) {
            // Double it
            BigInteger d = multiply(a, b.shiftLeft(1).subtract(a));
            BigInteger e = multiply(a, a).add(multiply(b, b));
            a = d;
            b = e;
            m *= 2;

            // Advance by one conditionally
            if ((n & bit) != 0) {
                BigInteger c = a.add(b);
                a = b;
                b = c;
                m++;
            }
        }
        return a;
    }


    /*
     * Fast matrix method. Easy to describe, but has a constant factor slowdown compared to doubling method.
     * [1 1]^n   [F(n+1) F(n)  ]
     * [1 0]   = [F(n)   F(n-1)].
     */
    private static BigInteger fastFibonacciMatrix(int n) {
        BigInteger[] matrix = {BigInteger.ONE, BigInteger.ONE, BigInteger.ONE, BigInteger.ZERO};
        return matrixPow(matrix, n)[1];
    }

    // Computes the power of a matrix. The matrix is packed in row-major order.
    private static BigInteger[] matrixPow(BigInteger[] matrix, int n) {
        if (n < 0)
            throw new IllegalArgumentException();
        BigInteger[] result = {BigInteger.ONE, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ONE};
        while (n != 0) {  // Exponentiation by squaring
            if (n % 2 != 0)
                result = matrixMultiply(result, matrix);
            n /= 2;
            matrix = matrixMultiply(matrix, matrix);
        }
        return result;
    }

    // Multiplies two matrices.
    private static BigInteger[] matrixMultiply(BigInteger[] x, BigInteger[] y) {
        return new BigInteger[]{
                multiply(x[0], y[0]).add(multiply(x[1], y[2])),
                multiply(x[0], y[1]).add(multiply(x[1], y[3])),
                multiply(x[2], y[0]).add(multiply(x[3], y[2])),
                multiply(x[2], y[1]).add(multiply(x[3], y[3]))
        };
    }

    private static BigInteger multiply(BigInteger x, BigInteger y) {
        return x.multiply(y);
    }
}
