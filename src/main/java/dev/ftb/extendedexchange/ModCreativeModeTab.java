package dev.ftb.extendedexchange;


import dev.ftb.extendedexchange.block.ModBlocks;
import dev.ftb.extendedexchange.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.rmi.registry.Registry;

public class ModCreativeModeTab {
    public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, ExtendedExchange.MOD_ID);

    public static final RegistryObject<CreativeModeTab> EXCHANGE_TAB = REGISTRY.register("exchange_tab",
            () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.extendedexchange"))
            .icon(() -> ModItems.ARCANE_TABLET.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        ModItems.REGISTRY.getEntries().stream().map(RegistryObject::get).forEach(output::accept);
                        ModBlocks.REGISTRY.getEntries().stream().map(RegistryObject::get).forEach(output::accept);
                    })

                    .build());


    public static void register(IEventBus bus){
        REGISTRY.register(bus);
    }
 }
