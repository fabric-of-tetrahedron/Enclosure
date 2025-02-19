package com.github.zly2006.enclosure.mixin;

import com.github.zly2006.enclosure.ServerMain;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.zly2006.enclosure.utils.Permission.permissions;

@Mixin(FarmlandBlock.class)
public class MixinFarmlandBlock {
    @Inject(method = "onLandedUpon", at = @At("HEAD"), cancellable = true)
    private void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci) {
        if (entity instanceof PlayerEntity player) {
            if (!ServerMain.INSTANCE.checkPermission(world, pos, player, permissions.FARMLAND_DESTROY)) {
                entity.sendMessage(permissions.FARMLAND_DESTROY.getNoPermissionMsg(player));
                ci.cancel();
            }
        }
    }
}
