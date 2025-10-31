package org.example;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fastcgi.FCGIInterface;

public class Main {

    public static void main(String[] args) {
        try {
            String portStr = System.getProperty("FCGI_PORT", "24019");
            int port = Integer.parseInt(portStr);

            FCGIInterface fcgiInterface = new FCGIInterface();

            System.err.println("FastCGI Server started on port " + port);
            System.err.println("Waiting for FastCGI requests...");

            while (fcgiInterface.FCGIaccept() >= 0) {
                try {
                    String method = FCGIInterface.request.params.getProperty("REQUEST_METHOD");
                    String queryString = FCGIInterface.request.params.getProperty("QUERY_STRING", "");
                    String contentLengthStr = FCGIInterface.request.params.getProperty("CONTENT_LENGTH", "0");

                    System.err.println("Processing " + method + " request, query: " + queryString);

                    if ("GET".equals(method)) {
                        if (queryString.contains("action=clear")) {
                            HistoryManager.clearHistory();
                            sendResponse(200, "OK", "application/json", "{\"status\":\"History cleared\"}");
                        } else {
                            String historyJson = buildHistoryJson();
                            sendResponse(200, "OK", "application/json", historyJson);
                        }
                        continue;
                    }

                    if ("POST".equals(method)) {
                        int contentLength = Integer.parseInt(contentLengthStr);
                        if (contentLength > 0) {
                            byte[] body = new byte[contentLength];
                            System.in.read(body);
                            String formData = new String(body, StandardCharsets.UTF_8);

                            System.err.println("Raw form data: " + formData);

                            String result = processPoint(formData);
                            sendResponse(200, "OK", "application/json", result);
                        } else {
                            sendResponse(400, "Bad Request", "application/json",
                                    "{\"error\":\"Empty request body\"}");
                        }
                        continue;
                    }

                    sendResponse(405, "Method Not Allowed", "application/json",
                            "{\"error\":\"Method not allowed\"}");

                } catch (Exception e) {
                    System.err.println("Error processing request: " + e.getMessage());
                    e.printStackTrace();
                    sendResponse(500, "Internal Server Error", "application/json",
                            "{\"error\":\"Server error: " + e.getMessage() + "\"}");
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to start FastCGI server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String processPoint(String formData) {

        String[] pairs = formData.split("&");
        String x = null, y = null, r = null;

        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length != 2) continue;

            String key = keyValue[0];
            String value = keyValue[1];

            switch (key) {
                case "x": x = value; break;
                case "y": y = value; break;
                case "r": r = value; break;
            }
        }

        if (x == null || y == null || r == null) {

            return "{\"error\":\"Missing parameters\"}";
        }

        try {
            System.err.println("Processing point - X: '" + x + "', Y: '" + y + "', R: '" + r + "'");

            if (!AreaChecker.validatePoint(x, y, r)) {
                System.err.println("Point validation failed");
                return "{\"error\":\"Invalid parameters\"}";
            }

            long startTime = System.nanoTime();
            boolean hit = AreaChecker.checkHit(x, y, r);
            long endTime = System.nanoTime();
            double executionTime = (endTime - startTime) / 1_000_000.0;

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Point result = new Point(x, y, r, hit,
                    dateFormat.format(new Date()), executionTime);

            System.err.println("Created point - X: '" + result.getX() + "', Hit: " + hit);

            HistoryManager.addPoint(result);

            return buildResponseJson(result);

        } catch (Exception e) {
            System.err.println("Processing error: " + e.getMessage());
            return "{\"error\":\"Processing error: " + e.getMessage() + "\"}";
        }
    }

    private static String buildResponseJson(Point currentResult) {
        StringBuilder json = new StringBuilder();
        json.append("{\"currentResult\":");
        appendPointJson(json, currentResult);
        json.append(",\"history\":[");

        java.util.List<Point> history = HistoryManager.getHistory();
        for (int i = 0; i < history.size(); i++) {
            if (i > 0) json.append(",");
            appendPointJson(json, history.get(i));
        }

        json.append("]}");
        return json.toString();
    }

    private static void appendPointJson(StringBuilder json, Point point) {
        json.append("{")
                .append("\"x\":\"").append(escapeJsonString(point.getX())).append("\",")
                .append("\"y\":\"").append(escapeJsonString(point.getY())).append("\",")
                .append("\"r\":\"").append(escapeJsonString(point.getR())).append("\",")
                .append("\"hit\":").append(point.isHit()).append(",")
                .append("\"requestTime\":\"").append(escapeJsonString(point.getRequestTime())).append("\",")
                .append("\"executionTime\":\"").append(escapeJsonString(point.getExecutionTime())).append("\"")
                .append("}");
    }

    private static String escapeJsonString(String str) {
        if (str == null) return "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    private static String buildHistoryJson() {
        StringBuilder json = new StringBuilder();
        json.append("{\"history\":[");

        java.util.List<Point> history = HistoryManager.getHistory();
        for (int i = 0; i < history.size(); i++) {
            if (i > 0) json.append(",");
            appendPointJson(json, history.get(i));
        }

        json.append("]}");
        return json.toString();
    }

    private static void sendResponse(int statusCode, String statusText, String contentType, String body) {
        try {
            String response = "HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +

                    "Content-Type: " + contentType + "\r\n" +
                    "Access-Control-Allow-Origin: *\r\n" +
                    "Access-Control-Allow-Methods: GET, POST, OPTIONS\r\n" +
                    "Access-Control-Allow-Headers: Content-Type\r\n" +
                    "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                    "\r\n" +
                    body;

            System.out.print(response);
            System.out.flush();

            System.err.println("Response sent: " + statusCode + " " + statusText);
        } catch (Exception e) {
            System.err.println("Error sending response: " + e.getMessage());
        }
    }
}
