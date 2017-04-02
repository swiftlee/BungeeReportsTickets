package org.jm.spigotmc.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import org.jm.spigotmc.core.BungeeReportsTickets;
import org.jm.spigotmc.utils.TextUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*************************************************************************
 *
 * J&M CONFIDENTIAL - @author Jon - 04/02/2017 | 16:30
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
public class PostLoginListener implements Listener {

    BungeeReportsTickets plugin;

    public PostLoginListener(BungeeReportsTickets plugin) {

        this.plugin = plugin;

    }

    @EventHandler
    public void onPostLogin(PostLoginEvent e) {

        for (String sName : plugin.getConfig().getStringList("staff")) {

            if (e.getPlayer().getName().equals(sName)) {

                ProxiedPlayer p = e.getPlayer();
                String query = ("SELECT viewed FROM {tableName};".replace("{tableName}", sName));

                try {

                    PreparedStatement statement = plugin.getMysql().getConnection().prepareStatement(query);
                    ResultSet set = statement.executeQuery();

                    int count = 0;

                    while (set.next()) {

                        if (set.getInt("viewed") == 0) {

                            count++;

                        }

                    }

                    if (count > 0)
                        p.sendMessage(TextUtils.formatString("&7&lYou have &e{reports}&7 unread reports! \n&r&7Use: &f/reports <page> &7to view reports.".replace("{reports}", String.valueOf(count))));
                    else
                        p.sendMessage(TextUtils.formatString("&aYou have no unread reports!"));

                } catch (SQLException e1) {

                    e1.printStackTrace();

                }


                break;

            }

        }

    }

}
