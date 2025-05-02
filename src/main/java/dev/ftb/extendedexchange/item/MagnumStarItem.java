package dev.ftb.extendedexchange.item;

import dev.ftb.extendedexchange.Star;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.capability.EmcHolderItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.items.IBarHelper;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.integration.IntegrationHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import javax.annotation.Nonnull;

public class MagnumStarItem extends ItemPE implements IItemEmcHolder, IBarHelper {
    public final Star tier;

    public MagnumStarItem(Star tier) {
        super(new Properties().stacksTo(1));

        this.tier = tier;

        addItemCapability(EmcHolderItemCapabilityWrapper::new);
        addItemCapability("curios", IntegrationHelper.CURIO_CAP_SUPPLIER);
    }

    @Override
    public boolean isBarVisible(@Nonnull ItemStack stack) {
        return stack.hasTag();
    }

    @Override
    public float getWidthForBar(ItemStack stack) {
        long starEmc = getStoredEmc(stack);
        return (float) (starEmc == 0L ? 1D : 1D - (double) starEmc / (double) getMaximumEmc(stack));
    }

    @Override
    public int getBarWidth(@Nonnull ItemStack stack) {
        return this.getScaledBarWidth(stack);
    }

    @Override
    public int getBarColor(@Nonnull ItemStack stack) {
        return this.getColorForBar(stack);
    }

    @Override
    public long insertEmc(@Nonnull ItemStack stack, long toInsert, IEmcStorage.EmcAction action) {
        if (toInsert < 0L) {
            return this.extractEmc(stack, -toInsert, action);
        } else {
            long toAdd = Math.min(this.getNeededEmc(stack), toInsert);
            if (action.execute()) {
                addEmcToStack(stack, toAdd);
            }

            return toAdd;
        }
    }

    @Override
    public long extractEmc(@Nonnull ItemStack stack, long toExtract, IEmcStorage.EmcAction action) {
        if (toExtract < 0L) {
            return this.insertEmc(stack, -toExtract, action);
        } else {
            long storedEmc = this.getStoredEmc(stack);
            long toRemove = Math.min(storedEmc, toExtract);
            if (action.execute()) {
                setEmc(stack, storedEmc - toRemove);
            }

            return toRemove;
        }
    }

    @Override
    public long getStoredEmc(@Nonnull ItemStack stack) {
        return getEmc(stack);
    }

    @Override
    public long getMaximumEmc(@Nonnull ItemStack stack) {
        return tier.getMaxEMC(false);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return tier == Star.OMEGA ? Rarity.EPIC : super.getRarity(stack);
    }
}
