package com.kaduvill.etvoidminerall.loot;

import com.kaduvill.etvoidminerall.ETVoidMinerAllDrops;
import com.valkyrieofnight.et.m_multiblocks.m_voidminer.registry.ITargetableRegistry;
import com.valkyrieofnight.vliblegacy.lib.stack.WeightedStackBase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.item.ItemStack;

/** Read-only snapshots of ET's clear and preferred lens tables. */
public final class VoidMinerDrops {
    private VoidMinerDrops() {
    }

    public static List<VoidMinerDrop> read(ITargetableRegistry registry) {
        WeightedTable base = new WeightedTable(registry.getList());
        Map<String, LensChances> lenses = new HashMap<>();
        List<VoidMinerDrop> drops = new ArrayList<>();
        for (WeightedStackBase weighted : base.entries) {
            if (weighted == null) continue;
            ItemStack stack = weighted.getMainStack();
            if (stack == null || stack.isEmpty()) continue;
            boolean duplicate = false;
            for (VoidMinerDrop existing : drops) {
                if (!sameStack(existing.getStack(), stack)) continue;
                duplicate = true;
                break;
            }
            if (duplicate) continue;

            // ET's target lookup uses stack identity, so resolve it before copying.
            String target = registry.getPriorityTarget(stack);
            LensChances lens = lenses.get(target);
            if (lens == null) {
                lens = readLens(registry, target, base);
                lenses.put(target, lens);
            }
            drops.add(new VoidMinerDrop(stack, base.chance(stack), lens.stack,
                    lens.table == null ? Float.NaN : lens.table.chance(stack)));
        }
        return drops;
    }

    private static LensChances readLens(ITargetableRegistry registry, String target, WeightedTable base) {
        if (target == null || target.isEmpty()) return new LensChances(ItemStack.EMPTY, null);
        List<ItemStack> candidates = registry.getLensesOfTarget(target);
        ItemStack lens = ItemStack.EMPTY;
        if (candidates != null) {
            for (ItemStack candidate : candidates) {
                if (candidate == null || candidate.isEmpty()) continue;
                lens = candidate;
                break;
            }
        }
        if (lens.isEmpty()) return new LensChances(lens, null);
        // ET divides by entryCount / 2 when targeting. Keep malformed tables visible
        // without invoking that calculation or inventing a percentage.
        if (base.totalWeight <= 0 || base.entries.size() < 2) return new LensChances(lens, null);
        try {
            // Like ET's own JEI page, use the base boost. This API copies weights;
            // never change its returned entries or their potentially shared stacks.
            return new LensChances(lens, new WeightedTable(registry.getTargetedList(target, 1.0f)));
        } catch (RuntimeException exception) {
            ETVoidMinerAllDrops.LOGGER.warn("Unable to read Void Miner chances for lens target {}", target, exception);
            return new LensChances(lens, null);
        }
    }

    private static boolean sameStack(ItemStack left, ItemStack right) {
        return ItemStack.areItemsEqual(left, right) && ItemStack.areItemStackTagsEqual(left, right);
    }

    private static final class LensChances {
        final ItemStack stack;
        final WeightedTable table;

        LensChances(ItemStack stack, WeightedTable table) {
            this.stack = stack;
            this.table = table;
        }
    }

    private static final class WeightedTable {
        final List<WeightedStackBase> entries;
        final int totalWeight;

        WeightedTable(List<WeightedStackBase> entries) {
            this.entries = entries == null ? Collections.emptyList() : new ArrayList<>(entries);
            long total = 0;
            for (WeightedStackBase entry : this.entries) {
                if (entry == null || entry.getWeight() < 0) {
                    total = -1;
                    break;
                }
                total += entry.getWeight();
            }
            this.totalWeight = total > 0 && total <= Integer.MAX_VALUE ? (int) total : -1;
        }

        float chance(ItemStack stack) {
            if (totalWeight <= 0) return Float.NaN;
            int weight = 0;
            for (WeightedStackBase entry : entries) {
                ItemStack candidate = entry.getMainStack();
                if (candidate != null && !candidate.isEmpty() && sameStack(candidate, stack)) weight += entry.getWeight();
            }
            // Match ET's JEI percentage arithmetic and rounding to two decimal places.
            return Math.round((float) weight / (float) totalWeight * 10000.0f) / 100.0f;
        }
    }
}
