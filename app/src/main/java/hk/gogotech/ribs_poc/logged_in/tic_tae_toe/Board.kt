package hk.gogotech.ribs_poc.logged_in.tic_tae_toe

import javax.inject.Inject

open class Board
@Inject
constructor() {
    var cells: Array<Array<MarkerType?>> = arrayOf()
    var currentRow: Int = 0
    var currentCol: Int = 0

    /**
     * Return true if it is a draw (i.e., no more EMPTY cell)
     */
    val isDraw: Boolean
        get() {
            for (row in 0 until ROWS) {
                for (col in 0 until COLS) {
                    if (cells[row][col] == null) {
                        return false
                    }
                }
            }
            return !hasWon(MarkerType.CROSS) && !hasWon(MarkerType.NOUGHT)
        }

    init {
        //Array(ROWS) { Array(COLS) }
        for (row in 0 until ROWS) {
            for (col in 0 until COLS) {
                cells[row][col] = null
            }
        }
    }

    /**
     * Return true if the player with "theSeed" has won after placing at (currentRow, currentCol)
     */
    fun hasWon(theSeed: MarkerType): Boolean {
        return ((cells[currentRow][0] == theSeed
                && cells[currentRow][1] == theSeed
                && cells[currentRow][2] == theSeed)
                || (cells[0][currentCol] == theSeed
                && cells[1][currentCol] == theSeed
                && cells[2][currentCol] == theSeed)
                || (currentRow == currentCol
                && cells[0][0] == theSeed
                && cells[1][1] == theSeed
                && cells[2][2] == theSeed)
                || (currentRow + currentCol == 2
                && cells[0][2] == theSeed
                && cells[1][1] == theSeed
                && cells[2][0] == theSeed))
    }

    enum class MarkerType {
        CROSS,
        NOUGHT
    }

    companion object {
        val ROWS = 3
        val COLS = 3
    }
}
