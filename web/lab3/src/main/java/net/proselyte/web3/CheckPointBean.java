package net.proselyte.web3;

import net.proselyte.web3.db.PointService;
import net.proselyte.web3.db.models.PointModel;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

@Named("checkPointBean")
@SessionScoped
public class CheckPointBean implements Serializable {
    private static final List<BigDecimal> ALLOWED_R = Arrays.asList(
            new BigDecimal("1"), new BigDecimal("1.5"), new BigDecimal("2"),
            new BigDecimal("2.5"), new BigDecimal("3")
    );
    private static final List<BigDecimal> ALLOWED_Y = Arrays.asList(
            new BigDecimal("-3"), new BigDecimal("-2"), new BigDecimal("-1"),
            BigDecimal.ZERO,
            new BigDecimal("1"), new BigDecimal("2"), new BigDecimal("3"), new BigDecimal("4"), new BigDecimal("5")
    );
    private static final BigDecimal MIN_X = new BigDecimal("-5");
    private static final BigDecimal MAX_X = new BigDecimal("3");
    private static final BigDecimal TWO = new BigDecimal("2");

    private String xInput;
    private BigDecimal yValue;
    private BigDecimal rValue = new BigDecimal("1");

    @Inject
    private PointService pointService;

    public void selectY(String y) {
        try {
            BigDecimal parsed = new BigDecimal(y);

            yValue = parsed.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : parsed;
        } catch (NumberFormatException ignored) {
        }
    }

    public void submit() {
        BigDecimal x = parse(xInput);
        if (x == null) {
            showNotify("Ошибка", "Введите значение для X", "error");
            return;
        }
        if (x.compareTo(MIN_X) < 0 || x.compareTo(MAX_X) > 0) {
            showNotify("Ошибка", "X должен быть в диапазоне от -5 до 3", "error");
            return;
        }
        if (yValue == null || !ALLOWED_Y.contains(yValue)) {
            showNotify("Ошибка", "Выберите значение Y", "error");
            return;
        }
        if (rValue == null || ALLOWED_R.stream().noneMatch(r -> r.compareTo(rValue) == 0)) {
            showNotify("Ошибка", "Выберите корректное значение R", "error");
            return;
        }
        persist(x, yValue, rValue);
    }

    public void submitFromCanvas(BigDecimal x, BigDecimal y) {
        if (rValue == null) {
            return;
        }
        persist(x, y, rValue);
    }

    private void persist(BigDecimal x, BigDecimal y, BigDecimal r) {
        long startTime = System.nanoTime();
        boolean hit = isHit(x, y, r);
        long endTime = System.nanoTime();
        long executionTimeMicroseconds = (endTime - startTime) / 1000;
        
        PointModel point = new PointModel(x, y, r, hit, executionTimeMicroseconds);
        pointService.save(point);
    }

    public boolean isHit(BigDecimal x, BigDecimal y, BigDecimal r) {
        if (x == null || y == null || r == null || r.signum() <= 0) {
            return false;
        }

        BigDecimal zero = BigDecimal.ZERO;
        BigDecimal halfR = r.divide(TWO, 20, RoundingMode.HALF_UP);


        boolean inCircleFirst =
                x.compareTo(zero) >= 0 &&
                        y.compareTo(zero) >= 0 &&
                        x.multiply(x)
                                .add(y.multiply(y))
                                .compareTo(halfR.multiply(halfR)) <= 0;


        boolean inRectThird =
                x.compareTo(halfR.negate()) >= 0 &&
                        x.compareTo(zero) <= 0 &&
                        y.compareTo(r.negate()) >= 0 &&
                        y.compareTo(zero) <= 0;


        boolean inTriangleFourth =
                x.compareTo(zero) >= 0 &&
                        x.compareTo(r) <= 0 &&
                        y.compareTo(r.negate()) >= 0 &&
                        y.compareTo(zero) <= 0 &&
                        y.compareTo(x.subtract(r)) >= 0;

        return inCircleFirst || inRectThird || inTriangleFourth;
    }


    private BigDecimal parse(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        try {
            String trimmed = raw.trim();
            // Заменяем запятую на точку для обработки дробных чисел с запятой
            trimmed = trimmed.replace(',', '.');
            BigDecimal result = new BigDecimal(trimmed);
            // Для нуля возвращаем BigDecimal.ZERO, чтобы избежать 0E-20
            if (result.compareTo(BigDecimal.ZERO) == 0) {
                return BigDecimal.ZERO;
            }
            return result;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public void clearHistory() {
        pointService.deleteAll();
        showNotify("Успешно", "История проверок очищена", "success");
    }

    private void showNotify(String summary, String detail, String severity) {
        String jsCode = String.format(
            "if (typeof showNotify === 'function') { showNotify('%s', '%s', '%s', 4000); }",
            escapeJavaScript(summary), escapeJavaScript(detail), severity
        );
        PrimeFaces.current().executeScript(jsCode);
    }
    
    private String escapeJavaScript(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("'", "\\'")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r");
    }

    public String getxInput() {
        return xInput;
    }

    public void setxInput(String xInput) {
        this.xInput = xInput;
    }

    public BigDecimal getyValue() {
        return yValue;
    }

    public void setyValue(BigDecimal yValue) {
        this.yValue = yValue;
    }

    public BigDecimal getrValue() {
        return rValue;
    }

    public void setrValue(BigDecimal rValue) {
        this.rValue = rValue;
    }

    public List<BigDecimal> getAllowedR() {
        return ALLOWED_R;
    }

    public List<BigDecimal> getAllowedY() {
        return ALLOWED_Y;
    }
}
