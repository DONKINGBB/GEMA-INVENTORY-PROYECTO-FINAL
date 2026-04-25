import com.google.gson.annotations.SerializedName

data class Producto(
    @SerializedName("id_producto")
    val idProducto: String,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("descripcion")
    val descripcion: String,

    @SerializedName("precio_venta")
    val precioVenta: Double
)