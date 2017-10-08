package com.github.kbngmoses.game.tetominoe

import java.awt.Color

class OShape(width: Int, height: Int, color: Color) : Tetrominoe {

    override var bricks: Array<Brick>
            = arrayOf(Brick(0, 0, width, height, color),
            Brick(1, 0, width, height, color),
            Brick(0, 1, width, height, color),
            Brick(1, 1, width, height, color))
}