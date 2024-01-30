package com.github.zly2006.reden.mixin.noTimeOut;

import com.github.zly2006.reden.RedenClient;
import com.github.zly2006.reden.malilib.HiddenOption;
import com.github.zly2006.reden.malilib.MalilibSettingsKt;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(DisconnectedScreen.class)
public class MixinDisconnectedScreen extends Screen {
    @Shadow
    @Final
    private Text reason;

    @Shadow
    @Final
    private DirectionalLayoutWidget grid;

    protected MixinDisconnectedScreen(Text title) {
        super(title);
    }

    @Inject(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/widget/DirectionalLayoutWidget;refreshPositions()V"
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void addSomething(CallbackInfo ci, ButtonWidget buttonWidget) {
        if (reason.getContent() instanceof TranslatableTextContent content && "disconnect.timeout".equals(content.getKey())) {
            if (HiddenOption.iSHOW_TIME_OUT_NOTIFICATION.getBooleanValue() && !MalilibSettingsKt.NO_TIME_OUT.getBooleanValue()) {
                grid.add(new TextWidget(
                        Text.of("If you are a developer debugging your server by breakpoints,\n try NoTimeOut provided by Reden Mod!"),
                        textRenderer
                ));
                grid.add(ButtonWidget.builder(
                        Text.literal("Enable NoTimeOut"),
                        s -> {
                            MalilibSettingsKt.NO_TIME_OUT.setBooleanValue(true);
                            RedenClient.saveMalilibOptions();
                        }
                ).build());
                grid.add(ButtonWidget.builder(
                        Text.literal("Dont show again"),
                        s -> {
                            HiddenOption.iSHOW_TIME_OUT_NOTIFICATION.setBooleanValue(false);
                            RedenClient.saveMalilibOptions();
                        }
                ).build());
            }
        }
    }
}
