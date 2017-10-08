package com.github.kbngmoses.game.tetominoe

import com.github.kbngmoses.game.GameCtrl
import java.awt.Dimension
import javax.swing.JFrame

class Tetris : JFrame() {
    init {
        add(GameCtrl())
        size = Dimension(700, 700)
        // isResizable = false
        title = "Tetris"
        defaultCloseOperation = EXIT_ON_CLOSE
    }
}