package org.example;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.List;

public class AreaChecker {

    // Допустимые значения для валидации
    private static final List<Integer> VALID_X_VALUES = Arrays.asList(-3, -2, -1, 0, 1, 2, 3, 4, 5);
    private static final List<BigDecimal> VALID_R_VALUES = Arrays.asList(
            new BigDecimal("1.0"), new BigDecimal("1.5"),
            new BigDecimal("2.0"), new BigDecimal("2.5"),
            new BigDecimal("3.0")
    );

    public static boolean validatePoint(String xStr, String yStr, String rStr) {
        try {
            // Валидация X (целые числа от -3 до 5)
            int xInt;
            try {
                xInt = Integer.parseInt(xStr);
            } catch (NumberFormatException e) {
                return false;
            }
            if (!VALID_X_VALUES.contains(xInt)) {
                return false;
            }

            // Валидация Y (от -3 до 3)
            BigDecimal y = new BigDecimal(yStr.replace(',', '.'));
            if (y.compareTo(new BigDecimal("-3")) < 0 || y.compareTo(new BigDecimal("3")) > 0) {
                return false;
            }

            // Валидация R (1, 1.5, 2, 2.5, 3)
            BigDecimal r = new BigDecimal(rStr.replace(',', '.'));
            boolean rValid = false;
            for (BigDecimal validR : VALID_R_VALUES) {
                if (r.compareTo(validR) == 0) {
                    rValid = true;
                    break;
                }
            }
            if (!rValid) {
                return false;
            }

            return true;

        } catch (Exception e) {
            System.err.println("Validation error: " + e.getMessage());
            return false;
        }
    }

    public static boolean checkHit(String xStr, String yStr, String rStr) {
        try {
            int x = Integer.parseInt(xStr);
            BigDecimal y = new BigDecimal(yStr.replace(',', '.'));
            BigDecimal r = new BigDecimal(rStr.replace(',', '.'));

            BigDecimal halfR = r.divide(new BigDecimal("2"), MathContext.DECIMAL128);

            // 1 четверть: четверть круга радиусом R/2
            if (x >= 0 && y.compareTo(BigDecimal.ZERO) >= 0) {
                BigDecimal xDecimal = new BigDecimal(x);
                BigDecimal distanceSquared = xDecimal.pow(2).add(y.pow(2));
                BigDecimal radiusSquared = halfR.pow(2);
                return distanceSquared.compareTo(radiusSquared) <= 0;
            }

            // 2 четверть: пусто - всегда false
            if (x < 0 && y.compareTo(BigDecimal.ZERO) > 0) {
                return false;
            }

            // 3 четверть: прямоугольник (-R/2, -R) - (0, 0)
            if (x <= 0 && y.compareTo(BigDecimal.ZERO) <= 0) {
                BigDecimal xDecimal = new BigDecimal(x);
                boolean inX = xDecimal.compareTo(halfR.negate()) >= 0 && xDecimal.compareTo(BigDecimal.ZERO) <= 0;
                boolean inY = y.compareTo(r.negate()) >= 0 && y.compareTo(BigDecimal.ZERO) <= 0;
                return inX && inY;
            }

            // 4 четверть: треугольник (0, -R/2) - (R/2, 0)
            if (x > 0 && y.compareTo(BigDecimal.ZERO) <= 0) {
                BigDecimal xDecimal = new BigDecimal(x);
                // Уравнение линии: y = -x - R/2
                // Но так как y отрицательный, проверяем y >= -x - R/2
                BigDecimal lineValue = xDecimal.negate().subtract(halfR);
                return y.compareTo(lineValue) >= 0 &&
                        xDecimal.compareTo(halfR) <= 0 &&
                        y.compareTo(halfR.negate()) >= 0;
            }

            return false;

        } catch (Exception e) {
            System.err.println("Check hit error: " + e.getMessage());
            return false;
        }
    }
}
