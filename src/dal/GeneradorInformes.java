package dal;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Calendar;
import java.io.*;

import dataClasses.Articulo;
import dataClasses.Cliente;
import dataClasses.LineaPedido;
import dataClasses.Pedido;
import main.Main;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Anchor;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Chunk;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;


/**
 * Esta clase no solo muestra los informes por pantalla, 
 * si no que además los exporta como pdf
 * 
 * Utiliza los métodos de las clases encargadas de conectar con la base de datos para generar el informe.
 * @author andres
 *
 */
public class GeneradorInformes {
	
	public static void informeArticulosPorCantidad() {
		Main.mND.borrarBaseDeDatos();
		Main.mND.sincronizacion();
		
		System.out.println("Este informe muestra un listado de los artículos solicitados y la cantidad solicitada de cada uno de ellos");
		
		List<List<String>> texto = Main.mND.exportarArticulosYCantidad();
		
		if(texto == null) {
			return;
		}
		
		String titulo = "Artículo Cantidad";
		
		List<String> cabecera = new ArrayList<String>();
		cabecera.add("Artículo");
		cabecera.add("Cantidad");
		
		String cabeceraPrint = String.format("%-15s %-15s\n", "Artículo", "Cantidad");
		System.out.printf(cabeceraPrint);
		
		for (List<String> linea : texto) {
			System.out.printf("%-15s %-15s\n", linea.get(0), linea.get(1));
		}
		
		GeneradorPdf.guardarPdf(titulo, null, cabecera, texto);
		
	}
	
	public static void informePedidoUnidadesPedidas() {
		Main.mND.borrarBaseDeDatos();
		Main.mND.sincronizacion();
		
		List<List<String>> texto = Main.mND.exportarPedidoUnidadesPedidas();
		
		if(texto == null) {
			return;
		}

		String titulo = "Número de unidades pedidas por pedido";
		
		List<String> cabecera = new ArrayList<String>();
		cabecera.add("Pedido");
		cabecera.add("Unidades pedidas");
		
		String cabeceraPrint = String.format("%-18s %-18s\n", "Pedido", "Unidades pedidas");
		System.out.print(cabeceraPrint);

		for (List<String> linea : texto) {
			System.out.printf("%-18s %-18s\n", linea.get(0), linea.get(1));
		}
		
		GeneradorPdf.guardarPdf(titulo, null, cabecera, texto);
		
	}
	

	public static void informeUnidadesPedidasPorArticulo() {
		Main.mND.borrarBaseDeDatos();
		Main.mND.sincronizacion();
		
		List<List<String>> texto = Main.mND.exportarUnidadesPedidasPorArticulo();
		
		if(texto == null) {
			System.out.println("No se ha podido generar el informe de la unidades pedidas de cada artículo");
			return;
		}
		
		String titulo = "Número de unidades pedidas por artículo";
		
		List<String> cabecera = new ArrayList<String>();
		cabecera.add("Código de artículo");
		cabecera.add("Unidades pedidas");
		
		String cabeceraPrint = String.format("%-18s %-18s\n", "Código de artículo", "Unidades pedidas");
		System.out.print(cabeceraPrint);
		
		for (List<String> linea : texto) {
			System.out.printf("%-18s %-18s\n", linea.get(0), linea.get(1));
		}
		
		GeneradorPdf.guardarPdf(titulo, null, cabecera, texto);
		
	}
	
	public static void informePedidosPorCliente() {
		Main.mND.borrarBaseDeDatos();
		Main.mND.sincronizacion();
		
		List<List<String>> texto = Main.mND.numeroPedidosPorCliente();
		
		if(texto == null) {
			System.out.println("No se ha podido generar el informe del número de pedidos por cliente");
			return;
		}
		
		String titulo = "Número de pedidos por cliente";
		
		List<String> cabecera = new ArrayList<String>();
		cabecera.add("Cliente");
		cabecera.add("Número de pedidos");
		
		String cabeceraPrint = String.format("%-18s %-18s\n", "Cliente", "Número de pedidos");
		System.out.print(cabeceraPrint);
		
		for (List<String> linea : texto) {
			String lineaPrint = String.format("%-18s %-18s\n", linea.get(0), linea.get(1));
			System.out.print(lineaPrint);
		}
		
		GeneradorPdf.guardarPdf(titulo, null, cabecera, texto);
	}
	
	/**
	 * Extrae toda la información de los pedidos de neodatis y la muestra en pantalla
	 * Además, la guarda en un pdf.
	 */
	public static void informeResumenPedidos() {
		Main.mND.borrarBaseDeDatos();
		Main.mND.sincronizacion();
		
		String titulo = "Informe Resumen Pedidos";
		
		List<String> texto = Main.mND.exportarInformeCompleto();
		
		for(String linea : texto) {
			System.out.print(linea);
		}
		
		GeneradorPdf.guardarPdf(titulo, texto);
	}
	
	public static void informePedidosRecibidos() {
		Main.mND.borrarBaseDeDatos();
		Main.mND.sincronizacion();

		List<List<String>> texto = Main.mND.exportarPedidosTexto();
		
		if(texto == null) {
			System.out.println("No se ha podido generar el informe del número de líneas de pedido recibidas");
			return;
		}
		
		String numero = Main.mND.numeroPedidosRecibidos();

		String titulo = "Número de pedidos recibidos";
		
		String introduccion = String.format("El número de pedidos recibidos es: %s\n", numero);
		System.out.print(introduccion);
		
		List<String> cabecera = new ArrayList<String>();
		cabecera.add("N.º de pedido");
		cabecera.add("Cód. de cliente");
		cabecera.add("Nombre");
		cabecera.add("Apellidos");
		cabecera.add("Empresa");
		cabecera.add("Teléfono");
		cabecera.add("Fecha");
		
		String cabeceraPrint = String.format("%-18s %-18s %-18s %-25s %-18s %-18s %-18s\n", 
				"N.º de pedido",
				"Cód. de cliente",
				"Nombre",
				"Apellidos",
				"Empresa",
				"Teléfono",
				"Fecha");
		System.out.print(cabeceraPrint);
		
		for (List<String> linea : texto) {
			
			String lineaPrint = String.format("%-18s %-18s %-18s %-25s %-18s %-18s %-18s\n", 
					linea.get(0),
					linea.get(1),
					linea.get(2),
					linea.get(3),
					linea.get(4),
					linea.get(5),
					linea.get(6));
			System.out.print(lineaPrint);
			
		}
			
		GeneradorPdf.guardarPdf(titulo, introduccion, cabecera, texto);
	}
	
	public static void informeLineasDePedido() {
		Main.mND.borrarBaseDeDatos();
		Main.mND.sincronizacion();
		
		List<List<String>> texto  = Main.mND.exportarLineasDePedidoTexto();
		
		if(texto == null) {
			System.out.println("No se ha podido generar el informe del número de líneas de pedido recibidas");
			return;
		}
		
		String titulo = "Número de líneas de pedido recibidas";
		
		List<String> cabecera = new ArrayList<String>();
		cabecera.add("Cantidad");
		cabecera.add("N.º de pedido");
		cabecera.add("N.º de artículo");
		cabecera.add("Nombre-Categoría");
		cabecera.add("Precio/ud.");
		cabecera.add("Precio total");
		
		String numero = Main.mND.numeroLineasPedido(); 
		
		if(numero == null) {
			numero = Integer.toString(texto.size());
		}
		
		String introduccion = String.format("El número de líneas de pedido recibidas es: %s\n", numero);
		System.out.print(introduccion);
		 
		String cabeceraPrint = String.format("%-18s %-18s %-18s %-25s %-18s %-18s\n", "Cantidad", "N.º de pedido", "N.º de artículo", "Nombre-Categoría", "Precio/ud.", "Precio total");
		System.out.print(cabeceraPrint);
		
		for(List<String> linea : texto) {
			String lineaPrint = String.format("%-18s %-18s %-18s %-25s %-18s %-18s\n",
					linea.get(0),
					linea.get(1),
					linea.get(2),
					linea.get(3),
					linea.get(4),
					linea.get(5));
			System.out.print(lineaPrint);
		}
		
		
		GeneradorPdf.guardarPdf(titulo, introduccion, cabecera, texto);
	}
	
	public static void informePedidoClienteFecha() {
		Main.mND.borrarBaseDeDatos();
		Main.mND.sincronizacion();

		List<List<String>> texto = Main.mND.exportarPedidoClienteFecha();
		
		if(texto == null) {
			System.out.println("No se ha podido generar el informe de pedidos por cliente y fecha.");
			return;
		}

		String titulo = "Número de pedido Código de cliente Fecha";
		List<String> cabecera = new ArrayList<String>();
		cabecera.add("N.º de pedido");
		cabecera.add("Cód. de cliente");
		cabecera.add("Fecha");
		
		String cabeceraPrint = String.format("%-18s %-18s %-18s\n", "N.º de pedido", "Cód. de cliente", "Fecha");
		System.out.printf(cabeceraPrint);

		for(List<String> linea : texto) {
			String lineaPrint = String.format("%-18s %-18s %-18s\n", linea.get(0), linea.get(1), linea.get(2));
			System.out.print(lineaPrint); 
		}
		
		GeneradorPdf.guardarPdf(titulo, null, cabecera, texto);
		
	}
	
	public static void informePedidosPorArticulo() {
		Main.mND.borrarBaseDeDatos();
		Main.mND.sincronizacion();
		
		List<List<String>> texto = Main.mND.exportarPedidosPorArticulo();
		
		if(texto == null) {
			System.out.println("No se ha podido generar el informe de pedidos por artículo");
			return;
		}
		
		String titulo = "Pedidos por artículo";
		
		List<String> cabecera = new ArrayList<String>();
		cabecera.add("Código de artículo");
		cabecera.add("Cantidad de pedidos");
		
		String cabeceraPrint = String.format("%-18s %-18s\n", "Cód. de artículos", "Cantidad de pedidos"); 
		System.out.print(cabeceraPrint);
		
		
		for (List<String> linea : texto) {
			System.out.printf("%-18s %-18s\n", linea.get(0), linea.get(1));
		}
		
		
		GeneradorPdf.guardarPdf(titulo, null, cabecera, texto);
	}
	
	public static void informeMediaArticulosPorPedido() {
		Main.mND.borrarBaseDeDatos();
		Main.mND.sincronizacion();

		String titulo = "Media de artículos por pedido";
		
		String media = Main.mND.mediaArticulosPorPedido();
		
		String introduccion = String.format("La media de artículos solicitados por pedido recibido es: %s\n", media);
		
		System.out.print(introduccion);
		
		GeneradorPdf.guardarPdf(titulo, introduccion, null, null);
	}
	
}
