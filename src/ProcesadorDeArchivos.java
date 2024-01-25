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

public class ProcesadorDeArchivos {
	
	public void procesarNuevoPedido() {
		File f = new File(".\\src\\archivosEntrada\\");
		
		File[] archivos = f.listFiles();
		System.out.println("¿Qué archivo quieres procesar? Introduce el número del archivo o 'T' para procesarlos todos");
		for (int i = 1; i<=archivos.length; i++) {
			System.out.printf("%d - %s\n", i, archivos[i-1].getName());
		}

		String opcion = Main.sc.nextLine();
		
		if(opcion.charAt(0) == 'T') {
			procesarTodos(archivos);
		} else {
			try {
				int numero = Integer.parseInt(opcion);
				procesar(archivos[numero-1]);
			} catch (Exception e) {
				System.err.println("Input no reconocido");
			}
		}
		
	}
	
	public void procesarTodos(File[] archivos) {
		for(File f : archivos) {
			procesar(f);
		}
	}
	
	public void procesar(File archivo) {
		
		List<Pedido> pedidos = new ArrayList<Pedido>();
		
		try {
			
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			
			
			Document doc = builder.parse(archivo);
			//Recuperamos el primer elemento del documetno
			Element root = (Element) doc.getDocumentElement();
			
			NodeList elementosPedidos = root.getChildNodes();
			
			//Vamos a recorrer todos los hijos
			for(int i = 0; i<elementosPedidos.getLength(); i++) {
				Node elementoPedido = elementosPedidos.item(i);
				
				//Filtramos únicamente aquellos que son de tipo válido (no son texto)
				if(elementoPedido.getNodeType() == Node.ELEMENT_NODE) {
					
					//Aquí ya estamos recorriendo los pedidos
					Pedido pedido = new Pedido();
					Cliente cliente = new Cliente();
					List<Articulo> articulos = null;
					
					NodeList partesPedido = elementoPedido.getChildNodes();
					for(int j = 0; j<partesPedido.getLength(); j++) {
						//Recorremos los elementos del pedido
						Node parte = partesPedido.item(j);
						
						
						if(parte.getNodeType() != Node.ELEMENT_NODE) {
							//Filtramos los que no sean válidos
						} else if(parte.getNodeName() == "numero-cliente") {
							cliente.setNumeroCliente(parte.getNodeValue());
						} else if(parte.getNodeName() == "numero-pedido") {
							pedido.setNumeroPedido(parte.getNodeValue());
						} else if(parte.getNodeName() == "fecha") {
							pedido.setFecha(parte.getNodeValue());
						} else if(parte.getNodeName() == "articulos") {
							
						}
					}
					pedido.setCliente(cliente);
					
					pedidos.add(pedido);
				
				}
			}
			
			
			for(Pedido p : pedidos) {
				System.out.println(p);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		/*
		try {
			
			File a = new File(".\\src\\db\\pedidos.db");
			
			Class.forName("org.sqlite.JDBC");
			Connection con = DriverManager.getConnection("jdbc:sqlite:" + a.getAbsolutePath());
			
			java.sql.Statement s = con.createStatement();
			con.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	
}
