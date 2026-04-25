package com.example.gemainventory.api

import com.example.gemainventory.model.AlmacenDto
import com.example.gemainventory.model.ProveedorDto
import com.example.gemainventory.model.CategoriaDto
import com.example.gemainventory.model.ClienteDto
import com.example.gemainventory.model.DashboardSummary
import com.example.gemainventory.model.GenericApiResponse
import com.example.gemainventory.model.GoogleLoginDto
import com.example.gemainventory.model.InventarioDto
import com.example.gemainventory.model.LoginResponse
import com.example.gemainventory.model.PedidoDto
import com.example.gemainventory.model.ProductoCreacionDto
import com.example.gemainventory.model.ProductoDto
import com.example.gemainventory.model.ProductoSeleccionDto
import com.example.gemainventory.model.Usuario
import com.example.gemainventory.model.UsuarioUpdateDto
import com.example.gemainventory.model.PasswordUpdateDto
import com.example.gemainventory.model.NegocioDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Multipart
import retrofit2.http.Part
import okhttp3.MultipartBody
import com.example.gemainventory.model.UploadResponse


interface ApiService {

    @FormUrlEncoded
    @POST("api/auth/login")
    fun loginUsuario(
        @Field("correo") correo: String,
        @Field("contrasena") contrasena: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("api/auth/register")
    fun registrarUsuario(
        @Field("nombre") nombre: String,
        @Field("correo") correo: String,
        @Field("contrasena") contrasena: String,
        @Field("direccion") direccion: String,
        @Field("telefono") telefono: String
    ): Call<LoginResponse>
 
    @POST("api/auth/forgot-password")
    fun forgotPassword(@Query("email") email: String): Call<Map<String, String>>
 
    @POST("api/auth/reset-password")
    fun resetPassword(
        @Query("email") email: String,
        @Query("code") code: String,
        @Query("newPassword") newPassword: String
    ): Call<Map<String, String>>

    @GET("api/dashboard/summary")
    fun getDashboardSummary(@Query("userId") userId: String): Call<DashboardSummary>

    @POST("api/productos")
    fun crearProducto(@Body producto: ProductoDto): Call<Void>

    @GET("api/inventario")
    fun getInventario(@Query("userId") userId: String): Call<List<InventarioDto>>

    @GET("api/productos/{id}")
    fun getProducto(@Path("id") id: String): Call<ProductoDto>

    @PUT("api/productos/{id}")
    fun actualizarProducto(
        @Path("id") id: String,
        @Body producto: ProductoDto
    ): Call<Void>

    @DELETE("api/productos/{id}")
    fun eliminarProducto(@Path("id") id: String): Call<Void>

    @PUT("api/usuarios/{id}/perfil")
    fun actualizarPerfil(
        @Path("id") id: String,
        @Body datos: UsuarioUpdateDto
    ): Call<Usuario>

    @POST("api/auth/google")
    fun loginGoogle(@Body datos: GoogleLoginDto): Call<LoginResponse>

    @GET("api/catalogos/almacenes")
    fun getAlmacenes(@Query("userId") userId: String): Call<List<AlmacenDto>>

    @POST("api/catalogos/almacenes")
    fun crearAlmacen(@Body almacen: AlmacenDto, @Query("userId") userId: String): Call<AlmacenDto>

    @PUT("api/catalogos/almacenes/{id}")
    fun actualizarAlmacen(@Path("id") id: Int, @Body almacen: AlmacenDto): Call<AlmacenDto>

    @DELETE("api/catalogos/almacenes/{id}")
    fun eliminarAlmacen(@Path("id") id: Int): Call<Void>

    @GET("api/clientes")
    fun getClientes(@Query("userId") userId: String): Call<List<ClienteDto>>

    @POST("api/clientes")
    fun crearCliente(@Body cliente: ClienteDto, @Query("userId") userId: String): Call<ClienteDto>

    @PUT("api/clientes/{id}")
    fun actualizarCliente(@Path("id") id: String, @Body cliente: ClienteDto): Call<ClienteDto>

    @DELETE("api/clientes/{id}")
    fun eliminarCliente(@Path("id") id: String): Call<Void>

    @GET("api/catalogos/categorias")
    fun getCategorias(@Query("userId") userId: String): Call<List<CategoriaDto>>

    @POST("api/catalogos/categorias")
    fun crearCategoria(
        @Body categoria: CategoriaDto,
        @Query("userId") userId: String
    ): Call<CategoriaDto>

    @PUT("api/catalogos/categorias/{id}")
    fun actualizarCategoria(
        @Path("id") id: Int,
        @Body categoria: CategoriaDto
    ): Call<CategoriaDto>

    @DELETE("api/catalogos/categorias/{id}")
    fun eliminarCategoria(@Path("id") id: Int): Call<Void>

    @GET("api/inventario")
    fun getProductos(@Query("userId") userId: String): Call<List<ProductoDto>>

    @POST("api/pedidos")
    fun crearPedido(
        @Body pedido: PedidoDto,
        @Query("userId") userId: String
    ): Call<Void>

    @GET("api/pedidos")
    fun getPedidos(@Query("userId") userId: String): Call<List<PedidoDto>>

    @GET("productos/usuario/{userId}")
    fun getProductosParaPedido(@Path("userId") userId: String): Call<List<ProductoSeleccionDto>>

    // 2. Para CREAR un producto (usamos CreacionDto)
    @POST("productos")
    fun crearProducto(@Body productoDto: ProductoCreacionDto): Call<Void>

    @GET("api/productos/usuario/{userId}")
    fun getProductosParaSeleccion(@Path("userId") userId: String): Call<List<ProductoSeleccionDto>>

    @GET("api/inventario/seleccion-por-almacen")
    fun getProductosPorAlmacen(
        @Query("userId") userId: String,
        @Query("almacenId") almacenId: Int
    ): Call<List<ProductoSeleccionDto>>

    @PUT("api/pedidos/{id}/entregado")
    fun marcarPedidoEntregado(@Path("id") id: String): Call<Void>

    @DELETE("api/pedidos/{id}")
    fun eliminarPedido(@Path("id") id: String, @Query("userId") userId: String): Call<Void>

    @GET("api/compra")
    fun getCompras(@Query("userId") userId: String): Call<List<com.example.gemainventory.model.CompraDto>>

    // --- GESTIÓN DE USUARIOS / EQUIPO ---
    @GET("api/usuarios/usuario")
    fun obtenerUsuarios(@Query("user") user: String): Call<List<Usuario>>

    @GET("api/usuarios/usuario/{id}")
    fun getUsuarioById(@Path("id") id: String): Call<Usuario>

    @POST("api/usuarios/usuario")
    fun guardarUsuario(@Body usuario: Usuario): Call<Usuario>

    @PUT("api/usuarios/usuario/{id}")
    fun actualizarUsuario(@Path("id") id: String, @Body usuario: Usuario): Call<Usuario>

    @POST("api/usuarios/{id}/fcm-token")
    fun registerFcmToken(@Path("id") id: String, @Body token: Map<String, String>): Call<Void>

    @PUT("api/usuarios/{id}/notificaciones")
    fun updateNotifications(@Path("id") id: String, @Body settings: Usuario): Call<Usuario>

    @GET("api/usuarios/{id}/preferencias")
    fun getUserPreferences(@Path("id") id: String): Call<Usuario>
 
    @PUT("api/usuarios/{id}/password")
    fun changePassword(@Path("id") id: String, @Body dto: PasswordUpdateDto): Call<Map<String, String>>
 
    @DELETE("api/usuarios/{id}")
    fun eliminarCuentaPropia(@Path("id") id: String): Call<Void>

    @DELETE("api/usuarios/{id}")
    fun eliminarUsuario(@Path("id") id: String): Call<Void>

    // --- SISTEMA DE NEGOCIOS ---
    @GET("api/negocios/mi-negocio")
    fun getMiNegocio(): Call<NegocioDto>

    @POST("api/negocios/create")
    fun createNegocio(@Body body: Map<String, String>): Call<Map<String, Any>>

    @POST("api/negocios/join")
    fun joinNegocio(@Body body: Map<String, String>): Call<Map<String, Any>>

    // --- MULTI-NEGOCIO ---
    @GET("api/negocios/mis-negocios")
    fun getMisNegocios(): Call<List<Map<String, String>>>

    @POST("api/negocios/switch/{id}")
    fun switchNegocio(@Path("id") id: String): Call<Map<String, Any>>

    @PUT("api/negocios/{id}")
    fun updateNegocio(@Path("id") id: String, @Body body: Map<String, String>): Call<NegocioDto>

    @Multipart
    @POST("api/upload")
    fun uploadImage(@Part file: MultipartBody.Part): Call<UploadResponse>

    @GET("api/balances")
    fun getBalances(@Query("userId") userId: String): Call<List<com.example.gemainventory.model.BalanceFinancieroDto>>

    // --- PROVEEDORES ---
    @GET("api/proveedor")
    fun getProveedores(@Query("userId") userId: String): Call<List<ProveedorDto>>

    @POST("api/proveedor")
    fun crearProveedor(@Body proveedor: ProveedorDto, @Query("userId") userId: String): Call<ProveedorDto>

    @PUT("api/proveedor/{id}")
    fun actualizarProveedor(@Path("id") id: String, @Body proveedor: ProveedorDto): Call<ProveedorDto>

    @DELETE("api/proveedor/{id}")
    fun eliminarProveedor(@Path("id") id: String): Call<Void>
}