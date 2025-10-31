package org.example;

import java.util.Locale;

public class Point {
    private String x;
    private String y;
    private String r;
    private boolean hit;
    private String requestTime;
    private String executionTime;

    public Point(String x, String y, String r, boolean hit, String requestTime, double executionTime) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.requestTime = requestTime;
        this.executionTime = String.format(Locale.US, "%.6f", executionTime);
    }

    public String getX() { return x; }
    public String getY() { return y; }
    public String getR() { return r; }
    public boolean isHit() { return hit; }
    public String getRequestTime() { return requestTime; }
    public String getExecutionTime() { return executionTime; }
}
