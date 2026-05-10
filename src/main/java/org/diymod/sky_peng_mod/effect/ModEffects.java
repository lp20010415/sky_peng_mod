package org.diymod.sky_peng_mod.effect;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;

import static org.diymod.sky_peng_mod.Sky_peng_mod.MOD_ID;

public class ModEffects {

    public static final MobEffect EARLY_ORAL_CANCER = register("early_oral_cancer", new EarlyOralCancerMobEffect());
    public static final MobEffect MIDDLE_ORAL_CANCER = register("middle_oral_cancer", new MiddleOralCancerMobEffect());
    public static final MobEffect LATE_ORAL_CANCER = register("late_oral_cancer", new LateOralCancerMobEffect());

    private static <T extends MobEffect> T register(String name, T effect) {
        return Registry.register(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath(MOD_ID, name), effect);
    }

    public static void initialize() {
        // 留空以便在 mod 初始化时引用类触发注册（如果需要）
    }
}
