import java.util.Scanner;

public class Main {
	
	public static boolean funcionando = true;
	public static Scanner sc;
	
	public static void main(String[] args) {
		 sc = new Scanner(System.in);
		
		
		while (funcionando) {
			menu();
		}
		sc.close();
	}
	
	public static void menu() {
		String[] opciones = {"1 Procesar nuevo pedido", "2 Mostrar pedidos", "3 Exportar pedido", "4 Configuración", "5 Salir"};
		System.out.println("Bienvenido al sistema gestor de pedidos de AdiDAM");
		for (String s : opciones) { System.out.println(s);}
		
		String input = sc.nextLine();
		
		try {
			int seleccion = Integer.parseInt(input);
			switch (seleccion) {
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
				//Configuración
				break;
			case 5:
				//Salir
				System.out.println("Saliendo.");
				funcionando = false;
				break;
			default:
				break;
			
			}
		}catch(Exception e) {
			System.err.println("Input no reconocido");
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
		Manejo_db mDB = new Manejo_db();
		mDB.mostrarPedido("123");
	}
	

}
