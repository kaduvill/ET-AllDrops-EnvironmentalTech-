package com.kaduvill.etvoidminerall;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = ETVoidMinerAllDrops.MOD_ID, name = ETVoidMinerAllDrops.MOD_NAME, version = ETVoidMinerAllDrops.VERSION,
        acceptedMinecraftVersions = "[1.12.2]", dependencies = "required-after:environmentaltech@[1.12.2-2.0.20.1,);",
        clientSideOnly = true, acceptableRemoteVersions = "*")
public final class ETVoidMinerAllDrops {
    public static final String MOD_ID = "etvoidminerall";
    public static final String MOD_NAME = "Environmental Tech Void Miner All Drops";
    public static final String VERSION = "0.1.1";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
}
