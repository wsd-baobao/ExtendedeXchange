package dev.ftb.extendedexchange.offline;

import dev.ftb.extendedexchange.ExtendedExchange;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

public class OfflineKnowledgeManager extends SavedData {
    private static final String DATA_NAME = ExtendedExchange.MOD_ID + ":offline_emc";

    // holds pending EMC loaded from world-saved-data before the actual offline player data is present
    private final Map<UUID, BigInteger> pendingEMC = new HashMap<>();
    // not saved as SavedData; loaded from player's <UUID>.dat file as needed
    private final Map<UUID,OfflineKnowledge> offlineKnowledgeMap = new HashMap<>();

    private static OfflineKnowledgeManager instance;

    private OfflineKnowledgeManager() {
    }

    public static OfflineKnowledgeManager getInstance() {
        if (instance == null) {
            ServerLevel overworld = ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD);
            if (overworld == null) {
                throw new IllegalStateException("Overworld not initialized!");
            }
            instance = overworld.getDataStorage().computeIfAbsent(OfflineKnowledgeManager::load, OfflineKnowledgeManager::new, DATA_NAME);
        }
        return instance;
    }

    private static OfflineKnowledgeManager load(CompoundTag tag) {
        OfflineKnowledgeManager manager = new OfflineKnowledgeManager();
        CompoundTag sub = tag.getCompound("EMC");
        for (String k : sub.getAllKeys()) {
            UUID id = UUID.fromString(k);
            manager.pendingEMC.put(id, new BigInteger(sub.getString(k)));
        }
        return manager;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        CompoundTag sub = new CompoundTag();
        offlineKnowledgeMap.forEach((id, knowledge) -> sub.putString(id.toString(), knowledge.getEmc().toString()));
        compoundTag.put("EMC", sub);
        return compoundTag;
    }

    public IKnowledgeProvider getKnowledgeProviderFor(Level level, UUID id) {
        if (level.isClientSide) {
            return null;
        } else if (level.getServer() != null) {
            Player player = level.getServer().getPlayerList().getPlayer(id);
            if (player != null) {
                return getOnlineProvider(player);
            } else {
                return getOfflineProvider(id);
            }
        }
        return null;
    }

    @Nullable
    private OfflineKnowledge getOfflineProvider(UUID id) {
        OfflineKnowledge knowledge = offlineKnowledgeMap.get(id);
        if (knowledge != null) return knowledge;

        // load saved knowledge from player's <UUID>.dat file
        OfflineKnowledge newKnowledge = OfflineKnowledge.forPlayerId(id);
        if (newKnowledge != null) {
            // pendingEMC contains possible EMC saved from the previous server run, which needs to be copied in
            if (pendingEMC.containsKey(id)) {
                newKnowledge.setEmc(pendingEMC.get(id));
                pendingEMC.remove(id);
            }
            offlineKnowledgeMap.put(id, newKnowledge);
        }
        return newKnowledge;
    }

    @Nullable
    private IKnowledgeProvider getOnlineProvider(Player player) {
        try {
            return player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY).orElseThrow(NoSuchElementException::new);
        } catch (NoSuchElementException e) {
            // ugly, but capability can be missing if called immediately after player death
            return null;
        }
    }

    public void onEmcChanged() {
        setDirty();
    }

    private void onPlayerConnect(Player player, IKnowledgeProvider handler) {
        UUID id = player.getUUID();
        if (offlineKnowledgeMap.containsKey(id)) {
            // we get here if some machine has queried offline data between server starting, and player connecting
            //   copy back offline EMC data to the live player capability and clear it
            handler.setEmc(offlineKnowledgeMap.get(id).getEmc());
            offlineKnowledgeMap.remove(id);
        } else if (pendingEMC.containsKey(id)) {
            // we get here if player logs in with saved EMC data from previous server run,
            //   but nothing has caused the offline data for the player to be loaded yet (unlikely but possible)
            handler.setEmc(pendingEMC.get(id));
            pendingEMC.remove(id);
            setDirty();
        }
    }

    @Mod.EventBusSubscriber(modid = ExtendedExchange.MOD_ID)
    public static class Listener {
        @SubscribeEvent
        public static void onPlayerConnect(PlayerEvent.PlayerLoggedInEvent event) {
            if (!event.getEntity().level().isClientSide()) {
                event.getEntity().getCapability(PECapabilities.KNOWLEDGE_CAPABILITY)
                        .ifPresent(handler -> OfflineKnowledgeManager.getInstance().onPlayerConnect(event.getEntity(), handler));
            }
        }
    }
}
