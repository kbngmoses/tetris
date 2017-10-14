package com.github.kbngmoses.game.tetrominoe

import java.awt.Color
import java.awt.Graphics

data class Brick(var x: Int, var y: Int, val width: Int, val height: Int, private val color: Color) {

    constructor(brick: Brick): this(brick.x, brick.y, brick.width, brick.height, brick.color)

    fun rotateRight(): Brick { val tmp = x; x = -y; y = tmp; return this }
    fun rotateLeft(): Brick  { val tmp = x; x = y; y = -tmp; return this }

    fun render(g: Graphics, xPos: Int, yPos: Int) {
        g.color = color
        g.fillRect(xPos + 1, yPos + 1, width - 2, height - 2)

        g.color = color.brighter()
        g.drawLine(xPos, yPos + height - 1, xPos, yPos)
        g.drawLine(xPos, yPos, xPos + width - 1, yPos)

        g.color = color.darker()
        g.drawLine(xPos + 1, yPos + height - 1,
                xPos + width - 1, yPos + height - 1)
        g.drawLine(xPos + width - 1, yPos + height - 1,
                xPos + width - 1, yPos + 1)
    }
}