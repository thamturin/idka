package net.ccbluex.liquidbounce.features.module.modules.combat.criticals.other


import net.ccbluex.liquidbounce.features.module.modules.combat.criticals.CriticalMode
import net.ccbluex.liquidbounce.event.PacketEvent
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
class Redesky: CriticalMode("Redesky") {
	private var canCrits = true

	override fun onEnable() {
        canCrits = true
    }
	
	override fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is C03PacketPlayer) {
            val packetPlayer: C03PacketPlayer = packet as C03PacketPlayer
            if(mc.thePlayer.onGround && canCrits) {
                packetPlayer.y += 0.000001
                packetPlayer.onGround = false
            }
            if(mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(
                            0.0, (mc.thePlayer.motionY - 0.08) * 0.98, 0.0).expand(0.0, 0.0, 0.0)).isEmpty()) {
                packetPlayer.onGround = true;
            }
        }
        if(packet is C07PacketPlayerDigging) {
            if(packet.status == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                canCrits = false;
            } else if(packet.status == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK || packet.status == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK) {
                canCrits = true;
            }
        }
	}
}
