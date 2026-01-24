package com.weenie.googleballs;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

// no way google balls IN MINECRAFT!!!

@Mod.EventBusSubscriber(modid = GoogleBalls.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class GoogleBallsApplet {

    static class Point {
        double curX, curY, curZ;
        double origX, origY;
        double targetX, targetY, targetZ;
        double velX, velY, velZ;
        int color;
        double radius, size;
        double friction = 0.8;
        double springStrength = 0.1;

        Point(double x, double y, double size, int color) {
            this.curX = this.origX = this.targetX = x;
            this.curY = this.origY = this.targetY = y;
            this.curZ = this.targetZ = 0;
            this.velX = this.velY = this.velZ = 0;
            this.size = this.radius = size;
            this.color = color;
        }

        void update(double deltaTime) {
            double timeScale = deltaTime / 0.03;
            
            double dx = targetX - curX;
            double ax = dx * springStrength * timeScale;
            velX += ax;
            velX *= Math.pow(friction, timeScale);
            if (Math.abs(dx) < 0.1 && Math.abs(velX) < 0.01) {
                curX = targetX;
                velX = 0;
            } else {
                curX += velX * timeScale;
            }

            double dy = targetY - curY;
            double ay = dy * springStrength * timeScale;
            velY += ay;
            velY *= Math.pow(friction, timeScale);
            if (Math.abs(dy) < 0.1 && Math.abs(velY) < 0.01) {
                curY = targetY;
                velY = 0;
            } else {
                curY += velY * timeScale;
            }

            double dox = origX - curX;
            double doy = origY - curY;
            double d = Math.sqrt(dox * dox + doy * doy);
            targetZ = d / 100.0 + 1.0;
            double dz = targetZ - curZ;
            double az = dz * springStrength * timeScale;
            velZ += az;
            velZ *= Math.pow(friction, timeScale);
            if (Math.abs(dz) < 0.01 && Math.abs(velZ) < 0.001) {
                curZ = targetZ;
                velZ = 0;
            } else {
                curZ += velZ * timeScale;
            }

            radius = size * curZ;
            if (radius < 1) radius = 1;
        }

        void draw(GuiGraphics graphics, int offsetX, int offsetY) {
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;
            
            int x = offsetX + (int)curX;
            int y = offsetY + (int)curY;
            int rad = (int)radius;
            
            graphics.fill(x - rad, y - rad, x + rad, y + rad, 
                0xFF000000 | (r << 16) | (g << 8) | b);
        }
    }

    public static class GoogleBallsScreen extends Screen {
        private final Screen parent;
        private List<Point> points = new ArrayList<>();
        private double mouseX;
        private double mouseY;
        private long lastFrameTime;

        public GoogleBallsScreen(Screen parent) {
            super(Component.literal("Google Balls"));
            this.parent = parent;
            this.lastFrameTime = System.nanoTime();
        }

        @Override
        protected void init() {
            super.init();
            initPoints();
            this.lastFrameTime = System.nanoTime();
        }

        private void initPoints() {
            points.clear();
            
            int[][] pointData = {
                {202, 78, 9, 0xed9d33}, {348, 83, 9, 0xd44d61}, {256, 69, 9, 0x4f7af2},
                {214, 59, 9, 0xef9a1e}, {265, 36, 9, 0x4976f3}, {300, 78, 9, 0x269230},
                {294, 59, 9, 0x1f9e2c}, {45, 88, 9, 0x1c48dd}, {268, 52, 9, 0x2a56ea},
                {73, 83, 9, 0x3355d8}, {294, 6, 9, 0x36b641}, {235, 62, 9, 0x2e5def},
                {353, 42, 8, 0xd53747}, {336, 52, 8, 0xeb676f}, {208, 41, 8, 0xf9b125},
                {321, 70, 8, 0xde3646}, {8, 60, 8, 0x2a59f0}, {180, 81, 8, 0xeb9c31},
                {146, 65, 8, 0xc41731}, {145, 49, 8, 0xd82038}, {246, 34, 8, 0x5f8af8},
                {169, 69, 8, 0xefa11e}, {273, 99, 8, 0x2e55e2}, {248, 120, 8, 0x4167e4},
                {294, 41, 8, 0x0b991a}, {267, 114, 8, 0x4869e3}, {78, 67, 8, 0x3059e3},
                {294, 23, 8, 0x10a11d}, {117, 83, 8, 0xcf4055}, {137, 80, 8, 0xcd4359},
                {14, 71, 8, 0x2855ea}, {331, 80, 8, 0xca273c}, {25, 82, 8, 0x2650e1},
                {233, 46, 8, 0x4a7bf9}, {73, 13, 8, 0x3d65e7}, {327, 35, 6, 0xf47875},
                {319, 46, 6, 0xf36764}, {256, 81, 6, 0x1d4eeb}, {244, 88, 6, 0x698bf1},
                {194, 32, 6, 0xfac652}, {97, 56, 6, 0xee5257}, {105, 75, 6, 0xcf2a3f},
                {42, 4, 6, 0x5681f5}, {10, 27, 6, 0x4577f6}, {166, 55, 6, 0xf7b326},
                {266, 88, 6, 0x2b58e8}, {178, 34, 6, 0xfacb5e}, {100, 65, 6, 0xe02e3d},
                {343, 32, 6, 0xf16d6f}, {59, 5, 6, 0x507bf2}, {27, 9, 6, 0x5683f7},
                {233, 116, 6, 0x3158e2}, {123, 32, 6, 0xf0696c}, {6, 38, 6, 0x3769f6},
                {63, 62, 6, 0x6084ef}, {6, 49, 6, 0x2a5cf4}, {108, 36, 6, 0xf4716e},
                {169, 43, 6, 0xf8c247}, {137, 37, 6, 0xe74653}, {318, 58, 6, 0xec4147},
                {226, 100, 5, 0x4876f1}, {101, 46, 5, 0xef5c5c}, {226, 108, 5, 0x2552ea},
                {17, 17, 5, 0x4779f7}, {232, 93, 5, 0x4b78f1}
            };

            double scale = 0.6;
            int centerX = (int)(width / 2 - 180 * scale);
            int centerY = (int)(height / 2 - 65 * scale);

            for (int[] data : pointData) {
                points.add(new Point(
                    centerX + data[0] * scale, 
                    centerY + data[1] * scale, 
                    data[2] * scale, 
                    data[3]
                ));
            }
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            long currentTime = System.nanoTime();
            double deltaTime = (currentTime - lastFrameTime) / 1_000_000_000.0;
            lastFrameTime = currentTime;
            
            if (deltaTime > 0.1) deltaTime = 0.1;
            
            this.mouseX = mouseX;
            this.mouseY = mouseY;

            if (parent != null) {
                parent.render(graphics, -1, -1, partialTick);
            }
            
            graphics.fill(0, 0, width, height, 0xCC000000);

            for (Point point : points) {
                double dx = this.mouseX - point.curX;
                double dy = this.mouseY - point.curY;
                double d = Math.sqrt(dx * dx + dy * dy);

                if (d < 150) {
                    point.targetX = point.curX - dx;
                    point.targetY = point.curY - dy;
                } else {
                    point.targetX = point.origX;
                    point.targetY = point.origY;
                }

                point.update(deltaTime);
                point.draw(graphics, 0, 0);
            }

            String tooltip = "press ESC to stop balling";
            int tooltipWidth = font.width(tooltip);
            graphics.drawString(this.font, tooltip, 
                width - tooltipWidth - 10, height - 20, 0xFFFFFFFF, true);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (keyCode == 256) {
                minecraft.setScreen(parent);
                return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean isPauseScreen() {
            return true;
        }
    }

    // lets me open google balls from anywhere
    public static void open() {
        Minecraft mc = Minecraft.getInstance();
        Screen currentScreen = mc.screen;
        mc.setScreen(new GoogleBallsScreen(currentScreen));
    }

    // ball button in the pause menu
    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof PauseScreen) {
            Screen screen = event.getScreen();
            
            ResourceLocation texture = new ResourceLocation(GoogleBalls.MODID, "textures/gui/balls.png");
            
            ImageButton button = new ImageButton(
                5, 5,
                20, 20,
                0, 0,
                20,
                texture,
                20, 40,
                (btn) -> {
                    open();
                }
            );
            
            event.addListener(button);
        }
    }
}