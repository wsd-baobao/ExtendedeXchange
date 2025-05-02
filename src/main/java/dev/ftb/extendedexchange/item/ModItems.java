package dev.ftb.extendedexchange.item;

import dev.ftb.extendedexchange.ExtendedExchange;
import dev.ftb.extendedexchange.Matter;
import dev.ftb.extendedexchange.Star;
import dev.ftb.extendedexchange.block.ModBlocks;
import net.minecraft.Util;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, ExtendedExchange.MOD_ID);

    private static RegistryObject<BlockItem> blockItem(String id, RegistryObject<? extends Block> sup) {
        return REGISTRY.register(id, () -> new BlockItem(sup.get(), new Item.Properties()));
    }

    public static final RegistryObject<BlockItem> ENERGY_LINK = blockItem("energy_link", ModBlocks.ENERGY_LINK);
    public static final RegistryObject<BlockItem> PERSONAL_LINK = blockItem("personal_link", ModBlocks.PERSONAL_LINK);
    public static final RegistryObject<BlockItem> REFINED_LINK = blockItem("refined_link", ModBlocks.REFINED_LINK);
    public static final RegistryObject<BlockItem> COMPRESSED_REFINED_LINK = blockItem("compressed_refined_link", ModBlocks.COMPRESSED_REFINED_LINK);

    public static final Map<Matter, RegistryObject<BlockItem>> COLLECTOR = Util.make(new LinkedHashMap<>(), map -> {
        for (Matter matter : Matter.VALUES) {
            map.put(matter, blockItem(matter.name + "_collector", ModBlocks.COLLECTOR.get(matter)));
        }
    });

    public static final Map<Matter, RegistryObject<BlockItem>> RELAY = Util.make(new LinkedHashMap<>(), map -> {
        for (Matter matter : Matter.VALUES) {
            map.put(matter, blockItem(matter.name + "_relay", ModBlocks.RELAY.get(matter)));
        }
    });

    public static final Map<Matter, RegistryObject<BlockItem>> POWER_FLOWER = Util.make(new LinkedHashMap<>(), map -> {
        for (Matter matter : Matter.VALUES) {
            map.put(matter, blockItem(matter.name + "_power_flower", ModBlocks.POWER_FLOWER.get(matter)));
        }
    });

    public static final RegistryObject<BlockItem> STONE_TABLE = blockItem("stone_table", ModBlocks.STONE_TABLE);
    public static final RegistryObject<BlockItem> ALCHEMY_TABLE = blockItem("alchemy_table", ModBlocks.ALCHEMY_TABLE);

    public static final Map<Star, RegistryObject<Item>> MAGNUM_STAR = Util.make(new LinkedHashMap<>(), map -> {
        for (Star star : Star.VALUES) {
            map.put(star, REGISTRY.register("magnum_star_" + star.getName(), () -> new MagnumStarItem(star)));
        }
    });

    public static final Map<Star, RegistryObject<Item>> COLOSSAL_STAR = Util.make(new LinkedHashMap<>(), map -> {
        for (Star star : Star.VALUES) {
            map.put(star, REGISTRY.register("colossal_star_" + star.getName(), () -> new ColossalStarItem(star)));
        }
    });

    public static final Map<Matter, RegistryObject<Item>> MATTER = Util.make(new LinkedHashMap<>(), map -> {
        for (Matter matter : Matter.VALUES) {
            if (matter.hasMatterItem) {
                map.put(matter, REGISTRY.register(matter.name + "_matter", BasicItem::new));
            }
        }
    });

    public static final RegistryObject<Item> FINAL_STAR_SHARD = REGISTRY.register("final_star_shard", BasicItem::new);
    public static final RegistryObject<Item> FINAL_STAR = REGISTRY.register("final_star", FinalStarItem::new);
    public static final RegistryObject<Item> KNOWLEDGE_SHARING_BOOK = REGISTRY.register("knowledge_sharing_book", KnowledgeSharingBookItem::new);
    public static final RegistryObject<Item> ARCANE_TABLET = REGISTRY.register("arcane_tablet", ArcaneTabletItem::new);

    public static final Map<Matter, RegistryObject<Item>> COMPRESSED_COLLECTOR = Util.make(new LinkedHashMap<>(), map -> {
        for (Matter matter : Matter.VALUES) {
            map.put(matter, REGISTRY.register(matter.name + "_compressed_collector", FoilItem::new));
        }
    });

}
