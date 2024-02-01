import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import dataClasses.Articulo;
import dataClasses.Pedido;


public class Manejo_db {
	
	private Connection con;
	private Statement s;
	
	public Manejo_db() {
		try {
			File dbPath = new File(".\\src\\db\\pedidosAdiDam.db");
			Class.forName("org.sqlite.JDBC");
			this.con = DriverManager.getConnection("jdbc:sqlite:"+dbPath.getAbsolutePath());
			
			this.s = con.createStatement();
		} catch (Exception e) {
			System.err.println("No se ha podido establecer conexión con la base de datos.");
		}
	}
	
	public void comprobarCliente(String num_Cliente) {
		try {
			ResultSet rs = s.executeQuery("SELECT * FROM clientes WHERE num_Cliente='"+num_Cliente+"';");
			
			if (!rs.next()) {
				//Esta vacío, no hay ningún cliente con ese número, vamos a generar uno nuevo
				
				String[] nombres = {"Andrés", "Carlos", "Adrián", "Niobe", "Victor", "Marco", "Sergio", "Santiago", "Javier", "Cristina"};
				String[] apellidos = {"Reyes", "Baños", "Alonso", "Tricas", "Torres", "Clavería", "Pueyo"};
				String[] empresas = {"Deloitte", "NTT Data", "Integra", "Movicoders"};
				String[] direcciones = {"Calle Alta", "Calle Baja", "Calle Media", "Calle Derecha", "Calle Izquierda"};
				
				String nombre = nombres[(int) (Math.random()*nombres.length)];
				String apellido = apellidos[(int) (Math.random()*apellidos.length)] + " " + apellidos[(int) (Math.random()*apellidos.length)];;
				String empresa = empresas[(int) (Math.random()*empresas.length)];
				String telefono= Integer.toString( (int) (Math.random()*100000000));
				String direccion = direcciones[(int) (Math.random()*direcciones.length)];
				String cliente = String.format("'%s', '%s', '%s', '%s', '%s', '%s'", num_Cliente, nombre, apellido, telefono, direccion, empresa);
				
				s.executeUpdate("INSERT INTO clientes VALUES("+cliente+");");
				
				System.out.println("Se ha registrado el nuevo cliente.");
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void insertNuevosPedidos(List<Pedido> pedidos) {
		for(Pedido p : pedidos) {
			insertNuevoPedido(p);
		}
	}
	
	public void insertNuevoPedido(Pedido pedido) {


		try {
			comprobarCliente(pedido.getCliente().getNumeroCliente());
			
			s.executeUpdate("INSERT INTO pedidos VALUES('"+pedido.getNumeroPedido()+"','"+pedido.getCliente().getNumeroCliente()+"','"+pedido.getFecha()+"');");
		} catch (SQLException e) {
			System.err.println("Se ha producido un error tratanto de guardar el pedido en la base de datos, el pedido número " + pedido.getNumeroPedido()+" ya existe.");
			System.err.println("1. Cancelar\n2. Sobreescribir el pedido guardado");
			int opcion = Main.sc.nextInt();
			switch (opcion) {
				case 1:
					return;
				case 2:
					borrarPedido(pedido);
					insertNuevoPedido(pedido);
					return;
				default:
					break;
			}
		}
		
		try {
			for(Articulo articulo : pedido.getArticulos()) {
				
				comprobarArticulo(articulo);
				
				s.executeUpdate("INSERT INTO rel_pedido_articulos VALUES('"+pedido.getNumeroPedido()+"','"+articulo.getCodigo()+"','"+articulo.getCantidad()+"');");
				
			}
		} catch (Exception e) {	
		}
	}
	
	public void comprobarArticulo(Articulo articulo) {
		try {
			ResultSet rs = s.executeQuery("SELECT * FROM articulos WHERE num_Articulo='"+articulo.getCodigo()+"';");
			
			if (!rs.next()) {
				//Esta vacío, no hay ningún articulo con ese número, vamos a generar uno nuevo
				
				String[] categoria = {"Deportes", "Informatica", "Ropa", "Comida", "Lujo", "Decoración", "Jardín"};
				
				s.executeUpdate("INSERT INTO clientes VALUES("+cliente+");");
				
				System.out.println("Se ha registrado el nuevo cliente.");
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void cerrarConexion() {
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void borrarPedido(Pedido pedido) {

		try {
			
			s.executeUpdate("DELETE FROM rel_pedido_articulos WHERE num_Pedido='"+pedido.getNumeroPedido()+"';");
			s.executeUpdate("DELETE FROM pedidos WHERE num_Pedido='"+pedido.getNumeroPedido()+"';");
			
			con.close();
			
		} catch (Exception e) {
			System.err.println("No se ha podido eliminar la información de la base de datos.");
			
		}
		
		
	}
	
	
	public void mostrarPedido(String numPedido) {
		try {

			ResultSet rs = s.executeQuery("SELECT * FROM pedidos;");
			
			while (rs.next()) {
				System.out.print(rs.getString(1));
				System.out.print("\t"+rs.getString(2));
				System.out.println("\t"+rs.getString(3));

			}
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}



