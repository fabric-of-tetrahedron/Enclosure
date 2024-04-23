package com.github.zly2006.enclosure.mixin;

import com.github.zly2006.enclosure.EnclosureArea;
import com.github.zly2006.enclosure.EnclosureList;
import com.github.zly2006.enclosure.ServerMain;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.block.TntBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.github.zly2006.enclosure.utils.Permission.permissions;

@Mixin(FireBlock.class)
public class MixinFireBlock {
    private void set(World instance, BlockPos pos, BlockState blockState, int i) {
        if (instance.isClient) return;
        EnclosureList list = ServerMain.INSTANCE.getAllEnclosures((ServerWorld) instance);
        EnclosureArea area = list.getArea(pos);
        if (area == null || area.areaOf(pos).hasPubPerm(permissions.FIRE_SPREADING)) {
            instance.setBlockState(pos, blockState, i);
        }
    }

    private void remove(World instance, BlockPos pos, boolean move) {
        if (instance.isClient) return;
        EnclosureList list = ServerMain.INSTANCE.getAllEnclosures((ServerWorld) instance);
        EnclosureArea area = list.getArea(pos);
        if (area != null && !area.areaOf(pos).hasPubPerm(permissions.FIRE_SPREADING)) {
            if (instance.getBlockState(pos).isOf(Blocks.FIRE)) {
                // 但是允许火熄灭
                instance.removeBlock(pos, move);
            }
        } else {
            instance.removeBlock(pos, move);
        }
    }

    @Redirect(method = "scheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    private boolean redirectSetter(ServerWorld instance, BlockPos pos, BlockState blockState, int i) {
        set(instance, pos, blockState, i);
        return true;
    }

    @Redirect(method = "scheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"))
    private boolean redirectSetter(ServerWorld instance, BlockPos pos, boolean b) {
        remove(instance, pos, b);
        return true;
    }

    @Redirect(method = "trySpreadingFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    private boolean redirectSetter(World instance, BlockPos pos, BlockState state, int flags) {
        set(instance, pos, state, flags);
        return true;
    }

    @Redirect(method = "trySpreadingFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"))
    private boolean redirectSetter(World instance, BlockPos pos, boolean move) {
        remove(instance, pos, move);
        return true;
    }

    @Redirect(method = "trySpreadingFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/TntBlock;primeTnt(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"))
    private void redirectSetter(World instance, BlockPos pos) {
        if (instance.isClient) return;
        EnclosureArea area = ServerMain.INSTANCE.getSmallestEnclosure((ServerWorld) instance, pos);
        if (area == null || area.hasPubPerm(permissions.FIRE_SPREADING)) {
            TntBlock.primeTnt(instance, pos);
        }  // 不允许火点燃TNT
    }
}
