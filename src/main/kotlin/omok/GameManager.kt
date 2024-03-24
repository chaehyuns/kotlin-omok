package omok

import omok.library.BlackStoneOmokRule
import omok.library.OmokRule
import omok.library.WhiteStoneOmokRule
import omok.model.Board
import omok.model.GameState
import omok.model.Position

class GameManager {
    private var omokRule: OmokRule = BlackStoneOmokRule()
    private val board = Board()
    private var gameState: GameState = GameState.Running.BlackTurn(board)

    fun play(
        onTurn: (GameState) -> Unit,
        onBoard: (Board) -> Unit,
        onPosition: () -> Position,
    ) {
        while (isRunning()) {
            setRuleBasedOnTurn()
            playTurn(onTurn, onBoard, onPosition, omokRule)
        }
    }

    private fun isRunning() = gameState is GameState.Running

    private fun setRuleBasedOnTurn() {
        omokRule =
            when (gameState) {
                is GameState.Running.WhiteTurn -> WhiteStoneOmokRule()
                is GameState.Running.BlackTurn -> BlackStoneOmokRule()
                is GameState.Finish -> throw IllegalArgumentException(FINISH_MESSAGE)
            }
    }

    private fun playTurn(
        onTurn: (GameState) -> Unit,
        onBoard: (Board) -> Unit,
        onPosition: () -> Position,
        onOmokRule: OmokRule,
    ) {
        var retry = true
        while (retry) {
            runCatching {
                gameState = gameState.updateState(onTurn, onBoard, onPosition, onOmokRule)
            }.onSuccess {
                retry = false
            }.onFailure { throwable ->
                println(ERROR_MESSAGE.format(throwable.message))
            }
        }
    }

    companion object {
        private const val ERROR_MESSAGE = "예외가 발생했습니다: %s \n다시 시도해주세요."
        private const val FINISH_MESSAGE = "게임이 종료되었습니다."
    }
}
