package dev.ftb.extendedexchange.item;

import dev.ftb.extendedexchange.config.ConfigHelper;
import dev.ftb.extendedexchange.util.EXUtils;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.block_entity.IDMPedestal;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.capability.EmcHolderItemCapabilityWrapper;
import moze_intel.projecte.capability.PedestalItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.items.ItemPE;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import javax.annotation.Nullable;
import java.util.List;

public class FinalStarItem extends ItemPE implements IItemEmcHolder, IPedestalItem {
    public FinalStarItem() {
        super(new Properties().stacksTo(1));

        addItemCapability(EmcHolderItemCapabilityWrapper::new);
        addItemCapability(PedestalItemCapabilityWrapper::new);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, level, list, flag);

        list.add(Component.translatable("item.extendedexchange.final_star.tooltip").withStyle(ChatFormatting.GRAY));
    }

//    @Override
//    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
//        if (!level.isClientSide() && player.isCrouching()) {
//            player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY).ifPresent(provider -> {
//                provider.setEmc(BigInteger.ZERO);
//                provider.syncEmc((ServerPlayer) player);
//            });
//            return InteractionResultHolder.success(player.getItemInHand(hand));
//        }
//
//        return super.use(level, player, hand);
//    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.EPIC;
    }

    @Override
    public <PEDESTAL extends BlockEntity & IDMPedestal> boolean updateInPedestal(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull PEDESTAL pedestal) {
        int interval = ConfigHelper.server().general.finalStarUpdateInterval.get();
        if (interval <= 0) {
            return false;
        }

        if (!level.isClientSide && level.getGameTime() % (long) interval == EXUtils.mod(blockPos.hashCode(), interval)) {
            List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, new AABB(blockPos).inflate(0D, 1D, 0D));
            if (!items.isEmpty()) {
                // TODO capability caching
                for (Direction facing : EXUtils.DIRECTIONS) {
                    if (facing != Direction.UP) {
                        BlockEntity be = level.getBlockEntity(blockPos.relative(facing));
                        boolean inserted = be != null && be.getCapability(ForgeCapabilities.ITEM_HANDLER, facing.getOpposite()).map(handler -> {
                            ItemStack stack = items.get(level.random.nextInt(items.size())).getItem();
                            if (ConfigHelper.server().general.finalStarCopiesAnyItem.get() || IEMCProxy.INSTANCE.hasValue(stack)) {
                                ItemStack toInsert = ItemHandlerHelper.copyStackWithSize(stack, stack.getMaxStackSize());
                                if (!ConfigHelper.server().general.finalStarCopiesNBT.get() && toInsert.hasTag() && !toInsert.is(PETags.Items.NBT_WHITELIST)) {
                                    toInsert.setTag(new CompoundTag());
                                }
                                ItemHandlerHelper.insertItem(handler, toInsert, false);
                                return true;
                            }
                            return false;
                        }).orElse(false);
                        if (inserted) return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public @NotNull List<Component> getPedestalDescription() {
        return List.of(Component.translatable("item.extendedexchange.final_star.pedestal"));
    }

    @Override
    public long insertEmc(@NotNull ItemStack itemStack, long l, IEmcStorage.EmcAction emcAction) {
        return 0L;
    }

    @Override
    public long extractEmc(@NotNull ItemStack itemStack, long l, IEmcStorage.EmcAction emcAction) {
        return l;
    }

    @Override
    public @Range(from = 0L, to = 9223372036854775807L) long getStoredEmc(@NotNull ItemStack itemStack) {
        return 1_000_000_000_000_000L;
    }

    @Override
    public @Range(from = 1L, to = 9223372036854775807L) long getMaximumEmc(@NotNull ItemStack itemStack) {
        return Long.MAX_VALUE;
    }

}
