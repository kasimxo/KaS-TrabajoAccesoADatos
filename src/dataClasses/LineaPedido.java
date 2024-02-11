package dataClasses;

public class LineaPedido {

	private String cantidad;
	private String num_Pedido;
	private String num_Articulo;
	private String descripcion_Categoria;
	private String precio_ud;
	private String precio_tot;
	
	public LineaPedido(String cantidad, String num_Pedido, String num_Articulo, String descripcion_categoria, String precio_ud) {
		this.cantidad = cantidad;
		this.num_Pedido = num_Pedido;
		this.num_Articulo = num_Articulo;
		this.descripcion_Categoria = descripcion_categoria;
		this.precio_ud = precio_ud;
		this.precio_tot = Integer.toString(Integer.parseInt(cantidad)*Integer.parseInt(precio_ud));
	}

	public String getCantidad() {
		return cantidad;
	}

	public void setCantidad(String cantidad) {
		this.cantidad = cantidad;
	}

	public String getNum_Pedido() {
		return num_Pedido;
	}

	public void setNum_Pedido(String num_Pedido) {
		this.num_Pedido = num_Pedido;
	}

	public String getNum_Articulo() {
		return num_Articulo;
	}

	public void setNum_Articulo(String num_Articulo) {
		this.num_Articulo = num_Articulo;
	}

	public String getDescripcion_Categoria() {
		return descripcion_Categoria;
	}

	public void setDescripcion_Categoria(String descripcion_Categoria) {
		this.descripcion_Categoria = descripcion_Categoria;
	}

	public String getPrecio_ud() {
		return precio_ud;
	}

	public void setPrecio_ud(String precio_ud) {
		this.precio_ud = precio_ud;
	}

	public String getPrecio_tot() {
		return precio_tot;
	}

	public void setPrecio_tot(String precio_tot) {
		this.precio_tot = precio_tot;
	}
	
}
