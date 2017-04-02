package org.jm.spigotmc.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import org.jm.spigotmc.core.BungeeReportsTickets;
import org.jm.spigotmc.utils.TextUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/*************************************************************************
 *
 * J&M CONFIDENTIAL - @author Jon - 04/02/2017 | 16:52
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
public class DeleteReportCommand extends Command {

    BungeeReportsTickets plugin;

    public DeleteReportCommand(BungeeReportsTickets plugin) {

        super("deletereport");
        this.plugin = plugin;

    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {

        if (commandSender.hasPermission("reports.delete")) {

            if (args.length == 1) {

                String query = ("DELETE FROM {tableName} WHERE reportUUID = ?;");

                try {

                    for (String sName : plugin.getConfig().getStringList("staff")) {

                        query = query.replace("{tableName}", sName);
                        PreparedStatement statement = plugin.getMysql().getConnection().prepareStatement(query);
                        statement.setString(1, args[0]);
                        statement.execute();

                    }

                    commandSender.sendMessage(TextUtils.formatString("&7The report has been &cdeleted&7."));


                } catch (SQLException e) {

                    e.printStackTrace();

                }

            } else {

                commandSender.sendMessage(TextUtils.formatString("&7Try: /deletereport <reportUUID>"));

            }

        } else {

            commandSender.sendMessage(TextUtils.sendableMsg("&fUnknown command. Type \"/help\" for help."));

        }

    }
}
