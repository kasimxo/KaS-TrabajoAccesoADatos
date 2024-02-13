package dataClasses;

public class Articulo {
	
	private String codigo;
	private String stock;
	private String categoria;
	private String proveedor;
	private String descripcion;
	private String precio;
	
	public Articulo() {};
	
	public Articulo(String codigo, String cantidad) {
		super();
		this.codigo = codigo;
		this.stock = cantidad;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getCantidad() {
		return stock;
	}
	public void setCantidad(String cantidad) {
		this.stock = cantidad;
	}
	@Override
	public String toString() {
		return "\tArticulo:\n\t\tCodigo=" + codigo + "\n\t\tCantidad: " + stock+"\n";
	}

	public String getProveedor() {
		return proveedor;
	}

	public void setProveedor(String proveedor) {
		this.proveedor = proveedor;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getPrecio() {
		return precio;
	}

	public void setPrecio(String precio) {
		this.precio = precio;
	}
	
}
