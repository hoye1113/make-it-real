package com.eufymake.real.repository

import com.eufymake.real.model.PointsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface PointsRepository {
    val pointsState: StateFlow<PointsState>
    fun checkIn(): Boolean
    fun spendAICredits(amount: Int): Boolean
    fun redeemMallItem(itemId: String): Boolean
}

class MockPointsRepository : PointsRepository {
    private val _pointsState = MutableStateFlow(PointsState())
    override val pointsState: StateFlow<PointsState> = _pointsState.asStateFlow()

    override fun checkIn(): Boolean {
        if (_pointsState.value.isCheckedInToday) return false
        _pointsState.value = _pointsState.value.copy(
            totalPoints = _pointsState.value.totalPoints + 15,
            consecutiveDays = _pointsState.value.consecutiveDays + 1,
            isCheckedInToday = true,
            tasks = _pointsState.value.tasks.map {
                if (it.id == "check_in") it.copy(isCompleted = true, progress = 1) else it
            }
        )
        return true
    }

    override fun spendAICredits(amount: Int): Boolean {
        if (_pointsState.value.aiCredits >= amount) {
            _pointsState.value = _pointsState.value.copy(
                aiCredits = _pointsState.value.aiCredits - amount,
                tasks = _pointsState.value.tasks.map {
                    if (it.id == "ai_gen") it.copy(isCompleted = true, progress = 1) else it
                }
            )
            return true
        }
        return false
    }

    override fun redeemMallItem(itemId: String): Boolean {
        val item = _pointsState.value.mallItems.firstOrNull { it.id == itemId } ?: return false
        if (_pointsState.value.totalPoints >= item.pointsCost && item.stockCount > 0) {
            val extraCredits = if (itemId == "ai_credit_bundle") 50 else 0
            _pointsState.value = _pointsState.value.copy(
                totalPoints = _pointsState.value.totalPoints - item.pointsCost,
                aiCredits = _pointsState.value.aiCredits + extraCredits,
                mallItems = _pointsState.value.mallItems.map {
                    if (it.id == itemId) it.copy(stockCount = it.stockCount - 1) else it
                }
            )
            return true
        }
        return false
    }
}
