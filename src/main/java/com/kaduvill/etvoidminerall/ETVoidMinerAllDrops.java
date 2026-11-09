package com.kaduvill.etvoidminerall;

import com.kaduvill.etvoidminerall.loot.WeightedStackAccess;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = ETVoidMinerAllDrops.MOD_ID, name = ETVoidMinerAllDrops.MOD_NAME, version = ETVoidMinerAllDrops.VERSION,
        acceptedMinecraftVersions = "[" + Tags.MC_VERSION + "]", dependencies = "required-after:environmentaltech@[1.12.2-2.0.20.1,);",
        clientSideOnly = true, acceptableRemoteVersions = "*")
public final class ETVoidMinerAllDrops {
    public static final String MOD_ID = "etvoidminerall";
    public static final String MOD_NAME = "Environmental Tech Void Miner All Drops";
    public static final String VERSION = Tags.VERSION;
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ArtifactVersion etVersion = Loader.instance().getIndexedModList().get("environmentaltech").getProcessedVersion();
        WeightedStackAccess.initialize(etVersion);
        LOGGER.info("Initialized Void Miner drop reader for Environmental Tech {}", etVersion.getVersionString());
    }
}
