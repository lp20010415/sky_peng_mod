package org.diymod.sky_peng_mod.effect;



import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.diymod.sky_peng_mod.entity.PlayerSavedData;
import org.diymod.sky_peng_mod.entity.EatTimeManager;

public class LateOralCancerMobEffect extends MobEffect {
    // 口腔癌晚期
    // 初期颜色代码 FF6347
    // 中期颜色代码 DC143C
    // 晚期颜色代码 8B0000

    public LateOralCancerMobEffect() {
        super(MobEffectCategory.HARMFUL, 0x8B0000);
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
            EatTimeManager.setPlayerMultiplier(player.getUUID(), 10f); // 将吃东西时长改为原来的10倍
        }
        super.onEffectStarted(livingEntity, i);
    }

    // 每 tick 检查效果
    @Override
    public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity livingEntity, int i) {
        if (livingEntity instanceof Player player) {
            float newHealth = Math.max(2, player.getHealth() - 2f);
            player.setHealth(newHealth);
        }
        return super.applyEffectTick(serverLevel, livingEntity, i);
    }

    // 效果结束
    @Override
    public void onEffectRemoved(MobEffectInstance effectInstance, LivingEntity entity) {
        super.onEffectRemoved(effectInstance, entity);
        if (entity instanceof Player player && !player.isDeadOrDying()) {
            PlayerSavedData savedData = PlayerSavedData.get(player.level());

            if (effectInstance.getDuration() <= 0) {
                // 只有自然到期(duration <= 0)，才会杀死玩家
                player.setHealth(0);
                // 清除大果计数
                savedData.setDaGuoEaten(player.getUUID(), 0);
                // 恢复吃东西速度
                EatTimeManager.setPlayerMultiplier(player.getUUID(), 1.0f);
                // 删除计时
                savedData.removeOralCancerRemaining(player.getUUID());
            } else {
                boolean isAmbient = effectInstance.isAmbient();
                boolean isVisible = effectInstance.isVisible();
                player.displayClientMessage(Component.literal(
                         "参数2：" + isVisible + "参数3：" + isAmbient), false);
//                player.addEffect(new MobEffectInstance(Holder.direct(ModEffects.LATE_ORAL_CANCER), effectInstance.getDuration(), 0));
            }

        }
    }


}
