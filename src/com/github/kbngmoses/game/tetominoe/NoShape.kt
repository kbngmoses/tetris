package com.github.kbngmoses.game.tetominoe

import java.awt.Color

class NoShape (override var bricks: Array<Brick>
              = Array(4, { _ -> Brick(0, 0, 0, 0, Color(0, 0, 0)) })) : Tetrominoe