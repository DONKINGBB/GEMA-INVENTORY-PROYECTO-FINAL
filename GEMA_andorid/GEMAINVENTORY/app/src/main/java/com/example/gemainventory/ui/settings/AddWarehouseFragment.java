package com.example.gemainventory.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.gemainventory.R;
import com.example.gemainventory.api.RetrofitClient;
import com.example.gemainventory.model.AlmacenDto;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.location.Geocoder;
import android.location.Address;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddWarehouseFragment extends Fragment implements OnMapReadyCallback {

    private TextInputEditText etName, etAddress;
    private Button btnSave, btnSelectLocation;
    private View mapCard;
    private GoogleMap googleMap;
    private LatLng selectedLatLng;
    private int warehouseId = 0; // 0 = New

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_warehouse, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etName = view.findViewById(R.id.et_warehouse_name);
        etAddress = view.findViewById(R.id.et_warehouse_address);
        btnSave = view.findViewById(R.id.btn_save_warehouse);
        btnSelectLocation = view.findViewById(R.id.btn_select_location);
        mapCard = view.findViewById(R.id.card_map_preview);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_lite);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        btnSelectLocation.setOnClickListener(v -> {
            if (mapCard.getVisibility() == View.GONE) {
                mapCard.setVisibility(View.VISIBLE);
                btnSelectLocation.setText("Confirmar Ubicación");
            } else {
                mapCard.setVisibility(View.GONE);
                btnSelectLocation.setText("Cambiar Ubicación");
            }
        });

        view.findViewById(R.id.btn_back).setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        // EDIT MODE
        if (getArguments() != null && getArguments().containsKey("warehouse_id")) {
             warehouseId = getArguments().getInt("warehouse_id");
             etName.setText(getArguments().getString("warehouse_name"));
             etAddress.setText(getArguments().getString("warehouse_address"));
             
             if (getArguments().containsKey("latitud") && getArguments().containsKey("longitud")) {
                 selectedLatLng = new LatLng(
                         getArguments().getDouble("latitud"),
                         getArguments().getDouble("longitud")
                 );
                 mapCard.setVisibility(View.VISIBLE);
             }
             
             ((android.widget.TextView)view.findViewById(R.id.tv_header_title)).setText("Editar Almacén"); 
             btnSave.setText("Actualizar Almacén");
        }

        btnSave.setOnClickListener(v -> guardarAlmacen());
    }

    private void reverseGeocode(LatLng latLng) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String addressText = address.getAddressLine(0);
                etAddress.setText(addressText);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        
        // Disable scroll in lite mode if we want it purely as a picker container, 
        // but since we want them to MOVE it, we enable it if it's visible.
        
        if (selectedLatLng != null) {
            updateMapMarker(selectedLatLng);
        } else {
            // Default position (CDMX or similar)
            LatLng defaultPos = new LatLng(19.4326, -99.1332);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultPos, 10f));
        }

        googleMap.setOnMapClickListener(latLng -> {
            selectedLatLng = latLng;
            updateMapMarker(latLng);
            reverseGeocode(latLng);
        });
    }

    private void updateMapMarker(LatLng latLng) {
        if (googleMap == null) return;
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
    }

    private void guardarAlmacen() {
        String nombre = etName.getText().toString().trim();
        String direccion = etAddress.getText().toString().trim();

        if (nombre.isEmpty()) {
            etName.setError("Requerido");
            return;
        }

        SharedPreferences prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        if (userId == null) {
            Toast.makeText(getContext(), "Error de sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSave.setEnabled(false);
        btnSave.setText("Guardando...");
        
        AlmacenDto dto = new AlmacenDto(warehouseId, nombre, direccion, null, null);
        
        if (selectedLatLng != null) {
            dto.setLatitud(selectedLatLng.latitude);
            dto.setLongitud(selectedLatLng.longitude);
        }

        if (warehouseId == 0) {
            // CREATE
            Call<AlmacenDto> call = RetrofitClient.INSTANCE.getInstance().crearAlmacen(dto, userId);
            call.enqueue(new Callback<AlmacenDto>() {
                @Override
                public void onResponse(Call<AlmacenDto> call, Response<AlmacenDto> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Almacén guardado", Toast.LENGTH_SHORT).show();
                        NavHostFragment.findNavController(AddWarehouseFragment.this).popBackStack();
                    } else {
                        btnSave.setEnabled(true);
                        btnSave.setText("Guardar Almacén");
                        Toast.makeText(getContext(), "Error del servidor", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<AlmacenDto> call, Throwable t) {
                    btnSave.setEnabled(true);
                    btnSave.setText("Guardar Almacén");
                    Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // UPDATE
            Call<AlmacenDto> call = RetrofitClient.INSTANCE.getInstance().actualizarAlmacen(warehouseId, dto);
            call.enqueue(new Callback<AlmacenDto>() {
                @Override
                public void onResponse(Call<AlmacenDto> call, Response<AlmacenDto> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Almacén actualizado", Toast.LENGTH_SHORT).show();
                        NavHostFragment.findNavController(AddWarehouseFragment.this).popBackStack();
                    } else {
                        btnSave.setEnabled(true);
                        btnSave.setText("Actualizar Almacén");
                        Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<AlmacenDto> call, Throwable t) {
                    btnSave.setEnabled(true);
                    btnSave.setText("Actualizar Almacén");
                    Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}