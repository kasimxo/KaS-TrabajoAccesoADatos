package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.HashMap;

public class Configuracion {
	
	//Las rutas que se usarán durante la ejecucción del programa para la entrada y salida de archivos
	public static File entradaArchivos;
	public static File archivosProcesados;
	public static File schema;
	public static File informes;
	public static File neoDatisDataBase;
	public static File sqliteDataBase;
	
	public static File archivoConfig;

	private static Map<String, String> rutas;
	
	public Configuracion() {
		rutas = new HashMap<String, String>();
		//Windows
		//archivoConfig = new File(".\\files\\config.txt"); 
		//Linux
		archivoConfig = new File("./files/config.txt"); 
		
		try {
			for (String linea : Files.readAllLines(archivoConfig.toPath())) {
				String[] separado = linea.split("=");
				
				rutas.put(separado[0], separado[1]);
			}
		} catch (IOException e) {
			
		}
		System.out.println("Vamos a poner las rutas");
		//Aqui ponemos cada ruta
		rutas.forEach((K, V) -> {
			switch (K) {
			case "entradaArchivos":
				entradaArchivos = new File(V);
				break;
			case "archivosProcesados":
				archivosProcesados = new File(V);
				break;
			case "schema":
				schema = new File(V);
				break;
			case "informes":
				informes = new File(V);
				break;
			case "neoDatisDataBase":
				neoDatisDataBase = new File(V);
				break;
			case "sqliteDataBase":
				sqliteDataBase = new File(V);
				break;
			default:
				break;
			}
		});
		System.out.println(rutas);
	}
	
	public static void actualizarArchivoConfig() {
		
		try {
			FileWriter fw = new FileWriter(archivoConfig);
			
			rutas.forEach((K, V) -> {
				String linea = String.format("%s=%s\n", K, V);
				try {
					fw.write(linea, 0, linea.length()); 
					fw.flush(); 
				} catch (Exception e) {
					
				}
			});
			

			fw.close(); 
		} catch (IOException e) {
			e.printStackTrace(); 
		}
	}
	
	public void actualizarRutas() {
		rutas.put("entradaArchivos", entradaArchivos.getAbsolutePath());
		rutas.put("archivosProcesados", archivosProcesados.getAbsolutePath());
		rutas.put("schema", schema.getAbsolutePath());
		rutas.put("informes", informes.getAbsolutePath());
		rutas.put("neoDatisDataBase", neoDatisDataBase.getAbsolutePath());
		rutas.put("sqliteDataBase", sqliteDataBase.getAbsolutePath());
		actualizarArchivoConfig();
	}
	
	public void actualizarEntradaArchivosPath(String filePath) {
		entradaArchivos = new File(filePath);
		actualizarRutas();
	}
	
	public void actualizarArchivosProcesadosPath(String filePath) {
		archivosProcesados = new File(filePath);
		actualizarRutas();
	}
	
	public void actualizarSchemaPath(String filePath) {
		schema = new File(filePath);
		actualizarRutas();
	}
	
	public void actualizarInformesPath(String filePath) {
		informes = new File(filePath);
		actualizarRutas();
	}
	
	public void actualizarNeoDatisPath(String filePath) {
		neoDatisDataBase = new File(filePath);
		actualizarRutas();
	}
	
	public void actualizarSQLitePath(String filePath) {
		sqliteDataBase = new File(filePath);
		actualizarRutas();
	}
	
	public void restaurarWidows() {
		entradaArchivos = new File(".\\\\files\\\\archivosEntrada\\\\");
		archivosProcesados = new File(".\\\\files\\\\archivosProcesados\\\\");
		schema = new File(".\\\\files\\\\schema\\\\pedidos.xsd");
		informes = new File(".\\\\files\\\\informes\\\\");
		neoDatisDataBase =  new File(".\\\\files\\\\db\\\\pedidosAdiDam.nd");
		sqliteDataBase = new File(".\\\\files\\\\db\\\\pedidosAdiDam.db");
		actualizarRutas();
	}
	
	public void restaurarLinux() {
		entradaArchivos = new File("./files/archivosEntrada/");
		archivosProcesados = new File("./files/archivosProcesados/");
		schema = new File("./files/schema/pedidos.xsd");
		informes = new File("./files/informes/");
		neoDatisDataBase =  new File("./files/db/informes");
		sqliteDataBase = new File("./files/db/pedidosAdiDam.db");
		actualizarRutas();
	}
	
}
