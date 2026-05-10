package org.diymod.sky_peng_mod.effect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.diymod.sky_peng_mod.entity.PlayerSavedData;
import org.diymod.sky_peng_mod.entity.EatTimeManager;

public class MiddleOralCancerMobEffect extends MobEffect {
    // 口腔癌晚期
    // 初期颜色代码 FF6347
    // 中期颜色代码 DC143C
    // 晚期颜色代码 8B0000

    public MiddleOralCancerMobEffect() {
        super(MobEffectCategory.HARMFUL, 0xDC143C);
    }

    // 触发检查，返回参数则为每 tick 是否触发 applyEffectTick 方法
    @Override
    public boolean shouldApplyEffectTickThisTick(int i, int j) {
        // 每20 tick(1秒)触发一次
        return i % 20 == 0;
    }

    @Override
    public void onEffectStarted(LivingEntity livingEntity, int i) {
        if (livingEntity instanceof Player player) {
            EatTimeManager.setPlayerMultiplier(player.getUUID(), 5f); // 将吃东西时长改为原来的5倍
        }
        super.onEffectStarted(livingEntity, i);
    }

    // 每 tick 检查效果
    @Override
    public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity livingEntity, int i) {
        if (livingEntity instanceof Player player) {
            float newHealth = Math.max(1, player.getHealth() - 1f);
            player.setHealth(newHealth);
        }
        return super.applyEffectTick(serverLevel, livingEntity, i);
    }

    @Override
    public void onEffectRemoved(MobEffectInstance effectInstance, LivingEntity entity) {
        if (entity instanceof Player player) {
            CompoundTag playerData = PlayerSavedData.get(player.level()).getPlayerDataByUUID(player.getUUID());
            if (playerData != null) {
                int oral_cancer_remaining = playerData.getIntOr("oral_cancer_remaining", 0);
                if (oral_cancer_remaining <= 0) {

                    PlayerSavedData savedData = PlayerSavedData.get(player.level());
                    // 清除大果计数
                    savedData.setDaGuoEaten(player.getUUID(), 0);
                    // 恢复吃东西速度
                    EatTimeManager.clearAll();
                }
            }
        }
        super.onEffectRemoved(effectInstance, entity);
    }
}
