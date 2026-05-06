package com.example.myapplication.data.service

import android.content.Context
import com.example.myapplication.data.db.entity.BudgetEntity
import com.example.myapplication.data.db.entity.BudgetItemEntity
import com.example.myapplication.data.db.entity.SettingsEntity
import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.Cell
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

        // Encabezado - Datos de la empresa
        addCompanyHeader(document, settings)

        // Espacio
        document.add(Paragraph("\n"))

        // Datos del presupuesto
        addBudgetInfo(document, budget, client)

        // Espacio
        document.add(Paragraph("\n"))

        // Tabla de items
        addItemsTable(document, items)

        // Espacio
        document.add(Paragraph("\n"))

        // Resumen de precios
        addPriceSummary(document, items, budget)

        // Notas
        if (budget.notes.isNotEmpty()) {
            document.add(Paragraph("NOTAS").setBold())
            document.add(Paragraph(budget.notes))
            document.add(Paragraph("\n"))
        }

        // Términos y condiciones
        if (settings.termsConditions.isNotEmpty()) {
            document.add(Paragraph("TÉRMINOS Y CONDICIONES").setBold())
            document.add(Paragraph(settings.termsConditions).setFontSize(10f))
        }

        document.close()

        return pdfFile.absolutePath
    }

    private fun addCompanyHeader(document: Document, settings: SettingsEntity) {
        val headerTable = Table(UnitValue.createPercentArray(floatArrayOf(70f, 30f)))
        headerTable.setWidth(UnitValue.createPercentValue(100f))

        // Datos de la empresa
        val companyInfo = StringBuilder()
        companyInfo.append("${settings.companyName}\n")
        if (settings.companyCuit.isNotEmpty()) companyInfo.append("CUIT: ${settings.companyCuit}\n")
        if (settings.companyAddress.isNotEmpty()) companyInfo.append("${settings.companyAddress}\n")
        if (settings.companyCity.isNotEmpty()) companyInfo.append("${settings.companyCity}")
        if (settings.companyPhone.isNotEmpty()) companyInfo.append("\nTeléfono: ${settings.companyPhone}\n")
        if (settings.companyEmail.isNotEmpty()) companyInfo.append("Email: ${settings.companyEmail}")

        val companyCell = Cell()
            .add(Paragraph(companyInfo.toString()).setBold().setFontSize(12f))
            .setBorder(null)

        headerTable.addCell(companyCell)

        // Logo placeholder (si existe)
        val logoCell = Cell()
            .add(Paragraph("LOGO").setTextAlignment(TextAlignment.RIGHT).setFontColor(ColorConstants.LIGHT_GRAY))
            .setBorder(null)

        headerTable.addCell(logoCell)

        document.add(headerTable)
    }

    private fun addBudgetInfo(
        document: Document,
        budget: BudgetEntity,
        client: com.example.myapplication.data.db.entity.ClientEntity?
    ) {
        val infoTable = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f)))
        infoTable.setWidth(UnitValue.createPercentValue(100f))

        // Presupuesto info
        val budgetInfoCell = Cell()
            .add(Paragraph("PRESUPUESTO #${budget.budgetNumber}").setBold())
            .add(Paragraph("Proyecto: ${budget.project}"))
            .add(Paragraph("Estado: ${budget.status}"))
            .add(Paragraph("Fecha: ${formatDate(budget.createdDate)}"))
            .setBorder(null)

        infoTable.addCell(budgetInfoCell)

        // Cliente info
        val clientInfoCell = Cell()
            .add(Paragraph("CLIENTE").setBold())
        if (client != null) {
            clientInfoCell
                .add(Paragraph(client.name))
            if (client.cuit.isNotEmpty()) clientInfoCell.add(Paragraph("CUIT: ${client.cuit}"))
            if (client.address.isNotEmpty()) clientInfoCell.add(Paragraph(client.address))
            if (client.city.isNotEmpty()) clientInfoCell.add(Paragraph("${client.city}${if (client.province.isNotEmpty()) ", ${client.province}" else ""}"))
            if (client.phone.isNotEmpty()) clientInfoCell.add(Paragraph("Tel: ${client.phone}"))
            if (client.email.isNotEmpty()) clientInfoCell.add(Paragraph(client.email))
        }
        clientInfoCell.setBorder(null)

        infoTable.addCell(clientInfoCell)

        document.add(infoTable)
    }

    private fun addItemsTable(document: Document, items: List<BudgetItemEntity>) {
        val table = Table(UnitValue.createPercentArray(floatArrayOf(15f, 25f, 15f, 15f, 15f, 15f)))
        table.setWidth(UnitValue.createPercentValue(100f))

        // Encabezados
        val headers = listOf("Tipo", "Descripción", "Cantidad", "Precio Unit.", "M.O.", "Subtotal")
        headers.forEach { header ->
            val cell = Cell()
                .add(Paragraph(header).setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
            table.addCell(cell)
        }

        // Items
        items.forEach { item ->
            val itemType = when (item.type) {
                "WINDOW" -> "Ventana"
                "DOOR" -> "Puerta"
                "RAILING" -> "Baranda"
                else -> "Otro"
            }
            val subtotal = item.quantity * item.unitPrice

            table.addCell(Cell().add(Paragraph(itemType)))
            table.addCell(Cell().add(Paragraph(item.description)))
            table.addCell(Cell().add(Paragraph(item.quantity.toString())).setTextAlignment(TextAlignment.CENTER))
            table.addCell(Cell().add(Paragraph("\$${String.format("%.2f", item.unitPrice)}")).setTextAlignment(TextAlignment.RIGHT))
            table.addCell(Cell().add(Paragraph("\$${String.format("%.2f", item.laborCost)}")).setTextAlignment(TextAlignment.RIGHT))
            table.addCell(Cell().add(Paragraph("\$${String.format("%.2f", subtotal)}")).setTextAlignment(TextAlignment.RIGHT))
        }

        document.add(table)
    }

    private fun addPriceSummary(document: Document, items: List<BudgetItemEntity>, budget: BudgetEntity) {
        val summaryTable = Table(UnitValue.createPercentArray(floatArrayOf(70f, 30f)))
        summaryTable.setWidth(UnitValue.createPercentValue(100f))

        val itemsTotal = items.sumOf { it.quantity * it.unitPrice }
        val laborTotal = items.sumOf { it.laborCost }
        val grandTotal = itemsTotal + laborTotal + budget.laborCostPerItem

        // Items
        var cell = Cell().add(Paragraph("Subtotal Items:")).setTextAlignment(TextAlignment.RIGHT).setBorder(null)
        summaryTable.addCell(cell)
        cell = Cell().add(Paragraph("\$${String.format("%.2f", itemsTotal)}")).setTextAlignment(TextAlignment.RIGHT).setBorder(null)
        summaryTable.addCell(cell)

        // Labor items
        cell = Cell().add(Paragraph("Mano de obra (items):")).setTextAlignment(TextAlignment.RIGHT).setBorder(null)
        summaryTable.addCell(cell)
        cell = Cell().add(Paragraph("\$${String.format("%.2f", laborTotal)}")).setTextAlignment(TextAlignment.RIGHT).setBorder(null)
        summaryTable.addCell(cell)

        // Labor presupuesto
        if (budget.laborCostPerItem > 0) {
            cell = Cell().add(Paragraph("Mano de obra (presupuesto):")).setTextAlignment(TextAlignment.RIGHT).setBorder(null)
            summaryTable.addCell(cell)
            cell = Cell().add(Paragraph("\$${String.format("%.2f", budget.laborCostPerItem)}")).setTextAlignment(TextAlignment.RIGHT).setBorder(null)
            summaryTable.addCell(cell)
        }

        // TOTAL
        cell = Cell().add(Paragraph("TOTAL:").setBold().setFontSize(14f)).setTextAlignment(TextAlignment.RIGHT)
            .setBorder(SolidBorder(1f))
        summaryTable.addCell(cell)
        cell = Cell().add(Paragraph("\$${String.format("%.2f", grandTotal)}").setBold().setFontSize(14f)).setTextAlignment(TextAlignment.RIGHT)
            .setBorder(SolidBorder(1f))
        summaryTable.addCell(cell)

        document.add(summaryTable)
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
