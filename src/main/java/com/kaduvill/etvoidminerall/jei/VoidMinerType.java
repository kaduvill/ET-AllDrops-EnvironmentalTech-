package com.kaduvill.etvoidminerall.jei;

import com.valkyrieofnight.et.m_multiblocks.m_voidminer.m_botanic.VMBotanic;
import com.valkyrieofnight.et.m_multiblocks.m_voidminer.m_botanic.features.BBlocks;
import com.valkyrieofnight.et.m_multiblocks.m_voidminer.m_ore.VMOre;
import com.valkyrieofnight.et.m_multiblocks.m_voidminer.m_ore.features.OBlocks;
import com.valkyrieofnight.et.m_multiblocks.m_voidminer.m_res.VMRes;
import com.valkyrieofnight.et.m_multiblocks.m_voidminer.m_res.features.RBlocks;
import com.valkyrieofnight.et.m_multiblocks.m_voidminer.registry.ITargetableRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;

public enum VoidMinerType {
    ORE("etvoidminerall.voidminer.ore", "jei.etvoidminerall.category.ore", "Void Ore Miner"),
    RESOURCE("etvoidminerall.voidminer.resource", "jei.etvoidminerall.category.resource", "Void Resource Miner"),
    BOTANIC("etvoidminerall.voidminer.botanic", "jei.etvoidminerall.category.botanic", "Void Botanic Miner");

    private final String uid;
    private final String titleKey;
    private final String name;

    VoidMinerType(String uid, String titleKey, String name) {
        this.uid = uid;
        this.titleKey = titleKey;
        this.name = name;
    }
    public String getUid() {return uid;}
    public String getTitle() {return I18n.format(titleKey);}
    public String getName() {
        return name;
    }
    public boolean isEnabled() {
        switch (this) {
            case ORE:
                return VMOre.getInstance().isEnabled();
            case RESOURCE:
                return VMRes.getInstance().isEnabled();
            case BOTANIC:
                return VMBotanic.getInstance().isEnabled();
            default:
                return false;
        }
    }

    public ITargetableRegistry[] getRegistries() {
        switch (this) {
            case ORE:
                VMOre ore = VMOre.getInstance();
                return new ITargetableRegistry[]{ore.VOM_T1, ore.VOM_T2, ore.VOM_T3, ore.VOM_T4, ore.VOM_T5, ore.VOM_T6};
            case RESOURCE:
                VMRes resource = VMRes.getInstance();
                return new ITargetableRegistry[]{resource.T1, resource.T2, resource.T3, resource.T4, resource.T5, resource.T6};
            case BOTANIC:
                VMBotanic botanic = VMBotanic.getInstance();
                return new ITargetableRegistry[]{botanic.T1, botanic.T2, botanic.T3, botanic.T4, botanic.T5, botanic.T6};
            default:
                throw new IllegalStateException("Unknown Void Miner type: " + this);
        }
    }

    public Block[] getControllers() {
        switch (this) {
            case ORE:
                return new Block[]{OBlocks.VOID_ORE_MINER_1, OBlocks.VOID_ORE_MINER_2, OBlocks.VOID_ORE_MINER_3,
                        OBlocks.VOID_ORE_MINER_4, OBlocks.VOID_ORE_MINER_5, OBlocks.VOID_ORE_MINER_6};
            case RESOURCE:
                return new Block[]{RBlocks.VOID_RES_MINER_1, RBlocks.VOID_RES_MINER_2, RBlocks.VOID_RES_MINER_3,
                        RBlocks.VOID_RES_MINER_4, RBlocks.VOID_RES_MINER_5, RBlocks.VOID_RES_MINER_6};
            case BOTANIC:
                return new Block[]{BBlocks.VOID_BOTANIC_MINER_1, BBlocks.VOID_BOTANIC_MINER_2, BBlocks.VOID_BOTANIC_MINER_3,
                        BBlocks.VOID_BOTANIC_MINER_4, BBlocks.VOID_BOTANIC_MINER_5, BBlocks.VOID_BOTANIC_MINER_6};
            default:
                throw new IllegalStateException("Unknown Void Miner type: " + this);
        }
    }
}
