package com.solarrabbit.largeraids.listener;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.raid.LargeRaid;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Raid;
import org.bukkit.Raid.RaidStatus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.event.raid.RaidSpawnWaveEvent;
import org.bukkit.event.raid.RaidStopEvent;
import org.bukkit.event.raid.RaidTriggerEvent;

public class BukkitRaidListener implements Listener {
    public final Set<LargeRaid> currentRaids = new HashSet<>();
    private final LargeRaids plugin;
    private boolean isIdle;

    public BukkitRaidListener(LargeRaids plugin) {
        this.plugin = plugin;
        isIdle = false;
    }

    public boolean isIdle() {
        return isIdle;
    }

    /**
     * Idles the listener, mainly used to signify that any {@link RaidTriggerEvent}
     * fired after part of a {@link LargeRaid}.
     */
    public void setIdle() {
        isIdle = true;
    }

    /**
     * Re-activates the listener, mainly used to signify that any
     * {@link RaidTriggerEvent} fired after are vanilla.
     */
    public void setActive() {
        isIdle = false;
    }

    public int getNumOfRegisteredRaids() {
        return currentRaids.size();
    }

    @EventHandler
    public void onSpawn(RaidSpawnWaveEvent evt) {
        matchingLargeRaid(evt.getRaid()).ifPresent(largeRaid -> {
            setIdle();
            largeRaid.spawnWave();
            setActive();
        });
    }

    /**
     * Disables normal raid if enabled in configurations.
     *
     * @param evt raid triggering event
     */
    @EventHandler
    public void onNormalRaidTrigger(RaidTriggerEvent evt) {
        if (evt.getRaid().getBadOmenLevel() != 0) // Raid is getting extended
            return;
        if (isIdle()) // LargeRaid triggering
            return;
        if (!plugin.getTriggerConfig().canNormalRaid())
            evt.setCancelled(true);
    }

    @EventHandler
    public void onFinish(RaidFinishEvent evt) {
        Raid raid = evt.getRaid();
        matchingLargeRaid(raid).ifPresent(largeRaid -> {
            RaidStatus status = raid.getStatus();
            if (status == RaidStatus.VICTORY)
                largeRaid.announceVictory();
            else if (status == RaidStatus.LOSS)
                largeRaid.announceDefeat();
        });
    }

    @EventHandler
    public void onRaidStop(RaidStopEvent evt) {
        matchingLargeRaid(evt.getRaid()).ifPresent(largeRaid -> currentRaids.remove(largeRaid));
    }

    public void init() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::tick, 0, 1);
    }

    private void tick() {
        for (LargeRaid largeRaid : currentRaids)
            if (largeRaid.isActive() && largeRaid.getTotalRaidersAlive() == 0 && !largeRaid.isLoading()
                    && !largeRaid.isLastWave()) {
                setIdle();
                largeRaid.triggerNextWave();
                setActive();
            }
    }

    public Optional<LargeRaid> getLargeRaidInRange(Location location) {
        return currentRaids.stream().filter(largeRaid -> largeRaid.isInRange(location)).findFirst();
    }

    public Optional<LargeRaid> matchingLargeRaid(Raid raid) {
        return currentRaids.stream().filter(largeRaid -> largeRaid.isSimilar(raid)).findFirst();
    }

}