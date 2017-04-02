package org.jm.spigotmc.core;

import net.md_5.bungee.api.chat.BaseComponent;

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

    public Report(BungeeReportsTickets plugin, List strings, int page, int invSize, Object back, Object next) {

        this.plugin = plugin;

        page = page - 1;
        invSize = invSize - 2;
        int totalPages = (int) Math.ceil(strings.size() / invSize);

        int first;
        int count;
        if (page >= 0 && page < totalPages) {
            if (page == 0) {
                first = page * invSize;
            } else {
                first = page * invSize;
            }
            count = first + invSize;
        } else {
            int n = strings.size() - (totalPages) * invSize;
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

    }

    public static BaseComponent[] singleReport(String reportUUID) {


        String query = ("SELECT playerName, playerReportedName, server, dateTime, flag, reason FROM {tableName} WHERE reportUUID = ?;");
        String query2 = ("SELECT viewed FROM {tableName} WHERE reportUUID = ?;");
        String query3 = ("INSERT INTO {tableName} (viewed) VALUES (1) WHERE reportUUID = ? ON DUPLICATE KEY UPDATE viewed = 1 WHERE reportUUID = ?;");

        try {
            for (String sName : plugin.getConfig().getStringList("staff")) {

                PreparedStatement statement = plugin.getMysql().getConnection().prepareStatement(query2.replace("{tableName}", sName));
                statement.setString(1, reportUUID);
                ResultSet set = statement.executeQuery();

                if (set.first() && set.getInt("viewed") == 0) {

                    PreparedStatement statement3 = plugin.getMysql().getConnection().prepareStatement(query3.replace("{tableName}", sName));
                    statement3.setString(1, reportUUID);
                    statement3.setString(2, reportUUID);
                    statement3.execute();

                }

                PreparedStatement statement2 = plugin.getMysql().getConnection().prepareStatement(query.replace("{tableName}", sName));
                statement2.setString(1, reportUUID);
                ResultSet set1 = statement.executeQuery();

                String playerName = "";
                String playerReportedName = "";
                String server = "";
                String dateTime = "";
                String flag = "";
                String reason = "";

                while(set1.next()) {

                    playerName = set1.getString("playerName");
                    playerReportedName = set1.getString("playerReportedName");
                    server = set1.getString("server");
                    dateTime = set1.getString("dateTime");
                    flag = set1.getString("flag");
                    reason = set1.getString("reason");

                }

                String report = ("&a--------------------\n&7Reported by: &b" + playerName + "\n&7Player reported: &c" + playerReportedName +
                        "\n&7Server: &b" + server + "\n&7Date and time: &f" + dateTime + "\n&7Flag: &b" + flag +
                        "\n&7Reason: &f" + reason + "\n&a--------------------");

                return TextUtils.sendableMsg(report);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return TextUtils.sendableMsg("&cThere has been an error with this report. Please check the console for errors.");

    }

}

