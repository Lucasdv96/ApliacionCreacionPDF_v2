package com.example.myapplication.data.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

class SharingService(private val context: Context) {

    fun sharePdf(pdfPath: String, budgetNumber: String) {
        val pdfFile = File(pdfPath)
        if (!pdfFile.exists()) return

        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Presupuesto $budgetNumber")
            putExtra(Intent.EXTRA_TEXT, "Adjunto encontrará el presupuesto $budgetNumber.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(shareIntent, "Compartir Presupuesto").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(chooser)
    }

    fun shareViaWhatsApp(pdfPath: String, budgetNumber: String) {
        val pdfFile = File(pdfPath)
        if (!pdfFile.exists()) return

        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )

        val whatsappIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            setPackage("com.whatsapp")
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "Presupuesto $budgetNumber")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(whatsappIntent)
        } catch (e: Exception) {
            // WhatsApp no instalado, fallback al selector general
            sharePdf(pdfPath, budgetNumber)
        }
    }

    fun shareViaEmail(pdfPath: String, budgetNumber: String) {
        val pdfFile = File(pdfPath)
        if (!pdfFile.exists()) return

        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )

        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Presupuesto $budgetNumber")
            putExtra(Intent.EXTRA_TEXT, "Estimado cliente,\n\nAdjunto encontrará el presupuesto $budgetNumber.\n\nSaludos.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val chooser = Intent.createChooser(emailIntent, "Enviar por Email").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(chooser)
    }
}
