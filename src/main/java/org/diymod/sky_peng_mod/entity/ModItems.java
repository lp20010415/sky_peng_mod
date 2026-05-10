package org.diymod.sky_peng_mod.entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import org.diymod.sky_peng_mod.component.DaGuoFoodComponent;
import org.diymod.sky_peng_mod.consumeComponent.DaGuoConsumeComponent;
import org.diymod.sky_peng_mod.foods.DaGuoItem;

import java.util.function.Function;

import static org.diymod.sky_peng_mod.Sky_peng_mod.MOD_ID;

public class ModItems {

    public static final Item DIY_DAGUO = register("da_guo", DaGuoItem::new,
            new Item.Properties().food(
                    DaGuoFoodComponent.DAGUO_COMPONENT,
                    DaGuoConsumeComponent.DaGuo_CONSUMABLE_COMPONENT
            ));

    public static <T extends Item> T register(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
        // Create the item key.
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, name));

        // Create the item instance.
        T item = itemFactory.apply(settings.setId(itemKey));

        // Register the item.
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        return item;
    }

    public static void initialize() {
    }

}
