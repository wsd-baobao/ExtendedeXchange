package dev.ftb.extendedexchange.item;

import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.proxy.IEMCProxy;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class KnowledgeSharingBookItem extends Item {
    public KnowledgeSharingBookItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, level, list, flag);

        list.add(Component.translatable("item.extendedexchange.knowledge_sharing_book.tooltip.1").withStyle(ChatFormatting.GRAY));
        list.add(Component.translatable("item.extendedexchange.knowledge_sharing_book.tooltip.2").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (player.isCrouching()) {
            if (!level.isClientSide()) {
                CompoundTag tag = stack.getOrCreateTag();
                tag.putString("id", player.getStringUUID());
                tag.putString("name", player.getDisplayName().getString());
            }

            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        if (stack.getTag() == null || !stack.getTag().contains("id")) {
            return InteractionResultHolder.fail(stack);
        }

        UUID id = UUID.fromString(stack.getTag().getString("id"));

//        if (id.equals(player.getUUID())) {
//            return InteractionResultHolder.fail(stack);
//        }

        if (!level.isClientSide()) {
            IKnowledgeProvider playerKnowledge = ProjectEAPI.getTransmutationProxy().getKnowledgeProviderFor(player.getUUID());
            IKnowledgeProvider otherKnowledge = ProjectEAPI.getTransmutationProxy().getKnowledgeProviderFor(id);

            int nLearned = 0;
            for (ItemInfo info : otherKnowledge.getKnowledge()) {
                if (playerKnowledge.addKnowledge(info)) nLearned++;
            }

            playerKnowledge.sync((ServerPlayer) player);
            player.displayClientMessage(Component.translatable("item.extendedexchange.knowledge_sharing_book.learned", nLearned)
                    .withStyle(ChatFormatting.GREEN), false);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS,
                    0.8F, 0.8F + level.random.nextFloat() * 0.4F);
        } else {
            for (int i = 0; i < 5; i++) {
                Vec3 vec3d = player.getLookAngle().scale(0.25);
                level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(this)), true,
                        player.getX(), player.getEyeY(), player.getZ(), vec3d.x, vec3d.y + 0.05D, vec3d.z);
            }
        }

        stack.shrink(1);

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getTag() != null && stack.getTag().contains("id");
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.RARE;
    }
}
