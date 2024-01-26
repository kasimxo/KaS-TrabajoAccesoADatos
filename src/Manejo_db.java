import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import dataClasses.Pedido;


public class Manejo_db {
	
	private boolean SUCCESS = true;
	private boolean FAIL = false;
	
	public boolean insert(String table, List<String> valores) {
		
		try {
			File dbPath = new File(".\\src\\db\\pedidosAdiDam.db");
			Class.forName("org.sqlite.JDBC");
			Connection con = DriverManager.getConnection("jdbc:sqlite:"+dbPath.getAbsolutePath());
			
			java.sql.Statement s = con.createStatement();

			
			/*
			 * Tabla articulos
			 * Una tabla va a tener el número de pedido, el número de articulo y la cantidad
			 * 
			 * 
			 * Tabla pedidos
			 * Otra tabla tendrá el número de pedido, la fecha y el número de cliente
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * */
			 
			
			//s.executeUpdate("INSERT INTO tabla1(campo1, campo2) VALUES('"+c1+"','"+c2+"')");
			
			//Queda pendiente escribir la consulta para insertar cosas
			int rs = s.executeUpdate("INSERT INTO ");
			
			
			
			con.close();
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return FAIL;
		}
	}
	
	public void insertNuevosPedidos(List<Pedido> pedidos) {
		for(Pedido p : pedidos) {
			insertNuevoPedido(p);
		}
	}
	
	public void insertNuevoPedido(Pedido pedido) {
		try {
			File dbPath = new File(".\\src\\db\\pedidosAdiDam.db");
			Class.forName("org.sqlite.JDBC");
			Connection con = DriverManager.getConnection("jdbc:sqlite:"+dbPath.getAbsolutePath());
			
			java.sql.Statement s = con.createStatement();

			
			//s.executeUpdate("INSERT INTO tabla1(campo1, campo2) VALUES('"+c1+"','"+c2+"')");
			
			//Queda pendiente escribir la consulta para insertar cosas
			int rs = s.executeUpdate("INSERT INTO ");
			
			
			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void mostrarPedido(String numPedido) {
		try {
			File dbPath = new File(".\\src\\db\\pedidosAdiDam.db");
			Class.forName("org.sqlite.JDBC");
			Connection con = DriverManager.getConnection("jdbc:sqlite:"+dbPath.getAbsolutePath());
			
			java.sql.Statement s = con.createStatement();

			
			//s.executeUpdate("INSERT INTO tabla1(campo1, campo2) VALUES('"+c1+"','"+c2+"')");
			
			//Queda pendiente escribir la consulta para insertar cosas
			//ResultSet rs = s.executeQuery("SELECT * FROM pedidos where num_Pedido like '"+numPedido+"';");
			
			ResultSet rs = s.executeQuery("SELECT * FROM pedidos;");
			
			while (rs.next()) {
				System.out.println(rs);
			}
			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
