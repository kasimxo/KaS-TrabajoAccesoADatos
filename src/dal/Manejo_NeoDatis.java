package dal;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.Objects;

import dataClasses.LineaPedido;
import dataClasses.Pedido;
import dataClasses.Articulo;
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
	 * Este método exporta todos los articulos solicitados y su cantidad solicitada
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
	
	public void cargarPedidos() {
		try {
			establecerConexion();
			
			List<Pedido> pedidos = Main.mDB.exportarPedidos();
			
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
	
	public void cargaLineasPedido() {
		try {
			establecerConexion();
			
			List<LineaPedido> lineas = Main.mDB.exportarLineasPedido();
			
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
	
}
