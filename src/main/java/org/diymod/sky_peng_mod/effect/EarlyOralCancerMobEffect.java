package org.diymod.sky_peng_mod.effect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.diymod.sky_peng_mod.Sky_peng_mod;
import org.diymod.sky_peng_mod.entity.PlayerSavedData;
import org.diymod.sky_peng_mod.entity.EatTimeManager;

public class EarlyOralCancerMobEffect extends MobEffect {
    // 口腔癌早期
    // 初期颜色代码 FF6347
    // 中期颜色代码 DC143C
    // 晚期颜色代码 8B0000

    public EarlyOralCancerMobEffect() {
        super(MobEffectCategory.HARMFUL, 0xFF6347);
    }

    @Override
    public void onEffectStarted(MobEffectInstance effectInstance, LivingEntity entity) {
        if (entity instanceof Player player) {
            EatTimeManager.setPlayerMultiplier(player.getUUID(), 2f); // 将吃东西时长改为原来的一倍
        }
        super.onEffectStarted(effectInstance, entity);
    }

    @Override
    public void onEffectRemoved(MobEffectInstance effectInstance, LivingEntity entity) {
        if (entity instanceof Player player) {
            CompoundTag playerData = PlayerSavedData.get(player.level()).getPlayerDataByUUID(player.getUUID());
            if (playerData != null) {
                int oral_cancer_remaining = playerData.getIntOr("oral_cancer_remaining", 0);

                Sky_peng_mod.LOGGER.info("early_oral_cancer_remaining：" + oral_cancer_remaining);

                if (oral_cancer_remaining <= 0) {

                    PlayerSavedData savedData = PlayerSavedData.get(player.level());
                    // 清除大果计数
                    savedData.setDaGuoEaten(player.getUUID(), 0);
                    // 恢复吃东西速度
                    EatTimeManager.setPlayerMultiplier(player.getUUID(), 1.0f);
                }
            }
        }

        super.onEffectRemoved(effectInstance, entity);
    }
}
