package com.example.gemainventory.ui.finances;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gemainventory.model.MovimientoFinancieroDto;
import com.example.gemainventory.model.PedidoDto;
import com.example.gemainventory.model.CompraDto;
import com.example.gemainventory.model.BalanceFinancieroDto;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

public class FinancesFragment extends Fragment {

    private String userId;
    private FinancesComposeHelper composeHelper;
    private List<PedidoDto> listaIngresosCache;
    private List<CompraDto> listaGastosCache;
    private List<BalanceFinancieroDto> listaBalancesCache;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        androidx.compose.ui.platform.ComposeView composeView = new androidx.compose.ui.platform.ComposeView(requireContext());
        composeHelper = new FinancesComposeHelper(composeView);
        return composeView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        if (composeHelper != null) {
            composeHelper.setOnGenerateReport(tipo -> {
                generarReporte(tipo);
            });
        }
        
        setupData();
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

        for (PedidoDto p : listaIngresosCache) {
            // Relaxed check: Include if status is 2 (delivered) OR if we are showing EVERYTHING
            boolean isDelivered = p.getIdEstado() != null && p.getIdEstado() == 2;
            if (isDelivered || !tipo.equals("MENSUAL") && !tipo.equals("ANUAL")) {
                 boolean incluir = false;
                 String fechaStr = p.getFechaPedido();
                 int pMes = obtenerMes(fechaStr);
                 int pAnio = obtenerAnio(fechaStr);

                 if (tipo.equals("ANUAL")) incluir = (pAnio == anioActual);
                 else if (tipo.equals("MENSUAL")) incluir = (pAnio == anioActual && pMes == mesActual);
                 else incluir = true;

                 if (incluir) {
                     movimientos.add(new MovimientoFinancieroDto(
                         p.getId(), 
                         "Venta - " + (p.getNombre() != null ? p.getNombre() : "Pedido"), 
                         p.getTotal() != null ? p.getTotal() : 0.0, 
                         "INGRESO", 
                         p.getFechaPedido() != null ? p.getFechaPedido() : "", 
                         userId != null ? userId : "N/A"
                     ));
                 }
            }
        }

        for (CompraDto c : listaGastosCache) {
             boolean incluir = false;
             String fechaStr = c.getFechaCompra() != null ? c.getFechaCompra().toString() : "";
             int cMes = obtenerMes(fechaStr);
             int cAnio = obtenerAnio(fechaStr);

             if (tipo.equals("ANUAL")) incluir = (cAnio == anioActual);
             else if (tipo.equals("MENSUAL")) incluir = (cAnio == anioActual && cMes == mesActual);
             else incluir = true;

             if (incluir) {
                 movimientos.add(new MovimientoFinancieroDto(
                     c.getId(), 
                     "Compra de Stock", 
                     c.getTotal() != null ? c.getTotal() : 0.0, 
                     "GASTO", 
                     fechaStr, 
                     userId != null ? userId : "N/A"
                 ));
             }
        }

        // --- NEW: Include Balances (Manual Incomes/Expenses) ---
        if (listaBalancesCache != null) {
            for (BalanceFinancieroDto b : listaBalancesCache) {
                boolean incluir = false;
                String fechaStr = b.getFecha();
                int bMes = obtenerMes(fechaStr);
                int bAnio = obtenerAnio(fechaStr);

                if (tipo.equals("ANUAL")) incluir = (bAnio == anioActual);
                else if (tipo.equals("MENSUAL")) incluir = (bAnio == anioActual && bMes == mesActual);
                else incluir = true;

                if (incluir) {
                    boolean esIngreso = b.getIdTipoBalance() != null && b.getIdTipoBalance() == 1;
                    movimientos.add(new MovimientoFinancieroDto(
                        b.getIdBalance(),
                        b.getFuente() != null ? b.getFuente() : (esIngreso ? "Ingreso Manual" : "Gasto Manual"),
                        b.getMonto() != null ? b.getMonto() : 0.0,
                        esIngreso ? "INGRESO" : "GASTO",
                        fechaStr,
                        userId != null ? userId : "N/A"
                    ));
                }
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

        com.example.gemainventory.api.RetrofitClient.INSTANCE.getInstance().getPedidos(userId).enqueue(new retrofit2.Callback<java.util.List<PedidoDto>>() {
            @Override
            public void onResponse(retrofit2.Call<java.util.List<PedidoDto>> call, retrofit2.Response<java.util.List<PedidoDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    java.util.List<PedidoDto> pedidos = response.body();
                    listaIngresosCache = pedidos;
                    
                    com.example.gemainventory.api.RetrofitClient.INSTANCE.getInstance().getCompras(userId).enqueue(new retrofit2.Callback<java.util.List<CompraDto>>() {
                         @Override
                         public void onResponse(retrofit2.Call<java.util.List<CompraDto>> call2, retrofit2.Response<java.util.List<CompraDto>> response2) {
                             java.util.List<CompraDto> compras = new java.util.ArrayList<>();
                             if (response2.isSuccessful() && response2.body() != null) {
                                 compras = response2.body();
                             }
                             listaGastosCache = compras;
                             
                             final List<CompraDto> finalCompras = compras;
                             com.example.gemainventory.api.RetrofitClient.INSTANCE.getInstance().getBalances(userId).enqueue(new retrofit2.Callback<java.util.List<BalanceFinancieroDto>>() {
                                  @Override
                                  public void onResponse(retrofit2.Call<java.util.List<BalanceFinancieroDto>> call3, retrofit2.Response<java.util.List<BalanceFinancieroDto>> response3) {
                                      java.util.List<BalanceFinancieroDto> balances = new java.util.ArrayList<>();
                                      if (response3.isSuccessful() && response3.body() != null) {
                                          balances = response3.body();
                                      }
                                      listaBalancesCache = balances;
                                      procesarDatosFinancieros(pedidos, finalCompras, balances);
                                  }

                                  @Override
                                  public void onFailure(retrofit2.Call<java.util.List<BalanceFinancieroDto>> call3, Throwable t) {
                                      procesarDatosFinancieros(pedidos, finalCompras, new java.util.ArrayList<>());
                                  }
                             });
                         }

                         @Override
                         public void onFailure(retrofit2.Call<java.util.List<CompraDto>> call2, Throwable t) {
                             com.example.gemainventory.api.RetrofitClient.INSTANCE.getInstance().getBalances(userId).enqueue(new retrofit2.Callback<java.util.List<BalanceFinancieroDto>>() {
                                  @Override
                                  public void onResponse(retrofit2.Call<java.util.List<BalanceFinancieroDto>> call3, retrofit2.Response<java.util.List<BalanceFinancieroDto>> response3) {
                                       java.util.List<BalanceFinancieroDto> balances = new java.util.ArrayList<>();
                                       if (response3.isSuccessful() && response3.body() != null) {
                                           balances = response3.body();
                                       }
                                       procesarDatosFinancieros(pedidos, new java.util.ArrayList<>(), balances);
                                  }
                                  @Override public void onFailure(retrofit2.Call<java.util.List<BalanceFinancieroDto>> call3, Throwable t) {
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
            public void onFailure(retrofit2.Call<java.util.List<PedidoDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void procesarDatosFinancieros(List<PedidoDto> pedidos, 
                                          List<CompraDto> compras,
                                          List<BalanceFinancieroDto> balances) {
        float[] ingresosPorMes = new float[13];
        float[] gastosPorMes = new float[13];
        double totalIngresos = 0.0;
        double totalGastos = 0.0;

        for (PedidoDto pedido : pedidos) {
            // Include order if it has a total and date, regardless of status (or status == 2 for delivered)
            if (pedido.getTotal() != null) {
                 totalIngresos += pedido.getTotal();
                 int mes = obtenerMes(pedido.getFechaPedido());
                 if (mes >= 1 && mes <= 12) {
                     ingresosPorMes[mes] += pedido.getTotal().floatValue();
                 }
            }
        }
        
        for (CompraDto compra : compras) {
            if (compra.getTotal() != null) {
                totalGastos += compra.getTotal();
                int mes = obtenerMes(compra.getFechaCompra() != null ? compra.getFechaCompra().toString() : null);
                if (mes >= 1 && mes <= 12) {
                    gastosPorMes[mes] += compra.getTotal().floatValue();
                }
            }
        }

        if (balances != null) {
            for (BalanceFinancieroDto balance : balances) {
                if (balance.getMonto() != null) {
                    if (balance.getIdTipoBalance() != null && balance.getIdTipoBalance() == 1) {
                        totalIngresos += balance.getMonto();
                        int mes = obtenerMes(balance.getFecha());
                        if (mes >= 1 && mes <= 12) {
                            ingresosPorMes[mes] += balance.getMonto().floatValue();
                        }
                    } else if (balance.getIdTipoBalance() != null && balance.getIdTipoBalance() == 2) {
                        totalGastos += balance.getMonto();
                        int mes = obtenerMes(balance.getFecha());
                        if (mes >= 1 && mes <= 12) {
                            gastosPorMes[mes] += balance.getMonto().floatValue();
                        }
                    }
                }
            }
        }
        
        List<Float> incomesList = new ArrayList<>();
        List<Float> expensesList = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            incomesList.add(ingresosPorMes[i]);
            expensesList.add(gastosPorMes[i]);
        }

        if (composeHelper != null) {
            composeHelper.updateData(
                totalIngresos,
                totalGastos,
                totalIngresos - totalGastos,
                incomesList,
                expensesList
            );
        }
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
}