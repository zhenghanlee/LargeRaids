package com.solarrabbit.largeraids.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.solarrabbit.largeraids.raid.mob.Bomber;
import com.solarrabbit.largeraids.raid.mob.FireworkPillager;
import com.solarrabbit.largeraids.raid.mob.MythicRaider;
import com.solarrabbit.largeraids.raid.mob.Necromancer;
import com.solarrabbit.largeraids.raid.mob.EventRaider;
import com.solarrabbit.largeraids.raid.mob.VanillaRaider;
import com.solarrabbit.largeraids.raid.mob.VanillaRaiderRider;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.mobs.MobManager;

public class RaiderConfig {
    private static final EntityType[] DEFAULT_RAIDER_TYPES = new EntityType[] { EntityType.PILLAGER,
            EntityType.VINDICATOR, EntityType.RAVAGER, EntityType.WITCH, EntityType.EVOKER, EntityType.ILLUSIONER };
    private final ConfigurationSection config;
    private final Map<EventRaider, List<Integer>> mobsMap;

    RaiderConfig(ConfigurationSection config) {
        this.config = config;
        mobsMap = new HashMap<>();
        init();
    }

    public List<EventRaider> getWaveMobs(int wave) {
        List<EventRaider> mobs = new ArrayList<>();
        mobsMap.forEach((raider, list) -> {
            int number = list.get(wave - 1);
            for (int i = 0; i < number; i++) {
                mobs.add(raider);
            }
        });
        return mobs;
    }

    private void init() {
        Map<String, List<Integer>> stringMappings = getStringMappings();
        loadVanillaRaiders(stringMappings);
        loadCustomRaiders(stringMappings);
        if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null)
            loadMythicRaiders(stringMappings);
    }

    private void loadVanillaRaiders(Map<String, List<Integer>> stringMappings) {
        for (EntityType type : DEFAULT_RAIDER_TYPES) {
            VanillaRaider vanillaRaider = new VanillaRaider(type);
            List<Integer> list = stringMappings.remove(type.name().toLowerCase());
            if (list != null)
                mobsMap.put(vanillaRaider, list);
            // Counterparts raiders riding ravagers
            VanillaRaiderRider raiderRider = new VanillaRaiderRider(type);
            List<Integer> riderList = stringMappings.remove(type.name().toLowerCase() + "rider");
            if (riderList != null)
                mobsMap.put(raiderRider, riderList);
        }
    }

    private void loadCustomRaiders(Map<String, List<Integer>> stringMappings) {
        FireworkPillager fireworkPillager = new FireworkPillager();
        List<Integer> list = stringMappings.remove("fireworkpillager");
        if (list != null)
            mobsMap.put(fireworkPillager, list);

        Bomber bomber = new Bomber();
        List<Integer> bomberList = stringMappings.remove("bomber");
        if (list != null)
            mobsMap.put(bomber, bomberList);

        Necromancer necromancer = new Necromancer();
        List<Integer> necromancerList = stringMappings.remove("necromancer");
        if (list != null)
            mobsMap.put(necromancer, necromancerList);
    }

    private void loadMythicRaiders(Map<String, List<Integer>> stringMappings) {
        MobManager manager = MythicProvider.get().getMobManager();
        for (Entry<String, List<Integer>> entry : stringMappings.entrySet())
            manager.getMythicMob(entry.getKey())
                    .ifPresent(mob -> mobsMap.put(new MythicRaider(mob), entry.getValue()));
    }

    private Map<String, List<Integer>> getStringMappings() {
        Set<String> keys = config.getKeys(false);
        Map<String, List<Integer>> mappings = new HashMap<>();
        for (String key : keys) {
            mappings.put(key, config.getIntegerList(key));
        }
        return mappings;
    }

}
