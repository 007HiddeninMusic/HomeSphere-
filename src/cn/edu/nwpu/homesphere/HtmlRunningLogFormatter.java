package cn.edu.nwpu.homesphere;

import java.text.SimpleDateFormat;
import java.util.List;

public class HtmlRunningLogFormatter implements RunningLogFormatter {

    @Override
    public String getFormatterName() {
        return "HTML格式";
    }

    @Override
    public String format(Household household) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("  <meta charset=\"UTF-8\">\n");
        html.append("  <title>智能家居系统运行日志</title>\n");
        html.append("  <style>\n");
        html.append("    body { font-family: Arial, sans-serif; margin: 20px; }\n");
        html.append("    table, th, td { border: 1px solid black; border-collapse: collapse; padding: 8px; }\n");
        html.append("    th { background-color: #f2f2f2; }\n");
        html.append("    .room { margin-bottom: 20px; }\n");
        html.append("    .device { margin-bottom: 15px; }\n");
        html.append("  </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("  <h1>智能家居生态系统\"HomeSphere\" v3.0</h1>\n");
        html.append("  <p><strong>家庭ID：</strong>").append(household.getHouseholdId()).append("</p>\n");
        html.append("  <p><strong>地址：</strong>").append(escapeHtml(household.getAddress())).append("</p>\n");
        html.append("  <hr>\n");

        List<Room> rooms = household.getRooms();
        for (Room room : rooms) {
            html.append("  <div class=\"room\">\n");
            html.append("    <h2>房间: ").append(escapeHtml(room.getName())).append(" (ID: ").append(room.getRoomId()).append(")</h2>\n");

            List<Device> devices = room.getDevices();
            for (Device device : devices) {
                html.append("    <div class=\"device\">\n");
                html.append("      <table style=\"width: 100%;\">\n");
                html.append("        <tr>\n");
                html.append("          <th style=\"width: 15%;\">设备ID</th>\n");
                html.append("          <td style=\"width: 35%;\">").append(device.getDeviceId()).append("</td>\n");
                html.append("          <th style=\"width: 15%;\">设备名称</th>\n");
                html.append("          <td style=\"width: 35%;\">").append(escapeHtml(device.getName())).append("</td>\n");
                html.append("        </tr>\n");
                html.append("        <tr>\n");
                html.append("          <th colspan=\"4\">运行日志</th>\n");
                html.append("        </tr>\n");
                html.append("        <tr>\n");
                html.append("          <td colspan=\"4\">\n");
                html.append("            <ul>\n");

                List<RunningLog> runningLogs = device.getRunningLogs();
                if (runningLogs.isEmpty()) {
                    html.append("              <li>暂无运行日志</li>\n");
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    for (RunningLog log : runningLogs) {
                        html.append("              <li>")
                                .append(sdf.format(log.getDateTime())).append(", ")
                                .append(escapeHtml(log.getEvent())).append(", ")
                                .append(log.getType().name()).append(", ")
                                .append(escapeHtml(log.getNote()))
                                .append("</li>\n");
                    }
                }

                html.append("            </ul>\n");
                html.append("          </td>\n");
                html.append("        </tr>\n");
                html.append("      </table>\n");
                html.append("    </div>\n");
            }

            html.append("  </div>\n");
            html.append("  <hr>\n");
        }

        html.append("</body>\n");
        html.append("</html>");

        return html.toString();
    }

    private String escapeHtml(String str) {
        if (str == null) return "";
        return str.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}