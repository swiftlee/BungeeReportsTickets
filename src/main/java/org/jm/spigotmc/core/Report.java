package org.jm.spigotmc.core;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.jm.spigotmc.utils.TextUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/*************************************************************************
 *
 * J&M CONFIDENTIAL - @author Jon - 04/01/2017 | 16:42
 * __________________
 *
 *  [2016] J&M Plugin Development 
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of J&M Plugin Development and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to J&M Plugin Development
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from J&M Plugin Development.
 */
public class Report {

    static BungeeReportsTickets plugin;

    public Report(BungeeReportsTickets plugin) {

        this.plugin = plugin;

    }

    public int page = 1;

    public List openPage(List<TextComponent> strings, int page, int size, TextComponent back, TextComponent next) {
        page -= 1;
        size -= 2;
        int totalPages = (int) Math.ceil(strings.size() / size);

        int first;
        int count;
        if (page >= 0 && page < totalPages) {
            if (page == 0) {
                first = page * size;
            } else {
                first = page * size;
            }
            count = first + size;

        } else {
            int n = strings.size() - (totalPages) * size;
            first = (strings.size()) - n;
            count = strings.size();
        }

        List newList;
        if (page >= totalPages) {

            newList = strings.subList(first + 1, count);

        } else if (page == 0) {

            newList = strings.subList(first, count + 1);

        } else {

            newList = strings.subList(first + 1, count + 1);
        }
        if (page >= totalPages) {

            newList.add(0, back);

        } else if (page == 0) {

            newList.add(next);

        } else {

            newList.add(0, back);
            newList.add(next);

        }

        return newList;
    }

    public void singleReport(String reportUUID, String commandSenderName) {


        String query = ("SELECT playerName, playerReportedName, server, dateTime, flag, reason FROM {tableName} WHERE reportUUID = ?;".replace("{tableName}", commandSenderName));
        String query2 = ("SELECT viewed FROM {tableName} WHERE reportUUID = ?;");
        String query3 = ("INSERT INTO {tableName} (reportUUID, viewed) VALUES (?, 1) ON DUPLICATE KEY UPDATE viewed = 1;");

        try {

            PreparedStatement statement = plugin.getMysql().getConnection().prepareStatement(query2.replace("{tableName}", commandSenderName));
            statement.setString(1, reportUUID);
            ResultSet set = statement.executeQuery();

            if (set.first() && set.getInt("viewed") == 0) {

                PreparedStatement statement3 = plugin.getMysql().getConnection().prepareStatement(query3.replace("{tableName}", commandSenderName));
                statement3.setString(1, reportUUID);
                statement3.execute();
                statement3.close();
                statement.close();

            }

            PreparedStatement statement2 = plugin.getMysql().getConnection().prepareStatement(query.replace("{tableName}", commandSenderName));
            statement2.setString(1, reportUUID);
            ResultSet set1 = statement2.executeQuery();

            String playerName = "";
            String playerReportedName = "";
            String server = "";
            String dateTime = "";
            String flag = "";
            String reason = "";

            while (set1.next()) {

                playerName = set1.getString("playerName");
                playerReportedName = set1.getString("playerReportedName");
                server = set1.getString("server");
                dateTime = set1.getString("dateTime");
                flag = set1.getString("flag");
                reason = set1.getString("reason");

            }

            statement2.close();

            TextComponent open = new TextComponent(TextUtils.formatString("&a&oOpen"));
            open.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextUtils.sendableMsg("&7Click to change status to: &aOpen")));
            open.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fetchdata {reportUUID} {flag}".replace("{reportUUID}", reportUUID).replace("{flag}", Flag.OPEN.toString())));

            TextComponent closed = new TextComponent(TextUtils.formatString("&c&oClosed"));
            closed.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextUtils.sendableMsg("&7Click to change status to: &cClosed")));
            closed.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fetchdata {reportUUID} {flag}".replace("{reportUUID}", reportUUID).replace("{flag}", Flag.CLOSED.toString())));

            TextComponent duplicate = new TextComponent(TextUtils.formatString("&e&oDuplicate"));
            duplicate.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextUtils.sendableMsg("&7Click to change status to: &eDuplicate")));
            duplicate.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fetchdata {reportUUID} {flag}".replace("{reportUUID}", reportUUID).replace("{flag}", Flag.DUPLICATE.toString())));

            TextComponent needAdmin = new TextComponent(TextUtils.formatString("&b&oNeed Admin"));
            needAdmin.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextUtils.sendableMsg("&7Click to change status to: &bNeed Admin")));
            needAdmin.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fetchdata {reportUUID} {flag}".replace("{reportUUID}", reportUUID).replace("{flag}", Flag.NEED_ADMIN.toString())));

            String report = ("&a--------------------\n&7Reported by: &b" + playerName + "\n&7Player reported: &c" + playerReportedName +
                    "\n&7Server: &b" + server + "\n&7Date and time: &f" + dateTime + "\n&7Flag: &b{" + flag + "}\n&7- Change flag: ");

            String reportPart2 = "&7Reason: &f" + reason + "\n&a--------------------";

            if (flag.equalsIgnoreCase(Flag.OPEN.toString())) {

                report = report.replace("{" + Flag.OPEN.toString() + "}", "&a" + Flag.OPEN.toString());

            } else if (flag.equalsIgnoreCase(Flag.CLOSED.toString())) {

                report = report.replace("{" + Flag.CLOSED.toString() + "}", "&c" + Flag.CLOSED.toString());

            } else if (flag.equalsIgnoreCase(Flag.DUPLICATE.toString())) {

                report = report.replace("{" + Flag.DUPLICATE.toString() + "}", "&e" + Flag.DUPLICATE.toString()); //I added a comment

            } else {

                report = report.replace("{" + Flag.NEED_ADMIN.toString() + "}", "&b" + Flag.NEED_ADMIN.toString());

            }

            TextComponent delete = new TextComponent(TextUtils.formatString("&c&lDelete"));
            delete.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/deletereport " + reportUUID));
            delete.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextUtils.sendableMsg("&4&lTHIS CANNOT BE UNDONE!")));

            plugin.getProxy().getPlayer(commandSenderName).sendMessage(TextUtils.formatString(report));
            plugin.getProxy().getPlayer(commandSenderName).sendMessage(open);
            plugin.getProxy().getPlayer(commandSenderName).sendMessage(closed);
            plugin.getProxy().getPlayer(commandSenderName).sendMessage(duplicate);
            plugin.getProxy().getPlayer(commandSenderName).sendMessage(needAdmin);
            plugin.getProxy().getPlayer(commandSenderName).sendMessage(delete);
            plugin.getProxy().getPlayer(commandSenderName).sendMessage(TextUtils.formatString(reportPart2));

        } catch (SQLException e) {
            plugin.getProxy().getPlayer(commandSenderName).sendMessage(TextUtils.formatString("&cThere has been an error with this report. &cPlease check the console for errors."));
            e.printStackTrace();
        }

    }

}

