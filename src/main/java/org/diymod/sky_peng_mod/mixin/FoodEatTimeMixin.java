package org.diymod.sky_peng_mod.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.diymod.sky_peng_mod.entity.EatTimeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class FoodEatTimeMixin {

    @Inject(method = "getUseDuration", at = @At("RETURN"), cancellable = true)
    private void modifyFoodUseDuration(ItemStack itemStack, LivingEntity livingEntity, CallbackInfoReturnable<Integer> cir) {
        // 调用动态时长计算逻辑

        int newDuration = EatTimeManager.getModifiedEatTime(itemStack, livingEntity, cir.getReturnValue());
        if (newDuration != cir.getReturnValue()) {
            cir.setReturnValue(newDuration);
        }
    }

}
