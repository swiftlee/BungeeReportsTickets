package org.jm.spigotmc.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import org.jm.spigotmc.core.BungeeReportsTickets;
import org.jm.spigotmc.core.Flag;
import org.jm.spigotmc.core.Report;
import org.jm.spigotmc.utils.TextUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/*************************************************************************
 *
 * J&M CONFIDENTIAL - @author Jon - 04/01/2017 | 16:47
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
public class FetchDataCommand extends Command {

    BungeeReportsTickets plugin;

    public FetchDataCommand(BungeeReportsTickets plugin) {
        super("fetchdata");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {

        if (commandSender.hasPermission("reports.fetchdata")) {

            if (args.length == 1) {

                new Report(plugin).singleReport(args[0], commandSender.getName());

            } else if (args.length == 2) {

                String query = ("INSERT INTO {tableName} (reportUUID, flag) VALUES (?, ?) ON DUPLICATE KEY UPDATE flag = ?;".replace("{tableName}", commandSender.getName()));

                try {

                    if (args[1].equals(Flag.OPEN.toString())) {


                        PreparedStatement statement = plugin.getMysql().getConnection().prepareStatement(query);
                        statement.setString(1, args[0]);
                        statement.setString(2, Flag.OPEN.toString());
                        statement.setString(3, Flag.OPEN.toString());
                        statement.execute();
                        commandSender.sendMessage(TextUtils.formatString("&7Flag has been updated to: &aOpen"));

                    } else if (args[1].equals(Flag.CLOSED.toString())) {

                        PreparedStatement statement = plugin.getMysql().getConnection().prepareStatement(query);
                        statement.setString(1, args[0]);
                        statement.setString(2, Flag.CLOSED.toString());
                        statement.setString(3, Flag.CLOSED.toString());
                        statement.execute();
                        commandSender.sendMessage(TextUtils.formatString("&7Flag has been updated to: &cClosed"));

                    } else if (args[1].equals(Flag.DUPLICATE.toString())) {

                        PreparedStatement statement = plugin.getMysql().getConnection().prepareStatement(query);
                        statement.setString(1, args[0]);
                        statement.setString(2, Flag.DUPLICATE.toString());
                        statement.setString(3, Flag.DUPLICATE.toString());
                        statement.execute();
                        commandSender.sendMessage(TextUtils.formatString("&7Flag has been updated to: &eDuplicate"));

                    } else if (args[1].equals(Flag.NEED_ADMIN.toString())) {

                        PreparedStatement statement = plugin.getMysql().getConnection().prepareStatement(query);
                        statement.setString(1, args[0]);
                        statement.setString(2, Flag.NEED_ADMIN.toString());
                        statement.setString(3, Flag.NEED_ADMIN.toString());
                        statement.execute();
                        commandSender.sendMessage(TextUtils.formatString("&7Flag has been updated to: &bNeed Admin"));

                    }

                } catch (SQLException e) {

                    commandSender.sendMessage(TextUtils.formatString("&cSQLException, ask an admin to check the console."));
                    e.printStackTrace();

                }

            } else {

                commandSender.sendMessage(TextUtils.sendableMsg("&7Try: /fetchdata <UUID> (<optional> flag)"));

            }
        } else {

            commandSender.sendMessage(TextUtils.sendableMsg("&fUnknown command. Type \"/help\" for help."));

        }

    }
}
