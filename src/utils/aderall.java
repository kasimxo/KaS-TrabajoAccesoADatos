public void step3() throws Exception {
    ODB odb = null;
 
    try {
        // Open the database
        odb = ODBFactory.open(ODB_NAME);
 
        IQuery query = new CriteriaQuery(Player.class,     Restrictions.equal("name", "olivier")); //para las tipicas restricciones
        Values valores3 = odb.getValues(new ValuesCriteriaQuery(Jugadores.class).min("edad").count("nombre").max("edad")); // Prefiero los valuescriteria

        // Se crea una consulta para sumar todas las cantidades de ventas.
	      IValuesQuery query = new ValuesCriteriaQuery(Venta.class).sum("cantidad");

      // Se crea una consulta que agrupa las ventas por color y suma las cantidades.
	        IValuesQuery query = new ValuesCriteriaQuery(Venta.class)
	                .field("color")
	                .sum("cantidad")
	                .groupBy("color");
 
        Objects players = odb.getObjects(query);
 
        System.out.println("\nStep 3 : Players with name olivier");
 
        // display each object
        while(players.hasNext()) {
            System.out.println((i + 1) + "\t: " + players.next());
            }
    } finally {
        if (odb != null) {
            // Close the database
            odb.close();
        }
    }
}

	public static void leerVentasDesdeXML() {
       
        try {
        	ODB odb = ODBFactory.open("NeodatisDB/ExamDB1.odb");
            File xmlFile = new File("xml/ventas.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("venta");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    Venta venta = new Venta();
                    venta.setFecha(eElement.getElementsByTagName("fecha").item(0).getTextContent());
                    venta.setProducto(eElement.getElementsByTagName("producto").item(0).getTextContent());
                    venta.setTalla(eElement.getElementsByTagName("talla").item(0).getTextContent());
                    venta.setColor(eElement.getElementsByTagName("color").item(0).getTextContent());
                    venta.setPrecio(Integer.parseInt(eElement.getElementsByTagName("precio").item(0).getTextContent()));
                    venta.setCantidad(Integer.parseInt(eElement.getElementsByTagName("cantidad").item(0).getTextContent()));

                    odb.store(venta);
                }
            }
            odb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }


// Transferimos los datos de la base de datos de neodatis a la base de datos de SQLite
		public static void transferirVentasASQLite() {
	        ODB odb = null;
	        try {
	            // Se abre Neodatis DB y recuperar objetos Venta
	            odb = ODBFactory.open("NeodatisDB/ExamDB1.odb");
	            Objects<Venta> ventas = odb.getObjects(Venta.class);

	            // Preparar la sentencia SQL para insertar en SQLite
	            String sql = "INSERT INTO Ventas (fecha, producto, talla, color, precio, cantidad) VALUES (?, ?, ?, ?, ?, ?)";
	            PreparedStatement pstmt = con.prepareStatement(sql);

	            //Se Inserta cada objeto Venta en SQLite
	            while (ventas.hasNext()) {
	                Venta venta = ventas.next();
	                pstmt.setString(1, venta.getFecha());
	                pstmt.setString(2, venta.getProducto());
	                pstmt.setString(3, venta.getTalla());
	                pstmt.setString(4, venta.getColor());
	                pstmt.setInt(5, venta.getPrecio());
	                pstmt.setInt(6, venta.getCantidad());
	                pstmt.executeUpdate();
	            }

	            System.out.println("\nTransferencia completada con éxito.");

	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            if (odb != null) {
	                odb.close();
	            }
	        }
	    }


/*
Consultas SQL
String sql = "SELECT producto, SUM(cantidad) AS total_unidades FROM Ventas GROUP BY producto ORDER BY total_unidades DESC LIMIT 1";
String sql = "SELECT color, SUM(cantidad) AS total_unidades_por_color FROM Ventas GROUP BY color";

*/

/*
equal 	    Returns an EqualCriterion
like 	      to execute a like
ilike 	    To execute a case insentive like
gt 	        A greater than criterion
ge 	        A greater or Equal criterion
lt 	        A lesser than criterion
le 	        A lesser or equal criterio
contain 	  To search in list. Returns true if a list contains a specific element
isNull 	    Returns true if the object is null
isNotNull 	Returns true if Object is not null
sizeEq 	    Returns true if the size of a list or array is equal to
sizeGt 	    Returns true if the size of a list or array is greater than
sizeGe 	    Returns true if the size of a list or array is greater or equal to
sizeLt 	    Returns true if the size of a list or array is lesser than
sizeLe 	    Returns true if the size of a list or array is lesser or equal to
or 	        To combine criterion with or : return criterio1 or criterio2 or criterion3
and 	      To combine criterion with and : return criterio1 and criterio2 and criterion3
not 	      To negate criterion

*/
