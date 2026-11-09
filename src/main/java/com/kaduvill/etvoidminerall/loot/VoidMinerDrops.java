package com.kaduvill.etvoidminerall.loot;

import com.kaduvill.etvoidminerall.ETVoidMinerAllDrops;
import com.valkyrieofnight.et.m_multiblocks.m_voidminer.registry.ITargetableRegistry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.item.ItemStack;

/** Read-only snapshots of ET's clear and preferred lens tables. */
public final class VoidMinerDrops {
    private VoidMinerDrops() {
    }

    public static List<VoidMinerDrop> read(ITargetableRegistry registry) {
        WeightedStackAccess access = WeightedStackAccess.get();
        WeightedTable base = new WeightedTable(registry.getList(), access);
        Map<String, LensChances> lenses = new HashMap<>();
        List<VoidMinerDrop> drops = new ArrayList<>();
        // Compare read-only registry references without copying saved drops in the duplicate scan.
        List<ItemStack> seen = new ArrayList<>();
        for (WeightedEntry weighted : base.entries) {
            if (weighted == null) continue;
            ItemStack stack = weighted.stack;
            if (stack == null || stack.isEmpty()) continue;
            boolean duplicate = false;
            for (ItemStack existing : seen) {
                if (!sameStack(existing, stack)) continue;
                duplicate = true;
                break;
            }
            if (duplicate) continue;
            seen.add(stack);

            // ET's target lookup uses stack identity, so resolve it before copying.
            String target = registry.getPriorityTarget(stack);
            LensChances lens = lenses.get(target);
            if (lens == null) {
                lens = readLens(registry, target, base, access);
                lenses.put(target, lens);
            }
            drops.add(new VoidMinerDrop(stack, base.chance(stack), lens.stack,
                    lens.table == null ? Float.NaN : lens.table.chance(stack)));
        }
        return drops;
    }

    private static LensChances readLens(ITargetableRegistry registry, String target, WeightedTable base,
            WeightedStackAccess access) {
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
        // Old ET divides by entryCount / 2; ET 2.1 fixes the single-output case.
        // Keep invalid tables visible without invoking unsafe weight calculations.
        if (base.totalWeight <= 0 || (!access.supportsSingleOutputLens() && base.entries.size() < 2)) {
            return new LensChances(lens, null);
        }
        try {
            // Like ET's own JEI page, use the base boost. This API copies weights;
            // never change its returned entries or their potentially shared stacks.
            return new LensChances(lens, new WeightedTable(registry.getTargetedList(target, 1.0f), access));
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

    private static final class WeightedEntry {
        final ItemStack stack;
        final int weight;

        WeightedEntry(ItemStack stack, int weight) {
            this.stack = stack;
            this.weight = weight;
        }
    }

    private static final class WeightedTable {
        final List<WeightedEntry> entries = new ArrayList<>();
        final int totalWeight;

        WeightedTable(List<?> entries, WeightedStackAccess access) {
            long total = 0;
            boolean valid = true;
            if (entries != null) {
                for (Object entry : entries) {
                    if (entry == null) {
                        this.entries.add(null);
                        valid = false;
                        continue;
                    }
                    int weight = access.getWeight(entry);
                    // Keep the original stack until ET's identity-based lens lookup is done.
                    this.entries.add(new WeightedEntry(access.getMainStack(entry), weight));
                    if (weight < 0) valid = false;
                    total += weight;
                }
            }
            this.totalWeight = valid && total > 0 && total <= Integer.MAX_VALUE ? (int) total : -1;
        }

        float chance(ItemStack stack) {
            if (totalWeight <= 0) return Float.NaN;
            int weight = 0;
            for (WeightedEntry entry : entries) {
                ItemStack candidate = entry.stack;
                if (candidate != null && !candidate.isEmpty() && sameStack(candidate, stack)) weight += entry.weight;
            }
            // Match ET's JEI percentage arithmetic and rounding to two decimal places.
            return Math.round((float) weight / (float) totalWeight * 10000.0f) / 100.0f;
        }
    }
}
