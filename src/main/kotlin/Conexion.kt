import java.sql.*



class Conexion {

    var conexion: Connection? = null

    init {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver")
        } catch (e: ClassNotFoundException) {
            throw SQLException("La base de datos no esta encendida")
        }
    }


    fun conectarBD(): Connection {
        val direccionBD = "jdbc:mysql://localhost:3306/venta"
        val usuario = "root"
        val contrasenia = ""
        try {
            if (conexion == null || conexion!!.isClosed) {
                conexion = DriverManager.getConnection(direccionBD, usuario, contrasenia)
                println("Bienvenido")
            }
            return conexion!!
        } catch (e: SQLException) {
            throw e
        }
    }


    fun cerrarBD() {
        try {
            conexion?.close()
        } catch (e: SQLException) {
            throw e
        }
    }

    fun crearTabla1(){
        try {
            val tabla1 = "CREATE OR REPLACE TABLE tabla("+
                    "    generacion VARCHAR(50),\n" +
                    "    x VARCHAR(50),\n" +
                    " y VARCHAR(50)" +
                    ");"
            val creaTabla1: PreparedStatement = conexion!!.prepareStatement(tabla1)
            creaTabla1.executeUpdate()
        }catch (e: Exception){
            System.err.println("Error de creacion de tabla 1")
        }

    }

    fun crearTabla2(){
        try {
            val tabla2 = "CREATE OR REPLACE TABLE tamaniotablas(" +
                    "    fila INT,\n" +
                    "    columna INT\n" +
                    ")"
            val creaTabla2: PreparedStatement = conexion!!.prepareStatement(tabla2)
            creaTabla2.executeUpdate()
        }catch (e: Exception){
            System.err.println("Error de creacion de tabla 2")
        }
    }


    fun ingresarCelulas(generacion:String, x:String, y:String){
        try {
            val insertar = "INSERT INTO tabla(generacion,x,y) VALUES(?, ? ,?)"
            val insercion: PreparedStatement = conexion!!.prepareStatement(insertar)
            insercion.setString(1,generacion)
            insercion.setString(2, x)
            insercion.setString(3, y)
            insercion.executeUpdate()
        }catch (e:Exception){
            System.err.println("Error de ingreso en tablas")
        }

    }

    fun ingresarTamanio( fila:Int, columna:Int){
        try{
            val insertar = "INSERT INTO tamaniotablas(fila,columna) VALUES(?,?)"

            val insercion: PreparedStatement = conexion!!.prepareStatement(insertar)
            insercion.setString(1,fila.toString())
            insercion.setString(2, columna.toString())
            insercion.executeUpdate()
        }catch (e:Exception){
            System.err.println("Error de ingreso en tamanioTablas")
        }
    }


    /**
     * Celulas vivas por generacion
     */
    fun celulasVivasXGeneracion(generacion: String) {
        try {
            val consulta = "SELECT COUNT(*) AS num FROM tabla WHERE generacion = ? GROUP BY generacion"
            val insert: PreparedStatement = conexion!!.prepareStatement(consulta)
            insert.setString(1, generacion)
            val valor = insert.executeQuery()


            if (valor.next()) {
                val numCelulasVivas = valor.getInt("num")
                println("Número de celulas vivas en la generacion $generacion: $numCelulasVivas")
            } else {
                println("No se encontraron resultados para la generación: $generacion")
            }

        } catch (e: Exception) {
            e.printStackTrace()
            println("Error: ${e.message}")
        }
    }


    /**
     * Imprime el numero de Celulas muertas x generacion
     * @param generacion
     */
    fun celulasMuertasXGeneracion(generacion: String){
        try {
            var consulta = "SELECT t.fila*t.columna\n" +
                    "- (SELECT COUNT(*) FROM tabla WHERE generacion = ? GROUP BY generacion) AS resultado\n" +
                    "FROM tamaniotablas t;"
            val select: PreparedStatement = conexion!!.prepareStatement(consulta)
            select.setString(1,generacion)
            val result = select.executeQuery()

            if(result.next()){
                val numCelulasMuertas = result.getInt("resultado")
                println("Num de celulas muertas en la $generacion: $numCelulasMuertas")
            }
        }catch (e:Exception){
            println(e.message)
        }
    }


    /**
     * Devuelve el valor de las celulas muertas por generacion
     * @param generacion
     * @return num Celulas
     */
    fun getCelulasMuertasXGeneracion(generacion: String): Int {
        var resultado = 0
        try {
            // Use an alias for the calculated result
            val consulta =
                "SELECT t.fila * t.columna - (SELECT COUNT(*) FROM tabla WHERE generacion = ?) AS resultado FROM tamaniotablas t;"
            val select: PreparedStatement = conexion!!.prepareStatement(consulta)
            select.setString(1, generacion)
            val result = select.executeQuery()

            // Check if there is a result before trying to retrieve it
            if (result.next()) {
                resultado = result.getInt("resultado")
            }
        } catch (e: Exception) {
            println("Error de consulta")
        }
        return resultado
    }


    /**
     * Devuelve el num de celulas vivas en un cuadrado de los parametros pasados
     * @param generacion
     * @param x1 primer parametro de x
     * @param x2 segundo parametro de x
     * @param y1 primer parametro de y
     * @param y2 segundo parametro de y
     * @return num de celulas de esa generacion que viven en el cuadrado con las dimensiones pasadas por parametros
     */
    fun numCelulasVivasCuadrado(generacion: String, x1:Int, x2:Int, y1: Int, y2: Int): Int{
        var resultado = 0
        try {
            val consulta = "SELECT COUNT(*) AS num FROM tabla" +
                    "WHERE generacion = ?" +
                    "AND x BETWEEN ? AND ? AND y BETWEEN ? AND ?;"
            val select: PreparedStatement = conexion!!.prepareStatement(consulta)
            select.setString(1,generacion)
            select.setInt(2,x1)
            select.setInt(3,x2)
            select.setInt(4,y1)
            select.setInt(5,y2)
            val result = select.executeQuery()

            if(result.next()){
                resultado = result.getInt("num")
            }
        }catch (e:Exception){
            println("Error al hacer la consulta")
        }

        return resultado
    }

}