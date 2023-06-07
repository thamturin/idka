package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.command.Command
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.ModuleManager
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly
import net.ccbluex.liquidbounce.features.module.modules.combat.criticals.CriticalMode
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.utils.ClassUtils
import net.minecraft.entity.EntityLivingBase

@ModuleInfo(name = "Criticals", description = "Automatically deals critical hits.", category = ModuleCategory.COMBAT)
class Criticals : Module() {

    private val modes = ClassUtils.resolvePackage("${this.javaClass.`package`.name}.criticals", CriticalMode::class.java)
        .map{ it.newInstance() as CriticalMode }
        .sortedBy{ it.modeName }

    private val mode: CriticalMode
        get() = modes.find{modeValue.get().equals(it.modeName)} ?: throw NullPointerException()

    val modeValue: ListValue = object: ListValue("Mode", modes.map{ it.modeName }.toTypedArray(), "Jump") {
        override fun onChange(oldValue: String, newValue: String) {
            if (state) onDisable()
        }
        override fun onChanged(oldValue: String, newValue: String) {
            if (state) onEnable()
        }
    }

    val delayValue = IntegerValue("Delay", 0, 0, 500, "ms")
    val jumpHeightValue = FloatValue("JumpHeight", 0.42F, 0.1F, 0.42F)
    val downYValue = FloatValue("DownY", 0f, 0f, 0.1F)
    private val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)
    private val onlyAuraValue = BoolValue("OnlyAura", false)

    val msTimer = MSTimer()

    var entity: EntityLivingBase? = null

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (onlyAuraValue.get() && !LiquidBounce.moduleManager[KillAura::class.java]!!.state) return

        if (event.targetEntity is EntityLivingBase) {
            entity = event.targetEntity

            if (!mc.thePlayer.onGround || mc.thePlayer.isOnLadder || mc.thePlayer.isInWeb || mc.thePlayer.isInWater ||
                    mc.thePlayer.isInLava || mc.thePlayer.ridingEntity != null || entity!!.hurtTime > hurtTimeValue.get() ||
                    LiquidBounce.moduleManager[Fly::class.java]!!.state || !msTimer.hasTimePassed(delayValue.get().toLong()))
                return
            mode.onAttack(event)
            msTimer.reset()
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (onlyAuraValue.get() && !LiquidBounce.moduleManager[KillAura::class.java]!!.state) return
        mode.onPacket(event)
    }

    override val tag: String?
        get() = modeValue.get()
}
