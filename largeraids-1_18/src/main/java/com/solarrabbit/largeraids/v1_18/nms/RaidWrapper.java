package com.solarrabbit.largeraids.v1_18.nms;

import java.util.Set;
import java.util.UUID;

import com.solarrabbit.largeraids.nms.AbstractRaidWrapper;
import com.solarrabbit.largeraids.nms.AbstractRaiderWrapper;

import org.apache.commons.lang3.reflect.FieldUtils;

import net.minecraft.world.entity.raid.Raid;

public class RaidWrapper implements AbstractRaidWrapper {
    final Raid raid;

    RaidWrapper(Raid raid) {
        this.raid = raid;
    }

    @Override
    public boolean isEmpty() {
        return this.raid == null;
    }

    @Override
    public void stop() {
        this.raid.stop();
    }

    @Override
    public void setBadOmenLevel(int level) {
        this.raid.setBadOmenLevel(level);
    }

    @Override
    public int getGroupsSpawned() {
        return this.raid.getGroupsSpawned();
    }

    @Override
    public void setGroupsSpawned(int groupsSpawned) {
        try {
            FieldUtils.writeField(this.raid, "L", groupsSpawned, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean addWaveMob(int wave, AbstractRaiderWrapper raider, boolean flag) {
        return this.raid.addWaveMob(wave, ((RaiderWrapper) raider).raider, flag);
    }

    @Override
    public void removeFromRaid(AbstractRaiderWrapper raider, boolean flag) {
        this.raid.removeFromRaid(((RaiderWrapper) raider).raider, flag);
    }

    @Override
    public Set<UUID> getHeroesOfTheVillage() {
        return this.raid.heroesOfTheVillage;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RaidWrapper wrapper) {
            return this.raid == wrapper.raid;
        } else {
            return false;
        }
    }
}