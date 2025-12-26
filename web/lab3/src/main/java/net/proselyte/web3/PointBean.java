package net.proselyte.web3;

import net.proselyte.web3.db.PointService;
import net.proselyte.web3.db.models.PointModel;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Named("pointBean")
@SessionScoped
public class PointBean implements Serializable {
    @Inject
    private PointService service;
    private List<PointModel> points = Collections.emptyList();
    private int firstRow = 0;

    @PostConstruct
    public void init() {
        points = service.listAll();
    }

    public void refresh() {
        points = service.listAll();

        if (points.isEmpty()) {
            firstRow = 0;
        }
    }

    public List<PointModel> getPoints() {
        refresh();
        return points;
    }

    public String getPointsJson(BigDecimal currentR) {
        refresh();
        String body = points.stream()
                .filter(p -> currentR != null && p.getR() != null && p.getR().compareTo(currentR) == 0)
                .map(p -> String.format(Locale.US,
                        "{\"x\":%s,\"y\":%s,\"hit\":%s}",
                        p.getX().toPlainString(), p.getY().toPlainString(), p.isHit()))
                .collect(Collectors.joining(","));
        return "[" + body + "]";
    }

    public String pointsJson(BigDecimal currentR) {
        return getPointsJson(currentR);
    }

    public String formatBigDecimal(BigDecimal value) {
        if (value == null) return "";
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return "0";
        }
        BigDecimal normalized = value.stripTrailingZeros();
        return normalized.toPlainString();
    }

    public int getFirstRow() {
        return firstRow;
    }

    public void setFirstRow(int firstRow) {
        this.firstRow = firstRow;
    }

    public int getRowNumber(int rowIndex) {
        int totalSize = points.size();
        return totalSize - rowIndex;
    }


    public String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss d.M");
        return dateTime.format(formatter);
    }
}
