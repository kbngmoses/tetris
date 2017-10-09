package com.github.kbngmoses.game

import java.awt.BorderLayout
import java.awt.Dimension
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
}