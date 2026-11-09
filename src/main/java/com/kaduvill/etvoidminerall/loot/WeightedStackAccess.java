package com.kaduvill.etvoidminerall.loot;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;

/** Keeps each ValkyrieLib stack type in a class loaded only for its ET version. */
public abstract class WeightedStackAccess {
    private static WeightedStackAccess instance;
    private final boolean singleOutputLensSupported;

    private WeightedStackAccess(boolean singleOutputLensSupported) {
        this.singleOutputLensSupported = singleOutputLensSupported;
    }

    public static void initialize(ArtifactVersion etVersion) {
        if (instance != null) return;
        instance = etVersion.compareTo(new DefaultArtifactVersion("1.12.2-2.1.0.0")) >= 0
                ? new Modern() : new Legacy();
    }

    static WeightedStackAccess get() {
        if (instance == null) throw new IllegalStateException("ET stack access has not been initialized");
        return instance;
    }

    abstract ItemStack getMainStack(Object entry);

    abstract int getWeight(Object entry);

    final boolean supportsSingleOutputLens() {
        return singleOutputLensSupported;
    }

    private static final class Legacy extends WeightedStackAccess {
        Legacy() {
            super(false);
        }

        @Override
        ItemStack getMainStack(Object entry) {
            return ((com.valkyrieofnight.vliblegacy.lib.stack.WeightedStackBase) entry).getMainStack();
        }

        @Override
        int getWeight(Object entry) {
            return ((com.valkyrieofnight.vliblegacy.lib.stack.WeightedStackBase) entry).getWeight();
        }
    }

    private static final class Modern extends WeightedStackAccess {
        Modern() {
            super(true);
        }

        @Override
        ItemStack getMainStack(Object entry) {
            return ((com.valkyrieofnight.vliblegacy.item.weighted.WeightedStackBase) entry).getMainStack();
        }

        @Override
        int getWeight(Object entry) {
            return ((com.valkyrieofnight.vliblegacy.item.weighted.WeightedStackBase) entry).getWeight();
        }
    }
}
