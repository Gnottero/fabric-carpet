package carpet.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BellBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static carpet.script.CarpetEventServer.Event.BELL_RINGS;

@Mixin(BellBlock.class)
public class BellBlock_scarpetMixin {

    @Inject(
        method = "attemptToRing(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/entity/BellBlockEntity;onHit(Lnet/minecraft/core/Direction;)V"
        ),
        cancellable = true
    )
    private void onAttemptToRing(Entity entity, Level level, BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> cir)
    {
        if (BELL_RINGS.isNeeded())
        {
            boolean cancelled = BELL_RINGS.onBellRings((ServerLevel) level, blockPos, direction, entity);
            if (cancelled)
            {
                cir.setReturnValue(false);
            }
        }
    }

}
