package org.jm.spigotmc.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import org.jm.spigotmc.core.BungeeReportsTickets;
import org.jm.spigotmc.utils.TextUtils;

/*************************************************************************
 *
 * J&M CONFIDENTIAL - @author Jon - 04/02/2017 | 17:11
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
public class ReloadConfigCommand extends Command {

    BungeeReportsTickets plugin;

    public ReloadConfigCommand(BungeeReportsTickets plugin) {

        super("reloadreportsconfig");
        this.plugin = plugin;

    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {

        if (commandSender.hasPermission("reports.reloadconfig")) {

            plugin.reloadConfig();
            commandSender.sendMessage(TextUtils.formatString("&7The configuration for &aBungeeReportsTickets &7has been reloaded."));

        } else {

            commandSender.sendMessage(TextUtils.sendableMsg("&fUnknown command. Type \"/help\" for help."));

        }

    }
}
