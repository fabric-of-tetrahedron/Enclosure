package com.github.zly2006.enclosure.mixin;

import com.github.zly2006.enclosure.EnclosureArea;
import com.github.zly2006.enclosure.ServerMain;
import net.minecraft.block.BlockState;
import net.minecraft.block.TurtleEggBlock;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.zly2006.enclosure.utils.Permission.permissions;

@Mixin(TurtleEggBlock.class)
public class MixinTurtleEggBlock {
    @Inject(method = "tryBreakEgg", at = @At("HEAD"), cancellable = true)
    private void onBreakEgg(World world, BlockState state, BlockPos pos, Entity entity, int inverseChance, CallbackInfo ci) {
        if (!world.isClient && entity instanceof ServerPlayerEntity player) {
            EnclosureArea area = ServerMain.INSTANCE.getAllEnclosures((ServerWorld) world).getArea(pos);
            if (area != null && !area.areaOf(pos).hasPerm(player, permissions.BREAK_TURTLE_EGG)) {
                ci.cancel();
            }
        }
    }
}
