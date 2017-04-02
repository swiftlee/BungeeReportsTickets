package org.jm.spigotmc.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import org.jm.spigotmc.core.BungeeReportsTickets;
import org.jm.spigotmc.core.Report;
import org.jm.spigotmc.utils.TextUtils;

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

        if (commandSender.hasPermission("reports.fetchadata")) {

            if (args.length > 0) {

                commandSender.sendMessage(Report.singleReport(args[0]));

            } else {

                commandSender.sendMessage(TextUtils.sendableMsg("&7Try: /fetchdata <UUID>"));

            }
        } else {

            commandSender.sendMessage("&fUnknown command. Type \"/help\" for help.");

        }

    }
}
