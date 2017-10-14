package com.github.kbngmoses.game.tetrominoe

import java.awt.Color

class NoShape : Tetrominoe {
    override var bricks: Array<Brick>
            = Array(4, { _ -> Brick(0, 0, 0, 0,  Color.BLACK)})
}