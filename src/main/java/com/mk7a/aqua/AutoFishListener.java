package com.mk7a.aqua;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


public class AutoFishListener implements Listener {

    private final AquaPlugin plugin;
    private final HashMap<UUID, List<fishAttempt>> fishEvents = new HashMap<>();

    AutoFishListener(AquaPlugin plugin) {
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

        UUID uuid = player.getUniqueId();
        Location playerLocation = player.getLocation();
        FishHook hook = event.getHook();
        Block hookBlock = hook.getLocation().getBlock();
        Long timestamp = System.currentTimeMillis();

        List<fishAttempt> playerFishAttempts = fishEvents.getOrDefault(uuid, new ArrayList<>());

        if (!hookBlock.getType().equals(Material.WATER)) {
            return;
        }

        if (event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {

            int consecutiveSuccess = getConsecutiveSuccess(playerFishAttempts);

            int attemptsSize = playerFishAttempts.size();

            if (attemptsSize > 3) {

                boolean noLocationChange = true;

                Location lastLoc = playerFishAttempts.get(attemptsSize - 1).playerLocation;

                //int locCheckSteps = consecutiveSuccess >= 3 ? 4 : 10;

                for (int i = 2; i <= 30; i++) {

                    if (attemptsSize > i) {

                        Location getLoc = playerFishAttempts.get(attemptsSize - i).playerLocation;

                        if (!(lastLoc.equals(getLoc) && lastLoc.getYaw() == getLoc.getYaw()
                                && lastLoc.getPitch() == getLoc.getPitch())) {
                            noLocationChange = false;
                            break;
                        }
                    }
                }

                if (noLocationChange) {

                    hook.remove();
                    event.setCancelled(true);
                    player.kickPlayer(plugin.autoForPlayer);
                    playerFishAttempts.clear();

                    Bukkit.broadcast(plugin.prefix + plugin.autoForAdmin.replaceAll("%p%", player.getName())
                            .replaceAll("%u%", player.getUniqueId().toString()), AquaPlugin.P_NOTIFY);
                }
            }


            fishAttempt attempt = new fishAttempt(timestamp, playerLocation, true, false);
            playerFishAttempts.add(attempt);

        } else if (event.getState().equals(PlayerFishEvent.State.FAILED_ATTEMPT)) {
            fishAttempt attempt = new fishAttempt(timestamp, playerLocation, false, false);
            playerFishAttempts.add(attempt);
        }

        fishEvents.remove(uuid);
        fishEvents.put(uuid, playerFishAttempts);
    }

    private int getConsecutiveSuccess(List<fishAttempt> fishAttempts) {

        int count = 0;
        for (int i = fishAttempts.size() - 1; i >= 0; i--) {
            if (fishAttempts.get(i).success) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    private class fishAttempt {

        final Long timestamp;
        final Location playerLocation;
        final boolean success;
        final boolean cancelled;

        fishAttempt(Long timestamp, Location playerLocation, boolean success, boolean cancelled) {
            this.timestamp = timestamp;
            this.playerLocation = playerLocation;
            this.success = success;
            this.cancelled = cancelled;
        }
    }
}
