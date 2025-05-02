package dev.ftb.extendedexchange.datagen;

import dev.ftb.extendedexchange.Matter;
import dev.ftb.extendedexchange.Star;
import dev.ftb.extendedexchange.datagen.recipes.AlchemyTableRecipeBuilder;
import dev.ftb.extendedexchange.datagen.recipes.Criteria;
import dev.ftb.extendedexchange.item.ModItems;
import dev.ftb.extendedexchange.recipes.AlchemyTableRecipe;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.Validate;

import java.util.function.Consumer;

import static dev.ftb.extendedexchange.util.EXUtils.rl;

class ModRecipeProvider extends RecipeProvider {
    // public final Tag<Item> CAST_IRON_GEAR = ItemTags.bind("forge:gears/cast_iron");

    public ModRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn.getPackOutput());
    }

//    @Override
//    protected void buildRecipes(Consumer<FinishedRecipe> writer) {
//
//    }
    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        /*
        ShapedRecipeBuilder.shaped(FTBJarModItems.CAST_IRON_BLOCK.get())
                .unlockedBy("has_item", has(CAST_IRON_INGOT))
                .group(MODID + ":cast_iron")
                .pattern("III")
                .pattern("III")
                .pattern("III")
                .define('I', CAST_IRON_INGOT)
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(FTBJarModItems.CAST_IRON_NUGGET.get(), 9)
                .unlockedBy("has_item", has(CAST_IRON_INGOT))
                .group(MODID + ":cast_iron")
                .requires(CAST_IRON_INGOT)
                .save(consumer, new ResourceLocation(MODID, "cast_iron_nugget_from_ingot"));

        SimpleCookingRecipeBuilder.cooking(Ingredient.of(IRON_INGOT), FTBJarModItems.CAST_IRON_INGOT.get(), 0.1F, 600, RecipeSerializer.CAMPFIRE_COOKING_RECIPE)
                .unlockedBy("has_item", has(IRON_INGOT))
                .save(consumer, new ResourceLocation(MODID, "cast_iron_ingot_from_smelting"));
         */

        for (Matter matter : Matter.VALUES) {
            if (matter.hasMatterItem && matter.getPrev() != null) {
                Item prevMatterItem = matter.getPrev().getItem().get();

                ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS,matter.getItem().get())
                        .unlockedBy("has_item", has(prevMatterItem))
                        .group(EXDataGen.MODID + ":matter/" + matter.name)
                        .pattern("FFF")
                        .pattern("MMM")
                        .pattern("FFF")
                        .define('F', PEItems.AETERNALIS_FUEL)
                        .define('M', prevMatterItem)
                        .save(consumer, rl("matter_h/" + matter.name));

                ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS,matter.getItem().get())
                        .unlockedBy("has_item", has(prevMatterItem))
                        .group(EXDataGen.MODID + ":matter/" + matter.name)
                        .pattern("FMF")
                        .pattern("FMF")
                        .pattern("FMF")
                        .define('F', PEItems.AETERNALIS_FUEL)
                        .define('M', prevMatterItem)
                        .save(consumer, rl("matter_v/" + matter.name));
            }
        }

        for (Matter matter : Matter.VALUES) {
            Item collector = ModItems.COLLECTOR.get(matter).get();
            Item relay = ModItems.RELAY.get(matter).get();
            Item powerFlower = ModItems.POWER_FLOWER.get(matter).get();
            Item compressedCollector = ModItems.COMPRESSED_COLLECTOR.get(matter).get();

            Matter prev = matter.getPrev();

            if (prev != null) {
                Item matterItem = matter.getItem().get();

                ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS,collector)
                        .unlockedBy("has_item", has(matterItem))
                        .group(EXDataGen.MODID + ":matter/" + matter.name)
                        .requires(ModItems.COLLECTOR.get(prev).get())
                        .requires(matterItem)
                        .save(consumer, rl("collector/" + matter.name));

                ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS,relay)
                        .unlockedBy("has_item", has(matterItem))
                        .group(EXDataGen.MODID + ":matter/" + matter.name)
                        .requires(ModItems.RELAY.get(prev).get())
                        .requires(matterItem)
                        .save(consumer, rl("relay/" + matter.name));

                ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS,powerFlower)
                        .unlockedBy("has_item", has(matterItem))
                        .group(EXDataGen.MODID + ":matter/" + matter.name)
                        .pattern("ADA")
                        .pattern("ARA")
                        .pattern("AAA")
                        .define('A', relay)
                        .define('D', collector)
                        .define('R', ModItems.POWER_FLOWER.get(prev).get())
                        .save(consumer, rl("power_flower/" + matter.name + "_upgrade"));
            }

            ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS,compressedCollector)
                    .unlockedBy("has_item", has(collector))
                    .group(EXDataGen.MODID + ":matter/" + matter.name)
                    .pattern("CCC")
                    .pattern("CCC")
                    .pattern("CCC")
                    .define('C', collector)
                    .save(consumer, rl("compressed_collector/" + matter.name));

            ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS,powerFlower)
                    .unlockedBy("has_item", has(compressedCollector))
                    .group(EXDataGen.MODID + ":matter/" + matter.name)
                    .pattern("CLC")
                    .pattern("RRR")
                    .pattern("RRR")
                    .define('L', ModItems.ENERGY_LINK.get())
                    .define('C', compressedCollector)
                    .define('R', relay)
                    .save(consumer, rl("power_flower/" + matter.name));
        }

        for (Star star : Star.VALUES) {
            if (star.getPrev() != null) {
                Item prevMagnum = ModItems.MAGNUM_STAR.get(star.getPrev()).get();
                Item prevColossal = ModItems.COLOSSAL_STAR.get(star.getPrev()).get();

                ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS,ModItems.MAGNUM_STAR.get(star).get())
                        .unlockedBy("has_item", has(prevMagnum))
                        .group(EXDataGen.MODID + ":magnum_star")
                        .requires(prevMagnum)
                        .requires(prevMagnum)
                        .requires(prevMagnum)
                        .requires(prevMagnum)
                        .save(consumer, rl("magnum_star/" + star.getName()));

                ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS,ModItems.COLOSSAL_STAR.get(star).get())
                        .unlockedBy("has_item", has(prevColossal))
                        .group(EXDataGen.MODID + ":colossal_star")
                        .requires(prevColossal)
                        .requires(prevColossal)
                        .requires(prevColossal)
                        .requires(prevColossal)
                        .save(consumer, rl("colossal_star/" + star.getName()));
            }
        }

        Item startMagnum = PEItems.KLEIN_STAR_OMEGA.get();
        Item startColossal = ModItems.MAGNUM_STAR.get(Star.OMEGA).get();

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS,ModItems.MAGNUM_STAR.get(Star.EIN).get())
                .unlockedBy("has_item", has(startMagnum))
                .group(EXDataGen.MODID + ":magnum_star")
                .requires(startMagnum)
                .requires(startMagnum)
                .requires(startMagnum)
                .requires(startMagnum)
                .save(consumer, rl("magnum_star/ein"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS,ModItems.COLOSSAL_STAR.get(Star.EIN).get())
                .unlockedBy("has_item", has(startColossal))
                .group(EXDataGen.MODID + ":colossal_star")
                .requires(startColossal)
                .requires(startColossal)
                .requires(startColossal)
                .requires(startColossal)
                .save(consumer, rl("colossal_star/ein"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS,ModItems.ALCHEMY_TABLE.get())
                .unlockedBy("has_item", has(ModItems.STONE_TABLE.get()))
                .group(EXDataGen.MODID + ":alchemy_table")
                .pattern("123")
                .pattern("TST")
                .pattern("LDL")
                .define('1', PEItems.LOW_COVALENCE_DUST)
                .define('2', PEItems.MEDIUM_COVALENCE_DUST)
                .define('3', PEItems.HIGH_COVALENCE_DUST)
                .define('T', Items.TORCH)
                .define('S', ModItems.STONE_TABLE.get())
                .define('D', Tags.Items.GEMS_DIAMOND)
                .define('L', Tags.Items.RODS_WOODEN)
                .save(consumer, rl("alchemy_table"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS,ModItems.ARCANE_TABLET.get())
                .unlockedBy("has_item", has(ModItems.STONE_TABLE.get()))
                .group(EXDataGen.MODID + ":arcane_tablet")
                .pattern("TWT")
                .pattern("MSM")
                .pattern("TCT")
                .define('T', ModItems.STONE_TABLE.get())
                .define('W', Items.CRAFTING_TABLE)
                .define('M', PEItems.TRANSMUTATION_TABLET)
                .define('S', ModItems.MAGNUM_STAR.get(Star.EIN).get())
                .define('C', Tags.Items.CHESTS_WOODEN)
                .save(consumer, rl("arcane_tablet"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS,ModItems.COMPRESSED_REFINED_LINK.get())
                .unlockedBy("has_item", has(ModItems.REFINED_LINK.get()))
                .group(EXDataGen.MODID + ":link")
                .pattern("LLL")
                .pattern("LLL")
                .define('L', ModItems.REFINED_LINK.get())
                .save(consumer, rl("compressed_refined_link"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS,ModItems.ENERGY_LINK.get())
                .unlockedBy("has_item", has(PEItems.RED_MATTER))
                .group(EXDataGen.MODID + ":link")
                .pattern("LMH")
                .pattern("SRS")
                .pattern("HML")
                .define('L', PEItems.LOW_COVALENCE_DUST)
                .define('M', PEItems.MEDIUM_COVALENCE_DUST)
                .define('H', PEItems.HIGH_COVALENCE_DUST)
                .define('S', Tags.Items.STONE)
                .define('R', PEItems.RED_MATTER)
                .save(consumer, rl("energy_link"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS,ModItems.FINAL_STAR.get())
                .unlockedBy("has_item", has(ModItems.POWER_FLOWER.get(Matter.FINAL).get()))
                .group(EXDataGen.MODID + ":star")
                .pattern("PPP")
                .pattern("PEP")
                .pattern("PPP")
                .define('P', ModItems.POWER_FLOWER.get(Matter.FINAL).get())
                .define('E', Items.DRAGON_EGG)
                .save(consumer, rl("final_star"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS,ModItems.FINAL_STAR_SHARD.get())
                .unlockedBy("has_item", has(ModItems.COLOSSAL_STAR.get(Star.OMEGA).get()))
                .group(EXDataGen.MODID + ":star")
                .pattern("CCC")
                .pattern("CSC")
                .pattern("CCC")
                .define('C', ModItems.COLOSSAL_STAR.get(Star.OMEGA).get())
                .define('S', Items.NETHER_STAR)
                .save(consumer, rl("final_star_shard"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS,ModItems.KNOWLEDGE_SHARING_BOOK.get())
                .unlockedBy("has_item", has(ModItems.MATTER.get(Matter.VIOLET).get()))
                .group(EXDataGen.MODID + ":tome")
                .pattern("RNR")
                .pattern("NBN")
                .pattern("RNR")
                .define('B', Items.WRITABLE_BOOK)
                .define('R', Matter.VIOLET.getItem().get())
                .define('N', Items.NETHER_STAR)
                .save(consumer, rl("knowledge_sharing_book"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS,ModItems.PERSONAL_LINK.get())
                .unlockedBy("has_item", has(ModItems.ENERGY_LINK.get()))
                .group(EXDataGen.MODID + ":link")
                .pattern("RBR")
                .pattern("BCB")
                .pattern("RBR")
                .define('B', ModItems.ENERGY_LINK.get())
                .define('R', Matter.RED.getItem().get())
                .define('C', PEBlocks.CONDENSER_MK2)
                .save(consumer, rl("personal_link"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS,ModItems.REFINED_LINK.get())
                .unlockedBy("has_item", has(ModItems.PERSONAL_LINK.get()))
                .group(EXDataGen.MODID + ":link")
                .pattern("LLL")
                .pattern("LLL")
                .pattern("LLL")
                .define('L', ModItems.PERSONAL_LINK.get())
                .save(consumer, rl("refined_link"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS,ModItems.STONE_TABLE.get())
                .unlockedBy("has_item", has(PEItems.PHILOSOPHERS_STONE))
                .group(EXDataGen.MODID + ":stone_table")
                .pattern("BBB")
                .pattern("BPB")
                .pattern("BBB")
                .define('B', Items.STONE_BRICKS)
                .define('P', PEItems.PHILOSOPHERS_STONE)
                .save(consumer, rl("stone_table_1"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS,PEItems.TOME_OF_KNOWLEDGE)
                .unlockedBy("has_item", has(ModItems.KNOWLEDGE_SHARING_BOOK.get()))
                .group(EXDataGen.MODID + ":stone_table")
                .pattern("BBB")
                .pattern("BSB")
                .pattern("BBB")
                .define('B', ModItems.KNOWLEDGE_SHARING_BOOK.get())
                .define('S', ModItems.FINAL_STAR_SHARD.get())
                .save(consumer, rl("tome_of_knowledge"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS,ModItems.COLLECTOR.get(Matter.BASIC).get())
                .unlockedBy("has_item", has(PEBlocks.AETERNALIS_FUEL))
                .group(EXDataGen.MODID + ":matter/basic")
                .pattern("GSG")
                .pattern("GAG")
                .pattern("GFG")
                .define('G', Items.GLOWSTONE)
                .define('S', Tags.Items.GLASS)
                .define('A', PEBlocks.AETERNALIS_FUEL)
                .define('F', Items.FURNACE)
                .save(consumer, rl("collector/basic"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS,ModItems.COLLECTOR.get(Matter.BASIC).get())
                .unlockedBy("has_item", has(PEBlocks.COLLECTOR))
                .group(EXDataGen.MODID + ":matter/basic")
                .requires(PEBlocks.COLLECTOR)
                .save(consumer, rl("collector/basic_2"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS,ModItems.COLLECTOR.get(Matter.DARK).get())
                .unlockedBy("has_item", has(PEBlocks.COLLECTOR_MK2))
                .group(EXDataGen.MODID + ":matter/dark")
                .requires(PEBlocks.COLLECTOR_MK2)
                .save(consumer, rl("collector/dark_2"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS,ModItems.COLLECTOR.get(Matter.RED).get())
                .unlockedBy("has_item", has(PEBlocks.COLLECTOR_MK3))
                .group(EXDataGen.MODID + ":matter/red")
                .requires(PEBlocks.COLLECTOR_MK3)
                .save(consumer, rl("collector/red_2"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS,ModItems.RELAY.get(Matter.BASIC).get())
                .unlockedBy("has_item", has(PEBlocks.AETERNALIS_FUEL))
                .group(EXDataGen.MODID + ":matter/basic")
                .pattern("OSO")
                .pattern("OAO")
                .pattern("OOO")
                .define('O', Items.OBSIDIAN)
                .define('S', Tags.Items.GLASS)
                .define('A', PEBlocks.AETERNALIS_FUEL)
                .save(consumer, rl("relay/basic"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS,ModItems.RELAY.get(Matter.BASIC).get())
                .unlockedBy("has_item", has(PEBlocks.RELAY))
                .group(EXDataGen.MODID + ":matter/basic")
                .requires(PEBlocks.RELAY)
                .save(consumer, rl("relay/basic_2"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS,ModItems.RELAY.get(Matter.DARK).get())
                .unlockedBy("has_item", has(PEBlocks.RELAY_MK2))
                .group(EXDataGen.MODID + ":matter/dark")
                .requires(PEBlocks.RELAY_MK2)
                .save(consumer, rl("relay/dark_2"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS,ModItems.RELAY.get(Matter.RED).get())
                .unlockedBy("has_item", has(PEBlocks.RELAY_MK3))
                .group(EXDataGen.MODID + ":matter/red")
                .requires(PEBlocks.RELAY_MK3)
                .save(consumer, rl("relay/red_2"));

        buildAlchemyTableRecipes(consumer);
    }

    private void buildAlchemyTableRecipes(Consumer<FinishedRecipe> consumer) {
        alchemyStep(consumer, Items.CHARCOAL, new ItemStack(Items.COAL));

        alchemyChain(consumer,
                Items.REDSTONE,
                Items.GUNPOWDER,
                Items.GLOWSTONE_DUST,
                Items.BLAZE_POWDER,
                Items.BLAZE_ROD
        );

        alchemyChain(consumer,
                Items.LAPIS_LAZULI,
                Items.PRISMARINE_SHARD,
                Items.PRISMARINE_CRYSTALS
        );

        alchemyChain(consumer,
                PEItems.LOW_COVALENCE_DUST,
                PEItems.MEDIUM_COVALENCE_DUST,
                PEItems.HIGH_COVALENCE_DUST
        );

        alchemyChain(consumer,
                Items.BEEF,
                Items.ROTTEN_FLESH,
                Items.LEATHER,
                Items.SPIDER_EYE,
                Items.BONE
        );

        alchemyChain(consumer,
                Items.WHEAT_SEEDS,
                Items.MELON,
                Items.APPLE,
                Items.CARROT,
                Items.BEETROOT,
                Items.POTATO,
                Blocks.PUMPKIN
        );

        alchemyChain(consumer,
                Items.COOKIE,
                Items.BREAD,
                Items.CAKE
        );

        alchemyChain(consumer,
                PEItems.ALCHEMICAL_COAL,
                Blocks.REDSTONE_BLOCK,
                Items.LAVA_BUCKET,
                Blocks.OBSIDIAN
        );

        alchemyChain(consumer,
                Blocks.OAK_LEAVES,
                Blocks.GRASS,
                Blocks.FERN,
                Blocks.VINE,
                Blocks.LILY_PAD
        );

        alchemyStep(consumer, Items.ENDER_EYE, new ItemStack(Items.CHORUS_FRUIT));
        alchemyStep(consumer, Items.STRING, new ItemStack(Items.FEATHER));
        alchemyStep(consumer, Items.STICK, new ItemStack(Blocks.GRASS));
    }

    private void alchemyChain(Consumer<FinishedRecipe> consumer, ItemLike... items) {
        Validate.isTrue(items.length >= 3, "3 or more items required!");
        for (int i = 1; i < items.length; i++) {
            alchemyStep(consumer, items[i-1], new ItemStack(items[i]));
        }
    }

    private void alchemyStep(Consumer<FinishedRecipe> consumer, ItemLike in, ItemStack out) {
        String name = "alchemy/" + ForgeRegistries.ITEMS.getKey(in.asItem()).getPath() + "_to_" + ForgeRegistries.ITEMS.getKey(out.getItem()).getPath();
        ResourceLocation id = rl(name);
        alchemyRecipe(id, Ingredient.of(in), out).build(consumer, id);
    }

    private AlchemyTableRecipeBuilder alchemyRecipe(ResourceLocation id, Ingredient input, ItemStack output) {
        return alchemyRecipe(id, input, output, 0L, 200);
    }

    private AlchemyTableRecipeBuilder alchemyRecipe(ResourceLocation id, Ingredient input, ItemStack output, long emcOverride, int craftingTime) {
        return new AlchemyTableRecipeBuilder(new AlchemyTableRecipe(id, input, output, emcOverride, craftingTime))
                .addCriterion(Criteria.has(ModItems.ALCHEMY_TABLE.get()));
    }

    
}
