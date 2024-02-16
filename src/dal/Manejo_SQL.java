package dal;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import dataClasses.Articulo;
import dataClasses.LineaPedido;
import dataClasses.Pedido;
import dataClasses.Cliente;
import main.Main;
import utils.Input;


public class Manejo_SQL {
	
	private File dbPath;
	
	private Connection con;
	private Statement s;
	
	public Manejo_SQL() {
		try {
			
			//WINDOWS:
			//dbPath = new File(".\\files\\db\\pedidosAdiDam.db");
			
			//LINUX:
			dbPath = new File("./files/db/pedidosAdiDam.db");
			
			if(!dbPath.exists()) {
				System.err.println("No se ha encontrado la base de datos SQL");
				throw new Exception();
			}
			
			Class.forName("org.sqlite.JDBC");
			this.con = DriverManager.getConnection("jdbc:sqlite:"+dbPath.getAbsolutePath());
			
			this.s = con.createStatement();
			
			 //db.execSQL("PRAGMA foreign_keys=ON");
			//s.executeQuery("PRAGMA foreign_keys=ON");
			s.execute("PRAGMA foreign_keys=ON");
			
			//Hacemos la carga de los productos y clientes del primer archivo, simulando que ya estuvieran guardados en la bbdd
			cargaInicial();
			
			cerrarConexion();
		} catch (Exception e) {
			System.err.println("No se ha podido establecer conexión con la base de datos SQL.");
			cerrarConexion();
			//e.printStackTrace();
		}
	}
	
	public void establecerConexion() {
		try {
			Class.forName("org.sqlite.JDBC");
			this.con = DriverManager.getConnection("jdbc:sqlite:"+dbPath.getAbsolutePath());
			
			this.s = con.createStatement();
			
			s.execute("PRAGMA foreign_keys=ON");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
	}
	
	/**
	 * Este método se usa únicamente para crear los datos del primer archivo y simular que ya estuvieran los clientes y los artículos creados
	 */
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
			//e.printStackTrace();
		}
	}
	
	public void insertNuevosPedidos(List<Pedido> pedidos) {
		for(Pedido p : pedidos) {
			int resultado = insertNuevoPedido(p);
			
			switch (resultado) {
			case 0:
				break;
			case -1:
				sobreescribirPedido(p);
				break;
			case -2:
				break;
			case -3:
				borrarPedido(p);
				break;
			default:
				break;
			}
			
		}
	}
	
	public void sobreescribirPedido(Pedido pedido) {
		try {
			
			establecerConexion();
			
			s.executeUpdate("INSERT OR REPLACE INTO pedidos VALUES('"+pedido.getNumeroPedido()+"','"+pedido.getCliente().getNumeroCliente()+"','"+pedido.getFecha()+"');");
			
			cerrarConexion();
			
			
		} catch (SQLException e) {
			System.err.println("No se ha podido sobreescribir el pedido");
			//e.printStackTrace();
			return;
		}
		try {
			for(Articulo articulo : pedido.getArticulos()) {
				establecerConexion();
				s.executeUpdate("INSERT INTO rel_pedido_articulos VALUES('"+pedido.getNumeroPedido()+"','"+articulo.getCodigo()+"','"+articulo.getCantidad()+"');");
				cerrarConexion();
			}
		} catch (Exception e) {	
			cerrarConexion();
		}
		cerrarConexion();
		System.out.println("Se ha reemplazado el pedido "+pedido.getNumeroPedido()+" con éxito.");

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
	
	/**
	 * 
	 * @param pedido
	 * @return 
	 * 	0 - Se ha insertado correctamente
	 * -1 - El usuario quiere sobreescribir un pedido existente 
	 * -2 - No ha sido posible insertar el pedido
	 * -3 - Es necesario eliminar el nuevo pedido por falta de stock
	 */
	public int insertNuevoPedido(Pedido pedido) {


		try {
			establecerConexion();
			
			System.out.println("Procesando el pedido: "+pedido.getNumeroPedido());
			if(comprobarCliente(pedido.getCliente().getNumeroCliente())) {
				
			} else {
				System.err.println("El cliente "+pedido.getCliente().getNumeroCliente()+" no está registrado.");
				System.out.println("1. Cancelar pedido\n2. Registrar nuevo cliente.");
				switch(Input.leerInt()) {
				case 1:
					System.out.println("Se ha cancelado el procesamiento de este pedido.");
					cerrarConexion();
					return -2;
				case 2:
					crearArticulo(pedido.getCliente().getNumeroCliente());
					break;
				default:
					System.out.println("Opción no reconocida. Se ha cancelado el procesamiento de este pedido.");
					cerrarConexion();
					return -2;
					
				}
			}

			for(Articulo articulo: pedido.getArticulos()) {
				if(!comprobarArticulo(articulo.getCodigo())) {
					System.err.println("El articulo " + articulo.getCodigo()+" no existe en el almacen.");
					System.out.println("1. Cancelar pedido\n2. Registrar nuevo artículo.");
					switch(Input.leerInt()) {
					case 1:
						System.out.println("Se ha cancelado el procesamiento del pedido "+pedido.getNumeroPedido());
						cerrarConexion();
						return -2;
					case 2:
						crearArticulo(articulo.getCodigo());
						break;
					default:
						System.out.println("Opción no reconocida. Se ha cancelado el procesamiento del pedido "+pedido.getNumeroPedido());
						cerrarConexion();
						return -2;
					}
				} 
				
				comprobarStockArticulo(articulo);
			}
			s.executeUpdate("INSERT INTO pedidos VALUES('"+pedido.getNumeroPedido()+"','"+pedido.getCliente().getNumeroCliente()+"','"+pedido.getFecha()+"');");
			cerrarConexion();
		} catch (SQLException e) {
			System.out.println("Se ha producido un error tratanto de guardar el pedido en la base de datos, el pedido número " + pedido.getNumeroPedido()+" ya existe.");
			System.out.println("1. Descartar el nuevo pedido\n2. Sobreescribir el antiguo pedido");
			//e.printStackTrace();
			switch (Input.leerInt()) {
				case 1:
					//borrarPedido(pedido);
					System.out.println("Se ha descartado el pedido "+pedido.getNumeroPedido());
					cerrarConexion();
					return 0;
				case 2:
					borrarPedido(pedido);
					insertNuevoPedido(pedido);
					cerrarConexion();
					return -1;
				default:
					break;
			}
		} catch (Exception e) {
		
			cerrarConexion();
			return -3;
		}
		
		try {
			for(Articulo articulo : pedido.getArticulos()) {
				establecerConexion();
				s.executeUpdate("INSERT INTO rel_pedido_articulos VALUES('"+pedido.getNumeroPedido()+"','"+articulo.getCodigo()+"','"+articulo.getCantidad()+"');");
				cerrarConexion();
			}
		} catch (Exception e) {	
		}
		System.out.println("Se ha guardado el pedido "+pedido.getNumeroPedido()+" con éxito.");
		cerrarConexion();

		return 0;
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
				String precio = Integer.toString((int)(Math.random()*40)+10);
				
				String input = String.format("'%s','%s','%s','%s','%s', '%s'", articulo, descripcion,categoria, stock, proveedor, precio);
				
				s.executeUpdate("INSERT INTO articulos VALUES("+input+");");
				
				System.out.println("Se ha registrado el nuevo articulo.");
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
	
	public void cerrarConexion() {

		try {
			if(con.isClosed()) {
				return;
			}
			s.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("No se ha podido cerrar la conexión de la base de datos.");
			//e.printStackTrace();
		}
	}
	
	
	public void borrarPedido(Pedido pedido) {

		try {
			establecerConexion();
			s.executeUpdate("DELETE FROM pedidos WHERE num_Pedido='"+pedido.getNumeroPedido()+"';");
			s.executeUpdate("DELETE FROM rel_pedido_articulos WHERE num_Pedido='"+pedido.getNumeroPedido()+"';");
			cerrarConexion();
		} catch (Exception e) {
			System.err.println("No se ha podido eliminar la información de la base de datos.");
			//e.printStackTrace();
			cerrarConexion();
		}
		

	}
	
	
	public void mostrarPedido(String numPedido) {
		try {
			establecerConexion();

			ResultSet rs = s.executeQuery("SELECT * FROM mostrar_pedidos WHERE 'Número de pedido'='"+numPedido+"';");
			
			ResultSetMetaData rsmd = rs.getMetaData();
			
			int nCol = rsmd.getColumnCount();
			
			for(int i = 1; i<= nCol; i++) {
				System.out.printf("%-25s",rsmd.getColumnLabel(i));
			}
			
			System.out.println();
			
			while (rs.next()) {
				System.out.printf("%-25s%-25s%-25s%-25s%-25s%-25s\n", rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6));
				
			}
			cerrarConexion();
		} catch (Exception e) {
			cerrarConexion();
			//e.printStackTrace();
		}
	}
	
	public void mostrarPedidos() {
		try {
			establecerConexion();

			ResultSet rs = s.executeQuery("SELECT * FROM mostrar_pedidos;");
			
			ResultSetMetaData rsmd = rs.getMetaData();
			
			int nCol = rsmd.getColumnCount();
			
			for(int i = 1; i<= nCol; i++) {
				System.out.printf("%-25s",rsmd.getColumnLabel(i));
			}
			
			System.out.println();
			
			while (rs.next()) {
				System.out.printf("%-25s%-25s%-25s%-25s%-25s%-25s\n", rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6));
				
			}
			cerrarConexion();
		} catch (Exception e) {

			cerrarConexion();
			//e.printStackTrace();
		}
	}
	
	public void borrarBaseDeDatos() {
		try {
			
			establecerConexion();
			s.execute("DELETE FROM pedidos");
			s.execute("DELETE FROM rel_pedido_articulos");
			s.execute("DELETE FROM clientes");
			s.execute("DELETE FROM articulos");
			
			cargaInicial();
			cerrarConexion();
			
			System.out.println("Se ha borrado la base de datos sqlite con éxito");
		} catch (SQLException e) {
			System.err.println("Ha surgido un error borrando la base de datos");
			//e.printStackTrace();
			cerrarConexion();
		}
	}
	
	public List<Pedido> exportarPedidos() {
		try {
			establecerConexion();
			
			List<Pedido> pedidos = new ArrayList<Pedido>();
			
			String numeroCliente = "";
			
			ResultSet rsPedidos = s.executeQuery("SELECT num_Pedido, num_Cliente, fecha FROM pedidos;");
			
			while (rsPedidos.next()) {
				
				Pedido p = new Pedido();
				
				p.setNumeroPedido(rsPedidos.getString(1));
				
				numeroCliente = rsPedidos.getString(2);
				
				Cliente c = new Cliente();
				
				c.setNumeroCliente(numeroCliente);
				
				p.setCliente(c);
				
				p.setFecha(rsPedidos.getString(3));
				
				pedidos.add(p);
				
			}
			
			//Se necesitan hacer las querys por partes para evitar problemas con sqlite
			
			for (Pedido p : pedidos) {
				
				ResultSet rsCliente = s.executeQuery("SELECT * FROM clientes WHERE num_Cliente='"+p.getCliente().getNumeroCliente()+"';");
				
				
				//Sacamos el cliente de ese pedido
				while (rsCliente.next()) {
					// Solo debería devolver un único cliente
					Cliente c = new Cliente();
					
					c.setNumeroCliente(rsCliente.getString(1));
					c.setNombre(rsCliente.getString(2));
					c.setApellidos(rsCliente.getString(3));
					c.setTelefono(rsCliente.getString(4));
					c.setDireccion(rsCliente.getString(5));
					c.setEmpresa(rsCliente.getString(6));
					
					// Añadimos el cliente al pedido
					p.setCliente(c);
				}
			}
			
			for (Pedido p : pedidos) {
				
				//Sacamos todos los artículos de ese pedido
				ResultSet rsArticulos = s.executeQuery("SELECT a.num_Articulo, rpa.cantidad, a.descripcion, a.categoria, a.proveedor, a.precio FROM rel_pedido_articulos rpa JOIN articulos a on (rpa.num_Articulo=a.num_Articulo) WHERE rpa.num_Pedido='"+p.getNumeroPedido()+"';");
				
				List<Articulo> articulos = new ArrayList<Articulo>();
				
				while (rsArticulos.next()) {
					//Aquí si que puede devolver más de un articulo
					Articulo art = new Articulo();
					
					art.setCodigo(rsArticulos.getString(1));
					art.setCantidad(rsArticulos.getString(2)); // Cantidad de este artículo pedida, no STOCK
					art.setDescripcion(rsArticulos.getString(3));
					art.setCategoria(rsArticulos.getString(4));
					art.setProveedor(rsArticulos.getString(5));
					art.setPrecio(rsArticulos.getString(6));
					
					articulos.add(art);
				}
				
				
				p.setArticulos(articulos);
			}
			
			cerrarConexion();
			return pedidos;
		} catch (Exception e) {
			System.out.println("No se han podido exportar los pedidos.");
			e.printStackTrace();
			cerrarConexion();
			return null;
		}
	}
	
	
	public List<LineaPedido> exportarLineasPedido(){
		try {
			establecerConexion();
			
			ResultSet rs = s.executeQuery("SELECT * FROM mostrar_lineas_pedido;");
			
			List<LineaPedido> exportacion = new ArrayList<LineaPedido>();
			
			while (rs.next()) {
				exportacion.add(new LineaPedido(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5)));
			}
			
			cerrarConexion();
			
			return exportacion;
		} catch (Exception e) {
			System.out.println("No se han podido exportar las líneas de pedido.");
			cerrarConexion();
			return null;
		}
	}
	
}



