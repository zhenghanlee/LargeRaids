package com.solarrabbit.largeraids.v1_16.nms;

import static java.util.Objects.requireNonNull;

import javax.annotation.Nonnull;

import com.solarrabbit.largeraids.nms.AbstractBlockPositionWrapper;

import net.minecraft.server.v1_16_R3.BlockPosition;

public class BlockPositionWrapper implements AbstractBlockPositionWrapper {
    final BlockPosition blockPos;

    public BlockPositionWrapper(double x, double y, double z) {
        this.blockPos = new BlockPosition(x, y, z);
    }

    BlockPositionWrapper(@Nonnull BlockPosition blockPos) {
        requireNonNull(blockPos);
        this.blockPos = blockPos;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BlockPositionWrapper))
            return false;
        return this.blockPos.equals(((BlockPositionWrapper) obj).blockPos);
    }
}
