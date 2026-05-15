package com.example.myapplication.data.service

import android.content.Context
import com.example.myapplication.data.db.entity.BudgetEntity
import com.example.myapplication.data.db.entity.BudgetItemEntity
import com.example.myapplication.utils.formatCurrency
import com.example.myapplication.data.db.entity.SettingsEntity
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PdfGeneratorService(private val context: Context) {

    fun generateBudgetPdf(
        budget: BudgetEntity,
        items: List<BudgetItemEntity>,
        client: com.example.myapplication.data.db.entity.ClientEntity?,
        settings: SettingsEntity
    ): String {
        val pdfDir = File(context.getExternalFilesDir(null), "presupuestos")
        pdfDir.mkdirs()

        val fileName = "Presupuesto_${budget.budgetNumber}_${System.currentTimeMillis()}.pdf"
        val pdfFile = File(pdfDir, fileName)

        val writer = PdfWriter(pdfFile.absolutePath)
        val pdfDocument = PdfDocument(writer)
        val document = Document(pdfDocument)

        addCompanyHeader(document, settings)
        document.add(Paragraph("\n"))
        addBudgetInfo(document, budget, client)
        document.add(Paragraph("\n"))
        addItemsTable(document, items)
        document.add(Paragraph("\n"))
        addPriceSummary(document, items, budget)

        if (budget.notes.isNotEmpty()) {
            document.add(Paragraph("\n"))
            document.add(Paragraph("NOTAS").setBold())
            document.add(Paragraph(budget.notes))
        }

        if (settings.termsConditions.isNotEmpty()) {
            document.add(Paragraph("\n"))
            document.add(Paragraph("TÉRMINOS Y CONDICIONES").setBold())
            document.add(Paragraph(settings.termsConditions).setFontSize(10f))
        }

        document.close()
        return pdfFile.absolutePath
    }

    private fun addCompanyHeader(document: Document, settings: SettingsEntity) {
        val headerTable = Table(UnitValue.createPercentArray(floatArrayOf(70f, 30f)))
        headerTable.setWidth(UnitValue.createPercentValue(100f))

        val companyInfo = StringBuilder()
        companyInfo.append("${settings.companyName}\n")
        if (settings.companyCuit.isNotEmpty()) companyInfo.append("CUIT: ${settings.companyCuit}\n")
        if (settings.companyAddress.isNotEmpty()) companyInfo.append("${settings.companyAddress}\n")
        if (settings.companyCity.isNotEmpty()) companyInfo.append("${settings.companyCity}")
        if (settings.companyPhone.isNotEmpty()) companyInfo.append("\nTeléfono: ${settings.companyPhone}\n")
        if (settings.companyEmail.isNotEmpty()) companyInfo.append("Email: ${settings.companyEmail}")

        headerTable.addCell(
            Cell().add(Paragraph(companyInfo.toString()).setBold().setFontSize(12f)).setBorder(null)
        )

        val logoFile = if (settings.logoPath.isNotEmpty()) File(settings.logoPath) else null
        if (logoFile != null && logoFile.exists()) {
            val imageData = ImageDataFactory.create(logoFile.absolutePath)
            val image = Image(imageData).setMaxHeight(100f).setMaxWidth(150f).setAutoScale(false)
            headerTable.addCell(
                Cell().add(image).setTextAlignment(TextAlignment.RIGHT).setBorder(null)
            )
        } else {
            headerTable.addCell(Cell().setBorder(null))
        }

        document.add(headerTable)
    }

    private fun addBudgetInfo(
        document: Document,
        budget: BudgetEntity,
        client: com.example.myapplication.data.db.entity.ClientEntity?
    ) {
        val infoTable = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f)))
        infoTable.setWidth(UnitValue.createPercentValue(100f))

        // 1. Sin estado
        val budgetInfoCell = Cell()
            .add(Paragraph("PRESUPUESTO #${budget.budgetNumber}").setBold())
            .add(Paragraph("Proyecto: ${budget.project}"))
            .add(Paragraph("Fecha: ${formatDate(budget.createdDate)}"))
            .setBorder(null)
        infoTable.addCell(budgetInfoCell)

        val clientInfoCell = Cell().add(Paragraph("CLIENTE").setBold())
        if (client != null) {
            clientInfoCell.add(Paragraph(client.name))
            if (client.cuit.isNotEmpty()) clientInfoCell.add(Paragraph("CUIT: ${client.cuit}"))
            if (client.address.isNotEmpty()) clientInfoCell.add(Paragraph(client.address))
            if (client.city.isNotEmpty()) clientInfoCell.add(
                Paragraph("${client.city}${if (client.province.isNotEmpty()) ", ${client.province}" else ""}")
            )
            if (client.phone.isNotEmpty()) clientInfoCell.add(Paragraph("Tel: ${client.phone}"))
            if (client.email.isNotEmpty()) clientInfoCell.add(Paragraph(client.email))
        }
        clientInfoCell.setBorder(null)
        infoTable.addCell(clientInfoCell)

        document.add(infoTable)
    }

    private fun addItemsTable(document: Document, items: List<BudgetItemEntity>) {
        // 2. Sin columna M.O. — columnas: Tipo | Descripción/Especificaciones/Notas | Cant | Precio | Subtotal
        val table = Table(UnitValue.createPercentArray(floatArrayOf(15f, 40f, 10f, 17f, 18f)))
        table.setWidth(UnitValue.createPercentValue(100f))

        listOf("Tipo", "Descripción", "Cant.", "Precio Unit.", "Subtotal").forEach { header ->
            table.addCell(
                Cell().add(Paragraph(header).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
            )
        }

        items.forEach { item ->
            val itemType = when (item.type) {
                "WINDOW" -> "Ventana"
                "DOOR" -> "Puerta"
                "RAILING" -> "Baranda"
                else -> "Otro"
            }
            val subtotal = item.quantity * item.unitPrice

            // 5. Descripción + especificaciones + notas en la misma celda
            val descCell = Cell()
            if (item.description.isNotEmpty()) descCell.add(Paragraph(item.description))
            if (item.specifications.isNotEmpty()) descCell.add(Paragraph(item.specifications).setFontSize(9f).setItalic())
            if (item.notes.isNotEmpty()) descCell.add(Paragraph("Nota: ${item.notes}").setFontSize(9f))

            table.addCell(Cell().add(Paragraph(itemType)))
            table.addCell(descCell)
            table.addCell(Cell().add(Paragraph(item.quantity.toString())).setTextAlignment(TextAlignment.CENTER))
            table.addCell(Cell().add(Paragraph(formatCurrency(item.unitPrice))).setTextAlignment(TextAlignment.RIGHT))
            table.addCell(Cell().add(Paragraph(formatCurrency(subtotal))).setTextAlignment(TextAlignment.RIGHT))
        }

        document.add(table)
    }

    private fun addPriceSummary(document: Document, items: List<BudgetItemEntity>, budget: BudgetEntity) {
        val summaryTable = Table(UnitValue.createPercentArray(floatArrayOf(70f, 30f)))
        summaryTable.setWidth(UnitValue.createPercentValue(100f))

        val itemsTotal = items.sumOf { it.quantity * it.unitPrice }
        val grandTotal = itemsTotal + budget.laborCostPerItem

        var cell = Cell().add(Paragraph("Subtotal Items:")).setTextAlignment(TextAlignment.RIGHT).setBorder(null)
        summaryTable.addCell(cell)
        cell = Cell().add(Paragraph(formatCurrency(itemsTotal))).setTextAlignment(TextAlignment.RIGHT).setBorder(null)
        summaryTable.addCell(cell)

        // 3. Sin "Mano de obra (items)" — 4. "Mano de obra" sin "(presupuesto)"
        if (budget.laborCostPerItem > 0) {
            cell = Cell().add(Paragraph("Mano de obra:")).setTextAlignment(TextAlignment.RIGHT).setBorder(null)
            summaryTable.addCell(cell)
            cell = Cell().add(Paragraph(formatCurrency(budget.laborCostPerItem))).setTextAlignment(TextAlignment.RIGHT).setBorder(null)
            summaryTable.addCell(cell)
        }

        cell = Cell().add(Paragraph("TOTAL:").setBold().setFontSize(14f)).setTextAlignment(TextAlignment.RIGHT)
            .setBorder(SolidBorder(1f))
        summaryTable.addCell(cell)
        cell = Cell().add(Paragraph(formatCurrency(grandTotal)).setBold().setFontSize(14f)).setTextAlignment(TextAlignment.RIGHT)
            .setBorder(SolidBorder(1f))
        summaryTable.addCell(cell)

        document.add(summaryTable)
    }

    private fun formatDate(timestamp: Long): String =
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(timestamp))
}
