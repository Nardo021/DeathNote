package com.yaazyy.deathnote.bukkit.modern;

import com.yaazyy.deathnote.api.DeathNoteRecipeBridge;
import com.yaazyy.deathnote.core.SignatureService;
import com.yaazyy.deathnote.core.config.DeathNoteConfig;
import com.yaazyy.deathnote.bukkit.common.AbstractDeathNotePlugin;
import com.yaazyy.deathnote.bukkit.common.item.AbstractItemBridge;
import com.yaazyy.deathnote.bukkit.common.item.MaterialAdapter;

/**
 * Modern Bukkit/Spigot/Paper plugin entry point (MC 1.13 - latest).
 */
public final class ModernDeathNotePlugin extends AbstractDeathNotePlugin {

    @Override
    protected MaterialAdapter createMaterialAdapter() {
        return new ModernMaterialAdapter();
    }

    @Override
    protected AbstractItemBridge createItemBridge(DeathNoteConfig config,
                                                  SignatureService signatures,
                                                  MaterialAdapter materials) {
        return new ModernItemBridge(this, config, signatures, materials);
    }

    @Override
    protected DeathNoteRecipeBridge createRecipeBridge(MaterialAdapter materials,
                                                       AbstractItemBridge itemBridge,
                                                       DeathNoteConfig config) {
        return new ModernRecipeBridge(this, materials, itemBridge, config);
    }
}
