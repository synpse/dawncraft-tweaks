package com.wackteam.tweaks.dawncraft;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("dawncrafttweaks")
public class DawncraftTweaks {

    public DawncraftTweaks() {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        String deathCause = event.getSource().getMsgId();

        // Delay the execution to ensure the corpse is spawned
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> removeXPFromCorpse(player, deathCause), 300, TimeUnit.MILLISECONDS);
    }

    private void removeXPFromCorpse(ServerPlayer player, String deathCause) {
        UUID senderUUID = player.getUUID();
        UUID inacioUUID = UUID.fromString("a0745351-3ebf-4c30-a844-ca116607fc9a");

        if (deathCause.equals("fall")) {
            if (senderUUID.equals(inacioUUID)) {
                player.sendMessage(new TextComponent("MAIS UMA VEZ ESCORREGASTE NA BANANA. TU NÃO APRENDES CARA DE PICHOTA?! QUERIAS EXP? MAMA AQUI NO CARALHO"), senderUUID);
                player.sendMessage(new TextComponent("MAIS UMA VEZ ESCORREGASTE NA BANANA. TU NÃO APRENDES CARA DE PICHOTA?! QUERIAS EXP? MAMA AQUI NO CARALHO"), senderUUID);
                player.sendMessage(new TextComponent("MAIS UMA VEZ ESCORREGASTE NA BANANA. TU NÃO APRENDES CARA DE PICHOTA?! QUERIAS EXP? MAMA AQUI NO CARALHO"), senderUUID);
                player.sendMessage(new TextComponent("MAIS UMA VEZ ESCORREGASTE NA BANANA. TU NÃO APRENDES CARA DE PICHOTA?! QUERIAS EXP? MAMA AQUI NO CARALHO"), senderUUID);
                player.sendMessage(new TextComponent("MAIS UMA VEZ ESCORREGASTE NA BANANA. TU NÃO APRENDES CARA DE PICHOTA?! QUERIAS EXP? MAMA AQUI NO CARALHO"), senderUUID);
            }
        }

        player.getServer().execute(() -> {
            List<Entity> corpses = player.getLevel().getEntities(null, player.getBoundingBox().inflate(5));

            for (Entity entity : corpses) {
                    
                CompoundTag entityData = entity.serializeNBT();
                if (entityData.contains("Death")) {
                    CompoundTag deathData = entityData.getCompound("Death");
                    if (deathData.contains("Items")) {
                        ListTag itemsList = deathData.getList("Items", 10); // 10 = CompoundTag type
                        boolean removedXP = false;

                        for (int i = 0; i < itemsList.size(); i++) {
                            CompoundTag itemTag = itemsList.getCompound(i);
                            if (itemTag.getString("id").equals("dawncraft:crystallized_xp")) {
                                itemsList.remove(i);
                                removedXP = true;
                                break; // Stop after removing one occurrence
                            }
                        }

                        if (removedXP) {
                            deathData.put("Items", itemsList);
                            entityData.put("Death", deathData);
                            entity.deserializeNBT(entityData);
                        }
                    }
                }
            }
        });
    }
}
