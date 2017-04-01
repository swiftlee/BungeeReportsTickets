package org.jm.spigotmc.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import org.jm.spigotmc.core.BungeeReportsTickets;
import org.jm.spigotmc.core.DuplicateReportException;
import org.jm.spigotmc.core.Flag;
import org.jm.spigotmc.core.ReportedSelfException;
import org.jm.spigotmc.utils.TextUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ReportCommand extends Command {

    BungeeReportsTickets plugin;

    public ReportCommand(BungeeReportsTickets plugin) {
        super("report");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {

        if (commandSender instanceof ProxiedPlayer) {

            if (args.length >= 2) {

                String reason = "";

                for (int i = 0; i < args.length; i++) {

                    if (i > 0) {

                        if (i == args.length - 1)
                            reason += args[i];
                        else
                            reason += args[i] + " ";
                    }
                }

                ProxiedPlayer target;

                try {

                    target = plugin.getProxy().getPlayer(args[0]);
                    String uuid = target.getUniqueId().toString();
                    String senderUUID = ((ProxiedPlayer) commandSender).getUniqueId().toString();

                    if (senderUUID.equals(uuid)) {

                        throw new ReportedSelfException();

                    }

                    String time;
                    String server = ((ProxiedPlayer) commandSender).getServer().getInfo().getName();

                    DateFormat df = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
                    Date date = new Date();

                    String reportUUID = UUID.randomUUID().toString();

                    time = df.format(date);
                    List<String> staff = plugin.getConfig().getStringList("staff");

                    for (String sName : staff) {

                        String reportExists = ("SELECT * FROM ? WHERE playerUUID = ? AND playerReported = ?;");

                        try {

                            PreparedStatement statement = plugin.getMysql().getConnection().prepareStatement(reportExists);
                            statement.setString(1, sName);
                            statement.setString(2, senderUUID);
                            statement.setString(3, uuid);
                            ResultSet set = statement.executeQuery();

                            if (set.first()) {

                                statement.close();
                                throw new DuplicateReportException();

                            }

                        } catch (SQLException e) {

                            e.printStackTrace();

                        }

                        ProxiedPlayer p = plugin.getProxy().getPlayer(sName);
                        System.out.print(reason);

                        if (p != null) {

                            TextComponent message = new TextComponent(TextUtils.formatString(
                                    "&7{name} &chas reported &7{target}&c. " +
                                            "&7Click here &r&bto see more.").replace("{name}", commandSender.getName()).replace("{target}", target.getName()));
                            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fetchdata {uuid}".replace("{uuid}", reportUUID)));
                            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextUtils.sendableMsg("&b&lClick for report data!")));
                            p.sendMessage(message);

                        }

                        String query = ("INSERT INTO ? (reportUUID, playerUUID, playerReported, viewed, server, " +
                                "dateTime, flag, reason) VALUES (?, ?, ?, ?, ?, ?, ?, ?);");

                        //TODO: JSON clickable messages so staff can view all reports. Create pages of reports etc
                        //TODO: Clickable text with options to resolve issue
                        //TODO: Create available reports notifications

                        try {
                            PreparedStatement statement = plugin.getMysql().getConnection().prepareStatement(query);
                            statement.setString(1, sName);
                            statement.setString(2, reportUUID);
                            statement.setString(3, senderUUID);
                            statement.setString(4, uuid);
                            statement.setInt(5, 0);
                            statement.setString(6, ((ProxiedPlayer) commandSender).getServer().toString());
                            statement.setString(7, time);
                            statement.setString(8, Flag.OPEN.toString());
                            statement.setString(9, reason);
                            statement.execute();
                            statement.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                    }

                    commandSender.sendMessage(TextUtils.sendableMsg("&aReport sent! Thank you."));

                } catch (NullPointerException e) {
                    commandSender.sendMessage(TextUtils.sendableMsg("&cThat is an invalid player."));
                } catch (ReportedSelfException e) {

                    commandSender.sendMessage(TextUtils.sendableMsg("&cYou cannot report yourself!"));

                } catch (DuplicateReportException e) {

                    commandSender.sendMessage(TextUtils.sendableMsg("&cYou have already reported that player!"));

                }

            } else {

                commandSender.sendMessage(TextUtils.sendableMsg("&7Try: /report <player> <reason>"));

            }

        } else {

            commandSender.sendMessage(TextUtils.sendableMsg("&cThe console cannot execute this command!"));

        }

    }

}
