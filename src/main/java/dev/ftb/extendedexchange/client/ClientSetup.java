package dev.ftb.extendedexchange.client;

import dev.ftb.extendedexchange.client.gui.*;
import dev.ftb.extendedexchange.menu.ModMenuTypes;
import dev.ftb.extendedexchange.menu.StoneTableMenu;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
//@SuppressWarnings("removal")
public class ClientSetup {
    public static void initEarly() {
        // run on mod construction
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::init);
    }

    static void init(FMLClientSetupEvent event) {
        event.enqueueWork(ClientSetup::initLate);
    }

    private static void initLate() {
        // stuff that needs doing on the main thread
        registerScreenFactories();
    }

    private static void registerScreenFactories() {
        MenuScreens.register(ModMenuTypes.PERSONAL_LINK.get(), PersonalLinkScreen::new);
        MenuScreens.register(ModMenuTypes.REFINED_LINK.get(), RefinedLinkScreen::new);
        MenuScreens.register(ModMenuTypes.COMPRESSED_REFINED_LINK.get(), CompressedRefinedLinkScreen::new);
        MenuScreens.register(ModMenuTypes.ALCHEMY_TABLE.get(), AlchemyTableScreen::new);
        MenuScreens.register(ModMenuTypes.STONE_TABLE.get(), StoneTableScreen::new);
        MenuScreens.register(ModMenuTypes.ARCANE_TABLET.get(), ArcaneTabletScreen::new);
    }
}
