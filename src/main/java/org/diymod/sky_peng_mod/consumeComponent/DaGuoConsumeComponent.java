package org.diymod.sky_peng_mod.consumeComponent;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

public class DaGuoConsumeComponent {

    /**
     * 食用大果后赋予的效果
     * 夜视、急迫2、迅捷2
     */
    public static final Consumable DaGuo_CONSUMABLE_COMPONENT = Consumables.defaultFood()
            // The duration is in ticks, 20 ticks = 1 second
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 120 * 20, 0), 1.0f))
            .onConsume(new ApplyStatusEffectsConsumeEffect(new  MobEffectInstance(MobEffects.HASTE, 120 * 20, 1), 1.0f))
            .onConsume(new ApplyStatusEffectsConsumeEffect(new  MobEffectInstance(MobEffects.SPEED, 120 * 20, 1), 1.0f))
            .build();

}
