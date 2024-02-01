package dataClasses;

public class Articulo {
	
	private String codigo;
	private String cantidad;
	private String categoria;
	private String proveedor;
	private String descripcion;
	
	public Articulo() {};
	
	public Articulo(String codigo, String cantidad) {
		super();
		this.codigo = codigo;
		this.cantidad = cantidad;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getCantidad() {
		return cantidad;
	}
	public void setCantidad(String cantidad) {
		this.cantidad = cantidad;
	}
	@Override
	public String toString() {
		return "\tArticulo:\n\t\tCodigo=" + codigo + "\n\t\tCantidad: " + cantidad+"\n";
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
	
}
