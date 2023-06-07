/*
 * LiquidBounce++ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/PlusPlusMC/LiquidBouncePlusPlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.JumpEvent
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.event.StrafeEvent
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.client.*
import net.minecraft.network.play.client.C03PacketPlayer.*
import net.minecraft.network.play.server.*
import net.minecraft.util.MathHelper
import net.minecraft.util.BlockPos

import java.lang.Math
import kotlin.math.sin
import kotlin.math.cos

@ModuleInfo(name = "Velocity", description = "Allows you to modify the amount of knockback you take.", category = ModuleCategory.COMBAT)
class Velocity : Module() {

    /**
     * OPTIONS
     */
    private val horizontalValue = FloatValue("Horizontal", 0F, -1F, 1F, "x")
    private val verticalValue = FloatValue("Vertical", 0F, -1F, 1F, "x")
    private val horizontalExplosionValue = FloatValue("HorizontalExplosion", 0F, 0F, 1F, "x")
    private val verticalExplosionValue = FloatValue("VerticalExplosion", 0F, 0F, 1F, "x")
    private val modeValue = ListValue("Mode", arrayOf("Cancel", "Simple", "AACv4", "AAC4Reduce", "AAC5Reduce", "AAC5.2.0", "AAC", "AACPush", "AACZero",
            "Reverse", "SmoothReverse", "Jump", "Glitch", "Grim", "Intave"), "Grim") // later
    
    private val aac5KillAuraValue = BoolValue("AAC5.2.0-Attack-Only", true, { modeValue.get().equals("aac5.2.0", true) })

    // Affect chance
    private val reduceChance = FloatValue("Reduce-Chance", 100F, 0F, 100F, "%")
    private var shouldAffect : Boolean = true

    // Reverse
    private val reverseStrengthValue = FloatValue("ReverseStrength", 1F, 0.1F, 1F, "x", { modeValue.get().equals("reverse", true) })
    private val reverse2StrengthValue = FloatValue("SmoothReverseStrength", 0.05F, 0.02F, 0.1F, "x", { modeValue.get().equals("smoothreverse", true) })

    // AAC Push
    private val aacPushXZReducerValue = FloatValue("AACPushXZReducer", 2F, 1F, 3F, "x", { modeValue.get().equals("aacpush", true) })
    private val aacPushYReducerValue = BoolValue("AACPushYReducer", true, { modeValue.get().equals("aacpush", true) })

    //add strafe in aac
    private val aacStrafeValue = BoolValue("AACStrafeValue", false, { modeValue.get().equals("aac", true) })

    /**
     * VALUES
     */
    private var velocityTimer = MSTimer()
    private var velocityInput = false

    // SmoothReverse
    private var reverseHurt = false

    // AACPush
    private var jump = false
	
	// Grim
	private var cancelPacket = 6
    private var resetPersec = 8
    private var grimTCancel = 0
    private var updates = 0
    private var counter = 0;
    private var reset = false;
    override val tag: String
        get() = modeValue.get()

    override fun onDisable() {
        mc.thePlayer?.speedInAir = 0.02F
    }
    override fun onEnable() {
		grimTCancel = 0; reset = false;
    }

	
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer.hurtTime <= 0) shouldAffect = (Math.random().toFloat() < reduceChance.get() / 100F)
        if (mc.thePlayer.isInWater || mc.thePlayer.isInLava || mc.thePlayer.isInWeb || !shouldAffect)
            return

        when (modeValue.get().lowercase()) {
            "jump" -> if (mc.thePlayer.hurtTime > 0 && mc.thePlayer.onGround) {
                mc.thePlayer.motionY = 0.42

                val yaw = mc.thePlayer.rotationYaw * 0.017453292F
                mc.thePlayer.motionX -= MathHelper.sin(yaw) * 0.2
                mc.thePlayer.motionZ += MathHelper.cos(yaw) * 0.2
            }

            "glitch" -> {
                mc.thePlayer.noClip = velocityInput
                if (mc.thePlayer.hurtTime == 7)
                    mc.thePlayer.motionY = 0.4

                velocityInput = false
            }

            "reverse" -> {
                if (!velocityInput)
                    return

                if (!mc.thePlayer.onGround) {
                    MovementUtils.strafe(MovementUtils.getSpeed() * reverseStrengthValue.get())
                } else if (velocityTimer.hasTimePassed(80L))
                    velocityInput = false
            }

            "aacv4" -> {
                if (!mc.thePlayer.onGround) {
                    if (velocityInput) {
                        mc.thePlayer.speedInAir = 0.02f
                        mc.thePlayer.motionX *= 0.6
                        mc.thePlayer.motionZ *= 0.6
                    }
                } else if (velocityTimer.hasTimePassed(80L)) {
                    velocityInput = false
                    mc.thePlayer.speedInAir = 0.02f
                }
            }

            "aac4reduce" -> {
                if (mc.thePlayer.hurtTime>0 && !mc.thePlayer.onGround && velocityInput && velocityTimer.hasTimePassed(80L)){
                    mc.thePlayer.motionX *= 0.62
                    mc.thePlayer.motionZ *= 0.62
                }
                if(velocityInput && (mc.thePlayer.hurtTime<4 || mc.thePlayer.onGround) && velocityTimer.hasTimePassed(120L)) {
                    velocityInput = false
                }
            }
            
            "aac5reduce" -> {
                if (mc.thePlayer.hurtTime>1 && velocityInput){
                    mc.thePlayer.motionX *= 0.81
                    mc.thePlayer.motionZ *= 0.81
                }
                if(velocityInput && (mc.thePlayer.hurtTime<5 || mc.thePlayer.onGround) && velocityTimer.hasTimePassed(120L)) {
                    velocityInput = false
                }
            }

            "smoothreverse" -> {
                if (!velocityInput) {
                    mc.thePlayer.speedInAir = 0.02F
                    return
                }

                if (mc.thePlayer.hurtTime > 0)
                    reverseHurt = true

                if (!mc.thePlayer.onGround) {
                    if (reverseHurt)
                        mc.thePlayer.speedInAir = reverse2StrengthValue.get()
                } else if (velocityTimer.hasTimePassed(80)) {
                    velocityInput = false
                    reverseHurt = false
                }
            }
            
            "aac" -> if (velocityInput && velocityTimer.hasTimePassed(50)) {
                mc.thePlayer.motionX *= horizontalValue.get()
                mc.thePlayer.motionZ *= horizontalValue.get()
                mc.thePlayer.motionY *= verticalValue.get()
                if(aacStrafeValue.get()){
                    MovementUtils.strafe()
                }
                velocityInput = false
            }

            "aacpush" -> {
                if (jump) {
                    if (mc.thePlayer.onGround)
                        jump = false
                } else {
                    // Strafe
                    if (mc.thePlayer.hurtTime > 0 && mc.thePlayer.motionX != 0.0 && mc.thePlayer.motionZ != 0.0)
                        mc.thePlayer.onGround = true

                    // Reduce Y
                    if (mc.thePlayer.hurtResistantTime > 0 && aacPushYReducerValue.get()
                            && !LiquidBounce.moduleManager[Speed::class.java]!!.state)
                        mc.thePlayer.motionY -= 0.014999993
                }

                // Reduce XZ
                if (mc.thePlayer.hurtResistantTime >= 19) {
                    val reduce = aacPushXZReducerValue.get()

                    mc.thePlayer.motionX /= reduce
                    mc.thePlayer.motionZ /= reduce
                }
            }

            "aaczero" -> if (mc.thePlayer.hurtTime > 0) {
                if (!velocityInput || mc.thePlayer.onGround || mc.thePlayer.fallDistance > 2F)
                    return

                mc.thePlayer.addVelocity(0.0, -1.0, 0.0)
                mc.thePlayer.onGround = true
            } else
                velocityInput = false
			
			"grim" -> {
				updates++

				if (resetPersec > 0) {
					if (updates >= 0 || updates >= resetPersec) {
						updates = 0
						if (grimTCancel > 0) grimTCancel--
					}
				}
			}
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        val killAura = LiquidBounce.moduleManager[KillAura::class.java] as KillAura

        if (packet is S12PacketEntityVelocity) {

            if (mc.thePlayer == null || (mc.theWorld?.getEntityByID(packet.entityID) ?: return) != mc.thePlayer || !shouldAffect)
                return

            velocityTimer.reset()

            when (modeValue.get().lowercase()) {
                "cancel" -> event.cancelEvent()
                "simple" -> {
                    val horizontal = horizontalValue.get()
                    val vertical = verticalValue.get()

                    packet.motionX = (packet.getMotionX() * horizontal).toInt()
                    packet.motionY = (packet.getMotionY() * vertical).toInt()
                    packet.motionZ = (packet.getMotionZ() * horizontal).toInt()
                }

                "aac4reduce" -> {
                    velocityInput = true
                    packet.motionX = (packet.getMotionX() * 0.6).toInt()
                    packet.motionZ = (packet.getMotionZ() * 0.6).toInt()
                }

                "aac", "aac5reduce", "reverse", "aacv4", "smoothreverse", "aaczero" -> velocityInput = true

                "aac5.2.0" -> {
                    event.cancelEvent()
                    if (!mc.isIntegratedServerRunning() && (!aac5KillAuraValue.get() || killAura.target != null)) mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,1.7976931348623157E+308,mc.thePlayer.posZ,true))
                }

                "glitch" -> {
                    if (!mc.thePlayer.onGround) return
                    velocityInput = true
                    event.cancelEvent()
                }
                
                "grim" -> {
                    event.cancelEvent();
                    grimTCancel = cancelPacket
                }
            }
			
        }
        if (packet is S27PacketExplosion) {
            mc.thePlayer.motionX = mc.thePlayer.motionX + packet.func_149149_c() * (horizontalExplosionValue.get())
            mc.thePlayer.motionY = mc.thePlayer.motionY + packet.func_149144_d() * (verticalExplosionValue.get())
            mc.thePlayer.motionZ = mc.thePlayer.motionZ + packet.func_149147_e() * (horizontalExplosionValue.get())
            event.cancelEvent()
        }
		
		if (packet is S32PacketConfirmTransaction && grimTCancel > 0 && modeValue.get().lowercase() == "grim") {
			event.cancelEvent()
			grimTCancel--
		}
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        if (mc.thePlayer == null || mc.thePlayer.isInWater || mc.thePlayer.isInLava || mc.thePlayer.isInWeb || !shouldAffect)
            return

        when (modeValue.get().lowercase()) {
            "aacpush" -> {
                jump = true

                if (!mc.thePlayer.isCollidedVertically)
                    event.cancelEvent()
            }
            "aacv4" -> {
                if (mc.thePlayer.hurtTime > 0) {
                    event.cancelEvent()
                }
            }
            "aaczero" -> if (mc.thePlayer.hurtTime > 0)
                event.cancelEvent()
        }
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (modeValue.get().lowercase() == "intave") {
            if (mc.thePlayer.hurtTime == 9 && mc.thePlayer.onGround && counter++ % 2 == 0) mc.thePlayer.movementInput.jump = true
        }
    }
}



