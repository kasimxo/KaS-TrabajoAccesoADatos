package dal;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import utils.Configuracion;

/***
 * Encargado de recibir un informe y volcarlo en un pdf
 * @author andres
 *
 */
public class GeneradorPdf {

	/**
	 * Crea un archivo pdf con toda la información del informe en formato tabla
	 * @param titulo El nombre que tendrá el archivo creado
	 * @param encabezado Opcional, se pone antes de la tabla con el contenido del informe
	 * @param cabecera El título de las columnas de la tabla
	 * @param texto Todo el contenido de la tabla del informe
	 */
	public static void guardarPdf(String titulo, String encabezado, List<String> cabecera, List<List<String>> texto) {
		
		File f = Configuracion.informes;
		
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
			
			if(cabecera != null && texto != null) {
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
			}
			
			
			
	        
	        document.close(); // Cerramos el documento
	        System.out.println("Se exportado el informe con éxito.");
		} catch (Exception e) {
			System.out.println("Ha surgido un error durante la exportación del informe.");
		}
		
	}
	
}
