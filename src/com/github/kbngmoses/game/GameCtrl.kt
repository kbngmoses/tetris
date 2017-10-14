package com.github.kbngmoses.game

import com.github.kbngmoses.game.sound.SoundManager
import com.github.kbngmoses.game.tetrominoe.NoShape
import com.github.kbngmoses.game.tetrominoe.OShape
import com.github.kbngmoses.game.tetrominoe.Tetrominoe
import java.awt.Color
import java.awt.Graphics
import java.awt.event.*
import java.util.*
import javax.swing.JPanel
import javax.swing.Timer


class GameCtrl(onScoreChangeListener: OnScoreChangeListener) : JPanel(),
    ActionListener, FocusListener {

    var mPieces: Array<Tetrominoe> = Array(nColumns * nRows, { NoShape() })
    var mFallingPiece: Tetrominoe = mPieces[0]
    var doneFalling = false

    private val mTimer = Timer(REPAINT_INTERVAL, this)
    private val mOnScoreChangeListener = onScoreChangeListener
    private var paused = false
    private var playing = false

    private var mScore = 0

    var mXCurr = 0
    var mYCurr = 0

    init {
        addKeyListener(KeyPressHandler())
        addFocusListener(this)
        isFocusable = true
        start()
    }

    override fun actionPerformed(e: ActionEvent) {
        if (playing) {
            if (doneFalling) {
                newPiece()
            } else {
                autoMove()
            }
        }
    }

    override fun focusLost(e: FocusEvent) {
        pause()
    }

    override fun focusGained(e: FocusEvent) {
        start()
    }

    override fun paint(g: Graphics) {
        super.paint(g)

        g.fillRect(0, 0, panelWidth, panelHeight)

        val top = panelHeight - nRows * brickHeight

        (0 until nRows).flatMap { i ->
            (0 until nColumns).map { j ->
                    val piece = resolvePiece(j, nRows - i - 1)
                    PiecePosition(piece, j * brickWidth, top + i * brickHeight)}}
                .filterNot { it -> it.piece is NoShape }
                .forEach { piecePosition ->
                    piecePosition.piece.renderStatic(g, piecePosition.xPos, piecePosition.yPos) }


        Optional.of(mFallingPiece)
                .filter{piece -> piece !is NoShape}
                .ifPresent {
                    mFallingPiece.renderMove(g, mXCurr, mYCurr, top)
                }

        // render text in front of everything
        if (paused) {
            g.font  = Tetris.font()
            val fm = g.fontMetrics
            val r = fm.getStringBounds(TEXT_PAUSED, g)
            val x = (panelWidth - (r.width).toInt()) / 2
            val y = (panelHeight - (r.height).toInt()) / 2 + fm.ascent
            g.color = Color.YELLOW
            g.drawString(TEXT_PAUSED, x, y)
        }

        g.dispose()
    }

    private fun start() {
        mTimer.start()

        if (!paused) {
            mScore = 0
            gameStartSound.play(false)
            newPiece()
        }

        paused = false
        playing = true
    }

    private fun pause() {
        mTimer.stop()
        paused = true
        playing = false
        repaint()
    }

    private fun autoMove() {
        Optional.of(mFallingPiece).filter { piece ->
            !tryPosition(piece, mXCurr, mYCurr - 1)
        }.ifPresent {
            stackToBottom()
            repaint()
            pieceFalling.play(false)
        }
    }

    private fun stackToBottom() {

        mFallingPiece.bricks .forEach { brick ->
            val x = mXCurr + brick.x
            val y = mYCurr - brick.y
            mPieces[y * nColumns + x] = mFallingPiece
        }

        computeScore()

        // TODO: Check if it's not game over
        newPiece()
    }

    private fun isFullRow(row: Int): Boolean =
        (0 until nColumns).none{ col -> resolvePiece(col, row) is NoShape}


    private fun clearRow(row: Int) {
        (0 until nColumns).forEach { col ->
            mPieces[row * nColumns + col] = resolvePiece(col, row + 1)
        }
    }

    private fun clearRowsBelow(maxRow: Int) {
        (maxRow until nRows - 1).forEach { row ->
            clearRow(row)
        }
    }

    private fun updateScore(numFullLines: Int) {
        val oldScore = mScore
        mScore += numFullLines
        doneFalling = true
        mFallingPiece = NoShape()
        mOnScoreChangeListener.onScoreChanged(oldScore, mScore)
        repaint()
    }

    private fun computeScore() {
        // determine if we've full rows
        val fullRows = IntProgression
                .fromClosedRange(nRows - 1, 0, -1)
                .filter{row -> isFullRow(row)}
        // number of full rows
        val fullCount = fullRows.count()
        // clear full rows
        fullRows.forEach{row -> clearRowsBelow(row)}
        // play appropriate sound
        when (fullCount) {
            1 -> lineRemovedSound.play(false)
            2 or 3 -> lineRemoved4Sound.play(false)
            4 -> wonderfulVoice.play(false)
        }
        // update score if necessary
        if (fullCount > 0) {
           updateScore(fullCount)
        }
    }

    private fun newPiece() {
        mXCurr = nColumns / 2 - 1
        mYCurr = nRows - 1 + mFallingPiece.top()
        mFallingPiece = Tetrominoe.randomPiece()
        doneFalling = false
    }

    private fun resolvePiece(row: Int, col: Int) = mPieces[(col * nColumns) + row]

    private fun acceptPiecePos(piece: Tetrominoe, xNew: Int, yNew: Int) {
        mFallingPiece = piece
        mXCurr = xNew
        mYCurr = yNew
        repaint()
    }

    private fun tryPosition(piece: Tetrominoe, xNew: Int, yNew: Int): Boolean {

        val isValidPos = piece.bricks
                .all { brick ->
                    val x = xNew + brick.x
                    val y = yNew - brick.y
                    (x in 0..(nColumns - 1) && y in 0..(nRows - 1)) && resolvePiece(x, y) is NoShape
                }

        if (isValidPos) {
            acceptPiecePos(piece, xNew, yNew)
        }

        return isValidPos

    }

    inner class KeyPressHandler : KeyAdapter() {

        private fun advanceLeft() {
            if (tryPosition(mFallingPiece, mXCurr - 1, mYCurr)) {
                buttonLRSound.play(false)
            }
        }

        private fun advanceRight() {
            if (tryPosition(mFallingPiece, mXCurr + 1, mYCurr)) {
                buttonLRSound.play(false)
            }
        }

        private fun rotateRight() {
            if (mFallingPiece is OShape)
                return
            if (tryPosition(Tetrominoe.rotateRight(mFallingPiece), mXCurr, mYCurr)) {
                pieceRotate.play(false)
            } else {
                pieceRotateFail.play(false)
            }
        }

        private fun rotateLeft() {
            if (mFallingPiece is OShape)
                return
            if (tryPosition(Tetrominoe.rotateLeft(mFallingPiece), mXCurr, mYCurr)) {
                pieceRotate.play(false)
            } else {
                pieceRotateFail.play(false)
            }
        }

        private fun togglePlayPause() {
            if (playing) {
                pause()
            } else {
                start()
            }
        }

        override fun keyReleased(e: KeyEvent?) {
            buttonUpSound.play(false)
        }

        override fun keyPressed(e: KeyEvent) {

            if (mFallingPiece is NoShape)
                return

            when (e.keyCode) {
                KeyEvent.VK_LEFT  -> { advanceLeft() }
                KeyEvent.VK_RIGHT -> { advanceRight() }
                KeyEvent.VK_UP    -> { rotateLeft() }
                KeyEvent.VK_DOWN  -> { rotateRight() }
                KeyEvent.VK_P     -> { togglePlayPause() }
                else -> println("Unhandled input")
            }

            buttonUpSound.play(false)
        }
    }

    inner class PiecePosition(val piece: Tetrominoe, val xPos: Int, val yPos: Int)

    interface OnScoreChangeListener {
        fun onScoreChanged(oldScore: Int, currentScore: Int)
    }

    companion object {

        val nColumns = 16
        val nRows = 28

        val panelWidth = 400
        val panelHeight = 700

        val brickWidth = panelWidth / nColumns
        val brickHeight = panelHeight / nRows

        val REPAINT_INTERVAL = 150

        // TODO: translate this text (internationalization feature)
        val TEXT_PAUSED = "PAUSED"

        val gameStartSound = SoundManager.GameStart()
        val buttonUpSound = SoundManager.ButtonUp()
        val buttonLRSound = SoundManager.ButtonLR()
        val pieceFalling  = SoundManager.PieceDown()
        val pieceRotate   = SoundManager.PieceRotate()
        val lineRemoved4Sound = SoundManager.LineRemoval4()
        val lineRemovedSound  = SoundManager.LineRemoval()
        val pieceRotateFail   = SoundManager.PieceRotateFail()
        val wonderfulVoice    = SoundManager.WonderfulVoice()
    }
}