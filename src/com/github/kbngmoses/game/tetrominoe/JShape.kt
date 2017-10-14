package com.github.kbngmoses.game.tetrominoe

import java.awt.Color

class JShape(val width: Int, val height: Int, val color: Color) : Tetrominoe {

    override var bricks: Array<Brick>
            = arrayOf(Brick(1, -1, width, height, color),
            Brick(0, -1, width, height, color),
            Brick(0, 0, width, height, color),
            Brick(0, 1, width, height, color))
}