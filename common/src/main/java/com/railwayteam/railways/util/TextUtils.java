package com.railwayteam.railways.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.apache.commons.lang3.StringUtils;
import org.joml.Matrix4f;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

public class TextUtils {
    public static String titleCaseConversion(String inputString)
    {
        if (StringUtils.isBlank(inputString)) {
            return "";
        }

        if (StringUtils.length(inputString) == 1) {
            return inputString.toUpperCase(Locale.ROOT);
        }

        StringBuffer resultPlaceHolder = new StringBuffer(inputString.length());

        Stream.of(inputString.split(" ")).forEach(stringPart ->
        {
            if (stringPart.length() > 1)
                resultPlaceHolder.append(stringPart.substring(0, 1)
                        .toUpperCase(Locale.ROOT))
                    .append(stringPart.substring(1)
                        .toLowerCase(Locale.ROOT));
            else
                resultPlaceHolder.append(stringPart.toUpperCase(Locale.ROOT));

            resultPlaceHolder.append(" ");
        });
        return StringUtils.trim(resultPlaceHolder.toString());
    }

    public static void renderMultilineDebugText(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                                                double baseY, boolean transparent, String... lines) {
        double y = baseY + (lines.length/4.0D);
        for (String line : lines) {
            renderDebugText(poseStack, buffer, packedLight, y, transparent, line);
            y -= 0.25D;
        }
    }

    public static void renderDebugText(PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight,
                                       double y, boolean transparent, String text) {
        poseStack.pushPose();
        poseStack.translate(0.0D, y, 0.0D);
        poseStack.mulPose(Minecraft.getInstance().getBlockEntityRenderDispatcher().camera.rotation());
        poseStack.scale(-0.025F, -0.025F, 0.025F);
        Matrix4f matrix4f = poseStack.last().pose();
        float f1 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
        int j = (int)(f1 * 255.0F) << 24;
        Font font = Minecraft.getInstance().font;
        float f2 = (float)(-font.width(text) / 2);
//        font.drawInBatch(text, f2, 0, 553648127, false, matrix4f, pBuffer, transparent, j, pPackedLight);
//
//        if (transparent) {
//            font.drawInBatch(text, f2, 0, -1, false, matrix4f, pBuffer, false, 0, pPackedLight);
//        }

        font.drawInBatch(text, f2, 0, 553648127, false, matrix4f, pBuffer, transparent ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, j, pPackedLight);

        if (transparent) {
            font.drawInBatch(text, f2, 0, -1, false, matrix4f, pBuffer, Font.DisplayMode.NORMAL, 0, pPackedLight);
        }


        poseStack.popPose();
    }

    public static Component translateWithFormatting(String key, Object... args) {
        MutableComponent base = Components.translatable(key, args);
        StringBuilder partsStringBuilder = new StringBuilder();
        base.visit((style, part) -> {
            partsStringBuilder.append(part);
            return Optional.empty();
        }, Style.EMPTY);
        return Components.literal(partsStringBuilder.toString());
    }
}
