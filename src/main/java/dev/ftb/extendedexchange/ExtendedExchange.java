package dev.ftb.extendedexchange;

import dev.ftb.extendedexchange.block.ModBlocks;
import dev.ftb.extendedexchange.block.entity.ModBlockEntityTypes;
import dev.ftb.extendedexchange.client.ClientSetup;
import dev.ftb.extendedexchange.config.ConfigHolder;
import dev.ftb.extendedexchange.item.ModItems;
import dev.ftb.extendedexchange.menu.ModMenuTypes;
import dev.ftb.extendedexchange.network.NetworkHandler;
import dev.ftb.extendedexchange.recipes.ModRecipeSerializers;
import dev.ftb.extendedexchange.recipes.ModRecipeTypes;
import dev.ftb.extendedexchange.recipes.RecipeCache;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ExtendedExchange.MOD_ID)
public class ExtendedExchange {
    public static final String MOD_ID = "extendedexchange";

    public ExtendedExchange() {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientSetup::initEarly);

        ConfigHolder.init();

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        forgeBus.register(Remapper.class);
//        ModCreativeModeTab.REGISTRY.register(modBus);
        ModCreativeModeTab.register(modBus);
        ModBlocks.REGISTRY.register(modBus);
        ModItems.REGISTRY.register(modBus);
        ModBlockEntityTypes.REGISTRY.register(modBus);
        ModMenuTypes.REGISTRY.register(modBus);
        ModRecipeTypes.REGISTRY.register(modBus);
        ModRecipeSerializers.REGISTRY.register(modBus);

        modBus.addListener(this::commonSetup);

        forgeBus.addListener(this::addReloadListeners);
        forgeBus.addListener(EMCSyncHandler.INSTANCE::onServerTick);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        NetworkHandler.init();
    }

    private void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(RecipeCache.getCacheReloadListener());
    }
}
