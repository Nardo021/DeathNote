package com.yaazyy.deathnote.bukkit.legacy;

import com.yaazyy.deathnote.api.DeathNoteRecipeBridge;
import com.yaazyy.deathnote.core.SignatureService;
import com.yaazyy.deathnote.core.config.DeathNoteConfig;
import com.yaazyy.deathnote.bukkit.common.AbstractDeathNotePlugin;
import com.yaazyy.deathnote.bukkit.common.item.AbstractItemBridge;
import com.yaazyy.deathnote.bukkit.common.item.MaterialAdapter;

/**
 * Legacy Bukkit/Spigot plugin entry point (runtime target MC 1.8.x - 1.12.2).
 */
public final class LegacyDeathNotePlugin extends AbstractDeathNotePlugin {

    @Override
    protected MaterialAdapter createMaterialAdapter() {
        return new LegacyMaterialAdapter();
    }

    @Override
    protected AbstractItemBridge createItemBridge(DeathNoteConfig config,
                                                  SignatureService signatures,
                                                  MaterialAdapter materials) {
        return new LegacyItemBridge(config, signatures, materials);
    }

    @Override
    protected DeathNoteRecipeBridge createRecipeBridge(MaterialAdapter materials,
                                                       AbstractItemBridge itemBridge,
                                                       DeathNoteConfig config) {
        return new LegacyRecipeBridge(materials, itemBridge, config);
    }
}
