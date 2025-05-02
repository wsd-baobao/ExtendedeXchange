package dev.ftb.extendedexchange.menu;

import dev.ftb.extendedexchange.ExtendedExchange;
import dev.ftb.extendedexchange.block.entity.AbstractEMCBlockEntity;
import dev.ftb.extendedexchange.network.NetworkHandler;
import dev.ftb.extendedexchange.network.PacketNotifyKnowledgeChange;
import dev.ftb.extendedexchange.util.EXUtils;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.config.ProjectEConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

public abstract class AbstractTableMenu extends AbstractEXMenu<AbstractEMCBlockEntity> implements IGuiButtonListener {
    private static final BigInteger MAX_INT = BigInteger.valueOf(Integer.MAX_VALUE);

    protected final Player player;
    protected final IKnowledgeProvider provider;

    public AbstractTableMenu(MenuType<?> type, int windowId, Inventory invPlayer, BlockPos pos) {
        super(type, windowId, invPlayer, pos);

        this.player = invPlayer.player;
        this.provider = ProjectEAPI.getTransmutationProxy().getKnowledgeProviderFor(player.getUUID());
    }

    @NotNull
    @Override
    protected Class<AbstractEMCBlockEntity> blockEntityClass() {
        return AbstractEMCBlockEntity.class;
    }

    @Override
    public void handleGUIButtonPress(String tag, boolean shiftHeld, ServerPlayer player) {
        switch (tag) {
            case "learn" -> learnItem();
            case "unlearn" -> unlearnItem();
            case "burn" -> burnItem(shiftHeld);
        }
        if (tag.startsWith("extract:")) tryExtractItem(tag.substring(8), shiftHeld);
    }

    private void tryExtractItem(String itemId, boolean pullStack) {
        ResourceLocation id = new ResourceLocation(itemId);
        Item item = ForgeRegistries.ITEMS.getValue(id);
        if (item != null && item != Items.AIR) {
            BigInteger availableEMC = provider.getEmc();
            BigInteger emc = BigInteger.valueOf(IEMCProxy.INSTANCE.getValue(item));
            if (emc.equals(BigInteger.ZERO)) {
                return;
            }
            BigInteger bigAvail = availableEMC.divide(emc);
            int available = bigAvail.compareTo(MAX_INT) >= 0 ? Integer.MAX_VALUE : bigAvail.intValue();
            if (available > 0) {
                ItemStack stack = new ItemStack(item);
                BigInteger cost = BigInteger.ZERO;
                if (pullStack) {
                    stack.setCount(Math.min(stack.getMaxStackSize(), available));
                    ItemHandlerHelper.giveItemToPlayer(player, stack);
                    cost = emc.multiply(BigInteger.valueOf(stack.getCount()));
                } else {
                    if (getCarried().isEmpty()) {
                        setCarried(stack);
                        cost = emc;
                    } else if (getCarried().getCount() < getCarried().getMaxStackSize()) {
                        getCarried().grow(1);
                        cost = emc;
                    }
                }
                if (!cost.equals(BigInteger.ZERO)) {
                    provider.setEmc(availableEMC.subtract(cost));
                    provider.syncEmc((ServerPlayer) player);
                }
            }
        }
    }

    private void tryAddKnowledge(ItemStack stack) {
        if (EXUtils.addKnowledge(player, provider, stack) == EXUtils.KnowledgeAddResult.ADDED) {
            provider.syncKnowledgeChange((ServerPlayer) player, ItemInfo.fromStack(stack), true);
            NetworkHandler.sendToPlayer((ServerPlayer) player, new PacketNotifyKnowledgeChange());
        }
    }

    private void burnItem(boolean storedEMConly) {
        ItemStack cursorStack = getCarried();
        if (!cursorStack.isEmpty()) {
            if (storedEMConly && cursorStack.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY).isPresent()) {
                System.out.println("点击shift的情况下。");
                cursorStack.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY).ifPresent(handler -> {
                    long extracted = handler.extractEmc(cursorStack, handler.getMaximumEmc(cursorStack), IEmcStorage.EmcAction.EXECUTE);
                    provider.setEmc(provider.getEmc().add(BigInteger.valueOf(extracted)));
                    provider.syncEmc((ServerPlayer) player);
                });
            } else if (IEMCProxy.INSTANCE.hasValue(cursorStack)) {
                ItemStack fixed = IEMCProxy.INSTANCE.getPersistentInfo(ItemInfo.fromStack(cursorStack)).createStack();
                if (isItemValid(fixed)) {
                    tryAddKnowledge(fixed);
                    long toAdd = (long) (IEMCProxy.INSTANCE.getValue(fixed) * cursorStack.getCount() * ProjectEConfig.server.difficulty.covalenceLoss.get());
                    provider.setEmc(provider.getEmc().add(BigInteger.valueOf(toAdd)));
                    provider.syncEmc((ServerPlayer) player);
                    setCarried(ItemStack.EMPTY);
                }
            }
        }
    }

    private void learnItem() {
        if (!getCarried().isEmpty()) {
            ItemStack fixed = IEMCProxy.INSTANCE.getPersistentInfo(ItemInfo.fromStack(getCarried())).createStack();
            tryAddKnowledge(fixed);
        }
    }

    private void unlearnItem() {
        if (!getCarried().isEmpty()) {
            ItemStack fixed = IEMCProxy.INSTANCE.getPersistentInfo(ItemInfo.fromStack(getCarried())).createStack();
            if (provider.removeKnowledge(fixed)) {
                provider.syncKnowledgeChange((ServerPlayer) player, ItemInfo.fromStack(fixed), false);
                NetworkHandler.sendToPlayer((ServerPlayer) player, new PacketNotifyKnowledgeChange());
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        ItemStack stack = slot.getItem();

        if (player instanceof ServerPlayer serverPlayer && index >= playerSlotsStart && !stack.isEmpty()) {
            if (!IEMCProxy.INSTANCE.hasValue(stack)) {
                return ItemStack.EMPTY;
            }

            ItemStack fixed = IEMCProxy.INSTANCE.getPersistentInfo(ItemInfo.fromStack(stack)).createStack();
            if (!isItemValid(fixed)) {
                return ItemStack.EMPTY;
            }

            tryAddKnowledge(fixed);

            long toAdd = (long) (IEMCProxy.INSTANCE.getValue(stack) * stack.getCount() * ProjectEConfig.server.difficulty.covalenceLoss.get());
            provider.setEmc(provider.getEmc().add(BigInteger.valueOf(toAdd)));
            provider.syncEmc(serverPlayer);
            slot.set(ItemStack.EMPTY);
            return fixed;
        }

        return ItemStack.EMPTY;
    }

    public boolean isItemValid(ItemStack stack) {
        return true;
    }

    public IKnowledgeProvider getProvider() {
        return provider;
    }

}
