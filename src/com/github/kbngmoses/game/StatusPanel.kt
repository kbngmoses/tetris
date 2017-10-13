package com.github.kbngmoses.game

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Font
import java.awt.GraphicsEnvironment
import java.io.File
import javax.swing.JLabel
import javax.swing.JPanel

class StatusPanel : JPanel(), GameCtrl.OnScoreChangeListener {

    val mScoreTextLabel = JLabel()

    override fun onScoreChanged(oldScore: Int, currentScore: Int) {
        displayScore(currentScore)
    }

    override fun getPreferredSize(): Dimension {
        return Dimension(300, 700)
    }

    private fun displayScore(score: Int) {
        mScoreTextLabel.text = "Score: $score"
    }

    private fun setFonts() {
        mScoreTextLabel.font = Tetris.font()
    }

    init {
        setFonts()
        layout = BorderLayout()
        add(mScoreTextLabel, BorderLayout.NORTH)
        displayScore(0)
    }
}