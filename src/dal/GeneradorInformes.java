package dal;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import java.io.*;

import dataClasses.Articulo;
import dataClasses.LineaPedido;
import main.Main;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Anchor;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Chunk;


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
		String cabecera = String.format("%-15s %-15s\n", "Artículo", "Cantidad");
		System.out.printf(cabecera);
		List<String> texto = new ArrayList<String>();
		procesado.forEach((K,V) -> {
			String linea = String.format("%-15s %-15s\n", K, V.toString());
			System.out.print(linea);
			texto.add(linea);
		});
		
		guardarPdf(titulo, cabecera, texto);
		
	}
	
	/**
	 * Este método recibe el título del informe y el contenido del mismo.
	 * Lo guarda en un archivo pdf
	 * @param titulo
	 * @param texto
	 */
	public static void guardarPdf(String titulo, String cabecera, List<String> texto) {
		
		//WINDOWS
		//File f = new File(".\\files\\archivosEntrada\\");
		
		//LINUX
		File f = new File("./files/informes/"+titulo.replace(' ', '_')+".pdf");
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		Document document = new Document();
		
		try {

			PdfWriter pdf = PdfWriter.getInstance(document, new FileOutputStream(f.getAbsolutePath()));
			
			document.open();
	        // step 4: we add a paragraph to the document
	        document.add(new Paragraph(cabecera)); // Añadimos el párrafo
	        for (String linea : texto) {
	        	document.add(new Phrase(linea)); //Añadimos las líneas de texto
	        }
	      
	        document.close(); // Cerramos el documento
	        System.out.println("Se exportado el informe con éxito.");
		} catch (Exception e) {
			System.out.println("Ha surgido un error durante la exportación del informe.");
		}
		
	}
	
}
