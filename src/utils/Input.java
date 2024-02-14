package utils;

import java.util.Scanner;

/**
 * El propósito de esta clase es manejar el input del usuario
 * esto es, leer el teclado. etc.
 * @author andres
 *
 */
public class Input {
	
	private static Scanner sc;
	
	public Input() {
		sc = new Scanner(System.in);
	}
	
	
	public static int leerInt() {
		
		String input = sc.nextLine();
		
		try {
			return Integer.parseInt(input);
		} catch (Exception e) {
			System.out.println("Input no válido");
			return -1;
		}
	}
	
	public static String leerString() {
		
		String input = sc.nextLine();
		
		return input;
	}
	
	public static boolean aceptarCancelar() {
		String input = sc.nextLine();
		
		switch(input.toLowerCase().charAt(0)) {
		case 's':
			return true;
		default:
			return false;
		}
		
	}
	
	public static void closeScanner() {
		sc.close();
	} 
	
}
