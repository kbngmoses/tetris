package com.github.kbngmoses.game

import com.github.kbngmoses.game.tetominoe.NoShape
import com.github.kbngmoses.game.tetominoe.Tetrominoe
import java.awt.Graphics
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JPanel
import javax.swing.Timer


class GameCtrl : JPanel(), ActionListener {

    var board: Array<Tetrominoe> = Array(nColumns * nRows, { NoShape() })
    var currentPiece: Tetrominoe = board[0]
    var isDoneFalling = false

    private val timer = Timer(REPAINT_INTERVAL, this)

    private var score = 0

    var xCurrent = 0
    var yCurrent = 0

    init {
        addKeyListener(KeyPressHandler())
        isFocusable = true
        start()
    }

    override fun actionPerformed(e: ActionEvent) {
        if (isDoneFalling) {
            newPiece()
        } else {
            autoMove()
        }
    }

    override fun paint(g: Graphics) {
        super.paint(g)

        g.fillRect(0, 0, panelWidth, panelHeight)

        val top = panelHeight - nRows * squareHeight()


        for (i in 0 until nRows) {
            for (j in 0 until nColumns) {
                val shape = shapeAt(j, nRows - i - 1)
                if (shape !is NoShape) {
                    shape.renderStatic(g, j * squareWidth(), top + i * squareHeight())
                }
            }
        }

        if (currentPiece !is NoShape) {
            currentPiece.renderMove(g, xCurrent, yCurrent, top)
        }

        g.dispose()
    }

    private fun squareWidth() = panelWidth / nColumns
    private fun squareHeight() = panelHeight / nRows

    private fun start() {
        newPiece()
        score = 0
        timer.start()
    }

    private fun autoMove() {
        if (!mayAdvance(currentPiece, xCurrent, yCurrent - 1))
            stackToBottom()
        repaint()
    }

    private fun stackToBottom() {

        for (i in 0..3) {
            val brick = currentPiece.brickAt(i)
            val x = xCurrent + brick.x
            val y = yCurrent - brick.y

            board[y * nColumns + x] = currentPiece
        }

        computeScore()

        if (!isDoneFalling)
            newPiece()
    }

    private fun computeScore() {

        var numFullLines = 0

        for (i in nRows - 1 downTo 0) {

            val lineIsFull = (0 until nColumns).none { shapeAt(it, i) is NoShape }

            if (lineIsFull) {
                ++numFullLines
                for (k in i until nRows - 1) {
                    for (j in 0 until nColumns)
                        board[k * nColumns + j] = shapeAt(j, k + 1)
                }
            }
        }

        if (numFullLines > 0) {
            score += numFullLines
            isDoneFalling = true
            currentPiece = NoShape()
            repaint()
        }
    }

    private fun newPiece() {
        xCurrent = nColumns / 2 - 1
        yCurrent = nRows - 1 + currentPiece.top()
        currentPiece = Tetrominoe.randomPiece(squareWidth(), squareHeight())
        isDoneFalling = false
    }

    private fun shapeAt(row: Int, col: Int) = board[(col * nColumns) + row]

    private fun mayAdvance(piece: Tetrominoe, xNew: Int, yNew: Int): Boolean {

        for (i in 0 until 4) {
            val brick = piece.brickAt(i)
            val x = xNew + brick.x
            val y = yNew - brick.y

            if (x < 0 || x >= nColumns || y < 0 || y >= nRows)
                return false
            if (shapeAt(x, y) !is NoShape)
                return false
        }

        currentPiece = piece
        xCurrent = xNew
        yCurrent = yNew
        repaint()
        return true
    }

    inner class KeyPressHandler : KeyAdapter() {
        override fun keyPressed(e: KeyEvent) {

            if (currentPiece is NoShape)
                return

            when (e.keyCode) {
                KeyEvent.VK_LEFT -> mayAdvance(currentPiece, xCurrent - 1, yCurrent)
                KeyEvent.VK_RIGHT -> mayAdvance(currentPiece, xCurrent + 1, yCurrent)
                KeyEvent.VK_UP -> mayAdvance(currentPiece.rotateLeft(), xCurrent, yCurrent)
                KeyEvent.VK_DOWN -> mayAdvance(currentPiece.rotateRight(), xCurrent, yCurrent)
                else -> println("Unhandled input")
            }
        }
    }

    companion object {

        val nColumns = 16
        val nRows = 28

        val panelWidth = 400
        val panelHeight = 700

        val REPAINT_INTERVAL = 150
    }
}