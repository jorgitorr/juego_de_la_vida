import java.util.concurrent.TimeUnit

class GameOfLife(val rows: Int, val cols: Int) {
    var modelo = Conexion()
    var generacion = -1
    var totalCelulasMuertas = 0

    var board = Array(rows) { BooleanArray(cols) }
    // Inicializa el tablero con células vivas de manera aleatoria
    fun initializeRandomBoard() {
        //incializa BDD
        modelo.conectarBD()
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                board[i][j] = Math.random() < 0.5
            }
        }
    }

    // Calcula la siguiente generación del Juego de la Vida
    fun nextGeneration() {
        val newBoard = Array(rows) { BooleanArray(cols) }
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val aliveNeighbors = countAliveNeighbors(i, j)
                if (board[i][j]) {
                    // Célula viva con menos de 2 o más de 3 vecinos vivos muere (soledad o superpoblación)
                    newBoard[i][j] = aliveNeighbors == 2 || aliveNeighbors == 3
                } else {
                    // Célula muerta con exactamente 3 vecinos vivos se convierte en viva (reproducción)
                    newBoard[i][j] = aliveNeighbors == 3
                }
            }
        }

        // Actualiza el tablero con la nueva generación
        board = newBoard

    }

    // Cuenta el número de vecinos vivos alrededor de una célula
    private fun countAliveNeighbors(row: Int, col: Int): Int {
        var count = 0
        for (i in -1..1) {
            for (j in -1..1) {
                val newRow = (row + i + rows) % rows
                val newCol = (col + j + cols) % cols
                count += if (board[newRow][newCol]) 1 else 0
            }
        }
        // Restar la célula central ya que fue contada dos veces
        count -= if (board[row][col]) 1 else 0
        return count
    }

    // Imprime el tablero en la consola
    fun printBoard() {
        generacion++
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                if (board[i][j]){
                    print("■ ")
                    modelo.ingresarCelulas(generacion.toString(),i.toString(),j.toString())
                } else
                    print("□ ")

            }
            println()
        }

        modelo.celulasVivasXGeneracion(generacion.toString())
        totalCelulasMuertas += modelo.getCelulasMuertasXGeneracion(generacion.toString())
        println("Numero de celulas muertas en la generacion $generacion:" + modelo.getCelulasMuertasXGeneracion(generacion.toString()))


    }
}


fun main() {
    val game = GameOfLife(rows = 10, cols = 10)
    game.modelo.conectarBD()
    game.modelo.crearTabla1()
    game.modelo.crearTabla2()
    game.modelo.ingresarTamanio(game.rows, game.cols)

    // Inicializa el tablero de manera aleatoria
    game.initializeRandomBoard()

    // Ejecuta el juego durante algunas generaciones
    repeat(10) {
        println("Generación: $it")
        game.printBoard()
        game.nextGeneration()
        TimeUnit.SECONDS.sleep(1) // Pausa de 1 segundo entre generaciones
    }

    println("total celulas muertas: " + game.totalCelulasMuertas)
    val celulasMuertaCuadrado = game.modelo.numCelulasVivasCuadrado("1",1,9,3,9);
    println("El numero de celulas muertas en de generacion: " +
            "1 en un cuadrado de x=(1,9) e y=(3,9) son $celulasMuertaCuadrado")
}



