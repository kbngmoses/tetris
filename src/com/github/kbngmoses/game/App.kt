package com.github.kbngmoses.game

import com.github.kbngmoses.game.tetominoe.Tetris
import javax.swing.SwingUtilities

fun main(args: Array<String>) {

    val tetris = Tetris()

    SwingUtilities.invokeLater {

        tetris.isVisible = true
    }
}