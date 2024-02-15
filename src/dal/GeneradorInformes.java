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
 * Esta clase no solo muestra los informes por pantalla, si no que además los exporta como pdf
 * @author andres
 *
 */
public class GeneradorInformes {
	
	public static void informeArticulosPorCantidad() {
		System.out.println("Este informe muestra un listado de los artículos solicitados y la cantidad solicitada de cada uno de ellos");
		
		List<LineaPedido> articulos = Main.mND.exportarArticulosYCantidad();
		
		if(articulos == null) {
			return;
		}
		
		//Como nos interesa sumar la cantidad pedida de cada artículo, podemos pasarlo a un mapa
		
		Map<String, Integer> procesado = new HashMap<String, Integer>();
		
		for(LineaPedido lp : articulos) {
			if (procesado.containsKey(lp.getNum_Articulo())) {
				Integer cantidadActual = procesado.get(lp.getNum_Articulo());
				cantidadActual += Integer.parseInt(lp.getCantidad());
				procesado.put(lp.getNum_Articulo(), cantidadActual);
			} else {
				procesado.put(lp.getNum_Articulo(), Integer.parseInt(lp.getCantidad()));
			}
		}
		
		String titulo = "Artículo Cantidad";
		
		List<String> cabecera = new ArrayList<String>();
		cabecera.add("Artículo");
		cabecera.add("Cantidad");
		
		String cabeceraPrint = String.format("%-15s %-15s\n", "Artículo", "Cantidad");
		System.out.printf(cabeceraPrint);
		
		List<List<String>> texto = new ArrayList<List<String>>();
		
		procesado.forEach((K,V) -> {
			List<String> linea = new ArrayList<String>();
			linea.add(K);
			linea.add(Integer.toString(V));
			
			String lineaPrint = String.format("%-15s %-15s\n", K, V.toString());
			System.out.print(lineaPrint);
			
			texto.add(linea);
		});
		
		GeneradorPdf.guardarPdf(titulo, null, cabecera, texto);
		
	}
	
	public static void informePedidoUnidadesPedidas() {
		List<Pedido> pedidos = Main.mND.exportarPedidos();
		
		String titulo = "Número de unidades pedidas por pedido";
		
		List<String> cabecera = new ArrayList<String>();
		cabecera.add("Pedido");
		cabecera.add("Unidades pedidas");
		
		String cabeceraPrint = String.format("%-18s %-18s\n", "Pedido", "Unidades pedidas");
		System.out.print(cabeceraPrint);
		
		List<List<String>> texto = new ArrayList<List<String>>();
		
		for (Pedido p : pedidos) {
			List<String> linea = new ArrayList<String>();
			linea.add(p.getNumeroPedido());
			
			int cantidad = 0;
			
			for (Articulo a : p.getArticulos()) {
				cantidad += Integer.parseInt(a.getCantidad());
			}
			
			String lineaPrint = String.format("%-18s %-18s\n", p.getNumeroPedido(), Integer.toString(cantidad));
			System.out.print(lineaPrint);
			
			linea.add(Integer.toString(cantidad));
			texto.add(linea);
		}
		
		GeneradorPdf.guardarPdf(titulo, null, cabecera, texto);
		
	}
	

	public static void informeUnidadesPedidasPorArticulo() {
		List<LineaPedido> lineasPedido = Main.mND.exportarLineasDePedido();
		
		if(lineasPedido == null) {
			System.out.println("No se ha podido generar el informe de la unidades pedidas de cada artículo");
			return;
		}
		
		String titulo = "Número de unidades pedidas por artículo";
		
		List<String> cabecera = new ArrayList<String>();
		cabecera.add("Código de artículo");
		cabecera.add("Unidades pedidas");
		
		String cabeceraPrint = String.format("%-18s %-18s\n", "Código de artículo", "Unidades pedidas");
		System.out.print(cabeceraPrint);
		
		//Generamos un mapa de los artículos, de este modo resulta mas sencillo contar el total de unidades
		// K, V -> K=Código de artículo, V=Unidades pedidas
		Map<String, Integer> mapa = new HashMap<String, Integer>();
		
		for(LineaPedido lp : lineasPedido) {
			if(mapa.containsKey(lp.getNum_Articulo())) {
				int cantidad = mapa.get(lp.getNum_Articulo());
				cantidad += Integer.parseInt(lp.getCantidad());
				mapa.put(lp.getNum_Articulo(), cantidad);
			} else {
				mapa.put(lp.getNum_Articulo(), Integer.parseInt(lp.getCantidad()));
			}
		}
		
		List<List<String>> texto = new ArrayList<List<String>>();

		mapa.forEach( (K, V) -> {
			List<String> linea = new ArrayList<String>();
			linea.add(K);
			linea.add(Integer.toString(V));
			
			String lineaPrint = String.format("%-18s %-18s\n", K, Integer.toString(V));
			System.out.print(lineaPrint);
			
			texto.add(linea);
		});
		
		GeneradorPdf.guardarPdf(titulo, null, cabecera, texto);
		
	}
	
	public static void informePedidosPorCliente() {
		List<Cliente> clientes = Main.mND.exportarClientes();
		if(clientes == null) {
			System.out.println("No se ha podido generar el informe del número de pedidos por cliente");
			return;
		}
		
		String titulo = "Número de pedidos por cliente";
		
		List<String> cabecera = new ArrayList<String>();
		cabecera.add("Cliente");
		cabecera.add("Número de pedidos");
		
		String cabeceraPrint = String.format("%-18s %-18s\n", "Cliente", "Número de pedidos");
		System.out.print(cabeceraPrint);
		
		List<List<String>> texto = new ArrayList<List<String>>();
		
		for (Cliente c : clientes) {
			int numeroPedidos = Main.mND.numeroPedidosPorCliente(c);
			
			List<String> linea = new ArrayList<String>();
			linea.add(c.getNumeroCliente());
			linea.add(Integer.toString(numeroPedidos));
			texto.add(linea);
			
			String lineaPrint = String.format("%-18s %-18d\n", c.getNumeroCliente(), numeroPedidos);
			System.out.print(lineaPrint);
		}
		
		GeneradorPdf.guardarPdf(titulo, null, cabecera, texto);
	}
	
	public static void informePedidosRecibidos() {
		List<Pedido> pedidos = Main.mND.exportarPedidos();
		
		if(pedidos == null) {
			System.out.println("No se ha podido generar el informe del número de líneas de pedido recibidas");
			return;
		}
		
		String numero = Main.mND.numeroPedidosRecibidos();
		//Si hubiera dado algún error ese método, prueba un método alternativo
		if(numero == null) {
			numero = Integer.toString(pedidos.size());
		}
		
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
		cabecera.add("Precio");
		
		String cabeceraPrint = String.format("%-18s %-18s %-18s %-25s %-18s %-18s %-18s %-18s\n", 
				"N.º de pedido",
				"Cód. de cliente",
				"Nombre","Apellidos",
				"Empresa",
				"Teléfono",
				"Fecha",
				"Valor");
		System.out.print(cabeceraPrint);
		
		List<List<String>> texto = new ArrayList<List<String>>();
		for (Pedido p : pedidos) {
			List<String> linea = new ArrayList<String>();
			linea.add(p.getNumeroPedido());
			linea.add(p.getCliente().getNumeroCliente());
			linea.add(p.getCliente().getNombre());
			linea.add(p.getCliente().getApellidos());
			linea.add(p.getCliente().getEmpresa());
			linea.add(p.getCliente().getTelefono());
			linea.add(p.getFecha());
			
			int valor = 0;
			
			for (Articulo a : p.getArticulos()) {
				valor += Integer.parseInt(a.getCantidad()) * Integer.parseInt(a.getPrecio());
			}
			
			linea.add(Integer.toString(valor));
			
			texto.add(linea);
			
			String lineaPrint = String.format("%-18s %-18s %-18s %-25s %-18s %-18s %-18s %-18s\n", 
					p.getNumeroPedido(),
					p.getCliente().getNumeroCliente(),
					p.getCliente().getNombre(),
					p.getCliente().getApellidos(),
					p.getCliente().getEmpresa(),
					p.getCliente().getTelefono(),
					p.getFecha(),
					Integer.toString(valor));
			System.out.print(lineaPrint);
			
		}
			
		GeneradorPdf.guardarPdf(titulo, introduccion, cabecera, texto);
	}
	
	public static void informeLineasDePedido() {
		List<LineaPedido> lineasPedido = Main.mND.exportarLineasDePedido();
		
		if(lineasPedido == null) {
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
		
		String numero = Main.mND.numeroLineasPedido(); // Utiliza un método de NeoDatis
		
		if(numero == null) {
			numero = Integer.toString(lineasPedido.size());
		}
		
		String introduccion = String.format("El número de líneas de pedido recibidas es: %s\n", numero);
		System.out.print(introduccion);
		 
		String cabeceraPrint = String.format("%-18s %-18s %-18s %-25s %-18s %-18s\n", "Cantidad", "N.º de pedido", "N.º de artículo", "Nombre-Categoría", "Precio/ud.", "Precio total");
		System.out.print(cabeceraPrint);
		
		List<List<String>> texto = new ArrayList<List<String>>();
		
		for(LineaPedido lp : lineasPedido) {
			
			List<String> linea = new ArrayList<String>();
			
			linea.add(lp.getCantidad());
			linea.add(lp.getNum_Pedido());
			linea.add(lp.getNum_Articulo());
			linea.add(lp.getDescripcion_Categoria());
			linea.add(lp.getPrecio_ud());
			linea.add(lp.getPrecio_tot());
			
			String lineaPrint = String.format("%-18s %-18s %-18s %-25s %-18s %-18s\n",
					lp.getCantidad(),
					lp.getNum_Pedido(),
					lp.getNum_Articulo(),
					lp.getDescripcion_Categoria(),
					lp.getPrecio_ud(),
					lp.getPrecio_tot());
			
			System.out.print(lineaPrint);
			texto.add(linea);
		}
		
		
		GeneradorPdf.guardarPdf(titulo, introduccion, cabecera, texto);
	}
	
	public static void informePedidoClienteFecha() {
		List<Pedido> pedidos = Main.mND.exportarPedidos();
		
		if(pedidos == null) {
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
		
		List<List<String>> texto = new ArrayList<List<String>>();
		
		for(Pedido p : pedidos) {
			
			List<String> linea = new ArrayList<String>();
			
			linea.add(p.getNumeroPedido());
			linea.add(p.getCliente().getNumeroCliente());
			linea.add(p.getFecha());
			
			String lineaPrint = String.format("%-18s %-18s %-18s\n", p.getNumeroPedido(), p.getCliente().getNumeroCliente(), p.getFecha());
			System.out.print(lineaPrint);
			texto.add(linea);
		}

		GeneradorPdf.guardarPdf(titulo, null, cabecera, texto);
		
	}
	
	public static void informePedidosPorArticulo() {
		
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

		String titulo = "Media de artículos por pedido";
		
		String media = Main.mND.mediaArticulosPorPedido();
		
		String introduccion = String.format("La media de artículos solicitados por pedido recibido es: %s\n", media);
		
		System.out.print(introduccion);
		
		GeneradorPdf.guardarPdf(titulo, introduccion, null, null);
	}
	
}
