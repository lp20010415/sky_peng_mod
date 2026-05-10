package org.diymod.sky_peng_mod.mixin;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.diymod.sky_peng_mod.effect.ModEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Objects;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "removeAllEffects", at = @At("HEAD"), cancellable = true)
    private void onClearStatusEffects(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity livingEntity = (LivingEntity)(Object)this;
        Holder<MobEffect> lateOralCancer = Holder.direct(ModEffects.LATE_ORAL_CANCER);

        // 如果有口腔癌晚期效果，阻止默认的清除行为并仅移除其他效果。
        if (livingEntity.hasEffect(lateOralCancer)) {
            java.util.List<Holder<MobEffect>> toRemove = new java.util.ArrayList<>();
            for (MobEffectInstance inst : livingEntity.getActiveEffects()) {
                if (!Objects.equals(inst.getEffect(), lateOralCancer)) {
                    toRemove.add(inst.getEffect());
                }
            }

            for (Holder<MobEffect> eff : toRemove) {
                livingEntity.removeEffect(eff);
            }
            // 取消原方法执行，防止晚期效果被原版继续移除。
            cir.setReturnValue(false);
        }
    }

}
