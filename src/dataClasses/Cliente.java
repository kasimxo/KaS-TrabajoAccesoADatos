package dataClasses;

public class Cliente {
	
	private String numeroCliente;
	private String nombre;
	private String apellidos;
	
	public Cliente() {
		
	}

	public Cliente(String numeroCliente, String nombre, String apellidos) {
		this.numeroCliente = numeroCliente;
		this.nombre = nombre;
		this.apellidos = apellidos;
	}

	public String getNumeroCliente() {
		return numeroCliente;
	}

	public void setNumeroCliente(String numeroCliente) {
		this.numeroCliente = numeroCliente;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	@Override
	public String toString() {
		return "Cliente: " + numeroCliente + " " + nombre + " " + apellidos;
	}

	
	
}
