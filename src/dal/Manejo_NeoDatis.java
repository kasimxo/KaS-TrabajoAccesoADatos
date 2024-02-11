package dal;

import java.io.File;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;

import dataClasses.LineaPedido;
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
	 * Este método exporta la información actual de la bbdd SQL y la inserta en la bbdd NeoDatis
	 */
	public void sincronizacion() {
		cargaLineasPedido();
		
		
	}
	
	public void cargaLineasPedido() {
		try {
			establecerConexion();
			
			for(LineaPedido lp : Main.mDB.exportarLineasPedido()) {
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
