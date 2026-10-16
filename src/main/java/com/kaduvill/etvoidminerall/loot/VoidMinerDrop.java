package com.kaduvill.etvoidminerall.loot;

import net.minecraft.item.ItemStack;

public final class VoidMinerDrop {
    private final ItemStack stack;
    private final float baseChance;
    private final ItemStack lens;
    private final float lensChance;

    VoidMinerDrop(ItemStack stack, float baseChance, ItemStack lens, float lensChance) {
        this.stack = stack.copy();
        this.baseChance = baseChance;
        this.lens = lens.copy();
        this.lensChance = lensChance;
    }

    public ItemStack getStack() {
        return stack;
    }

    public float getBaseChance() {
        return baseChance;
    }

    public ItemStack getLens() {
        return lens;
    }

    public float getLensChance() {
        return lensChance;
    }
}
