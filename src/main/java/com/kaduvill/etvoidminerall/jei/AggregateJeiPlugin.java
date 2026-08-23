package com.kaduvill.etvoidminerall.jei;

import com.kaduvill.etvoidminerall.ETVoidMinerAllDrops;
import com.valkyrieofnight.et.m_multiblocks.m_voidminer.registry.ITargetableRegistry;
import com.valkyrieofnight.vliblegacy.lib.stack.WeightedStackBase;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

@JEIPlugin
public final class AggregateJeiPlugin implements IModPlugin {
    private final Map<VoidMinerType, List<AggregateRecipeWrapper>> recipes = new EnumMap<>(VoidMinerType.class);
    private boolean prepared;

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        prepareRecipes();
        for (VoidMinerType type : VoidMinerType.values()) {
            List<AggregateRecipeWrapper> typeRecipes = recipes.get(type);
            if (typeRecipes == null) continue;
            int maxDrops = 0;
            for (AggregateRecipeWrapper recipe : typeRecipes) maxDrops = Math.max(maxDrops, recipe.getDrops().size());
            registry.addRecipeCategories(new AggregateRecipeCategory(registry.getJeiHelpers().getGuiHelper(), type, maxDrops));
        }
    }

    @Override
    public void register(IModRegistry registry) {
        prepareRecipes();
        for (VoidMinerType type : VoidMinerType.values()) {
            List<AggregateRecipeWrapper> typeRecipes = recipes.get(type);
            if (typeRecipes == null) continue;
            registry.addRecipes(typeRecipes, type.getUid());
            for (Block controller : type.getControllers()) registry.addRecipeCatalyst(new ItemStack(controller), type.getUid());
        }
    }

    private void prepareRecipes() {
        if (prepared) return;
        prepared = true;
        int total = 0;
        for (VoidMinerType type : VoidMinerType.values()) {
            if (!type.isEnabled()) continue;
            List<AggregateRecipeWrapper> typeRecipes = new ArrayList<>(6);
            ITargetableRegistry[] registries = type.getRegistries();
            StringBuilder counts = new StringBuilder();
            for (int index = 0; index < registries.length; index++) {
                List<ItemStack> drops = copyUniqueDrops(registries[index]);
                typeRecipes.add(new AggregateRecipeWrapper(type, index + 1, drops));
                if (index > 0) counts.append(", ");
                counts.append("T").append(index + 1).append("=").append(drops.size());
            }
            recipes.put(type, typeRecipes);
            total += typeRecipes.size();
            ETVoidMinerAllDrops.LOGGER.debug("{} aggregate drops: {}", type.getName(), counts);
        }
        ETVoidMinerAllDrops.LOGGER.debug("Prepared {} Environmental Tech aggregate Void Miner recipes", total);
    }

    private static List<ItemStack> copyUniqueDrops(ITargetableRegistry registry) {
        List<ItemStack> drops = new ArrayList<>();
        for (WeightedStackBase weighted : registry.getList()) {
            ItemStack stack = weighted.getMainStack();
            if (stack == null || stack.isEmpty()) continue;
            boolean duplicate = false;
            for (ItemStack existing : drops) {
                if (!ItemStack.areItemsEqual(existing, stack) || !ItemStack.areItemStackTagsEqual(existing, stack)) continue;
                duplicate = true;
                break;
            }
            if (!duplicate) drops.add(stack.copy());
        }
        return drops;
    }
}
