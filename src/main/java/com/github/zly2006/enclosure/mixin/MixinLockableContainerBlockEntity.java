package com.github.zly2006.enclosure.mixin;

import com.github.zly2006.enclosure.EnclosureArea;
import com.github.zly2006.enclosure.ServerMain;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.github.zly2006.enclosure.utils.Permission.permissions;

@Mixin(LockableContainerBlockEntity.class)
public class MixinLockableContainerBlockEntity extends BlockEntity {
    public MixinLockableContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(at = @At("HEAD"), method = "checkUnlocked(Lnet/minecraft/entity/player/PlayerEntity;)Z", cancellable = true)
    private void checkUnlocked(PlayerEntity p, CallbackInfoReturnable<Boolean> cir) {
        if (p instanceof ServerPlayerEntity player) {
            if (player.interactionManager.getGameMode() == GameMode.SPECTATOR) {
                return;
            }
            EnclosureArea area = ServerMain.INSTANCE.getSmallestEnclosure((ServerWorld) player.getWorld(), getPos());
            if (area != null && !area.hasPerm(player, permissions.CONTAINER)) {
                player.sendMessage(permissions.CONTAINER.getNoPermissionMsg(player));
                cir.setReturnValue(false);
            }
        }
    }
}
