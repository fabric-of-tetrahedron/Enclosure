package com.github.zly2006.enclosure.mixin;

import com.github.zly2006.enclosure.EnclosureArea;
import com.github.zly2006.enclosure.EnclosureList;
import com.github.zly2006.enclosure.ServerMain;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkCatalystBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.zly2006.enclosure.utils.Permission.permissions;

@Mixin(SculkCatalystBlock.class)
public class MixinSculkCatalystBlock {
    @Inject(method = "scheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"), cancellable = true)
    private void setBlock(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        EnclosureList list = ServerMain.INSTANCE.getAllEnclosures(world);
        EnclosureArea area = list.getArea(pos);
        if (area != null && !area.areaOf(pos).hasPubPerm(permissions.SCULK_SPREAD)) {
            ci.cancel();
        }
    }
}
