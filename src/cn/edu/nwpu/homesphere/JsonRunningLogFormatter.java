package cn.edu.nwpu.homesphere;

import java.text.SimpleDateFormat;
import java.util.List;

public class JsonRunningLogFormatter implements RunningLogFormatter {

    @Override
    public String getFormatterName() {
        return "JSON格式";
    }

    @Override
    public String format(Household household) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"householdId\": ").append(household.getHouseholdId()).append(",\n");
        json.append("  \"address\": \"").append(escapeJson(household.getAddress())).append("\",\n");
        json.append("  \"rooms\": [\n");

        List<Room> rooms = household.getRooms();
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            json.append("    {\n");
            json.append("      \"roomId\": ").append(room.getRoomId()).append(",\n");
            json.append("      \"roomName\": \"").append(escapeJson(room.getName())).append("\",\n");
            json.append("      \"devices\": [\n");

            List<Device> devices = room.getDevices();
            for (int j = 0; j < devices.size(); j++) {
                Device device = devices.get(j);
                json.append("        {\n");
                json.append("          \"deviceId\": ").append(device.getDeviceId()).append(",\n");
                json.append("          \"deviceName\": \"").append(escapeJson(device.getName())).append("\",\n");
                json.append("          \"runningLogs\": [\n");

                List<RunningLog> runningLogs = device.getRunningLogs();
                for (int k = 0; k < runningLogs.size(); k++) {
                    RunningLog log = runningLogs.get(k);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    json.append("            {\n");
                    json.append("              \"dateTime\": \"").append(sdf.format(log.getDateTime())).append("\",\n");
                    json.append("              \"event\": \"").append(escapeJson(log.getEvent())).append("\",\n");
                    json.append("              \"note\": \"").append(escapeJson(log.getNote())).append("\",\n");
                    json.append("              \"type\": \"").append(log.getType().name()).append("\"\n");
                    json.append("            }");
                    if (k < runningLogs.size() - 1) {
                        json.append(",");
                    }
                    json.append("\n");
                }

                json.append("          ]\n");
                json.append("        }");
                if (j < devices.size() - 1) {
                    json.append(",");
                }
                json.append("\n");
            }

            json.append("      ]\n");
            json.append("    }");
            if (i < rooms.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }

        json.append("  ]\n");
        json.append("}");
        return json.toString();
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}