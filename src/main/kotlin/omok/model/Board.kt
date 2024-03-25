package omok.model

import omok.library.BlackStoneOmokRule

class Board(private val boardSize: Int = BOARD_SIZE) {
    private var boardLayout = BoardLayout(boardSize)
    var lastCoordinate: Coordinate? = null
        private set
    private val blackStoneOmokRule: BlackStoneOmokRule = BlackStoneOmokRule

    fun getBoardLayout(): Array<Array<PositionType>> {
        return boardLayout.deepCopy()
    }

    fun placeStone(
        coordinate: Coordinate,
        positionType: PositionType,
    ) {
        if (boardLayout[coordinate.x, coordinate.y] == PositionType.EMPTY) {
            boardLayout[coordinate.x, coordinate.y] = positionType
            lastCoordinate = coordinate
        } else {
            throw IllegalArgumentException(ERROR_INVALID_POSITION)
        }
    }

    fun setupBoard(current: PositionType) {
        when (current) {
            PositionType.BLACK_STONE -> {
                setBlock(
                    blackStoneOmokRule::isThreeThree,
                    blackStoneOmokRule::isFourFour,
                    blackStoneOmokRule::isMoreThanFive,
                )
            }

            PositionType.WHITE_STONE -> {
                removeBlock()
            }

            else -> throw IllegalArgumentException(ERROR_POSITION_TYPE)
        }
    }

    private fun setBlock(
        isThreeThree: (Int, Int, Array<Array<PositionType>>) -> Boolean,
        isFourFour: (Int, Int, Array<Array<PositionType>>) -> Boolean,
        isMoreThanFive: (Int, Int, Array<Array<PositionType>>) -> Boolean,
    ) {
        val blockPositions: MutableList<Pair<Int, Int>> = mutableListOf()

        for (i in MIN_INDEX until boardSize) {
            for (j in MIN_INDEX until boardSize) {
                if (isBlockPosition(i, j, isThreeThree, isFourFour, isMoreThanFive)) {
                    blockPositions.add(Pair(i, j))
                }
            }
        }
        blockPositions.forEach {
            boardLayout[it.first, it.second] = PositionType.BLOCK
        }
    }

    private fun isBlockPosition(
        i: Int,
        j: Int,
        isThreeThree: (Int, Int, Array<Array<PositionType>>) -> Boolean,
        isFourFour: (Int, Int, Array<Array<PositionType>>) -> Boolean,
        isMoreThanFive: (Int, Int, Array<Array<PositionType>>) -> Boolean,
    ) = (
        isThreeThree(i, j, boardLayout.deepCopy()) ||
            isFourFour(i, j, boardLayout.deepCopy()) ||
            isMoreThanFive(i, j, boardLayout.deepCopy())
    ) && boardLayout.isEmpty(i, j)

    private fun removeBlock() {
        for (x in 0 until boardSize) {
            for (y in 0 until boardSize) {
                if (boardLayout[x, y] == PositionType.BLOCK) {
                    boardLayout[x, y] = PositionType.EMPTY
                }
            }
        }
    }

    companion object {
        private const val MIN_INDEX = 0
        private const val BOARD_SIZE = 15
        private const val ERROR_INVALID_POSITION = "돌을 놓을 수 없는 자리입니다."
        private const val ERROR_POSITION_TYPE = "올바른 PositionType이 아닙니다."
    }
}
