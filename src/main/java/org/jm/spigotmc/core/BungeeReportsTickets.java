package org.jm.spigotmc.core;

import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import org.jm.spigotmc.commands.ReportCommand;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BungeeReportsTickets extends Plugin {

    private static MySQL mysql;
    public File configFile;
    Configuration configuration;

    @Override
    public void onEnable() {

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        configFile = new File(getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                try (InputStream is = getResourceAsStream("config.yml");
                     OutputStream os = new FileOutputStream(configFile)) {
                    ByteStreams.copy(is, os);
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to create configuration file", e);
            }
        }

        try {

            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);

            mysql = new MySQL(
                    configuration.getString("hostname"),
                    configuration.getString("port"),
                    configuration.getString("databasename"),
                    configuration.getString("username"),
                    configuration.getString("password"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            /*connection = */
            mysql.openConnection();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        String query = ("CREATE TABLE IF NOT EXISTS {tablename} (reportUUID VARCHAR(255), playerUUID VARCHAR(255)," +
                " playerReported VARCHAR(255), viewed TINYINT(0)," +
                " server VARCHAR(1000), dateTime VARCHAR(255)," +
                " flag VARCHAR(255), reason VARCHAR(1000), PRIMARY KEY (reportUUID)) " +
                "DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;");

        for (String sName : getConfig().getStringList("staff")) {

            try {

                PreparedStatement statement = mysql.getConnection().prepareStatement(query.replace("{tablename}", sName));
                statement.execute();

            } catch (SQLException e) {

                e.printStackTrace();

            }

        }


        getProxy().getPluginManager().registerCommand(this, new ReportCommand(this));

    }

    @Override
    public void onDisable() {

        try {

            getMysql().getConnection().close();

        } catch (SQLException e) {

            e.printStackTrace();

        }

    }

    public Configuration getConfig() {

        return configuration;

    }

    public void loadConfig() {

        try {

            ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    public void saveConfig() {

        try {

            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, configFile);

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    public void reloadConfig() {

        saveConfig();
        loadConfig();

    }

    public MySQL getMysql() {

        return mysql;

    }

}
