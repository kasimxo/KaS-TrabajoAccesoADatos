package dal;

import java.io.File;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;

public class Manejo_NeoDatis {
	
	private ODB odb;

	public  Manejo_NeoDatis() {
		
		try {
			//WINDOWS
			//File dbPath =  new File(".\\files\\db\\informes");
			
			//LINUX
			File dbPath =  new File("./files/db/informes");
			
			if(!dbPath.exists()) {
				System.err.println("No se ha encontrado la base de datos NeoDatis");
				throw new Exception();
			}
			
			this.odb = ODBFactory.open(dbPath.getAbsolutePath());
			
		} catch (Exception e) {
			System.err.println("No se ha podido establecer conexión con la base de datos NeoDatis.");
		}
		
		
	}
	
}
