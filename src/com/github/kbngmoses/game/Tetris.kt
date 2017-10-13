package com.github.kbngmoses.game

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Font
import java.awt.GraphicsEnvironment
import java.io.File
import javax.swing.JComponent
import javax.swing.JFrame

class Tetris : JFrame() {

    init {

        val statusPanel = StatusPanel()
        add(statusPanel, BorderLayout.EAST)
        add(GameCtrl(statusPanel))
        size = Dimension(700, 700)
        // isResizable = false
        title = "Tetris"
        defaultCloseOperation = EXIT_ON_CLOSE
    }

    companion object {
        fun font(): Font {
            var font = Font.createFont(Font.TRUETYPE_FONT,
                    File(".${File.separator}resource${File.separator}font${File.separator}neuropolregular.ttf"))
            font = font.deriveFont(Font.BOLD, 24.0f)
            val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
            ge.registerFont(font)
            return font
        }
    }
}