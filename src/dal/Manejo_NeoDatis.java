package dal;

import java.io.File;
import java.math.BigInteger;
import java.util.List;
import java.util.ArrayList;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.Objects;
import org.neodatis.odb.Values;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.criteria.*;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.impl.core.query.values.ValuesCriteriaQuery;

import dataClasses.LineaPedido;
import dataClasses.Pedido;
import dataClasses.Articulo;
import dataClasses.Cliente;
import main.Main;

public class Manejo_NeoDatis {
	
	private File dbPath;
	private ODB odb;

	public  Manejo_NeoDatis() {
		
		try {
			//WINDOWS
			//dbPath =  new File(".\\files\\db\\informes");
			
			//LINUX
			dbPath =  new File("./files/db/informes");
			
			if(!dbPath.exists()) {
				System.err.println("No se ha encontrado la base de datos NeoDatis");
				throw new Exception();
			}
			
			sincronizacion();
			
		} catch (Exception e) {
			System.err.println("No se ha podido establecer conexión con la base de datos NeoDatis.");
		}

	}
	
	/**
	 * Exporta todos los articulos solicitados y su cantidad solicitada
	 */
	public List<LineaPedido> exportarArticulosYCantidad() {
		List<LineaPedido> articulos = new ArrayList<LineaPedido>();
		try {
			establecerConexion();
			
			Objects<LineaPedido> objetos = odb.getObjects(LineaPedido.class);
			
			objetos.forEach((O) -> {
				articulos.add((LineaPedido) O);
			});
			
			cerrarConexion();
			return articulos;
		} catch (Exception e) {
			System.out.println("Se ha producido un error exportando los articulos por cantidad de NeoDatis.");
			return null;
		}
	}
	
	/**
	 * Este método exporta la información actual de la bbdd SQL y la inserta en la bbdd NeoDatis
	 * Quizás habría que prevenir duplicados(?)
	 */
	public void sincronizacion() {
		cargaLineasPedido();
		cargarPedidos();	
	}
	
	/**
	 * Extrae los datos de los pedidos de la base de datos de sqlite y
	 * y los inserta en la base de datos de NeoDatis.
	 */
	public void cargarPedidos() {
		try {
			establecerConexion();
			
			List<Pedido> pedidos = Main.mSQL.exportarPedidos();
			
			if(pedidos == null) {
				System.out.println("No se han podido insertar los pedidos en la bbdd NeoDatis.");
				return;
			}
			
			for(Pedido p : pedidos) {
				odb.store(p);
			}
			
			System.out.println("Se han insertado los pedidos en la bbdd NeoDatis correctamente.");
			
			cerrarConexion();
		} catch (Exception e) {
			System.out.println("No se han podido insertar los pedidos en la bbdd NeoDatis.");
		}
	}
	
	public String numeroLineasPedido() {
		try {
			establecerConexion();
			
			BigInteger a = odb.count(new CriteriaQuery(LineaPedido.class));
			
			cerrarConexion();
			return a.toString();
		} catch (Exception e) {
			System.out.println("No se han podido contar las líneas de pedido");
			cerrarConexion();

			return null;
		}
		
	}
	

	
	public String numeroPedidosRecibidos() {
		try {
			establecerConexion();
			
			BigInteger a = odb.count(new CriteriaQuery(Pedido.class));
			
			cerrarConexion();
			return a.toString();
		} catch (Exception e) {
			System.out.println("No se han podido contar los pedidos procesados");
			cerrarConexion();
			return null;
		}
		
	}
	
	public int numeroPedidosPorCliente(Cliente c) {
		try {
			establecerConexion();
			
			ICriterion criterio = Where.equal("cliente.numeroCliente", c.getNumeroCliente());
			
			IQuery query = new CriteriaQuery(Pedido.class, criterio); 

			//Recuperamos todos los pedidos que coincidan con el cliente
			Objects<Pedido> raw = odb.getObjects(query);
			
			cerrarConexion();
			return raw.size(); //Devolvemos el número de esos pedidos
		} catch (Exception e) {
			System.out.println("No se ha podido contar el número de pedidos del cliente " + c);
			return 0;
		}
		
	}
	
	public List<Cliente> exportarClientes(){
		try {
			establecerConexion();
			
			Objects<Cliente> clientesExportado = odb.getObjects(Cliente.class);
			
			List<Cliente> clientes = new ArrayList<Cliente>();
			
			clientesExportado.forEach((C) -> {clientes.add(C);});
			
			cerrarConexion();
			return clientes;
		} catch (Exception e) {
			System.out.println("No se han podido exportar los clientes");
			return null;
		}
	}
	
	
	public List<LineaPedido> exportarLineasDePedido(){
		try {
			establecerConexion();
			
			List<LineaPedido> lineasPedido = new ArrayList<LineaPedido>();
			
			Objects<LineaPedido> lineasPedidoExportado = odb.getObjects(LineaPedido.class);
			
			for(LineaPedido p : lineasPedidoExportado) {
				lineasPedido.add(p);
			}
			
			cerrarConexion();
			return lineasPedido;
		} catch (Exception e) {
			cerrarConexion();
			return null;
		}
	}
	
	public List<Pedido> exportarPedidos(){
		try {
			establecerConexion();
			
			List<Pedido> pedidos = new ArrayList<Pedido>();
			
			Objects<Pedido> pedidosExportado = odb.getObjects(Pedido.class);
			
			for(Pedido p : pedidosExportado) {
				pedidos.add(p);
			}
			
			cerrarConexion();
			return pedidos;
		} catch (Exception e) {
			cerrarConexion();
			return null;
		}
	}
	
	/**
	 * Extrae los datos de las líneas de pedido de la base de datos de sqlite y
	 * y los inserta en la base de datos de NeoDatis.
	 */
	public void cargaLineasPedido() {
		try {
			establecerConexion();
			
			List<LineaPedido> lineas = Main.mSQL.exportarLineasPedido();
			
			if (lineas == null) {
				System.out.println("No se han podido insertar las líneas de pedido en la bbdd NeoDatis.");
				return;
			}
			
			for(LineaPedido lp : lineas) {
				odb.store(lp);
			}
			
			System.out.println("Se han insertado las líneas de pedido en la bbdd NeoDatis correctamente");
			
			cerrarConexion();	
		} catch (Exception e) {
			System.out.println("No se han podido insertar las líneas de pedido en la bbdd NeoDatis.");
		}
		
	}
	
	public void establecerConexion() {
		try {
			this.odb = ODBFactory.open(dbPath.getAbsolutePath());
		} catch (Exception e) {
			System.out.println("No se ha podido establecer conexión con la base de datos NeoDatis.");
		}
	}
	
	
	public void cerrarConexion() {
		try {
			odb.close();
		} catch (Exception e) {
			System.out.println("No se ha podido cerrar la conexión de la base de datos NeoDatis.");
		}
	}
	
	public void borrarLineasPedido() {
		establecerConexion();
		//Borramos lineas de pedido
		Objects lineas = odb.getObjects(LineaPedido.class);
		for (Object lp : lineas) {
			odb.delete(lp);
		}
		cerrarConexion();
	}
	
	public void borrarPedidos() {

		establecerConexion();
		//Borramos los pedidos
		Objects pedidos = odb.getObjects(Pedido.class);
		for (Object p : pedidos) {
			odb.delete(p);
		}
		
		//Al borrar los pedidos, no borra ni al ciente ni los articulos
		//Borramos clientes
		Objects clientes = odb.getObjects(Cliente.class);
		for(Object c : clientes) {
			odb.delete(c);
		}
		
		//Borramos los articulos
		Objects articulos = odb.getObjects(Articulo.class);
		for(Object a : articulos) {
			odb.delete(a);
		}
		cerrarConexion();
	}
	
	/**
	 * Borra todo lo que tiene la base de datos NeoDatis
	 */
	public void borrarBaseDeDatos() {
		try {
			establecerConexion();
			
			borrarLineasPedido();
			borrarPedidos();
	
			cerrarConexion();
			sincronizacion();
			System.out.println("Se ha borrado la base de datos NeoDatis con éxito");
		} catch (Exception e) {
			System.out.println("Ha surgido un error borrando la base de datos");
			cerrarConexion();
		}
	}
	
}
