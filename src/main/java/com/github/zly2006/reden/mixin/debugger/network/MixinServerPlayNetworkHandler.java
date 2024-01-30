package com.github.zly2006.reden.mixin.debugger.network;

import com.github.zly2006.reden.access.ServerData;
import com.github.zly2006.reden.debugger.stages.TickStageWorldProvider;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.zly2006.reden.access.ServerData.getData;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinServerPlayNetworkHandler {
    @Shadow public ServerPlayerEntity player;

    @Inject(
            method = "method_44356",
            at = @At("HEAD")
    )
    private void startCommandExecute(CallbackInfo ci) {
        ServerData data = getData(player.server);
        ServerWorld world = player.getServerWorld();
        data.getTickStageTree().push$reden_is_what_we_made(new TickStageWorldProvider("commands_stage", data.getTickStageTree().getActiveStage(), world));
    }

    @Inject(
            method = "method_44356",
            at = @At("RETURN")
    )
    private void endCommandExecute(CallbackInfo ci) {
        ServerData data = getData(player.server);
        data.getTickStageTree().pop(TickStageWorldProvider.class);
    }
}
