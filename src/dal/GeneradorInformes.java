package dal;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import dataClasses.Articulo;
import dataClasses.LineaPedido;
import main.Main;


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
		
		String titulo = String.format("%-15s %-15s\n", "Artículo", "Cantidad");
		System.out.print(titulo);
		List<String> texto = new ArrayList<String>();
		procesado.forEach((K,V) -> {
			String linea = String.format("%-15s %-15s\n", K, V.toString());
			System.out.print(linea);
			texto.add(linea);
		});
		
		guardarPdf(titulo, texto);
		
	}
	
	/**
	 * Este método recibe el título del informe y el contenido del mismo.
	 * Lo guarda en un archivo pdf
	 * @param titulo
	 * @param texto
	 */
	public static void guardarPdf(String titulo, List<String> texto) {
		
	}
	
}
