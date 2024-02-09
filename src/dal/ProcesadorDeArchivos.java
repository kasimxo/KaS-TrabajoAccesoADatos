package dal;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import dataClasses.Articulo;
import dataClasses.Cliente;
import dataClasses.Pedido;

import main.Main;

public class ProcesadorDeArchivos {
	
	public void procesarNuevoPedido() {
		
		Manejo_SQL mDB = new Manejo_SQL();
		
		File f = new File(".\\files\\archivosEntrada\\");
		
		File[] archivos = f.listFiles();
		System.out.println("¿Qué archivo quieres procesar? Introduce el número del archivo o 'T' para procesarlos todos");
		for (int i = 1; i<=archivos.length; i++) {
			System.out.printf("%d - %s\n", i, archivos[i-1].getName());
		}

		String opcion = Main.sc.nextLine();
		
		if(opcion.toUpperCase().charAt(0) == 'T') {
			List<List<Pedido>> listadoPedidos = procesarTodos(archivos);
			
			System.out.println("Se han procesado el archivo correctamente.\nSe van a insertar los datos en la base de datos.");

			for(List<Pedido> pedido : listadoPedidos) {
				mDB.insertNuevosPedidos(pedido);
			}
		} else {
			try {
				int numero = Integer.parseInt(opcion);
				List<Pedido> pedido = procesar(archivos[numero-1]);
				
				System.out.println("Se ha procesado el archivo correctamente.\nSe van a insertar los datos en la base de datos.");
				
				mDB.insertNuevosPedidos(pedido);
			} catch (Exception e) {
				System.err.println("Input no reconocido");
			}
		}
	}
	
	/*
	 * Procesamos un array de archivos xml
	 */
	public List<List<Pedido>> procesarTodos(File[] archivos) {
		List<List<Pedido>> listadoPedidos = new ArrayList<List<Pedido>>();
		for(File f : archivos) {
			listadoPedidos.add(procesar(f));
		}
		return listadoPedidos;
	}
	
	/*
	 * Procesamos un único archivo xml
	 */
	public List<Pedido> procesar(File archivo) {
		
		//Esta tremenda mierda se tendría que refactorizar con métodos para procesar pedido/cliente/articulo
		List<Pedido> pedidos = new ArrayList<Pedido>();
		
		try {
			
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			
			Document doc = builder.parse(archivo);
			Element root = (Element) doc.getDocumentElement();
			
			//<pedidos>
			NodeList elementosPedidos = root.getChildNodes();
			
			//Vamos a recorrer todos los hijos
			for(int i = 0; i<elementosPedidos.getLength(); i++) {
				
				Node elementoPedido = elementosPedidos.item(i);
				
				//Filtramos únicamente aquellos que son de tipo válido (no son texto)
				if(elementoPedido.getNodeType() == Node.ELEMENT_NODE) {
					//<pedido>
					Pedido pedido = new Pedido();
					Cliente cliente = new Cliente();
					List<Articulo> articulos = new ArrayList<Articulo>();
					
					NodeList partesPedido = elementoPedido.getChildNodes();
					//Recorremos los hijos de <pedido>
					for(int j = 0; j<partesPedido.getLength(); j++) {
						//Recorremos los elementos del pedido
						Node parte = partesPedido.item(j);
						
						if(parte.getNodeType() != Node.ELEMENT_NODE) {
							//Filtramos los que no sean válidos
						} else if(parte.getNodeName() == "numero-cliente") {
							cliente.setNumeroCliente(parte.getTextContent());
						} else if(parte.getNodeName() == "numero-pedido") {
							pedido.setNumeroPedido(parte.getTextContent());
						} else if(parte.getNodeName() == "fecha") {
							pedido.setFecha(parte.getTextContent());
						} else if(parte.getNodeName() == "articulos") {
							articulos = procesarArticulos(parte);
						}
					}
					pedido.setCliente(cliente);
					pedido.setArticulos(articulos);
					pedidos.add(pedido);
				
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return pedidos;
	}
	
	/*
	 * Procesa un nodo <articulos> del xml
	 */
	private List<Articulo> procesarArticulos(Node nodo){
		List<Articulo> articulos = new ArrayList<Articulo>();
		//Aqui la parte son los articulos
		NodeList articulosNodo = nodo.getChildNodes();
		
		for (int x = 0; x<articulosNodo.getLength(); x++) {
			Node articuloNodo = articulosNodo.item(x);
			
			if(articuloNodo.getNodeType() != Node.ELEMENT_NODE) {
				//Saltamos los tipo no válido
			} else if(articuloNodo.getNodeName() == "articulo"){
				
				Articulo art = procesarArticulo(articuloNodo);
				
				articulos.add(art);
			}
		}
		return articulos;
	}
	
	/*
	 * Procesa un nodo <Articulo> del archivo xml
	 */
	private Articulo procesarArticulo(Node nodo) {
		Articulo art = new Articulo();
		NodeList nodosArticulo = nodo.getChildNodes();
		
		for(int z = 0; z<nodosArticulo.getLength(); z++) {
			Node nodoArticulo = nodosArticulo.item(z);
			if(nodoArticulo.getNodeType() != Node.ELEMENT_NODE) {
				//Saltamos no validos
			} else if(nodoArticulo.getNodeName() == "codigo"){
				art.setCodigo(nodoArticulo.getTextContent());
			} else if(nodoArticulo.getNodeName() == "cantidad") {
				art.setCantidad(nodoArticulo.getTextContent());
			}
		}
		return art;
	}
	
}
