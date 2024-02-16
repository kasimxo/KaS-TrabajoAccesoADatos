package main;

import java.io.File;

import dal.GeneradorInformes;
import dal.Manejo_NeoDatis;
import dal.Manejo_SQL;
import dal.ProcesadorDeArchivos;
import utils.Input;

public class Main {
	
	public static boolean funcionando = true;
	
	// Clase que maneja la conexión y peticiones con la bbdd SQLite
	public static Manejo_SQL mSQL;
	
	// Clase que maneja la conexión y peticiones con la bbdd NeoDatis
	public static Manejo_NeoDatis mND;
	
	public static Input i;
	
	//Las rutas que se usarán durante la ejecucción del programa para la entrada y salida de archivos
	public static File entradaArchivos;
	public static File archivosProcesados;
	public static File schema;
	public static File informes;
	
	
	public static void main(String[] args) {
		
		//Iniciamos las rutas de los archivos
//		entradaArchivos = new File(".\\files\\archivosEntrada\\");
//		archivosProcesados = new File(".\\files\\archivosProcesados\\");
//		schema = new File(".\\files\\schema\\pedidos.xsd");
//		informes = new File(".\\files\\informes\\");
		
		entradaArchivos = new File("./files/archivosEntrada/");
		archivosProcesados = new File("./files/archivosProcesados/");
		schema = new File("./files/schema/pedidos.xsd");
		informes = new File("./files/informes/");
		
		//Con esto iniciamos la clase que manejará el input
		i = new Input();
		
		mSQL = new Manejo_SQL();
		mND = new Manejo_NeoDatis();
		
		while (funcionando) {
			menu();
		}
	}
	
	public static void menu() {
		String[] opciones = {
				"1. Procesar nuevo pedido",
				"2. Mostrar pedidos",
				"3. Generar informes",
				"4. Configuración",
				"5. Borrar base de datos",
				"6. Salir"
				};
		
		System.out.println("Bienvenido al sistema gestor de pedidos de AdiDAM");
		for (String s : opciones) { System.out.println(s);}
		
		switch (Input.leerInt()) {
		case 1:
			//Procesar nuevo pedido
			procesarNuevo();
			break;
		case 2:
			//Mostrar todos los pedidos
			mostrarPedidos();
			break;
		case 3:
			//Generar informes
			menuInformes();
			break;
		case 4:
			//Configuración
			configuracion();
			break;
		case 5:
			//Borrar base de datos
			borrarBaseDeDatos();
			break;
		case 6:
			//Salir
			System.out.println("Saliendo");
			funcionando = false;
			mSQL.cerrarConexion();
			break;
		default:
			System.out.println("Opción no reconocida");
			break;
		
		}

	}
	
	public static void configuracion() {
		System.out.println("¿Qué ruta deseas modificar?");
		String[] opciones = {
				"1. Entrada de archivos", 
				"2. Archivos procesados", 
				"3. Esquema de validación de pedidos", 
				"4. Salida de informes", 
				"5. Restaurar configuración predeterminada"
				};
		for (String o : opciones) {System.out.println(o);};
		switch(Input.leerInt()) {
		case 1:
			System.out.println("Introduce la nueva ruta:");
			entradaArchivos = new File(Input.leerString());
			System.out.println("Ruta modificada");
			break;
		case 2:
			System.out.println("Introduce la nueva ruta:");
			archivosProcesados = new File(Input.leerString());
			System.out.println("Ruta modificada");
			break;
		case 3:
			System.out.println("Introduce la nueva ruta:");
			schema = new File(Input.leerString());
			System.out.println("Ruta modificada");
			break;
		case 4:
			System.out.println("Introduce la nueva ruta:");
			informes = new File(Input.leerString());
			System.out.println("Ruta modificada");
			break;
		case 5:
			entradaArchivos = new File(".\\files\\archivosEntrada\\");
			archivosProcesados = new File(".\\files\\archivosProcesados\\");
			schema = new File(".\\files\\schema\\pedidos.xsd");
			informes = new File(".\\files\\informes\\");
			System.out.println("Rutas restauradas");
			break;
		default:
				System.out.println("Opción no reconocida");
				break;
		}
	}

	public static void menuInformes() {
		String[] informes = {
				"1. Informe del número de pedidos recibidos y procesados correctamente",
				"2. Informe del número de líneas de pedido recibidas",
				"3. Informe de los artículos únicos que se han solicitado por cantidad de pedidos",
				"4. Informe de número de pedidos por cliente",
				"5. Informe de las unidades de cada artículo por servir",
				"6. Informe del total de unidades pedidas por pedido",
				"7. Informe de media de artículos por pedido recibidos",
				"8. Informe resumen de pedidos por cliente y fecha", //Opcional 1
				"9. Informe de artículos por servir" //Opcional 2: Informe artículos por cantidad
			};
		System.out.println("Indica el tipo de informe que desea generar:");
		for(String informe : informes) {
			System.out.println(informe);
		}
		
		switch (Input.leerInt()) {
		case 1:
			//1. Informe del número de pedidos recibidos y procesados correctamente
			GeneradorInformes.informePedidosRecibidos();
			break;
		case 2:
			//2. Informe del número de líneas de pedido recibidas
			GeneradorInformes.informeLineasDePedido();
			break;
		case 3:
			//3. Informe de los artículos únicos que se han solicitado por cantidad de pedidos
			GeneradorInformes.informePedidosPorArticulo();
			break;
		case 4:
			//4. Informe de número de pedidos por cliente
			GeneradorInformes.informePedidosPorCliente();
			break;
		case 5:
			//5. Informe de las unidades de cada artículo por servir
			GeneradorInformes.informeUnidadesPedidasPorArticulo();
			break;
		case 6:
			//6. Informe del total de unidades pedidas por pedido
			GeneradorInformes.informePedidoUnidadesPedidas();
			break;
		case 7:
			//7. Informe de media de artículos por pedido recibidos
			GeneradorInformes.informeMediaArticulosPorPedido();
			break;
		case 8:
			//Opcional I: 8. Informe resumen de pedidos por cliente y fecha
			GeneradorInformes.informePedidoClienteFecha();
			break;
		case 9:
			//Opcional II: 9. Informe de artículos por servir
			GeneradorInformes.informeArticulosPorCantidad();
			break;
		default:
			System.out.println("Opción no reconocida");
			break;
		}
		
	}
	
	
	
	/**
	 * Facilita la posibilidad de hacer pruebas a la hora de desarrollar el programa.
	 * Borra todos los datos de la base de datos, dejándola como si estuviera recién creada.
	 * Únicamente insertará los datos del primer archivo de pedidos 
	 * También sincroniza NeoDatis con los datos de sqlite
	 */
	public static void borrarBaseDeDatos() {
		System.out.println("¿Estás seguro? Si borras los datos no habrá manera de recuperarlos.\nS/N");
		switch (Input.leerString().toLowerCase()) {
		case "s":
			mSQL.borrarBaseDeDatos();
			mND.borrarBaseDeDatos();
			break;
		default:
			break;
		}
	}
	
	public static void procesarNuevo() {
		try {
			ProcesadorDeArchivos procesador = new ProcesadorDeArchivos();
			procesador.procesarNuevoPedido();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void mostrarPedidos() {
		mSQL.mostrarPedidos();
		
	}
	

}
