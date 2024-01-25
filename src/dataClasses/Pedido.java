package dataClasses;

import java.util.List;

public class Pedido {
	
	private String numeroPedido;
	private Cliente cliente;
	private List<Articulo> articulos;
	private String fecha;
	
	
	public Pedido() {
		
	}
	
	public Pedido(String numeroPedido, Cliente cliente, List<Articulo> articulos, String fecha) {
		this.numeroPedido = numeroPedido; 
		this.cliente = cliente;
		this.articulos = articulos;
		this.fecha = fecha;
	}
	
	

	public String getNumeroPedido() {
		return numeroPedido;
	}



	public void setNumeroPedido(String numeroPedido) {
		this.numeroPedido = numeroPedido;
	}



	public Cliente getCliente() {
		return cliente;
	}



	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}



	public List<Articulo> getArticulos() {
		return articulos;
	}



	public void setArticulos(List<Articulo> articulos) {
		this.articulos = articulos;
	}



	public String getFecha() {
		return fecha;
	}



	public void setFecha(String fecha) {
		this.fecha = fecha;
	}



	@Override
	public String toString() {
		String s = String.format("Pedido número: %s\n%s\nFecha: %s\nArtículos: %s", numeroPedido, cliente, fecha, articulos);
		return s;
	}
	
	

}
