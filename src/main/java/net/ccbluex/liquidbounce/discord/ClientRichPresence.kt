package net.ccbluex.liquidbounce.discordrpc

import com.jagrosh.discordipc.IPCClient
import com.jagrosh.discordipc.IPCListener
import com.jagrosh.discordipc.entities.RichPresence
import com.jagrosh.discordipc.entities.pipe.PipeStatus
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.ui.client.gui.GuiMainMenu
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.ServerUtils
import net.minecraft.client.gui.GuiMultiplayer
import org.json.JSONObject
import java.time.OffsetDateTime
import kotlin.concurrent.thread

class ClientRichPresence : MinecraftInstance() {

    var showRichPresenceValue = true

    private val ipcClient = IPCClient(962156741200597032)
    private val timestamp = OffsetDateTime.now()
    private var running = false


    fun run() {
        ipcClient.setListener(object : IPCListener {
            override fun onReady(client: IPCClient?) {
                running = true
                thread {
                    while (running) {
                        update()
                        try {
                            Thread.sleep(1000L)
                        } catch (ignored: InterruptedException) {
                        }
                    }
                }
            }

            override fun onClose(client: IPCClient?, json: JSONObject?) {
                running = false
            }
        })
        try {
            ipcClient.connect()
        } catch (e: Exception) {
            ClientUtils.logError("DiscordRPC failed to start")
        } catch (e: RuntimeException) {
            ClientUtils.logError("DiscordRPC failed to start")
        }
    }

    private fun update() {
        val builder = RichPresence.Builder()
        // Set playing client time
        builder.setStartTimestamp(timestamp)

        builder.setLargeImage("https://i.ibb.co/3SmN8nc/giphy.gif", "LiquidBounce Minus")
        builder.setSmallImage("https://crosssine.github.io/file/bruh.gif", "hi")

        if (mc.currentScreen is GuiMainMenu) {
            builder.setDetails("MainMenu")
        }
        else
        if (mc.currentScreen is GuiMultiplayer) {
            builder.setDetails("Selecting Server")
        }
        else
        if (mc.theWorld != null && mc.theWorld.isRemote) {
            builder.setDetails("Playing : ${ServerUtils.getRemoteIp()}")
        }
        else
            builder.setDetails(mc.session.username + "is best player")

        builder.setState(Liquidbounce.CLIENT_NAME + " " + Liquidbounce.CLIENT_VERSION + if (Liquidbounce.CLIENT_STATUS) " Beta" else " Releases")
        // Check ipc client is connected and send rpc
        if (ipcClient.status == PipeStatus.CONNECTED) ipcClient.sendRichPresence(builder.build())
    }

    fun stop() {
        if (ipcClient.status == PipeStatus.CONNECTED) ipcClient.close()
    }
}
