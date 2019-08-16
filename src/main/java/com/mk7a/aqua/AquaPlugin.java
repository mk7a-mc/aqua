package com.mk7a.aqua;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;


public final class AquaPlugin extends JavaPlugin {

    protected static final String P_NOTIFY = "aqua.notify";
    protected static final String P_BYPASS = "aqua.bypass";

    private static final double CONFIG_VER = 1.1;
    private static final String VERSION_PATH = "configVersionDoNotModify";

    protected String prefix;
    protected String afkForPlayer;
    protected String afkForAdmin;
    protected String autoForPlayer;
    protected String autoForAdmin;

    @Override
    public void onEnable() {

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        reloadConfig();

        if (CONFIG_VER == getConfig().getDouble(VERSION_PATH)) {
            Bukkit.getLogger().info("=================================================================================");
            this.getLogger().info(" Warning: outdated config. Some default values will be used.");
            this.getLogger().info(" Please re-generate or get the latest config from spigot.");
            Bukkit.getLogger().info("=================================================================================");
        }

        reloadMessages();

        AfkFishListener afkFishListener = new AfkFishListener(this);
        afkFishListener.setup();

        boolean autoFishDetection = getConfig().getBoolean("autoFishDetection");

        if (autoFishDetection) {
            AutoFishListener autoFishListener = new AutoFishListener(this);
            autoFishListener.setup();
        }

    }

    protected void reloadMessages() {

        prefix = t(getConfig().getString("messages.messagePrefix"));
        afkForPlayer = t(getConfig().getString("messages.afkForPlayer"));
        afkForAdmin = t(getConfig().getString("messages.afkForAdmin"));
        autoForPlayer = t(getConfig().getString("messages.autoForPlayer"));
        autoForAdmin = t(getConfig().getString("messages.autoForAdmin"));
    }

    private static String t(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
