package org.jm.spigotmc.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class TextUtils {

    public static String formatString(String txt) {

        return ChatColor.translateAlternateColorCodes('&', txt);

    }

    public static BaseComponent[] sendableMsg(String txt) {

        return new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', txt)).create();

    }

}
