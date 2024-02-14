package dal;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Calendar;
import java.io.*;

import dataClasses.Articulo;
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
		
		guardarPdf(titulo, null, cabecera, texto);
		
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
		
		guardarPdf(titulo, null, cabecera, texto);
		
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
		
		guardarPdf(titulo, null, cabecera, texto);
		
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
		
		String introduccion = String.format("El número de líneas de pedido recibidas es: %d\n", lineasPedido.size());
		System.out.print(introduccion);
		 
		String cabeceraPrint = String.format("%-18s %-18s %-18s %-18s %-18s %-18s\n", "Cantidad", "N.º de pedido", "N.º de artículo", "Nombre-Categoría", "Precio/ud.", "Precio total");
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
			
			String lineaPrint = String.format("%-18s %-18s %-18s %-18s %-18s %-18s\n",
					lp.getCantidad(),
					lp.getNum_Pedido(),
					lp.getNum_Articulo(),
					lp.getDescripcion_Categoria(),
					lp.getPrecio_ud(),
					lp.getPrecio_tot());
			
			System.out.print(lineaPrint);
			texto.add(linea);
		}
		
		
		guardarPdf(titulo, introduccion, cabecera, texto);
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
			
			//String linea = String.format("%-18s %-18s %-18s\n", p.getNumeroPedido(), p.getCliente().getNumeroCliente(), p.getFecha());
			//System.out.print(linea);
			texto.add(linea);
		}

		guardarPdf(titulo, null, cabecera, texto);
		
	}
	
	
	
	/**
	 * Crea un archivo pdf con toda la información del informe en formato tabla
	 * @param titulo El nombre que tendrá el archivo creado
	 * @param encabezado Opcional, se pone antes de la tabla con el contenido del informe
	 * @param cabecera El título de las columnas de la tabla
	 * @param texto Todo el contenido de la tabla del informe
	 */
	public static void guardarPdf(String titulo, String encabezado, List<String> cabecera, List<List<String>> texto) {
		
		//WINDOWS
		//File f = new File(".\\files\\archivosEntrada\\");
		
		//LINUX
		File f = new File("./files/informes/"+titulo.replace(' ', '_')+".pdf");
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		Document document = new Document();
		
		try {

			PdfWriter pdf = PdfWriter.getInstance(document, new FileOutputStream(f.getAbsolutePath()));
			
			document.open();
			
			//Creamos el párrado con los datos de la empresa y le damos estilo
			Font destacado = new Font(Font.TIMES_ROMAN, 18, Font.ITALIC);
			Phrase empresa = new Phrase("AdiDam S.L\n", destacado);
			Font normal = new Font(Font.TIMES_ROMAN, 14, Font.NORMAL);
			Phrase fecha = new Phrase(Calendar.getInstance().getTime().toString(), normal);
			Paragraph cabeceraEmpresa = new Paragraph();
			cabeceraEmpresa.setAlignment(Element.ALIGN_RIGHT);
			cabeceraEmpresa.add(empresa);
			cabeceraEmpresa.add(fecha);
			cabeceraEmpresa.add("\n\n\n\n"); // Añadimos unos saltos de línea para separar la información
			document.add(cabeceraEmpresa);
			
			Font subrayado = new Font(Font.TIMES_ROMAN, 16, Font.BOLD);
			
			//Aqui comprobamos si hay algún encabezado para agregarlo
			if (encabezado != null) {
				Phrase introduccion = new Phrase(encabezado, normal);
				Paragraph parrafoIntroduccion = new Paragraph(introduccion);
				parrafoIntroduccion.add("\n"); //Añade un salto de línea para separar los componentes
				parrafoIntroduccion.setAlignment(Element.ALIGN_LEFT);
				document.add(parrafoIntroduccion);
			}
			
			
			//Creamos la tabla que contendrá todo el informe
			PdfPTable table = new PdfPTable(cabecera.size());
			for(String th : cabecera) {
				Phrase textoCelda = new Phrase(th, normal);
				PdfPCell celda = new PdfPCell(textoCelda);
				celda.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(celda);
			}
			for(List<String> linea : texto) {
				
				for (String dato : linea) {
					Phrase datoCelda = new Phrase(dato, normal);
					PdfPCell celda = new PdfPCell(datoCelda);
					celda.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(celda);
				}
			}
			
			
			document.add(table);
	        
	        document.close(); // Cerramos el documento
	        System.out.println("Se exportado el informe con éxito.");
		} catch (Exception e) {
			System.out.println("Ha surgido un error durante la exportación del informe.");
		}
		
	}
	
}
