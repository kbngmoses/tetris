package com.github.kbngmoses.game.tetrominoe

import com.github.kbngmoses.game.GameCtrl
import java.awt.Color
import java.awt.Graphics
import java.util.*

interface Tetrominoe {

    var bricks: Array<Brick>

    fun top() = bricks.minBy { it -> it.y }!!.y

    fun brickAt(index: Int) = bricks[index]

    fun renderMove(g: Graphics, xPos: Int, yPos: Int, top: Int = 0) {
        (0 until 4).forEach { i ->
            val brick = brickAt(i)
            val newX = brick.x + xPos
            val newY = yPos - brick.y
            val rectX = 0 + newX * brick.width
            val rectY = top  + (GameCtrl.nRows - newY - 1) * brick.height
            bricks[i].render(g, rectX, rectY) }
    }

    fun renderStatic(g: Graphics, x: Int, y: Int) {
        (0 until 4).forEach { i -> bricks[i].render(g, x, y) }
    }

    companion object {

        private val colors = arrayOf(Color(215,215,105),
                Color(215,105,215),
                Color(105,215,215),
                Color(105,105,215),
                Color(105,215,105),
                Color(215,105,105),
                Color(233,129,87))

        fun rotateLeft(piece: Tetrominoe): Tetrominoe {
            if (piece is OShape)
                return piece
            val newPiece = Tetrominoe.new(piece)
            newPiece.bricks = piece.bricks.map { brick -> Brick(brick).rotateLeft() }.toTypedArray()
            return newPiece
        }

        fun rotateRight(piece: Tetrominoe): Tetrominoe {
            if (piece is OShape)
                return piece
            val newPiece = Tetrominoe.new(piece)
            newPiece.bricks = piece.bricks.map { brick -> Brick(brick).rotateRight() }.toTypedArray()
            return newPiece
        }

        private fun new(tetrominoe: Tetrominoe): Tetrominoe {
            val width = GameCtrl.brickWidth
            val height = GameCtrl.brickHeight
            return when (tetrominoe) {
                is ZShape -> ZShape(width, height, colors[0])
                is OShape -> OShape(width, height, colors[1])
                is TShape -> TShape(width, height, colors[3])
                is IShape -> IShape(width, height, colors[2])
                is LShape -> LShape(width, height, colors[4])
                is JShape -> JShape(width, height, colors[5])
                else -> SShape(width, height, colors[6])
            }
        }

        fun randomPiece(): Tetrominoe {
            val int = (Math.abs(Random().nextInt()) % 7)
            val color = colors[int]
            val width = GameCtrl.brickWidth
            val height = GameCtrl.brickHeight
            return when (int + 1) {
                1 -> ZShape(width, height, color)
                2 -> OShape(width, height, color)
                3 -> IShape(width, height, color)
                4 -> TShape(width, height, color)
                5 -> LShape(width, height, color)
                6 -> JShape(width, height, color)
                else -> SShape(width, height, color)
            }
        }
    }
}