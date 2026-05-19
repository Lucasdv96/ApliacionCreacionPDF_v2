package com.example.myapplication.data.service

import com.example.myapplication.data.db.entity.BudgetItemEntity
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject
import com.itextpdf.layout.element.Image
import kotlin.math.min

class TechnicalDiagramDrawer {

    companion object {
        const val CANVAS_WIDTH = 220f
        const val CANVAS_HEIGHT = 170f
        private const val MARGIN_LEFT = 32f
        private const val MARGIN_BOTTOM = 28f
        private const val MARGIN_TOP = 10f
        private const val MARGIN_RIGHT = 10f
        private val DRAW_AREA_W = CANVAS_WIDTH - MARGIN_LEFT - MARGIN_RIGHT
        private val DRAW_AREA_H = CANVAS_HEIGHT - MARGIN_BOTTOM - MARGIN_TOP
    }

    fun createDiagram(
        item: BudgetItemEntity,
        pdfDocument: PdfDocument,
        logoImageData: com.itextpdf.io.image.ImageData? = null
    ): Image? {
        if (item.widthMm <= 0 || item.heightMm <= 0) return null
        if (item.type !in listOf("WINDOW", "DOOR", "RAILING")) return null

        val xObject = PdfFormXObject(Rectangle(CANVAS_WIDTH, CANVAS_HEIGHT))
        val canvas = PdfCanvas(xObject, pdfDocument)
        val font = PdfFontFactory.createFont(StandardFonts.HELVETICA)

        if (logoImageData != null) {
            try {
                val logoW = CANVAS_WIDTH * 0.65f
                val logoH = logoImageData.height.toFloat() * (logoW / logoImageData.width.toFloat())
                val lx = (CANVAS_WIDTH - logoW) / 2f
                val ly = (CANVAS_HEIGHT - logoH) / 2f
                val gs = com.itextpdf.kernel.pdf.extgstate.PdfExtGState()
                    .setFillOpacity(0.08f).setStrokeOpacity(0.08f)
                canvas.saveState()
                canvas.setExtGState(gs)
                canvas.addImageFittedIntoRectangle(
                    logoImageData,
                    com.itextpdf.kernel.geom.Rectangle(lx, ly, logoW, logoH),
                    false
                )
                canvas.restoreState()
            } catch (_: Exception) { }
        }

        val scale = min(DRAW_AREA_W / item.widthMm, DRAW_AREA_H / item.heightMm)
        val drawW = item.widthMm * scale
        val drawH = item.heightMm * scale

        // Center element within the drawing area
        val originX = MARGIN_LEFT + (DRAW_AREA_W - drawW) / 2f
        val originY = MARGIN_BOTTOM + (DRAW_AREA_H - drawH) / 2f

        when (item.type) {
            "WINDOW" -> drawWindow(canvas, originX, originY, drawW, drawH, item.panelCount.coerceAtLeast(1), item.panelTypes)
            "DOOR"   -> drawDoor(canvas, originX, originY, drawW, drawH, item.panelCount.coerceAtLeast(1))
            "RAILING" -> drawRailing(canvas, originX, originY, drawW, drawH)
        }

        drawDimensions(canvas, font, originX, originY, drawW, drawH, item.widthMm, item.heightMm)
        canvas.release()

        return Image(xObject)
    }

    // ── WINDOW ────────────────────────────────────────────────────────────────

    private fun drawWindow(
        canvas: PdfCanvas, x: Float, y: Float, w: Float, h: Float,
        panels: Int, panelTypes: String = ""
    ) {
        val inset = 3f
        val typeList = panelTypes.split(",")

        // Outer frame
        canvas.setLineWidth(2f)
        canvas.rectangle(x.toDouble(), y.toDouble(), w.toDouble(), h.toDouble())
        canvas.stroke()

        // Inner frame
        canvas.setLineWidth(0.8f)
        canvas.rectangle(
            (x + inset).toDouble(), (y + inset).toDouble(),
            (w - inset * 2).toDouble(), (h - inset * 2).toDouble()
        )
        canvas.stroke()

        // Panel dividers
        val panelW = w / panels
        for (i in 1 until panels) {
            val divX = x + panelW * i
            canvas.setLineWidth(0.8f)
            canvas.moveTo(divX.toDouble(), (y + inset).toDouble())
            canvas.lineTo(divX.toDouble(), (y + h - inset).toDouble())
            canvas.stroke()
        }

        // Per-panel indicator: F = cross (✕), M = sliding arrow
        canvas.setLineWidth(0.5f)
        for (i in 0 until panels) {
            val px = x + panelW * i
            val isFijo = typeList.getOrElse(i) { "M" } == "F"
            if (isFijo) {
                drawFixedCross(canvas, px + inset, y + inset, panelW - inset * 2, h - inset * 2)
            } else {
                val panelCenterX = px + panelW / 2f
                val arrowY = y + h / 2f
                val dir = if (i % 2 == 0) 1f else -1f
                drawSlidingArrow(canvas, panelCenterX, arrowY, panelW * 0.3f, dir)
            }
        }
    }

    private fun drawFixedCross(canvas: PdfCanvas, x: Float, y: Float, w: Float, h: Float) {
        val margin = minOf(w, h) * 0.15f
        canvas.setLineWidth(0.6f)
        canvas.moveTo((x + margin).toDouble(), (y + margin).toDouble())
        canvas.lineTo((x + w - margin).toDouble(), (y + h - margin).toDouble())
        canvas.stroke()
        canvas.moveTo((x + w - margin).toDouble(), (y + margin).toDouble())
        canvas.lineTo((x + margin).toDouble(), (y + h - margin).toDouble())
        canvas.stroke()
    }

    private fun drawSlidingArrow(canvas: PdfCanvas, cx: Float, cy: Float, half: Float, dir: Float) {
        val startX = cx - half * dir
        val endX   = cx + half * dir
        val tip    = 3.5f

        canvas.moveTo(startX.toDouble(), cy.toDouble())
        canvas.lineTo(endX.toDouble(), cy.toDouble())
        canvas.stroke()

        // Filled arrowhead
        canvas.moveTo(endX.toDouble(), cy.toDouble())
        canvas.lineTo((endX - tip * dir).toDouble(), (cy + tip * 0.5f).toDouble())
        canvas.lineTo((endX - tip * dir).toDouble(), (cy - tip * 0.5f).toDouble())
        canvas.closePath()
        canvas.fill()
    }

    // ── DOOR ──────────────────────────────────────────────────────────────────

    private fun drawDoor(canvas: PdfCanvas, x: Float, y: Float, w: Float, h: Float, panels: Int) {
        val inset = 3f

        // Outer frame
        canvas.setLineWidth(2f)
        canvas.rectangle(x.toDouble(), y.toDouble(), w.toDouble(), h.toDouble())
        canvas.stroke()

        canvas.setLineWidth(0.8f)

        if (panels == 1) {
            // Door leaf (vertical line at left edge = closed)
            canvas.moveTo((x + inset).toDouble(), (y + inset).toDouble())
            canvas.lineTo((x + inset).toDouble(), (y + h - inset).toDouble())
            canvas.stroke()

            // Swing arc — radius equals door width
            val r = (w - inset * 2f).toDouble()
            canvas.arc(
                (x + inset).toDouble(), (y + inset).toDouble(),
                (x + inset + r * 2).toDouble(), (y + inset + r * 2).toDouble(),
                0.0, 90.0
            )
            canvas.stroke()
        } else {
            // Centre divider
            val midX = x + w / 2f
            canvas.moveTo(midX.toDouble(), (y + inset).toDouble())
            canvas.lineTo(midX.toDouble(), (y + h - inset).toDouble())
            canvas.stroke()

            // Left leaf swing arc
            val rLeft = ((w / 2f) - inset).toDouble()
            canvas.arc(
                (x + inset).toDouble(), (y + inset).toDouble(),
                (x + inset + rLeft * 2).toDouble(), (y + inset + rLeft * 2).toDouble(),
                0.0, 90.0
            )
            canvas.stroke()

            // Right leaf swing arc (mirrors left)
            canvas.arc(
                (midX).toDouble(), (y + inset).toDouble(),
                (midX + rLeft * 2).toDouble(), (y + inset + rLeft * 2).toDouble(),
                90.0, 90.0
            )
            canvas.stroke()
        }
    }

    // ── RAILING ───────────────────────────────────────────────────────────────

    private fun drawRailing(canvas: PdfCanvas, x: Float, y: Float, w: Float, h: Float) {
        val postW  = (w * 0.05f).coerceAtLeast(4f).coerceAtMost(9f)
        val railH  = (h * 0.14f).coerceAtLeast(5f).coerceAtMost(11f)
        val balW   = 2.5f
        val gray   = com.itextpdf.kernel.colors.DeviceGray(0.22f)

        canvas.setLineWidth(0.5f)
        canvas.setFillColor(gray)

        // Left post
        canvas.rectangle(x.toDouble(), y.toDouble(), postW.toDouble(), h.toDouble())
        canvas.fillStroke()
        // Right post
        canvas.rectangle((x + w - postW).toDouble(), y.toDouble(), postW.toDouble(), h.toDouble())
        canvas.fillStroke()
        // Top handrail
        canvas.rectangle(x.toDouble(), (y + h - railH).toDouble(), w.toDouble(), railH.toDouble())
        canvas.fillStroke()

        // Balusters — thin hollow rectangles evenly spaced between posts
        val innerX = x + postW
        val innerW = w - postW * 2f
        val innerY = y
        val innerH = h - railH
        val count  = (innerW / 10f).toInt().coerceAtLeast(1)

        canvas.setFillColor(com.itextpdf.kernel.colors.DeviceGray(0f))
        canvas.setLineWidth(0.4f)
        for (i in 1..count) {
            val bx = innerX + innerW * i.toFloat() / (count + 1).toFloat() - balW / 2f
            canvas.rectangle(bx.toDouble(), innerY.toDouble(), balW.toDouble(), innerH.toDouble())
            canvas.stroke()
        }

        // Reset fill to black
        canvas.setFillColor(com.itextpdf.kernel.colors.DeviceGray(0f))
    }

    // ── DIMENSION LINES ───────────────────────────────────────────────────────

    private fun drawDimensions(
        canvas: PdfCanvas,
        font: com.itextpdf.kernel.font.PdfFont,
        x: Float, y: Float,
        drawW: Float, drawH: Float,
        widthMm: Int, heightMm: Int
    ) {
        canvas.setLineWidth(0.4f)
        val tick = 4f
        val fontSize = 7f

        // Width dimension (bottom)
        val dimY = y - 14f
        canvas.moveTo(x.toDouble(), dimY.toDouble())
        canvas.lineTo((x + drawW).toDouble(), dimY.toDouble())
        canvas.stroke()
        canvas.moveTo(x.toDouble(), (dimY - tick).toDouble()); canvas.lineTo(x.toDouble(), (dimY + tick).toDouble()); canvas.stroke()
        canvas.moveTo((x + drawW).toDouble(), (dimY - tick).toDouble()); canvas.lineTo((x + drawW).toDouble(), (dimY + tick).toDouble()); canvas.stroke()

        val wText = "${widthMm}mm"
        val wTextW = font.getWidth(wText, fontSize)
        canvas.beginText()
        canvas.setFontAndSize(font, fontSize)
        canvas.moveText((x + drawW / 2 - wTextW / 2).toDouble(), (dimY - 9).toDouble())
        canvas.showText(wText)
        canvas.endText()

        // Height dimension (left, rotated 90°)
        val dimX = x - 16f
        canvas.moveTo(dimX.toDouble(), y.toDouble())
        canvas.lineTo(dimX.toDouble(), (y + drawH).toDouble())
        canvas.stroke()
        canvas.moveTo((dimX - tick).toDouble(), y.toDouble()); canvas.lineTo((dimX + tick).toDouble(), y.toDouble()); canvas.stroke()
        canvas.moveTo((dimX - tick).toDouble(), (y + drawH).toDouble()); canvas.lineTo((dimX + tick).toDouble(), (y + drawH).toDouble()); canvas.stroke()

        val hText = "${heightMm}mm"
        val hTextW = font.getWidth(hText, fontSize)
        canvas.beginText()
        canvas.setFontAndSize(font, fontSize)
        canvas.setTextMatrix(0f, 1f, -1f, 0f, dimX - 8f, y + drawH / 2 - hTextW / 2)
        canvas.showText(hText)
        canvas.endText()
    }
}
