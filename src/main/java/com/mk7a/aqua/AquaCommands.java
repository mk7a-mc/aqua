package com.mk7a.aqua;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.ArrayList;
import java.util.List;

public class AquaCommands implements CommandExecutor, Listener {

    private static final String CMD_MAIN = "aqua";
    private static final String ARG_RELOAD = "reload";

    private final AquaPlugin plugin;

    AquaCommands(AquaPlugin plugin) {
        this.plugin = plugin;
    }

    protected void setup() {
        plugin.getCommand(CMD_MAIN).setExecutor(this);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        String cmd = command.getName().toLowerCase();

        if (cmd.equals(CMD_MAIN)) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase(ARG_RELOAD)) {
                    plugin.reloadMessages();
                    commandSender.sendMessage(ChatColor.GREEN + "Messages reloaded from config.");
                    return true;
                }
            }
        }

        return false;
    }

    @EventHandler
    public void onTab(TabCompleteEvent event) {

        if (event.getSender().hasPermission(AquaPlugin.P_ADMIN) && event.getBuffer().equalsIgnoreCase("aqua r")) {

            List<String> complete = new ArrayList<>();
            complete.add(ARG_RELOAD);
            event.setCompletions(complete);
        }
    }
}
