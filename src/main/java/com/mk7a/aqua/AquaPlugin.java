package com.mk7a.aqua;

import org.bukkit.plugin.java.JavaPlugin;


public final class AquaPlugin extends JavaPlugin {

    static final String P_NOTIFY = "aqua.notify";
    static final String P_BYPASS = "aqua.bypass";


    @Override
    public void onEnable() {

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        AfkFishListener afkFishListener = new AfkFishListener(this);
        afkFishListener.setup();

        boolean autoFishDetection = getConfig().getBoolean("autoFishDetection");

        if (autoFishDetection) {
            AutoFishListener autoFishListener = new AutoFishListener(this);
            autoFishListener.setup();
        }

    }

    @Override
    public void onDisable() {
    }
}
