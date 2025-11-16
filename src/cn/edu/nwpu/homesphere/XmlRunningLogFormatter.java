package cn.edu.nwpu.homesphere;

import java.text.SimpleDateFormat;
import java.util.List;

public class XmlRunningLogFormatter implements RunningLogFormatter {

    @Override
    public String getFormatterName() {
        return "XML格式";
    }

    @Override
    public String format(Household household) {
        StringBuilder xml = new StringBuilder();

        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<household householdId=\"").append(household.getHouseholdId())
                .append("\" address=\"").append(escapeXml(household.getAddress())).append("\">\n");
        xml.append("  <rooms>\n");

        List<Room> rooms = household.getRooms();
        for (Room room : rooms) {
            xml.append("    <room roomId=\"").append(room.getRoomId())
                    .append("\" roomName=\"").append(escapeXml(room.getName())).append("\">\n");
            xml.append("      <devices>\n");

            List<Device> devices = room.getDevices();
            for (Device device : devices) {
                xml.append("        <device deviceId=\"").append(device.getDeviceId())
                        .append("\" deviceName=\"").append(escapeXml(device.getName())).append("\">\n");
                xml.append("          <runningLogs>\n");

                List<RunningLog> runningLogs = device.getRunningLogs();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (RunningLog log : runningLogs) {
                    xml.append("            <runningLog dateTime=\"").append(sdf.format(log.getDateTime()))
                            .append("\" event=\"").append(escapeXml(log.getEvent()))
                            .append("\" type=\"").append(log.getType().name())
                            .append("\" note=\"").append(escapeXml(log.getNote())).append("\" />\n");
                }

                xml.append("          </runningLogs>\n");
                xml.append("        </device>\n");
            }

            xml.append("      </devices>\n");
            xml.append("    </room>\n");
        }

        xml.append("  </rooms>\n");
        xml.append("</household>");

        return xml.toString();
    }

    private String escapeXml(String str) {
        if (str == null) return "";
        return str.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}