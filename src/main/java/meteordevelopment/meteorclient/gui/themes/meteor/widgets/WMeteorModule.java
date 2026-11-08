
/*
 * This file is part of the Fuck IT Client distribution.
 */

package meteordevelopment.meteorclient.gui.themes.meteor.widgets;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorGuiTheme;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorWidget;
import meteordevelopment.meteorclient.gui.utils.AlignmentX;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPressable;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.util.Mth;

import static com.mojang.blaze3d.platform.InputConstants.MOUSE_BUTTON_LEFT;
import static com.mojang.blaze3d.platform.InputConstants.MOUSE_BUTTON_RIGHT;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class WMeteorModule extends WPressable implements MeteorWidget {
    private final Module module;
    private final String title;

    private double titleWidth;

    private double animationProgress1;
    private double animationProgress2;

    public WMeteorModule(Module module, String title) {
        this.module = module;
        this.title = title;
        this.tooltip = module.description;

        if (module.isActive()) {
            animationProgress1 = 1;
            animationProgress2 = 1;
        } else {
            animationProgress1 = 0;
            animationProgress2 = 0;
        }
    }

    @Override
    public double pad() {
        return theme.scale(5);
    }

    @Override
    protected void onCalculateSize() {
        double pad = pad();

        if (titleWidth == 0) titleWidth = theme.textWidth(title);

        width = pad + titleWidth + pad;
        height = pad + theme.textHeight() + pad;
    }

    @Override
    protected void onPressed(int button) {
        if (button == MOUSE_BUTTON_LEFT) module.toggle();
        else if (button == MOUSE_BUTTON_RIGHT) mc.gui.setScreen(theme.moduleScreen(module));
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        MeteorGuiTheme theme = theme();
        double pad = pad();

        animationProgress1 += delta * 4 * ((module.isActive() || mouseOver) ? 1 : -1);
        animationProgress1 = Mth.clamp(animationProgress1, 0, 1);

        animationProgress2 += delta * 6 * (module.isActive() ? 1 : -1);
        animationProgress2 = Mth.clamp(animationProgress2, 0, 1);

        // Subtle navy background animation on hover or activation.
        if (animationProgress1 > 0) {
            renderer.quad(x, y, width * animationProgress1, height, theme.moduleBackground.get());
        }

        // Electric-blue active indicator.
        if (animationProgress2 > 0) {
            renderer.quad(
                x,
                y + height * (1 - animationProgress2),
                theme.scale(3),
                height * animationProgress2,
                theme.accentColor.get()
            );
        }

        double textX = this.x + pad;
        double textWidth = width - pad * 2;

        if (theme.moduleAlignment.get() == AlignmentX.Center) {
            textX += textWidth / 2 - titleWidth / 2;
        } else if (theme.moduleAlignment.get() == AlignmentX.Right) {
            textX += textWidth - titleWidth;
        }

        // Active module names use the electric-blue accent.
        renderer.text(
            title,
            textX,
            y + pad,
            module.isActive() ? theme.accentColor.get() : theme.textColor.get(),
            false
        );
    }
}
