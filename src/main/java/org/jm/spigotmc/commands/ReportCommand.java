package org.jm.spigotmc.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import org.jm.spigotmc.core.BungeeReportsTickets;
import org.jm.spigotmc.core.Flag;
import org.jm.spigotmc.utils.TextUtils;

import java.sql.PreparedStatement;
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

                    ProxiedPlayer target;

                    try {
                        target = plugin.getProxy().getPlayer(args[0]);
                        String uuid = String.valueOf(target.getUniqueId());
                        String time;
                        String server = ((ProxiedPlayer) commandSender).getServer().getInfo().getName();

                        DateFormat df = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
                        Date date = new Date();

                        String reportUUID = UUID.randomUUID().toString();

                        time = df.format(date);
                        List<String> staff = plugin.getConfig().getStringList("staff");

                        for (String sName : staff) {

                            ProxiedPlayer p = plugin.getProxy().getPlayer(sName);
                            System.out.print(reason);

                            if (p != null) {

                                TextComponent message = new TextComponent(TextUtils.formatString(
                                        "{name} &chas reported &r{target}&c. " +
                                                "&l&7Click here &r&bto see more.").replace("{name}", commandSender.getName()).replace("{target}", target.getName()));
                                message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fetchdata {uuid}".replace("{uuid}", reportUUID)));
                                p.sendMessage(message);

                            }

                            String query = ("INSERT INTO {tablename} (reportUUID, playerUUID, playerReported, viewed, server, " +
                                    "dateTime, flag, reason) VALUES ('{reportUUID}', '{playerUUID}', '{playerReported}', {viewed}," +
                                    "'{server}', '{dateTime}', '{flag}', '{reason}')").replace("{reportUUID}", reportUUID).replace("{tablename}",
                                    sName).replace("{playerUUID}", String.valueOf(((ProxiedPlayer) commandSender).getUniqueId())).replace(
                                            "{playerReported}", uuid).replace("{viewed}", String.valueOf(0)).replace("{server}",
                                    server).replace("{dateTime}", time).replace("{flag}", Flag.OPEN.toString()).replace("{reason}", reason);

                            //TODO: JSON clickable messages so staff can view all requests. Create pages of reports etc
                            //TODO: Clickable GUI with options to resolve issue
                            //TODO: Create available reports notifications

                            try {
                                PreparedStatement statement = plugin.getMysql().getConnection().prepareStatement(query);
                                statement.execute();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                        }

                        commandSender.sendMessage(TextUtils.sendableMsg("&aReport sent! Thank you."));

                    } catch (NullPointerException e) {
                        commandSender.sendMessage(TextUtils.sendableMsg("&cThat is an invalid player."));
                    }

                }

            } else {

                commandSender.sendMessage(TextUtils.sendableMsg("&7/report <player> <reason>"));

            }

        } else {

            commandSender.sendMessage(TextUtils.sendableMsg("&cThe console cannot execute this command!"));

        }

    }

}
