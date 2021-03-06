package com.solarrabbit.largeraids.v1_17.nms;

import com.solarrabbit.largeraids.nms.AbstractCraftVexWrapper;

import org.bukkit.craftbukkit.v1_17_R1.entity.CraftVex;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Vex;

import net.minecraft.world.entity.Mob;

public class CraftVexWrapper implements AbstractCraftVexWrapper {
    private final Vex vex;

    public CraftVexWrapper(Vex vex) {
        this.vex = vex;
    }

    @Override
    public LivingEntity getOwner() {
        Mob owner = ((CraftVex) vex).getHandle().getOwner();
        return owner == null ? null : (LivingEntity) owner.getBukkitEntity();
    }
}
