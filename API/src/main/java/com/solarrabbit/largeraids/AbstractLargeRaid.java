package com.solarrabbit.largeraids;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Raid;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Raider;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public abstract class AbstractLargeRaid {
    private static final int RADIUS = 96;
    protected final LargeRaids plugin;
    private final int totalWaves;
    protected Location centre;
    protected Raid currentRaid;
    protected int currentWave;
    protected Set<UUID> pendingHeroes;
    protected boolean loading;

    public AbstractLargeRaid(LargeRaids plugin, Location location) {
        this.plugin = plugin;
        this.centre = location; // Not yet a real centre...
        this.totalWaves = plugin.getConfig().getInt("raid.waves");
        this.currentWave = 1;
        this.pendingHeroes = new HashSet<>();
    }

    public boolean isSimilar(Raid raid) {
        return isSimilar(raid.getLocation());
    }

    public boolean isLoading() {
        return this.loading;
    }

    public void announceVictory() {
        Sound sound = getSound(this.plugin.getConfig().getString("raid.sounds.victory", null));
        if (sound != null)
            playSoundToPlayers(sound);

        currentRaid.getHeroes().forEach(uuid -> pendingHeroes.add(uuid));
        ConfigurationSection conf = this.plugin.getConfig().getConfigurationSection("hero-of-the-village");
        int level = conf.getInt("level");
        int duration = conf.getInt("duration") * 60 * 20;
        this.pendingHeroes.forEach(
                uuid -> Optional.ofNullable(Bukkit.getPlayer(uuid)).filter(Player::isOnline).ifPresent(player -> {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, duration, level));
                }));
    }

    public void announceDefeat() {
        Sound sound = getSound(this.plugin.getConfig().getString("raid.sounds.defeat", null));
        if (sound != null)
            playSoundToPlayers(sound);
    }

    public List<Raider> getRemainingRaiders() {
        return this.currentRaid == null ? new ArrayList<>() : this.currentRaid.getRaiders();
    }

    public boolean isLastWave() {
        return this.currentWave == this.totalWaves;
    }

    protected void setRaid(Raid raid) {
        this.currentRaid = raid;
        this.centre = raid.getLocation();
        this.loading = true;
    }

    protected void playSoundToPlayers(Sound sound) {
        getPlayersInRadius().forEach(player -> player.playSound(player.getLocation(), sound, 50, 1));
    }

    protected Location getWaveSpawnLocation() {
        List<Raider> list = this.currentRaid.getRaiders();
        return list.isEmpty() ? null : list.get(0).getLocation();
    }

    protected Set<Player> getPlayersInRadius() {
        Collection<Entity> collection = this.centre.getWorld().getNearbyEntities(this.centre, RADIUS, RADIUS, RADIUS,
                entity -> entity instanceof Player
                        && centre.distanceSquared(entity.getLocation()) <= Math.pow(RADIUS, 2));
        Set<Player> set = new HashSet<>();
        collection.forEach(player -> set.add((Player) player));
        return set;
    }

    protected void broadcastWave() {
        boolean title = this.plugin.getConfig().getBoolean("raid.announce-waves.title");
        boolean message = this.plugin.getConfig().getBoolean("raid.announce-waves.message");
        this.getPlayersInRadius().forEach(player -> {
            if (title)
                player.sendTitle(ChatColor.GOLD + (isLastWave() ? "Final Wave" : "Wave " + currentWave), null, 10, 70,
                        20);
            if (message)
                player.sendMessage(
                        ChatColor.GOLD + "Spawning " + (isLastWave() ? "final wave" : "wave " + currentWave) + "...");
        });
    }

    protected Sound getSound(String name) {
        return Stream.of(Sound.values()).filter(value -> value.name().equals(name)).findFirst().orElse(null);
    }

    protected boolean needTrigger() {
        return this.totalWaves - this.currentWave >= (getDefaultWaveNumber(this.centre.getWorld()) + 1);
    }

    public static int getDefaultWaveNumber(World world) {
        Difficulty difficulty = world.getDifficulty();
        switch (difficulty) {
        case EASY:
            return 3;
        case NORMAL:
            return 5;
        case HARD:
            return 7;
        default:
            return 0;
        }
    }

    public abstract void startRaid();

    public abstract void stopRaid();

    public abstract boolean isSimilar(Location location);

    public abstract void triggerNextWave();

    public abstract void spawnNextWave();

}