package com.mk7a.aqua;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.Arrays;

public class AfkFishListener implements Listener {

    private final AquaPlugin plugin;
    private static final Material[] allowedMediums = {Material.WATER, Material.SEAGRASS, Material.TALL_SEAGRASS, Material.KELP, Material.KELP_PLANT};

    AfkFishListener(AquaPlugin plugin) {
        this.plugin = plugin;
    }

    void setup() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {

        Player player = event.getPlayer();

        if (player.hasPermission(AquaPlugin.P_BYPASS)) {
            return;
        }

        FishHook hook = event.getHook();
        Block hookBlock = hook.getLocation().getBlock();

        if (event.getState().equals(PlayerFishEvent.State.BITE)) {

            if (!Arrays.asList(allowedMediums).contains(hookBlock.getType())
                    || hookBlock.getRelative(BlockFace.UP).getType().equals(Material.TRIPWIRE)) {

                hook.remove();
                event.setCancelled(true);

                player.sendTitle("", plugin.afkForPlayer, 1, 100, 1);
                player.playSound(player.getLocation(), Sound.BLOCK_CONDUIT_DEACTIVATE, 1F, 1F);

                Location playerLocation = player.getLocation();
                playerLocation.setPitch(0F);
                player.teleport(playerLocation);

                Bukkit.broadcast(plugin.prefix + plugin.afkForAdmin.replaceAll("%p%", player.getName())
                        .replaceAll("%u%", player.getUniqueId().toString()), AquaPlugin.P_NOTIFY);

            }
        }
    }
}
