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
			cargaInicial();
		} catch (Exception e) {
			System.err.println("No se ha podido establecer conexión con la base de datos.");
		}
	}
	
	public void cargaInicial() {
		String[] clientes = {"1234567890", "9876543210","1234567891", "1234567892", "1234567893"}; 
		String[] articulos = {"123456","234567","345678","456789","567890","678901","789012","890123","123457","234568","901234","012345"};
		for (String c : clientes) {
			crearCliente(c);
		}
		for (String a : articulos) {
			crearArticulo(a);
		}
	}
	
	
	public void crearCliente(String num_Cliente) {
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
	
	public boolean comprobarCliente(String num_Cliente) {
		try {
			ResultSet rs = s.executeQuery("SELECT * FROM clientes WHERE num_Cliente='"+num_Cliente+"';");
			if(rs.next()) {
				//Si hay un cliente con ese id devuelve true
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
		
	}
	
	
	public void insertNuevoPedido(Pedido pedido) {


		try {
			System.out.println("Procesando el pedido: "+pedido.getNumeroPedido());
			if(comprobarCliente(pedido.getCliente().getNumeroCliente())) {
				
			} else {
				System.err.println("El cliente "+pedido.getCliente().getNumeroCliente()+" no está registrado.");
				System.err.println("1. Cancelar pedido\n2. Registrar nuevo cliente.");
				switch(Main.sc.nextInt()) {
				case 1:
					System.out.println("Se ha cancelado el procesamiento de este pedido.");
					return;
				case 2:
					crearArticulo(pedido.getCliente().getNumeroCliente());
					break;
				default:
					System.out.println("Opción no reconocida. Se ha cancelado el procesamiento de este pedido.");
					return;
					
				}
			}

			for(Articulo articulo: pedido.getArticulos()) {
				if(!comprobarArticulo(articulo.getCodigo())) {
					System.err.println("El articulo " + articulo.getCodigo()+" no existe en el almacen.");
					System.err.println("1. Cancelar pedido\n2. Registrar nuevo artículo.");
					switch(Main.sc.nextInt()) {
					case 1:
						System.out.println("Se ha cancelado el procesamiento de este pedido.");
						return;
					case 2:
						crearArticulo(articulo.getCodigo());
						break;
					default:
						System.out.println("Opción no reconocida. Se ha cancelado el procesamiento.");
						return;
					}
				} 
				
				comprobarStockArticulo(articulo);
			}
			s.executeUpdate("INSERT INTO pedidos VALUES('"+pedido.getNumeroPedido()+"','"+pedido.getCliente().getNumeroCliente()+"','"+pedido.getFecha()+"');");
			
		} catch (SQLException e) {
			System.err.println("Se ha producido un error tratanto de guardar el pedido en la base de datos, el pedido número " + pedido.getNumeroPedido()+" ya existe.");
			System.err.println("1. Descartar el nuevo pedido\n2. Sobreescribir el antiguo pedido");
			int opcion = Main.sc.nextInt();
			switch (opcion) {
				case 1:
					borrarPedido(pedido);
					return;
				case 2:
					borrarPedido(pedido);
					insertNuevoPedido(pedido);
					return;
				default:
					break;
			}
		} catch (Exception e) {
			borrarPedido(pedido);
			return;
		}
		
		try {
			for(Articulo articulo : pedido.getArticulos()) {
				s.executeUpdate("INSERT INTO rel_pedido_articulos VALUES('"+pedido.getNumeroPedido()+"','"+articulo.getCodigo()+"','"+articulo.getCantidad()+"');");
			}
		} catch (Exception e) {	
		}
		System.out.println("Se ha guardado el pedido "+pedido.getNumeroPedido()+" con éxito.");
	}

	public boolean comprobarArticulo(String num_Articulo) {
		try {
			ResultSet rs = s.executeQuery("SELECT * FROM articulos WHERE num_Articulo='"+num_Articulo+"';");
			if(rs.next()) {
				//Si hay un articulo con ese id devuelve true
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;

	}
	
	
	public void comprobarStockArticulo(Articulo articulo) throws Exception {
		try {
			ResultSet rs = s.executeQuery("SELECT stock FROM articulos WHERE num_Articulo='"+articulo.getCodigo()+"';");
			rs.next(); //movemos al primer resultado
			
			if(Integer.parseInt(rs.getString(1)) < Integer.parseInt(articulo.getCantidad())){
				System.err.println("No hay sufiente stock para atender el pedido con el articulo " + articulo.getCodigo());
				System.err.println("Se ha cancelado este pedido.");
				throw new Exception();
			}
		} catch (Exception e) {
			throw new Exception();
		}
		
	}
	
	
	public void crearArticulo(String articulo) {
		try {
			ResultSet rs = s.executeQuery("SELECT * FROM articulos WHERE num_Articulo='"+articulo+"';");
			
			if (!rs.next()) {
				//Esta vacío, no hay ningún articulo con ese número, vamos a generar uno nuevo
				
				String[] categorias = {"Deportes", "Informatica", "Ropa", "Comida", "Lujo", "Decoración", "Jardín"};
				String[] proveedores = {"Adidas", "Nike", "Sportiva", "Hoka", "New Balance"};
				String[] descripciones = {"Cepillo", "Pantalón", "Camisa", "Mesa", "Teclado", "Pantalla","Piano", "Coche"};
				
				String stock = Integer.toString((int)(Math.random()*90)+40);
				String categoria = categorias[(int) (Math.random()*categorias.length)];
				String proveedor = proveedores[(int) (Math.random()*proveedores.length)];
				String descripcion = descripciones[(int) (Math.random()*descripciones.length)];
				
				String input = String.format("'%s','%s','%s','%s','%s'", articulo, descripcion,categoria, stock, proveedor);
				
				s.executeUpdate("INSERT INTO articulos VALUES("+input+");");
				
				System.out.println("Se ha registrado el nuevo articulo.");
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
	
	public void mostrarPedidos() {
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



