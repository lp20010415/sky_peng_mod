package org.diymod.sky_peng_mod.component;

import net.minecraft.world.food.FoodProperties;

public class DaGuoFoodComponent {

    public static final FoodProperties DAGUO_COMPONENT = new FoodProperties.Builder()
            .nutrition(1)                   // 恢复的饥饿值，每1点=半个鸡腿，4=2个鸡腿
            .saturationModifier(0.3f)     // 饱和度，影响饥饿消耗的速度
            .alwaysEdible()                 // 可选：是否可以在饱的时候继续吃
            .build();

}
