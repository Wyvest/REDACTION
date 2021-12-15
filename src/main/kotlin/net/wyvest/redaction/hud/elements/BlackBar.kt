package net.wyvest.redaction.hud.elements

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.wyvest.redaction.Redaction.mc
import net.wyvest.redaction.config.RedactionConfig
import net.wyvest.redaction.hud.Element
import net.wyvest.redaction.utils.GlUtil
import net.wyvest.redaction.utils.MathUtil
import java.awt.event.ActionListener
import javax.swing.Timer

class BlackBar : Element() {

    private var data: BlackBarData? = null
    private var scaledResolution: ScaledResolution? = null
    private var partialTicks: Float? = null
    private val texPath = ResourceLocation("textures/gui/widgets.png")
    private var firstTime = true
    private var entityplayer: EntityPlayer? = null
    private var timer: Timer? = null

    private val timerTask: ActionListener = ActionListener {
        if (entityplayer != null && mc.thePlayer != null && mc.theWorld != null && scaledResolution != null && partialTicks != null) {
            data?.let {
                val scaledHeight = scaledResolution!!.scaledHeight
                val scaledWidth = scaledResolution!!.scaledWidth
                it.hiding = Minecraft.getMinecraft().currentScreen is GuiChat
                if (it.hiding) {
                    it.y = MathUtil.lerp(it.y.toFloat(), scaledHeight.toFloat() + 2, partialTicks!! / 4).toInt()
                } else if (it.y != scaledHeight - 21) {
                    it.y = MathUtil.lerp(it.y.toFloat(), scaledHeight.toFloat() - 22, partialTicks!! / 4).toInt()
                }
                if (it.lastSlot != entityplayer!!.inventory.currentItem) {
                    if (scaledWidth / 2 - 91F + entityplayer!!.inventory.currentItem * 20 != it.x) {
                        it.x = MathUtil.lerp(
                            it.x,
                            scaledWidth / 2 - 91F + entityplayer!!.inventory.currentItem * 20,
                            partialTicks!! / 4
                        )
                    } else {
                        it.lastSlot = entityplayer!!.inventory.currentItem
                    }
                } else {
                    it.lastSlot = entityplayer!!.inventory.currentItem
                    it.x = scaledWidth / 2 - 91F + entityplayer!!.inventory.currentItem * 20
                }
            }
        }
    }


    override fun initialize() {
        setTimer()
        val resolution = ScaledResolution(Minecraft.getMinecraft())
        scaledResolution = resolution
        data = BlackBarData(-1.0F, resolution.scaledHeight - 22)
    }

    fun setTimer() {
        timer?.stop()
        timer = Timer(RedactionConfig.blackbarSpeed, timerTask)
        timer?.start()
    }

    override fun render(
        res: ScaledResolution,
        partialTicks: Float
    ) {
        data?.let {
            scaledResolution = res
            this.partialTicks = partialTicks
            entityplayer = mc.renderViewEntity as EntityPlayer
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            mc.textureManager.bindTexture(texPath)
            val scaledWidth = res.scaledWidth
            val scaledHeight = res.scaledHeight
            if (firstTime) {
                firstTime = false
                it.x = 0F
                it.y = scaledHeight - 22
                it.hiding = false
            }
            if (RedactionConfig.blackbarColor.alpha != 0) {
                GlUtil.drawRectEnhanced(0, it.y, scaledWidth, 22, RedactionConfig.blackbarColor.rgb)
            }
            if (RedactionConfig.blackbarItemColor.alpha != 0) {
                when(RedactionConfig.blackbarSecondColor) {
                    0 -> GlUtil.drawRectangle(it.x, it.y.toFloat(), 22F, 22F, RedactionConfig.blackbarItemColor)
                    1 -> GlUtil.drawGradientRect(it.x, it.y.toFloat(), 22f, RedactionConfig.blackbarItemHeight.toFloat(), RedactionConfig.blackbarItemColor.rgb, RedactionConfig.blackbarItemColor2.rgb)
                    2 -> GlUtil.drawRectangle(it.x, it.y.toFloat() + 18f, 22F, 5f, RedactionConfig.blackbarItemColor)
                }
            }
        }
    }

    override fun actuallyRender(): Boolean {
        return false
    }
}

private class BlackBarData(
    var x: Float,
    var y: Int
) {
    var hiding = false
    var lastSlot = 10
}