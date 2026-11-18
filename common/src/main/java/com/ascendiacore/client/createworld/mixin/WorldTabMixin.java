package com.ascendiacore.client.createworld.mixin;

import com.ascendiacore.client.createworld.WorldTabMount;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.gui.screens.worldselection.CreateWorldScreen$WorldTab")
public class WorldTabMixin {
    @Inject(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/layouts/CommonLayouts;labeledElement(Lnet/minecraft/client/gui/Font;Lnet/minecraft/client/gui/layouts/LayoutElement;Lnet/minecraft/network/chat/Component;)Lnet/minecraft/client/gui/layouts/Layout;",
                    ordinal = 0))
    private void ascendia_core$insertRows(CallbackInfo ci, @Local GridLayout.RowHelper helper,
            @Local(argsOnly = true) CreateWorldScreen screen) {
        WorldTabMount.insert(helper, screen.getUiState());
    }
}
