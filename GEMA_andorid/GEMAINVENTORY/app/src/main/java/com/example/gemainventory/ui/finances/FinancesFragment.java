package com.example.gemainventory.ui.finances;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.gemainventory.R;
import com.example.gemainventory.model.MovimientoFinancieroDto;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.color.MaterialColors;

import java.util.ArrayList;
import java.util.List;

public class FinancesFragment extends Fragment {

    private LineChart lineChart;
    private Button btnGenerarReporte;
    private String userId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_finances, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lineChart = view.findViewById(R.id.financial_chart);
        setupChart();
        setupData();

        btnGenerarReporte = view.findViewById(R.id.generate_report_button);
        android.widget.AutoCompleteTextView tvReportType = view.findViewById(R.id.tv_report_type);

        String[] options = {"Reporte Mensual", "Reporte Anual", "Historial Completo"};
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                requireContext(), 
                android.R.layout.simple_dropdown_item_1line, 
                options);
        
        if (tvReportType != null) {
            tvReportType.setAdapter(adapter);
            if (tvReportType.getText().toString().isEmpty()) {
                tvReportType.setText(options[0], false);
            }
        }

        btnGenerarReporte.setOnClickListener(v -> {
            String selected = "";
            if (tvReportType != null) {
                selected = tvReportType.getText().toString();
            }
            
            String tipo = "MENSUAL";
            if (selected.contains("Anual")) tipo = "ANUAL";
            else if (selected.contains("Historial")) tipo = "TODO";
            else tipo = "MENSUAL";

            generarReporte(tipo);
        });
    }



    private void generarReporte(String tipo) {
        if (listaIngresosCache == null || listaGastosCache == null) {
            Toast.makeText(getContext(), "Cargando datos, intente de nuevo...", Toast.LENGTH_SHORT).show();
            return;
        }

        List<MovimientoFinancieroDto> movimientos = new ArrayList<>();
        java.util.Calendar now = java.util.Calendar.getInstance();
        int mesActual = now.get(java.util.Calendar.MONTH) + 1;
        int anioActual = now.get(java.util.Calendar.YEAR);
        
        String tituloReporte = "Reporte General";

        for (com.example.gemainventory.model.PedidoDto p : listaIngresosCache) {
            if (p.getIdEstado() == 2) {
                 boolean incluir = false;
                 String fechaStr = p.getFechaPedido();
                 int pMes = obtenerMes(fechaStr);
                 int pAnio = obtenerAnio(fechaStr);

                 if (tipo.equals("TODO")) incluir = true;
                 else if (tipo.equals("ANUAL") && pAnio == anioActual) incluir = true;
                 else if (tipo.equals("MENSUAL") && pAnio == anioActual && pMes == mesActual) incluir = true;

                 if (incluir) {
                     movimientos.add(new MovimientoFinancieroDto(
                         p.getId(), 
                         "Venta - " + (p.getNombre() != null ? p.getNombre() : "Pedido"), 
                         p.getTotal(), 
                         "INGRESO", 
                         p.getFechaPedido(), 
                         userId
                     ));
                 }
            }
        }

        for (com.example.gemainventory.model.CompraDto c : listaGastosCache) {
             boolean incluir = false;
             String fechaStr = c.getFechaCompra() != null ? c.getFechaCompra().toString() : "";
             int cMes = obtenerMes(fechaStr);
             int cAnio = obtenerAnio(fechaStr);

             if (tipo.equals("TODO")) incluir = true;
             else if (tipo.equals("ANUAL") && cAnio == anioActual) incluir = true;
             else if (tipo.equals("MENSUAL") && cAnio == anioActual && cMes == mesActual) incluir = true;

             if (incluir) {
                 movimientos.add(new MovimientoFinancieroDto(
                     c.getId(), 
                     "Compra de Stock", 
                     c.getTotal(), 
                     "GASTO", 
                     fechaStr, 
                     userId
                 ));
             }
        }
        


        
        if (tipo.equals("MENSUAL")) {
             String[] meses = new String[]{"", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
             String nombreMes = (mesActual >= 1 && mesActual <= 12) ? meses[mesActual] : "";
             tituloReporte = "Reporte Mensual - " + nombreMes + " " + anioActual;
        }
        else if (tipo.equals("ANUAL")) {
             tituloReporte = "Reporte Anual - " + anioActual;
        }
        else {
             java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
             tituloReporte = "Historial Completo - " + sdf.format(now.getTime());
        }

        try {
            PdfGenerator.generarReporteFinanciero(requireContext(), movimientos, tituloReporte);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private List<com.example.gemainventory.model.PedidoDto> listaIngresosCache;
    private List<com.example.gemainventory.model.CompraDto> listaGastosCache;
    private List<com.example.gemainventory.model.BalanceFinancieroDto> listaBalancesCache;
    
    private int obtenerAnio(String fechaStr) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        if (fechaStr == null || fechaStr.isEmpty()) return cal.get(java.util.Calendar.YEAR);
        try {
            if (fechaStr.length() >= 4) {
                 return Integer.parseInt(fechaStr.substring(0, 4));
            }
        } catch (Exception e) {}
        return cal.get(java.util.Calendar.YEAR);
    }

    private void setupData() {
        android.content.SharedPreferences prefs = requireActivity().getSharedPreferences("GemaPrefs", android.content.Context.MODE_PRIVATE);
        userId = prefs.getString("user_id", null);
        
        if (userId == null) {
            Toast.makeText(getContext(), "Error: Usuario no identificado.", Toast.LENGTH_SHORT).show();
            return;
        }

        com.example.gemainventory.api.RetrofitClient.INSTANCE.getInstance().getPedidos(userId).enqueue(new retrofit2.Callback<java.util.List<com.example.gemainventory.model.PedidoDto>>() {
            @Override
            public void onResponse(retrofit2.Call<java.util.List<com.example.gemainventory.model.PedidoDto>> call, retrofit2.Response<java.util.List<com.example.gemainventory.model.PedidoDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    java.util.List<com.example.gemainventory.model.PedidoDto> pedidos = response.body();
                    listaIngresosCache = pedidos;
                    
                    com.example.gemainventory.api.RetrofitClient.INSTANCE.getInstance().getCompras(userId).enqueue(new retrofit2.Callback<java.util.List<com.example.gemainventory.model.CompraDto>>() {
                         @Override
                         public void onResponse(retrofit2.Call<java.util.List<com.example.gemainventory.model.CompraDto>> call2, retrofit2.Response<java.util.List<com.example.gemainventory.model.CompraDto>> response2) {
                             java.util.List<com.example.gemainventory.model.CompraDto> compras = new java.util.ArrayList<>();
                             if (response2.isSuccessful() && response2.body() != null) {
                                 compras = response2.body();
                             }
                             listaGastosCache = compras;
                             
                             // 3. Cargar Balances (Históricos/Manualmente agregados)
                             final List<com.example.gemainventory.model.CompraDto> finalCompras = compras;
                             com.example.gemainventory.api.RetrofitClient.INSTANCE.getInstance().getBalances(userId).enqueue(new retrofit2.Callback<java.util.List<com.example.gemainventory.model.BalanceFinancieroDto>>() {
                                  @Override
                                  public void onResponse(retrofit2.Call<java.util.List<com.example.gemainventory.model.BalanceFinancieroDto>> call3, retrofit2.Response<java.util.List<com.example.gemainventory.model.BalanceFinancieroDto>> response3) {
                                      java.util.List<com.example.gemainventory.model.BalanceFinancieroDto> balances = new java.util.ArrayList<>();
                                      if (response3.isSuccessful() && response3.body() != null) {
                                          balances = response3.body();
                                      }
                                      listaBalancesCache = balances;
                                      procesarDatosFinancieros(pedidos, finalCompras, balances);
                                  }

                                  @Override
                                  public void onFailure(retrofit2.Call<java.util.List<com.example.gemainventory.model.BalanceFinancieroDto>> call3, Throwable t) {
                                      procesarDatosFinancieros(pedidos, finalCompras, new java.util.ArrayList<>());
                                  }
                             });
                         }

                         @Override
                         public void onFailure(retrofit2.Call<java.util.List<com.example.gemainventory.model.CompraDto>> call2, Throwable t) {
                             // Fallback a procesar solo pedidos y balances
                             com.example.gemainventory.api.RetrofitClient.INSTANCE.getInstance().getBalances(userId).enqueue(new retrofit2.Callback<java.util.List<com.example.gemainventory.model.BalanceFinancieroDto>>() {
                                  @Override
                                  public void onResponse(retrofit2.Call<java.util.List<com.example.gemainventory.model.BalanceFinancieroDto>> call3, retrofit2.Response<java.util.List<com.example.gemainventory.model.BalanceFinancieroDto>> response3) {
                                       java.util.List<com.example.gemainventory.model.BalanceFinancieroDto> balances = new java.util.ArrayList<>();
                                       if (response3.isSuccessful() && response3.body() != null) {
                                           balances = response3.body();
                                           if (!balances.isEmpty()) {
                                               android.widget.Toast.makeText(getContext(), "Datos recuperados: " + balances.size(), android.widget.Toast.LENGTH_SHORT).show();
                                           }
                                       }
                                       procesarDatosFinancieros(pedidos, new java.util.ArrayList<>(), balances);
                                  }
                                  @Override public void onFailure(retrofit2.Call<java.util.List<com.example.gemainventory.model.BalanceFinancieroDto>> call3, Throwable t) {
                                      procesarDatosFinancieros(pedidos, new java.util.ArrayList<>(), new java.util.ArrayList<>());
                                  }
                             });
                         }
                    });

                } else {
                    Toast.makeText(getContext(), "Error al cargar pedidos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<java.util.List<com.example.gemainventory.model.PedidoDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void procesarDatosFinancieros(List<com.example.gemainventory.model.PedidoDto> pedidos, 
                                          List<com.example.gemainventory.model.CompraDto> compras,
                                          List<com.example.gemainventory.model.BalanceFinancieroDto> balances) {
        ArrayList<Entry> ingresosEntries = new ArrayList<>();
        ArrayList<Entry> gastosEntries = new ArrayList<>(); 
        
        float[] ingresosPorMes = new float[13];
        float[] gastosPorMes = new float[13];
        double totalIngresos = 0.0;
        double totalGastos = 0.0;

        // 1. Procesar Pedidos (Ingresos activos)
        for (com.example.gemainventory.model.PedidoDto pedido : pedidos) {
            if (pedido.getIdEstado() != null && pedido.getIdEstado() == 2 && pedido.getTotal() != null) {
                 totalIngresos += pedido.getTotal();

                 int mes = obtenerMes(pedido.getFechaPedido());
                 if (mes >= 1 && mes <= 12) {
                     ingresosPorMes[mes] += pedido.getTotal().floatValue();
                 }
            }
        }
        
        // 2. Procesar Compras (Gastos en stock)
        for (com.example.gemainventory.model.CompraDto compra : compras) {
            if (compra.getTotal() != null) {
                totalGastos += compra.getTotal();
                
                int mes = obtenerMes(compra.getFechaCompra() != null ? compra.getFechaCompra().toString() : null);
                if (mes >= 1 && mes <= 12) {
                    gastosPorMes[mes] += compra.getTotal().floatValue();
                }
            }
        }

        // 3. Procesar Balances (Históricos e ingresos fijos del sistema)
        if (balances != null) {
            for (com.example.gemainventory.model.BalanceFinancieroDto balance : balances) {
                if (balance.getMonto() != null) {
                    // ID 1 es INGRESO oficial
                    if (balance.getIdTipoBalance() != null && balance.getIdTipoBalance() == 1) {
                        totalIngresos += balance.getMonto();

                        int mes = obtenerMes(balance.getFecha());
                        if (mes >= 1 && mes <= 12) {
                            ingresosPorMes[mes] += balance.getMonto().floatValue();
                        }
                    } else if (balance.getIdTipoBalance() != null && balance.getIdTipoBalance() == 2) {
                        // Opcional: Soporte para egresos manuales en el futuro
                        totalGastos += balance.getMonto();
                        int mes = obtenerMes(balance.getFecha());
                        if (mes >= 1 && mes <= 12) {
                            gastosPorMes[mes] += balance.getMonto().floatValue();
                        }
                    }
                }
            }
        }
        
        actualizarTextosFinanzas(totalIngresos, totalGastos);


        
        for (int i = 1; i <= 12; i++) {
             ingresosEntries.add(new Entry(i, ingresosPorMes[i])); 
             gastosEntries.add(new Entry(i, gastosPorMes[i]));
        }

        actualizarGrafica(ingresosEntries, gastosEntries);
    }
    
    private int obtenerMes(String fechaStr) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        if (fechaStr == null || fechaStr.isEmpty()) return cal.get(java.util.Calendar.MONTH) + 1;
        try {
            if (fechaStr.length() >= 7) {
                 String mesStr = fechaStr.substring(5, 7);
                 return Integer.parseInt(mesStr);
            }
        } catch (Exception e) {
            return cal.get(java.util.Calendar.MONTH) + 1;
        }
        return cal.get(java.util.Calendar.MONTH) + 1;
    }

    private void actualizarTextosFinanzas(double ingresos, double gastos) {
        if (getView() == null) return;
        
        android.widget.TextView tvIncome = getView().findViewById(R.id.tv_total_income);
        android.widget.TextView tvExpenses = getView().findViewById(R.id.tv_total_expenses);
        android.widget.TextView tvProfit = getView().findViewById(R.id.tv_net_profit);

        java.text.NumberFormat formatMoneda = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.forLanguageTag("es-MX"));
        
        if (tvIncome != null) tvIncome.setText(formatMoneda.format(ingresos));
        if (tvExpenses != null) tvExpenses.setText(formatMoneda.format(gastos));
        
        double beneficio = ingresos - gastos;
        if (tvProfit != null) {
            tvProfit.setText(formatMoneda.format(beneficio));
            if (beneficio >= 0) {
                 tvProfit.setTextColor(android.graphics.Color.parseColor("#10B981")); // Emerald
            } else {
                 tvProfit.setTextColor(android.graphics.Color.parseColor("#EF4444")); // Coral Red
            }
        }
    }

    private void actualizarGrafica(ArrayList<Entry> ingresos, ArrayList<Entry> gastos) {
        int colorIngresos = Color.parseColor("#10B981"); // Emerald
        int colorGastos = Color.parseColor("#EF4444");   // Coral Red
        int textColor = MaterialColors.getColor(requireContext(), com.google.android.material.R.attr.colorOnSurface, Color.BLACK);

        LineDataSet setIngresos = new LineDataSet(ingresos, "Ingresos");
        setIngresos.setColor(colorIngresos);
        setIngresos.setLineWidth(3f);
        setIngresos.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        setIngresos.setCubicIntensity(0.15f);
        setIngresos.setDrawCircles(true);
        setIngresos.setCircleColor(colorIngresos);
        setIngresos.setCircleRadius(4f);
        setIngresos.setDrawCircleHole(false);
        setIngresos.setDrawValues(false); // Mantener limpio
        setIngresos.setDrawFilled(true);
        setIngresos.setFillColor(colorIngresos);
        setIngresos.setFillAlpha(40); // Sutil

        LineDataSet setGastos = new LineDataSet(gastos, "Gastos (Est.)");
        setGastos.setColor(colorGastos);
        setGastos.setLineWidth(3f);
        setGastos.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        setGastos.setCubicIntensity(0.15f);
        setGastos.setDrawCircles(true);
        setGastos.setCircleColor(colorGastos);
        setGastos.setCircleRadius(4f);
        setGastos.setDrawCircleHole(false);
        setGastos.setDrawValues(false);
        setGastos.setDrawFilled(true);
        setGastos.setFillColor(colorGastos);
        setGastos.setFillAlpha(40);

        LineData lineData = new LineData(setIngresos, setGastos);
        lineChart.setData(lineData);
        
        lineChart.getXAxis().setTextColor(textColor);
        lineChart.getAxisLeft().setTextColor(textColor);
        lineChart.getLegend().setTextColor(textColor);
        lineChart.getLegend().setForm(com.github.mikephil.charting.components.Legend.LegendForm.CIRCLE);
        
        lineChart.animateX(1200);
        lineChart.invalidate();
    }

    private void setupChart() {
        lineChart.getDescription().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        
        // Estilo de cuadrícula sutil
        lineChart.getAxisLeft().setDrawGridLines(true);
        lineChart.getAxisLeft().setGridColor(Color.parseColor("#1A888888"));
        lineChart.getAxisLeft().setDrawAxisLine(false);
        
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getXAxis().setDrawAxisLine(true);
        lineChart.getXAxis().setAxisLineColor(Color.parseColor("#33888888"));
        
        final String[] meses = new String[]{"", "Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
        lineChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index > 0 && index < meses.length) {
                    return meses[index];
                }
                return "";
            }
        });
        lineChart.getXAxis().setGranularity(1f);
        lineChart.getXAxis().setLabelCount(12);

        int textColor = MaterialColors.getColor(requireContext(), com.google.android.material.R.attr.colorOnSurface, Color.BLACK);
        lineChart.getXAxis().setTextColor(textColor);
        lineChart.getAxisLeft().setTextColor(textColor);
        lineChart.getLegend().setTextColor(textColor);
        lineChart.getLegend().setVerticalAlignment(com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.BOTTOM);
        lineChart.getLegend().setHorizontalAlignment(com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER);
        lineChart.getLegend().setOrientation(com.github.mikephil.charting.components.Legend.LegendOrientation.HORIZONTAL);
        lineChart.getLegend().setDrawInside(false);
    }
}