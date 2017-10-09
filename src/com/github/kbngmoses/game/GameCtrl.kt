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


class GameCtrl(onScoreChangeListener: OnScoreChangeListener) : JPanel(), ActionListener {

    var mPieces: Array<Tetrominoe> = Array(nColumns * nRows, { NoShape() })
    var mFallingPiece: Tetrominoe = mPieces[0]
    var doneFalling = false

    private val mTimer = Timer(REPAINT_INTERVAL, this)
    private val mOnScoreChangeListener = onScoreChangeListener

    private var mScore = 0

    var mXCurr = 0
    var mYCurr = 0

    init {
        addKeyListener(KeyPressHandler())
        isFocusable = true
        start()
    }

    override fun actionPerformed(e: ActionEvent) {
        if (doneFalling) {
            newPiece()
        } else {
            autoMove()
        }
    }

    override fun paint(g: Graphics) {
        super.paint(g)

        g.fillRect(0, 0, panelWidth, panelHeight)

        val top = panelHeight - nRows * brickHeight()


        for (i in 0 until nRows) {
            for (j in 0 until nColumns) {
                val shape = resolvePiece(j, nRows - i - 1)
                if (shape !is NoShape) {
                    shape.renderStatic(g, j * brickWidth(), top + i * brickHeight())
                }
            }
        }

        if (mFallingPiece !is NoShape) {
            mFallingPiece.renderMove(g, mXCurr, mYCurr, top)
        }

        g.dispose()
    }

    private fun brickWidth() = panelWidth / nColumns
    private fun brickHeight() = panelHeight / nRows

    private fun start() {
        newPiece()
        mScore = 0
        mTimer.start()
    }

    private fun autoMove() {
        if (!mayAdvance(mFallingPiece, mXCurr, mYCurr - 1))
            stackToBottom()
        repaint()
    }

    private fun stackToBottom() {

        for (i in 0..3) {
            val brick = mFallingPiece.brickAt(i)
            val x = mXCurr + brick.x
            val y = mYCurr - brick.y

            mPieces[y * nColumns + x] = mFallingPiece
        }

        computeScore()

        if (!doneFalling)
            newPiece()
    }

    private fun computeScore() {

        var numFullLines = 0

        for (i in nRows - 1 downTo 0) {

            val lineIsFull = (0 until nColumns).none { resolvePiece(it, i) is NoShape }

            if (lineIsFull) {
                ++numFullLines
                for (k in i until nRows - 1) {
                    for (j in 0 until nColumns)
                        mPieces[k * nColumns + j] = resolvePiece(j, k + 1)
                }
            }
        }

        if (numFullLines > 0) {
            val oldScore = mScore
            mScore += numFullLines
            mOnScoreChangeListener.onScoreChanged(oldScore, mScore)
            doneFalling = true
            mFallingPiece = NoShape()
            repaint()
        }
    }

    private fun newPiece() {
        mXCurr = nColumns / 2 - 1
        mYCurr = nRows - 1 + mFallingPiece.top()
        mFallingPiece = Tetrominoe.randomPiece(brickWidth(), brickHeight())
        doneFalling = false
    }

    private fun resolvePiece(row: Int, col: Int) = mPieces[(col * nColumns) + row]

    private fun mayAdvance(piece: Tetrominoe, xNew: Int, yNew: Int): Boolean {

        for (i in 0 until 4) {
            val brick = piece.brickAt(i)
            val x = xNew + brick.x
            val y = yNew - brick.y

            if (x < 0 || x >= nColumns || y < 0 || y >= nRows)
                return false
            if (resolvePiece(x, y) !is NoShape)
                return false
        }

        mFallingPiece = piece
        mXCurr = xNew
        mYCurr = yNew
        repaint()
        return true
    }

    inner class KeyPressHandler : KeyAdapter() {
        override fun keyPressed(e: KeyEvent) {

            if (mFallingPiece is NoShape)
                return

            when (e.keyCode) {
                KeyEvent.VK_LEFT -> mayAdvance(mFallingPiece, mXCurr - 1, mYCurr)
                KeyEvent.VK_RIGHT -> mayAdvance(mFallingPiece, mXCurr + 1, mYCurr)
                KeyEvent.VK_UP -> mayAdvance(mFallingPiece.rotateLeft(), mXCurr, mYCurr)
                KeyEvent.VK_DOWN -> mayAdvance(mFallingPiece.rotateRight(), mXCurr, mYCurr)
                else -> println("Unhandled input")
            }
        }
    }

    interface OnScoreChangeListener {
        fun onScoreChanged(oldScore: Int, currentScore: Int)
    }

    companion object {

        val nColumns = 16
        val nRows = 28

        val panelWidth = 400
        val panelHeight = 700

        val REPAINT_INTERVAL = 150
    }
}