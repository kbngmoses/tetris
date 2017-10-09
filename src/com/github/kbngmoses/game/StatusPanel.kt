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
        var font = Font.createFont(Font.TRUETYPE_FONT,
                File(".${File.separator}resource${File.separator}font${File.separator}neuropolregular.ttf"))
        font = font.deriveFont(Font.BOLD, 24.0f)
        val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
        ge.registerFont(font)
        mScoreTextLabel.font = font
    }

    init {
        setFonts()
        layout = BorderLayout()
        add(mScoreTextLabel, BorderLayout.NORTH)
        displayScore(0)
    }
}