package dev.ftb.extendedexchange.util;

import dev.ftb.extendedexchange.ExtendedExchange;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.event.PlayerAttemptLearnEvent;
import moze_intel.projecte.api.proxy.IEMCProxy;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import java.math.BigInteger;

public class EXUtils {
    public static final Direction[] DIRECTIONS = Direction.values();

    private static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
    private static final BigInteger MAX_INT = BigInteger.valueOf(Integer.MAX_VALUE);

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(ExtendedExchange.MOD_ID, path);
    }

    public static int mod(int i, int n) {
        i = i % n;
        return i < 0 ? i + n : i;
    }

    public static KnowledgeAddResult addKnowledge(Player player, IKnowledgeProvider knowledgeProvider, ItemStack stack) {
        if (stack.isEmpty() || !IEMCProxy.INSTANCE.hasValue(stack)) {
            return KnowledgeAddResult.NOT_ADDED;
        }

        if (!knowledgeProvider.hasKnowledge(stack)) {
            ItemInfo info = ItemInfo.fromStack(stack);
            ItemInfo cleaned = IEMCProxy.INSTANCE.getPersistentInfo(info);
            if (MinecraftForge.EVENT_BUS.post(new PlayerAttemptLearnEvent(player, info, cleaned))) {
                return KnowledgeAddResult.NOT_ADDED;
            }
            return knowledgeProvider.addKnowledge(stack) ? KnowledgeAddResult.ADDED : KnowledgeAddResult.NOT_ADDED;
        }

        return KnowledgeAddResult.ALREADY_KNOWN;
    }

    public static boolean playerHasKnowledge(Player player, ItemStack stack) {
        return ProjectEAPI.getTransmutationProxy().getKnowledgeProviderFor(player.getUUID()).hasKnowledge(stack);
    }

    public static int bigIntToInt(BigInteger n) {
        return n.compareTo(MAX_INT) > 0 ? Integer.MAX_VALUE : n.intValueExact();
    }

    public enum KnowledgeAddResult {
        NOT_ADDED,
        ALREADY_KNOWN,
        ADDED
    }
}
