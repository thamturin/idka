package net.ccbluex.liquidbounce.features.module.modules.misc.autoplays.heromc

import net.ccbluex.liquidbounce.features.module.modules.misc.autoplays.AutoPlayMode
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.event.PacketEvent
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.network.play.server.S02PacketChat
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.inventory.Slot

class HeroMCSkywars: AutoPlayMode("HeroMC_Skywars") {
	private val Delay = MSTimer()
	private var joining = false
	private var leaving = false
	private var currentServer = ""

	override fun onEnable() {
		joining = false
		leaving = false
		currentServer = ""
	}

	fun openItem(slot: Int) {
		mc.netHandler.addToSendQueue(C09PacketHeldItemChange(slot))
        mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()))
	}

	fun clickItem(slot: Slot, name: String): Boolean {
		val stack = slot.stack
        if (stack != null && stack!!.item.unlocalizedName == name) {
        	(mc.currentScreen as GuiChest).handleMouseClick(slot, slot.slotNumber, 0, 1)
            return true
        }
        return false
    }

	fun handleInv(name: String): Boolean {
		val slots = (mc.currentScreen as GuiChest).inventorySlots.inventorySlots
        for (slot in slots) {
            if (clickItem(slot, name)) {
            	Delay.reset()
                return true
            }
        }
        return false
	}

	fun doJoin(slot: Int, name: String): Boolean {
		openItem(slot)
		if (mc.currentScreen is GuiChest) return handleInv(name)
        return false
	}

	override fun onUpdate() {
		if (currentServer == "skywars_room" && mc.thePlayer.isSpectator) {
			leaving = true
		}
		if (Delay.hasTimePassed(500)) {
			if (leaving) 
				if (mc.currentScreen is GuiChest) handleInv("tile.cloth")
			else 
				mc.thePlayer.sendChatMessage("/lobby")
			if (joining) joining = false
			Delay.reset()
			return
		}

		val scoreObjectives = mc.theWorld.getScoreboard().getScoreObjectives()

		for (scoreObjective in scoreObjectives) {
			if (scoreObjective.displayName == "§e§lHEROMC" && currentServer != "minigame") {
				currentServer = "minigame"
				joining = false
				leaving = false
			}
			if (scoreObjective.displayName == "§a§lSKYWARS" && currentServer != "skywars_lobby") {
				val stack = mc.thePlayer.inventoryContainer.getSlot(36).stack
				if (stack != null && stack!!.item.unlocalizedName == "item.compass") {
					currentServer = "skywars_lobby"
					joining = false
				} else {
					currentServer = "skywars_room"
				}
			}
		}

		if (!joining) {
			if (currentServer == "minigame") {
				joining = doJoin(0, "item.eyeOfEnder")
			}
			if (currentServer == "skywars_lobby") {
				openItem(0)
				joining = true
				currentServer = "skywars_room"
			}
		}
	}

	override fun onPacket(event: PacketEvent) {
		val packet = event.packet
		if (packet is S02PacketChat) {
			val message = (packet as S02PacketChat).chatComponent.getUnformattedText()
			if (message.contains("Could not connect to a default or fallback server")) {
				if (currentServer == "skywars_room") leaving = true
				joining = false
			}
			if (message.contains("Ket noi that bai")) joining = false
			if (message.contains("thắng trận đấu") || message.contains("lobby")) {
				mc.thePlayer.sendChatMessage("/lobby")
                Delay.reset()
                leaving = true
			}
		}
	}

}