package dal;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.ArrayList;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ObjectValues;
import org.neodatis.odb.Objects;
import org.neodatis.odb.Values;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.criteria.*;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.impl.core.query.values.ValuesCriteriaQuery;

import dataClasses.LineaPedido;
import dataClasses.Pedido;
import dataClasses.Articulo;
import dataClasses.Cliente;
import main.Main;
import utils.Configuracion;

public class Manejo_NeoDatis {
	
	private File dbPath;
	private ODB odb;

	public  Manejo_NeoDatis() {
		
		try {
			
			dbPath = Configuracion.neoDatisDataBase;
			
			if(!dbPath.exists()) {
				System.err.println("No se ha encontrado la base de datos NeoDatis en la ruta especificada");
				dbPath.createNewFile();
				//throw new Exception();
			}
			
			sincronizacion();
			
		} catch (Exception e) {
			System.err.println("No se ha podido establecer conexión con la base de datos NeoDatis.");
		}

	}
	
	public List<List<String>> exportarUnidadesPedidasPorArticulo() {
		try {
			establecerConexion();
			
			List<List<String>> texto = new ArrayList<List<String>>();

			Values valores = odb.getValues(new ValuesCriteriaQuery(LineaPedido.class).field("num_Articulo").sum("cantidad").groupBy("num_Articulo"));
			
			while (valores.hasNext()) {
				List<String> linea = new ArrayList<String>();
				
				ObjectValues ov = valores.next();
				BigDecimal cantidad = (BigDecimal) ov.getByIndex(1);

				linea.add((String)ov.getByIndex(0));
				linea.add(cantidad.toString());
				texto.add(linea);
			}
			
			cerrarConexion();
			return texto;
		} catch (Exception e) {
			System.out.println("No se han podido exportar las  unidades pedidas por artículo");
			cerrarConexion();
			return null;	
		}
	}
	
	
	
	public List<List<String>> exportarPedidoUnidadesPedidas(){
		try {
			establecerConexion();
			
			List<List<String>> texto = new ArrayList<List<String>>();
			
			Values valores = odb.getValues(new ValuesCriteriaQuery(LineaPedido.class).field("num_Pedido").sum("cantidad").groupBy("num_Pedido"));

			while (valores.hasNext()) {
				List<String> linea = new ArrayList<String>();
				
				ObjectValues ov = valores.next();
				BigDecimal cantidad = (BigDecimal) ov.getByIndex(1);

				linea.add((String)ov.getByIndex(0));
				linea.add(cantidad.toString());
				texto.add(linea);
			}

			
			cerrarConexion();
			return texto;
		} catch (Exception e) {
			System.out.println("No se han podido exportar las unidades pedidas por pedido");
			cerrarConexion();
			return null;
		}
	}
	
	/**
	 * Exporta todos los articulos solicitados y su cantidad solicitada
	 */
	public List<List<String>>exportarArticulosYCantidad() {
		try {
			establecerConexion();
			
			List<List<String>> texto = new ArrayList<List<String>>();

			Values valores = odb.getValues(new ValuesCriteriaQuery(LineaPedido.class).field("num_Articulo").sum("cantidad").groupBy("num_Articulo"));
			
			while (valores.hasNext()) {
				List<String> linea = new ArrayList<String>();
				
				ObjectValues ov = valores.next();
				BigDecimal cantidad = (BigDecimal) ov.getByIndex(1);

				linea.add((String)ov.getByIndex(0));
				linea.add(cantidad.toString());
				texto.add(linea);
			}
			
			cerrarConexion();
			return texto;
		} catch (Exception e) {
			System.out.println("Se ha producido un error exportando los articulos por cantidad de NeoDatis.");
			cerrarConexion();
			return null;
		}
	}
	
	/**
	 * Este método exporta la información actual de la bbdd SQL y la inserta en la bbdd NeoDatis
	 * Quizás habría que prevenir duplicados(?)
	 */
	public void sincronizacion() {
		cargaLineasPedido();
		cargarPedidos();	
	}
	
	/**
	 * Extrae los datos de los pedidos de la base de datos de sqlite y
	 * y los inserta en la base de datos de NeoDatis.
	 */
	public void cargarPedidos() {
		try {
			establecerConexion();
			
			List<Pedido> pedidos = Main.mSQL.exportarPedidos();
			
			if(pedidos == null) {
				System.out.println("No se han podido insertar los pedidos en la bbdd NeoDatis.");
				return;
			}
			
			for(Pedido p : pedidos) {
				odb.store(p);
			}
			
			System.out.println("Se han insertado los pedidos en la bbdd NeoDatis correctamente.");
			
			cerrarConexion();
		} catch (Exception e) {
			System.out.println("No se han podido insertar los pedidos en la bbdd NeoDatis.");
		}
	}
	
	public String numeroLineasPedido() {
		try {
			establecerConexion();
			
			BigInteger a = odb.count(new CriteriaQuery(LineaPedido.class));
			
			cerrarConexion();
			return a.toString();
		} catch (Exception e) {
			System.out.println("No se han podido contar las líneas de pedido");
			cerrarConexion();

			return null;
		}
		
	}
	
	/**
	 * Exporta toda la información de los pedidos con el siguiente formato:<br>
	 * Pedido       Cliente            Fecha<br>
	 * 9999       	 9999         		2024-01-22<br>
     *                                         		Artículo   Cantidad<br>
     *                                            	999999         99<br>
	 * @return
	 */
	public List<String> exportarInformeCompleto() {
		try {
			establecerConexion();
			
			List<String> texto = new ArrayList<String>();
			
			Objects<Pedido> objs = odb.getObjects(Pedido.class);

			String cabecera1 = String.format("%-12s %-18s %-10s\n", "Pedido", "Cliente", "Fecha");
			texto.add(cabecera1);
			
			for(Pedido p : objs) {
				
				String datosPedido = String.format("%-12s %-18s %-10s\n", p.getNumeroPedido(), p.getCliente().getNumeroCliente(), p.getFecha());
				texto.add(datosPedido);
				
				String cabecera2 = String.format("%-12s %-18s %-10s %12s %10s\n"," "," "," ",  "Artículo", "Cantidad");
				texto.add(cabecera2);
				
				for(Articulo a : p.getArticulos()) {
					String datosArticulo = String.format("%-12s %-18s %-10s %12s %10s\n"," "," "," ", a.getCodigo(), a.getCantidad());
					texto.add(datosArticulo);
				}
			}
			
			cerrarConexion();
			return texto;
		} catch (Exception e) {
			System.out.println("No se ha podido recuperar el informe completo de pedidos");
			cerrarConexion();
			return null;
		} 
		
	}
	
	
	/**
	 * Calcula la cantidad de artículos total dividida entre el número de pedidos
	 * @return
	 */
	public String mediaArticulosPorPedido() {
		try {
			establecerConexion();
			
			Values valoresSumados = odb.getValues(new ValuesCriteriaQuery(LineaPedido.class).sum("cantidad")); //Suma la cantidad total de artículos pedidos
			Values numeroPedidos = odb.getValues(new ValuesCriteriaQuery(Pedido.class).count("num_Pedido")); //Cuenta el número de pedidos que tenemos
			int numPedidos = 0;
			int cantidadArticulos = 0;
			
			//Solo lo debe hacer una vez puesto que es un valor único
			while (numeroPedidos.hasNext()) {
				ObjectValues ov = numeroPedidos.next();
				
				BigInteger nPedidos = (BigInteger) ov.getByIndex(0);
				numPedidos = nPedidos.intValue();
			}
			
			//Solo lo hace una vez puesto que es un valor único
			while (valoresSumados.hasNext()) {
				ObjectValues ov = valoresSumados.next();
				BigDecimal cantidad = (BigDecimal) ov.getByIndex(0);
				cantidadArticulos = cantidad.intValue();
			}

			cerrarConexion();
			return Integer.toString(cantidadArticulos/numPedidos);
			//return Integer.toString(rawMedia);
		} catch (Exception e) {
			System.out.println("No se ha podido calcular la media de artículos por pedido recibido");
			cerrarConexion();
			return null;
		}
	}
	
	public String numeroPedidosRecibidos() {
		try {
			establecerConexion();
			
			BigInteger a = odb.count(new CriteriaQuery(Pedido.class));
			
			cerrarConexion();
			return a.toString();
		} catch (Exception e) {
			System.out.println("No se han podido contar los pedidos procesados");
			cerrarConexion();
			return null;
		}
		
	}
	
	public List<List<String>> numeroPedidosPorCliente() {
		try {
			establecerConexion();
			
			List<List<String>> texto = new ArrayList<List<String>>();
			
			Values valores = odb.getValues(new ValuesCriteriaQuery(Pedido.class).field("cliente.numeroCliente").count("num_Pedido").groupBy("cliente.numeroCliente")); //Cantidad de articulos por pedido

			while (valores.hasNext()) {
				ObjectValues ov = valores.next();
				
				Object o = ov; 
				
				List<String> linea = new ArrayList<String>();
				
				linea.add((String) ov.getByIndex(0));
				
				BigInteger cantidad = (BigInteger) ov.getByIndex(1);
				
				linea.add(cantidad.toString());

				texto.add(linea);
			}
			
			cerrarConexion();
			return texto;
		} catch (Exception e) {
			System.out.println("No se ha podido contar el número de pedidos del cliente");
			cerrarConexion();
			return null;
		}
		
	}
	
	public List<List<String>> exportarPedidosPorArticulo(){
		try {
			establecerConexion();
			
			List<List<String>> texto = new ArrayList<List<String>>();
			
			
			Values valores = odb.getValues(new ValuesCriteriaQuery(LineaPedido.class).field("num_Articulo").count("num_Pedido").groupBy("num_Articulo")); //Cantidad de articulos por pedido

			while (valores.hasNext()) {
				ObjectValues ov = valores.next();
				
				List<String> linea = new ArrayList<String>();
				
				linea.add((String) ov.getByIndex(0));
				
				BigInteger cantidad = (BigInteger) ov.getByIndex(1);
				
				linea.add(cantidad.toString());

				texto.add(linea);
			}
			
			cerrarConexion();
			return texto;
		} catch (Exception e) {
			System.out.println("No se ha podido exportar el número de pedidos por artículo");
			return null;
		}
	}
	
	public List<Cliente> exportarClientes(){
		try {
			establecerConexion();
			
			Objects<Cliente> clientesExportado = odb.getObjects(Cliente.class);
			
			List<Cliente> clientes = new ArrayList<Cliente>();
			
			clientesExportado.forEach((C) -> {clientes.add(C);});
			
			cerrarConexion();
			return clientes;
		} catch (Exception e) {
			System.out.println("No se han podido exportar los clientes");
			return null;
		}
	}
	
	public List<List<String>> exportarPedidosTexto() {
		try {
			establecerConexion();
			
			List<List<String>> texto = new ArrayList<List<String>>();
			
			Values valores = odb.getValues(new ValuesCriteriaQuery(Pedido.class)
					.field("numeroPedido")
					.field("cliente.numeroCliente")
					.field("cliente.nombre")
					.field("cliente.apellidos")
					.field("cliente.empresa")
					.field("cliente.telefono")
					.field("fecha")); //Cantidad de articulos por pedido

			while (valores.hasNext()) {
				ObjectValues ov = valores.next();
				
				List<String> linea = new ArrayList<String>();
				
				linea.add((String) ov.getByIndex(0));
				linea.add((String) ov.getByIndex(1));
				linea.add((String) ov.getByIndex(2));
				linea.add((String) ov.getByIndex(3));
				linea.add((String) ov.getByIndex(4));
				linea.add((String) ov.getByIndex(5));
				linea.add((String) ov.getByIndex(6));

				texto.add(linea);
			}
			
			cerrarConexion();
			return texto;
		} catch (Exception e) {
			cerrarConexion();
			return null;
		}
	}
	
	public List<List<String>> exportarLineasDePedidoTexto(){
		try {
			establecerConexion();
			
			List<List<String>> texto = new ArrayList<List<String>>();
			
			Objects<LineaPedido> lineasPedidoExportado = odb.getObjects(LineaPedido.class);
			
			for(LineaPedido p : lineasPedidoExportado) {
				texto.add(p.exportarComoLinea());
			}
			
			cerrarConexion();
			return texto;
		} catch (Exception e) {
			cerrarConexion();
			return null;
		}
	}
	
	
	public List<LineaPedido> exportarLineasDePedido(){
		try {
			establecerConexion();
			
			List<LineaPedido> lineasPedido = new ArrayList<LineaPedido>();
			
			Objects<LineaPedido> lineasPedidoExportado = odb.getObjects(LineaPedido.class);
			
			for(LineaPedido p : lineasPedidoExportado) {
				lineasPedido.add(p);
			}
			
			cerrarConexion();
			return lineasPedido;
		} catch (Exception e) {
			cerrarConexion();
			return null;
		}
	}
	
	public List<Pedido> exportarPedidos(){
		try {
			establecerConexion();
			
			List<Pedido> pedidos = new ArrayList<Pedido>();
			
			Objects<Pedido> pedidosExportado = odb.getObjects(Pedido.class);
			
			for(Pedido p : pedidosExportado) {
				pedidos.add(p);
			}
			
			cerrarConexion();
			return pedidos;
		} catch (Exception e) {
			cerrarConexion();
			return null;
		}
	}
	
	public List<List<String>> exportarPedidoClienteFecha() {
		try {
			establecerConexion();
			
			List<List<String>> texto = new ArrayList<List<String>>();
			

			Values valores = odb.getValues(new ValuesCriteriaQuery(Pedido.class).field("numeroPedido").field("cliente.numeroCliente").field("fecha")); //Cantidad de articulos por pedido

			while (valores.hasNext()) {
				ObjectValues ov = valores.next();
				
				List<String> linea = new ArrayList<String>();
				
				linea.add((String) ov.getByIndex(0));
				linea.add((String) ov.getByIndex(1));
				linea.add((String) ov.getByIndex(2));
				texto.add(linea);
			}
			
			cerrarConexion();
			return texto;
		} catch (Exception e) {
			System.out.println("No se han podido exportar los pedidos por cliente y fecha");
			return null;
		}
	}
	
	
	/**
	 * Extrae los datos de las líneas de pedido de la base de datos de sqlite y
	 * y los inserta en la base de datos de NeoDatis.
	 */
	public void cargaLineasPedido() {
		try {
			establecerConexion();
			
			List<LineaPedido> lineas = Main.mSQL.exportarLineasPedido();
			
			if (lineas == null) {
				System.out.println("No se han podido insertar las líneas de pedido en la bbdd NeoDatis.");
				return;
			}
			
			for(LineaPedido lp : lineas) {
				odb.store(lp);
			}
			
			System.out.println("Se han insertado las líneas de pedido en la bbdd NeoDatis correctamente");
			
			cerrarConexion();	
		} catch (Exception e) {
			System.out.println("No se han podido insertar las líneas de pedido en la bbdd NeoDatis.");
		}
		
	}
	
	public void establecerConexion() {
		try {
			this.odb = ODBFactory.open(dbPath.getAbsolutePath());
		} catch (Exception e) {
			System.out.println("No se ha podido establecer conexión con la base de datos NeoDatis.");
		}
	}
	
	
	public void cerrarConexion() {
		try {
			odb.close();
		} catch (Exception e) {
			System.out.println("No se ha podido cerrar la conexión de la base de datos NeoDatis.");
		}
	}
	
	public void borrarLineasPedido() {
		establecerConexion();
		//Borramos lineas de pedido
		Objects lineas = odb.getObjects(LineaPedido.class);
		for (Object lp : lineas) {
			odb.delete(lp);
		}
		cerrarConexion();
	}
	
	public void borrarPedidos() {

		establecerConexion();
		//Borramos los pedidos
		Objects pedidos = odb.getObjects(Pedido.class);
		for (Object p : pedidos) {
			odb.delete(p);
		}
		
		//Al borrar los pedidos, no borra ni al ciente ni los articulos
		//Borramos clientes
		Objects clientes = odb.getObjects(Cliente.class);
		for(Object c : clientes) {
			odb.delete(c);
		}
		
		//Borramos los articulos
		Objects articulos = odb.getObjects(Articulo.class);
		for(Object a : articulos) {
			odb.delete(a);
		}
		cerrarConexion();
	}
	
	/**
	 * Borra todo lo que tiene la base de datos NeoDatis
	 */
	public void borrarBaseDeDatos() {
		try {
			borrarLineasPedido();
			borrarPedidos();
			System.out.println("Se ha borrado la base de datos NeoDatis con éxito");
		} catch (Exception e) {
			System.out.println("Ha surgido un error borrando la base de datos");
		}
	}
	
}
