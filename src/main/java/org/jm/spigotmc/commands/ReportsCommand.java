package org.jm.spigotmc.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import org.jm.spigotmc.core.BungeeReportsTickets;
import org.jm.spigotmc.core.Flag;
import org.jm.spigotmc.core.Report;
import org.jm.spigotmc.utils.TextUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*************************************************************************
 *
 * J&M CONFIDENTIAL - @author Jon - 04/02/2017 | 13:57
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
public class ReportsCommand extends Command {

    BungeeReportsTickets plugin;

    public ReportsCommand(BungeeReportsTickets plugin) {

        super("reports");
        this.plugin = plugin;

    }


    @Override
    public void execute(CommandSender commandSender, String[] args) {

        if (commandSender.hasPermission("reports.reports")) {

            if (args.length == 1) {

                Integer.parseInt(args[0]);
                String query = ("SELECT playerName, playerReportedName, flag, reportUUID FROM {tableName};".replace("{tableName}", commandSender.getName()));

                try {

                    PreparedStatement statement = plugin.getMysql().getConnection().prepareStatement(query);
                    ResultSet set = statement.executeQuery();
                    List<TextComponent> list = new ArrayList<>();

                    String report = "";
                    String playerName = "";
                    String playerReportedName = "";
                    String flag = "";
                    String uuid = "";

                    while (set.next()) {

                        playerName = set.getString("playerName");
                        playerReportedName = set.getString("playerReportedName");
                        flag = set.getString("flag");
                        uuid = set.getString("reportUUID");
                        String result = "";

                        for (char c : uuid.toCharArray()) {

                            result += "§" + c;

                        }


                        report = "&b" + playerName + " &7reported &c" + playerReportedName + "&7. Flag: {flag} {hiddenId}".replace("{hiddenId}", result);

                        if (flag.equalsIgnoreCase(Flag.OPEN.toString())) {

                            report = (report.replace("{flag}", "&a" + Flag.OPEN.toString()));
                            TextComponent reportMsg = new TextComponent(TextUtils.formatString(report));
                            reportMsg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fetchdata " + result.replace("§", "")));
                            reportMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextUtils.sendableMsg("&bClick here &7to see more.")));
                            list.add(reportMsg);

                        } else if (flag.equalsIgnoreCase(Flag.CLOSED.toString())) {

                            report = report.replace("{flag}", "&c" + Flag.CLOSED.toString());
                            TextComponent reportMsg = new TextComponent(TextUtils.formatString(report));
                            reportMsg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fetchdata " + result.replace("§", "")));
                            reportMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextUtils.sendableMsg("&bClick here &7to see more.")));
                            list.add(reportMsg);

                        } else if (flag.equalsIgnoreCase(Flag.DUPLICATE.toString())) {

                            report = report.replace("{flag}", "&e" + Flag.DUPLICATE.toString());
                            TextComponent reportMsg = new TextComponent(TextUtils.formatString(report));
                            reportMsg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fetchdata " + result.replace("§", "")));
                            reportMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextUtils.sendableMsg("&bClick here &7to see more.")));
                            list.add(reportMsg);

                        } else {

                            report = report.replace("{flag}", "&b" + Flag.NEED_ADMIN.toString());
                            TextComponent reportMsg = new TextComponent(TextUtils.formatString(report));
                            reportMsg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fetchdata " + result.replace("§", "")));
                            reportMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextUtils.sendableMsg("&bClick here &7to see more.")));
                            list.add(reportMsg);

                        }

                    }

                    TextComponent next = new TextComponent(TextUtils.formatString("&a&lNext"));
                    next.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/reports {page}".replace("{page}", String.valueOf(Integer.parseInt(args[0]) + 1))));
                    next.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextUtils.sendableMsg("&aNext page.")));
                    TextComponent back = new TextComponent(TextUtils.formatString("&c&lBack"));
                    back.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/reports {page}".replace("{page}", String.valueOf(Integer.parseInt(args[0]) - 1))));
                    back.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextUtils.sendableMsg("&cBack.")));

                    String topBar = "&a----------[pg. {page}]----------".replace("{page}", args[0]);

                    commandSender.sendMessage(TextUtils.formatString(topBar));

                    List<TextComponent> l = new Report(plugin).openPage(list, Integer.parseInt(args[0]), 3, back, next);

                    for (TextComponent msg : l) {

                        if (msg != null)
                            commandSender.sendMessage(msg);

                    }

                    String bottomBar = "";

                    for (int j = 0; j < topBar.length(); j++) {

                        bottomBar += "-";

                    }

                    commandSender.sendMessage(TextUtils.formatString("&a" + bottomBar));

                } catch (SQLException e) {

                    e.printStackTrace();
                    commandSender.sendMessage(TextUtils.formatString("&cSQLException, ask an admin to check the console."));

                } catch (NumberFormatException e) {

                    commandSender.sendMessage(TextUtils.formatString("&7Your argument must be an integer."));

                } catch (IndexOutOfBoundsException e) {

                    commandSender.sendMessage(TextUtils.formatString("&7That page does not exist."));

                }

            } else {

                commandSender.sendMessage(TextUtils.formatString("&7Try: /reports <page>"));

            }

        } else {

            commandSender.sendMessage(TextUtils.sendableMsg("&fUnknown command. Type \"/help\" for help."));

        }

    }
}
