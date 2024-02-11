package main;
import java.util.Scanner;

import dal.Manejo_NeoDatis;
import dal.Manejo_SQL;
import dal.ProcesadorDeArchivos;
import utils.Input;

public class Main {
	
	public static boolean funcionando = true;
	
	// Clase que maneja la conexión y peticiones con la bbdd SQLite
	public static Manejo_SQL mDB;
	
	// Clase que maneja la conexión y peticiones con la bbdd NeoDatis
	public static Manejo_NeoDatis mND;
	
	
	public static void main(String[] args) {
		
		//Con esto iniciamos la clase que manejará el input
		new Input();
		
		mDB = new Manejo_SQL();
		mND = new Manejo_NeoDatis();
		
		while (funcionando) {
			menu();
		}
	}
	
	public static void menu() {
		String[] opciones = {"1. Procesar nuevo pedido", "2. Mostrar pedidos", "3. Exportar pedido", "4. Generar informes", "5. Configuración", "6. Borrar base de datos", "7. Salir"};
		System.out.println("Bienvenido al sistema gestor de pedidos de AdiDAM");
		for (String s : opciones) { System.out.println(s);}
		
		switch (Input.leerInt()) {
		case 1:
			//Procesar nuevo pedido
			procesarNuevo();
			break;
		case 2:
			//Mostrar pedidos
			mostrarPedidos();
			break;
		case 3:
			//Exportar pedido
			break;
		case 4:
			//Generar informes
			break;
		case 5:
			//Configuración
			break;
		case 6:
			//Borrar base de datos
			borrarBaseDeDatos();
			break;
		case 7:
			//Salir
			System.out.println("Saliendo.");
			funcionando = false;
			mDB.cerrarConexion();
			break;
		default:
			break;
		
		}

		
	}
	
	/**
	 * Facilita la posibilidad de hacer pruebas a la hora de desarrollar el programa.
	 * Borra todos los datos de la base de datos, dejándola como si estuviera recién creada.
	 * Únicamente insertará los datos del primer archivo de pedidos
	 */
	public static void borrarBaseDeDatos() {
		System.out.println("¿Estás seguro? Si borras los datos no habrá manera de recuperarlos.\nS/N");
		switch (Input.leerString().toLowerCase()) {
		case "s":
			mDB.borrarBaseDeDatos();
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
		mDB.mostrarPedidos();
		
	}
	

}
