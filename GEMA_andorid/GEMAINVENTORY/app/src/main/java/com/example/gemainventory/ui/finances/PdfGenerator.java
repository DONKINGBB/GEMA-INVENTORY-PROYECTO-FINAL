package com.example.gemainventory.ui.finances;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;

import com.example.gemainventory.model.MovimientoFinancieroDto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PdfGenerator {

    public static void generarReporteFinanciero(Context context, List<MovimientoFinancieroDto> listaMovimientos, String reportTitle) {
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // Colores de la Marca
        int colorPrimary = Color.parseColor("#1A1F2B"); // Dark Blue GEMA
        int colorAccent = Color.parseColor("#10B981");  // Emerald
        int colorExpense = Color.parseColor("#EF4444"); // Coral
        int colorRowAlt = Color.parseColor("#F9FAFB");  // Light Gray for Zebra
        int colorTextDim = Color.parseColor("#6B7280"); // Gray text

        Paint paint = new Paint();
        Paint titlePaint = new Paint();
        
        // 1. Dibujar Cabecera Profesional
        paint.setColor(colorPrimary);
        canvas.drawRect(0, 0, pageInfo.getPageWidth(), 100, paint);

        titlePaint.setColor(Color.WHITE);
        titlePaint.setTextSize(22);
        titlePaint.setFakeBoldText(true);
        canvas.drawText("GEMA INVENTORY", 40, 45, titlePaint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(12);
        paint.setFakeBoldText(false);
        canvas.drawText("Sistema de Gestión de Inventarios y Finanzas", 40, 65, paint);

        // Subtítulo del Reporte
        titlePaint.setTextSize(16);
        titlePaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(reportTitle.toUpperCase(), pageInfo.getPageWidth() - 40, 55, titlePaint);
        
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTextSize(10);
        canvas.drawText("Generado: " + java.text.DateFormat.getDateInstance().format(new java.util.Date()), pageInfo.getPageWidth() - 40, 75, paint);

        // 2. Definir Tabla
        int startX = 40;
        int startY = 140;
        int rowHeight = 25;
        
        // Cabecera de Tabla
        paint.setColor(Color.parseColor("#F3F4F6"));
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawRect(startX - 5, startY - 20, pageInfo.getPageWidth() - startX + 5, startY + 5, paint);
        
        paint.setColor(colorPrimary);
        paint.setFakeBoldText(true);
        paint.setTextSize(11);
        canvas.drawText("FECHA", startX, startY, paint);
        canvas.drawText("DESCRIPCIÓN", startX + 80, startY, paint);
        canvas.drawText("TIPO", startX + 280, startY, paint);
        canvas.drawText("MONTO", startX + 400, startY, paint);

        // 3. Dibujar Filas con "Zebra Striping"
        int y = startY + rowHeight + 5;
        paint.setFakeBoldText(false);
        double totalIngresos = 0;
        double totalGastos = 0;

        int index = 0;
        for (MovimientoFinancieroDto mov : listaMovimientos) {
            if (y > pageInfo.getPageHeight() - 150) break;

            // Fondo alterno
            if (index % 2 == 0) {
                paint.setColor(colorRowAlt);
                canvas.drawRect(startX - 5, y - 18, pageInfo.getPageWidth() - startX + 5, y + 7, paint);
            }

            paint.setColor(Color.BLACK);
            String fecha = mov.getFecha() != null && mov.getFecha().length() >= 10 ? mov.getFecha().substring(0, 10) : "";
            canvas.drawText(fecha, startX, y, paint);

            String desc = mov.getDescripcion();
            if (desc.length() > 35) desc = desc.substring(0, 32) + "...";
            canvas.drawText(desc, startX + 80, y, paint);

            // Tipo con color
            boolean esIngreso = "INGRESO".equalsIgnoreCase(mov.getTipo());
            paint.setColor(esIngreso ? colorAccent : colorExpense);
            canvas.drawText(mov.getTipo(), startX + 280, y, paint);

            // Monto
            paint.setColor(Color.BLACK);
            paint.setFakeBoldText(true);
            String montoStr = String.format("$%,.2f", mov.getMonto());
            canvas.drawText(montoStr, startX + 400, y, paint);
            paint.setFakeBoldText(false);

            if (esIngreso) totalIngresos += mov.getMonto();
            else totalGastos += mov.getMonto();

            y += rowHeight;
            index++;
        }

        // 4. Resumen Final Estilizado
        y += 40;
        paint.setColor(Color.parseColor("#F9FAFB"));
        canvas.drawRoundRect(startX, y, pageInfo.getPageWidth() - startX, y + 100, 10, 10, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#E5E7EB"));
        canvas.drawRoundRect(startX, y, pageInfo.getPageWidth() - startX, y + 100, 10, 10, paint);
        
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(colorPrimary);
        paint.setTextSize(12);
        paint.setFakeBoldText(true);
        canvas.drawText("RESUMEN DEL REPORTE", startX + 20, y + 30, paint);

        paint.setFakeBoldText(false);
        paint.setColor(Color.BLACK);
        canvas.drawText("Total Ingresos:", startX + 20, y + 55, paint);
        canvas.drawText("Total Gastos:", startX + 20, y + 75, paint);
        
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setColor(colorAccent);
        canvas.drawText(String.format("$%,.2f", totalIngresos), pageInfo.getPageWidth() - startX - 20, y + 55, paint);
        paint.setColor(colorExpense);
        canvas.drawText(String.format("$%,.2f", totalGastos), pageInfo.getPageWidth() - startX - 20, y + 75, paint);

        // Balance Neto resaltado
        y += 120;
        double balance = totalIngresos - totalGastos;
        paint.setColor(balance >= 0 ? colorAccent : colorExpense);
        paint.setTextSize(16);
        paint.setFakeBoldText(true);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("BALANCE NETO: " + String.format("$%,.2f", balance), pageInfo.getPageWidth() / 2, y, paint);

        // Pie de página
        paint.setColor(colorTextDim);
        paint.setTextSize(9);
        paint.setFakeBoldText(false);
        canvas.drawText("Este documento es un reporte oficial generado por GEMA Inventory. Prohibida su alteración.", pageInfo.getPageWidth() / 2, pageInfo.getPageHeight() - 40, paint);

        pdfDocument.finishPage(page);

        // Guardar Archivo
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String fileName = "GEMA_Reporte_" + System.currentTimeMillis() + ".pdf";
        File file = new File(downloadDir, fileName);

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(context, "PDF Profesional generado: " + fileName, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        pdfDocument.close();
    }
}