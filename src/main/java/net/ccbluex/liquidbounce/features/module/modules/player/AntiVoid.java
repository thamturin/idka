/*
 * LiquidBounce++ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/PlusPlusMC/LiquidBouncePlusPlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.player;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.*;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly;
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.ccbluex.liquidbounce.utils.PacketUtils;
import net.ccbluex.liquidbounce.utils.block.BlockUtils;
import net.ccbluex.liquidbounce.utils.misc.NewFallingPlayer;
import net.ccbluex.liquidbounce.utils.misc.RandomUtils;
import net.ccbluex.liquidbounce.value.*;

import net.minecraft.block.BlockAir;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.Packet;

import java.util.ArrayList;
import java.util.LinkedList;

import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(name = "AntiVoid", spacedName = "Anti Void", description = "Prevents you from falling into the void.", category = ModuleCategory.PLAYER)
public class AntiVoid extends Module {

    public final ListValue voidDetectionAlgorithm = new ListValue("Detect-Method", new String[]{"Collision", "Predict"}, "Collision");
    public final ListValue setBackModeValue = new ListValue("SetBack-Mode", new String[]{"Teleport", "FlyFlag", "IllegalPacket", "IllegalTeleport", "StopMotion", "Position", "Edit", "SpoofBack"}, "Teleport");
    public final IntegerValue maxFallDistSimulateValue = new IntegerValue("Predict-CheckFallDistance", 255, 0, 255, "m", () -> voidDetectionAlgorithm.get().equalsIgnoreCase("predict"));
    public final IntegerValue maxFindRangeValue = new IntegerValue("Predict-MaxFindRange", 60, 0, 255, "m", () -> voidDetectionAlgorithm.get().equalsIgnoreCase("predict"));
    public final IntegerValue illegalDupeValue = new IntegerValue("Illegal-Dupe", 1, 1, 5, "x", () -> setBackModeValue.get().toLowerCase().contains("illegal"));
    public final FloatValue setBackFallDistValue = new FloatValue("Max-FallDistance", 5F, 0F, 255F, "m");
    public final BoolValue resetFallDistanceValue = new BoolValue("Reset-FallDistance", true);
    public final BoolValue renderTraceValue = new BoolValue("Render-Trace", true);
    public final BoolValue scaffoldValue = new BoolValue("AutoScaffold", true);
    public final BoolValue noFlyValue = new BoolValue("NoFly", true);

    private BlockPos detectedLocation = BlockPos.ORIGIN;
    private double lastX = 0; 
    private double lastY = 0; 
    private double lastZ = 0;
    private double lastFound = 0;
    private boolean shouldRender, shouldStopMotion, shouldEdit = false;

    private final LinkedList<double[]> positions = new LinkedList<>();

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if (noFlyValue.get() && LiquidBounce.moduleManager.getModule(Fly.class).getState())
            return;

        detectedLocation = null;

        if (voidDetectionAlgorithm.get().equalsIgnoreCase("collision")) {
            if (mc.thePlayer.onGround && !(BlockUtils.getBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ)) instanceof BlockAir)) {
                lastX = mc.thePlayer.prevPosX;
                lastY = mc.thePlayer.prevPosY;
                lastZ = mc.thePlayer.prevPosZ;
            }

            shouldRender = renderTraceValue.get() && !MovementUtils.isBlockUnder();

            shouldStopMotion = false;
            shouldEdit = false;
            if (!MovementUtils.isBlockUnder()) {
                if (mc.thePlayer.fallDistance >= setBackFallDistValue.get()) {
                    shouldStopMotion = true;
                    switch (setBackModeValue.get()) {
                    case "IllegalTeleport":
                        mc.thePlayer.setPositionAndUpdate(lastX, lastY, lastZ);
                    case "IllegalPacket":
                        for (int i = 0; i < illegalDupeValue.get(); i++) PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1E+159, mc.thePlayer.posZ, false));
                        break;
                    case "Teleport":
                        mc.thePlayer.setPositionAndUpdate(lastX, lastY, lastZ);
                        break;
                    case "FlyFlag":
                        mc.thePlayer.motionY = 0F;
                        break;
                    case "StopMotion":
                        float oldFallDist = mc.thePlayer.fallDistance;
                        mc.thePlayer.motionY = 0F;
                        mc.thePlayer.fallDistance = oldFallDist;
                        break;
                    case "Position":
                        PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY + RandomUtils.nextDouble(6D, 10D), mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
                        break;
                    case "Edit":
                    case "SpoofBack":
                        shouldEdit = true;
                        break;
                    }
                    if (resetFallDistanceValue.get() && !setBackModeValue.get().equalsIgnoreCase("StopMotion")) mc.thePlayer.fallDistance = 0;

                    if (scaffoldValue.get() && !LiquidBounce.moduleManager.getModule(Scaffold.class).getState())
                        LiquidBounce.moduleManager.getModule(Scaffold.class).setState(true);
                }
            }
        } else {
            if (mc.thePlayer.onGround && !(BlockUtils.getBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ)) instanceof BlockAir)) {
                lastX = mc.thePlayer.prevPosX;
                lastY = mc.thePlayer.prevPosY;
                lastZ = mc.thePlayer.prevPosZ;
            }

            shouldStopMotion = false;
            shouldEdit = false;
            shouldRender = false;

            if (!mc.thePlayer.onGround && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWater()) {
                NewFallingPlayer NewFallingPlayer = new NewFallingPlayer(mc.thePlayer);

                try {
                    detectedLocation = NewFallingPlayer.findCollision(maxFindRangeValue.get());
                } catch (Exception e) {
                    // do nothing. i hate errors
                }

                if (detectedLocation != null && Math.abs(mc.thePlayer.posY - detectedLocation.getY()) +
                    mc.thePlayer.fallDistance <= maxFallDistSimulateValue.get()) {
                    lastFound = mc.thePlayer.fallDistance;
                }

                shouldRender = renderTraceValue.get() && detectedLocation == null;

                if (mc.thePlayer.fallDistance - lastFound > setBackFallDistValue.get()) {
                    shouldStopMotion = true;
                    switch (setBackModeValue.get()) {
                    case "IllegalTeleport":
                        mc.thePlayer.setPositionAndUpdate(lastX, lastY, lastZ);
                    case "IllegalPacket":
                        for (int i = 0; i < illegalDupeValue.get(); i++) PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1E+159, mc.thePlayer.posZ, false));
                        break;
                    case "Teleport":
                        mc.thePlayer.setPositionAndUpdate(lastX, lastY, lastZ);
                        break;
                    case "FlyFlag":
                        mc.thePlayer.motionY = 0F;
                        break;
                    case "StopMotion":
                        float oldFallDist = mc.thePlayer.fallDistance;
                        mc.thePlayer.motionY = 0F;
                        mc.thePlayer.fallDistance = oldFallDist;
                        break;
                    case "Position":
                        PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY + RandomUtils.nextDouble(6D, 10D), mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
                        break;
                    case "Edit":
                    case "SpoofBack":
                        shouldEdit = true;
                        break;
                    }
                    if (resetFallDistanceValue.get() && !setBackModeValue.get().equalsIgnoreCase("StopMotion")) mc.thePlayer.fallDistance = 0;

                    if (scaffoldValue.get() && !LiquidBounce.moduleManager.getModule(Scaffold.class).getState())
                        LiquidBounce.moduleManager.getModule(Scaffold.class).setState(true);

                    /*if (towerValue.get() && !LiquidBounce.moduleManager.getModule(Tower.class).getState())
                        LiquidBounce.moduleManager.getModule(Tower.class).setState(true);*/
                }
            }
        }

        if (shouldRender) synchronized (positions) {
            positions.add(new double[]{mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY, mc.thePlayer.posZ});
        }
        else synchronized (positions) {
            positions.clear();
        }
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        if (noFlyValue.get() && LiquidBounce.moduleManager.getModule(Fly.class).getState())
            return;
            
        if (setBackModeValue.get().equalsIgnoreCase("StopMotion") && event.getPacket() instanceof S08PacketPlayerPosLook)
            mc.thePlayer.fallDistance = 0;

        if (setBackModeValue.get().equalsIgnoreCase("Edit") && shouldEdit && event.getPacket() instanceof C03PacketPlayer) {
            final C03PacketPlayer packetPlayer = (C03PacketPlayer) event.getPacket();
            packetPlayer.y += 100D;
            shouldEdit = false;
        }

        if (setBackModeValue.get().equalsIgnoreCase("SpoofBack") && shouldEdit && event.getPacket() instanceof C03PacketPlayer) {
            final C03PacketPlayer packetPlayer = (C03PacketPlayer) event.getPacket();
            packetPlayer.x = lastX;
            packetPlayer.y = lastY;
            packetPlayer.z = lastZ;
            packetPlayer.setMoving(false);
            shouldEdit = false;
        }
    }

    @EventTarget
    public void onMove(MoveEvent event) {
        if (noFlyValue.get() && LiquidBounce.moduleManager.getModule(Fly.class).getState())
            return;

        if (setBackModeValue.get().equalsIgnoreCase("StopMotion") && shouldStopMotion) {
            event.zero();
        }
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        if (noFlyValue.get() && LiquidBounce.moduleManager.getModule(Fly.class).getState())
            return;
            
        if (shouldRender) synchronized (positions) {
            glPushMatrix();

            glDisable(GL_TEXTURE_2D);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glEnable(GL_LINE_SMOOTH);
            glEnable(GL_BLEND);
            glDisable(GL_DEPTH_TEST);
            mc.entityRenderer.disableLightmap();
            glLineWidth(1F);
            glBegin(GL_LINE_STRIP);
            glColor4f(1F, 1F, 0.1F, 1F);
            final double renderPosX = mc.getRenderManager().viewerPosX;
            final double renderPosY = mc.getRenderManager().viewerPosY;
            final double renderPosZ = mc.getRenderManager().viewerPosZ;

            for (final double[] pos : positions)
                glVertex3d(pos[0] - renderPosX, pos[1] - renderPosY, pos[2] - renderPosZ);

            glColor4d(1, 1, 1, 1);
            glEnd();
            glEnable(GL_DEPTH_TEST);
            glDisable(GL_LINE_SMOOTH);
            glDisable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);
            glPopMatrix();
        }
    }

    @Override
    public void onDisable() {
        reset();
        super.onDisable();
    }

    @Override
    public void onEnable() {
        reset();
        super.onEnable();
    }

    @Override
    public String getTag() {
        return setBackModeValue.get();
    }

    private void reset() {
        detectedLocation = null;
        lastX = lastY = lastZ = lastFound = 0;
        shouldStopMotion = shouldRender = false;
        synchronized (positions) {
            positions.clear();
        }
    }

}
