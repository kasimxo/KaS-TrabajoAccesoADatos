package dataClasses;

public class Articulo {
	
	private String codigo;
	private String cantidad;
	
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
	
}
