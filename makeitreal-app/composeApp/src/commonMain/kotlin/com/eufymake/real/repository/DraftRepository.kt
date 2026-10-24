package com.eufymake.real.repository

import com.eufymake.real.model.CanvasLayer
import com.eufymake.real.model.Draft
import com.eufymake.real.model.Substrate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface DraftRepository {
    val drafts: StateFlow<List<Draft>>
    val currentDraft: StateFlow<Draft?>
    val canUndo: StateFlow<Boolean>
    val canRedo: StateFlow<Boolean>
    fun createNewDraft(substrate: Substrate): Draft
    fun setCurrentDraft(draft: Draft?)
    fun undo()
    fun redo()
    fun recordHistorySnapshot()
    fun updateLayerTransient(layer: CanvasLayer)
    fun commitTransientUpdate()
    fun addLayer(layer: CanvasLayer)
    fun updateLayer(layer: CanvasLayer)
    fun removeLayer(layerId: String)
    fun deleteDraft(draftId: String)
    fun renameDraft(draftId: String, newTitle: String)
    fun duplicateDraft(draftId: String): Draft?
}

class MockDraftRepository : DraftRepository {

    private val _drafts = MutableStateFlow<List<Draft>>(
        listOf(
            Draft(
                id = "draft_initial_1",
                title = "Geometric Neon Coaster",
                substrate = Substrate.ALL.first { it.id == "coaster" },
                layers = listOf(
                    CanvasLayer(
                        id = "l1",
                        name = "Gold Hexagon Frame",
                        type = com.eufymake.real.model.LayerType.SHAPE,
                        widthMm = 70f,
                        heightMm = 70f,
                        shapeType = "CIRCLE",
                        reliefDepthMm = 1.0f
                    ),
                    CanvasLayer(
                        id = "l2",
                        name = "Cyberpunk Monogram",
                        type = com.eufymake.real.model.LayerType.TEXT,
                        textContent = "MK",
                        fontSizeSp = 36f,
                        textColor = 0xFFFF5E36,
                        reliefDepthMm = 2.2f
                    )
                ),
                updatedAt = System.currentTimeMillis() - 1000 * 60 * 30
            ),
            Draft(
                id = "draft_initial_2",
                title = "A5 Cyberpunk Cover",
                substrate = Substrate.ALL.first { it.id == "sheet" },
                layers = listOf(
                    CanvasLayer(
                        id = "l3",
                        name = "Landscape Neon Matrix",
                        type = com.eufymake.real.model.LayerType.IMAGE,
                        widthMm = 120f,
                        heightMm = 90f,
                        reliefDepthMm = 1.6f
                    )
                ),
                updatedAt = System.currentTimeMillis() - 1000 * 60 * 120
            )
        )
    )
    override val drafts: StateFlow<List<Draft>> = _drafts.asStateFlow()

    private val _currentDraft = MutableStateFlow<Draft?>(null)
    override val currentDraft: StateFlow<Draft?> = _currentDraft.asStateFlow()

    // Undo / Redo history for the active editor session
    private val undoStack = mutableListOf<Draft>()
    private val redoStack = mutableListOf<Draft>()

    private val _canUndo = MutableStateFlow(false)
    override val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()

    private val _canRedo = MutableStateFlow(false)
    override val canRedo: StateFlow<Boolean> = _canRedo.asStateFlow()

    override fun createNewDraft(substrate: Substrate): Draft {
        val draft = Draft(
            id = "draft_${System.currentTimeMillis()}",
            title = "${substrate.name} Project",
            substrate = substrate,
            layers = emptyList(),
            updatedAt = System.currentTimeMillis()
        )
        _drafts.value = listOf(draft) + _drafts.value
        setCurrentDraft(draft)
        return draft
    }

    override fun setCurrentDraft(draft: Draft?) {
        _currentDraft.value = draft
        undoStack.clear()
        redoStack.clear()
        _canUndo.value = false
        _canRedo.value = false
    }

    private fun pushHistory(previous: Draft) {
        undoStack.add(previous)
        if (undoStack.size > 20) undoStack.removeAt(0)
        redoStack.clear()
        _canUndo.value = undoStack.isNotEmpty()
        _canRedo.value = false
    }

    override fun undo() {
        val current = _currentDraft.value ?: return
        if (undoStack.isNotEmpty()) {
            val prev = undoStack.removeAt(undoStack.lastIndex)
            redoStack.add(current)
            _currentDraft.value = prev
            updateDraftInList(prev)
            _canUndo.value = undoStack.isNotEmpty()
            _canRedo.value = true
        }
    }

    override fun redo() {
        val current = _currentDraft.value ?: return
        if (redoStack.isNotEmpty()) {
            val next = redoStack.removeAt(redoStack.lastIndex)
            undoStack.add(current)
            _currentDraft.value = next
            updateDraftInList(next)
            _canUndo.value = true
            _canRedo.value = redoStack.isNotEmpty()
        }
    }

    override fun addLayer(layer: CanvasLayer) {
        val current = _currentDraft.value ?: return
        pushHistory(current)
        val updated = current.copy(
            layers = current.layers + layer.copy(zIndex = current.layers.size),
            updatedAt = System.currentTimeMillis(),
            isDirty = true
        )
        _currentDraft.value = updated
        updateDraftInList(updated)
    }

    override fun recordHistorySnapshot() {
        val current = _currentDraft.value ?: return
        pushHistory(current)
    }

    override fun updateLayerTransient(layer: CanvasLayer) {
        val current = _currentDraft.value ?: return
        val updated = current.copy(
            layers = current.layers.map { if (it.id == layer.id) layer else it }
        )
        _currentDraft.value = updated
    }

    override fun commitTransientUpdate() {
        val current = _currentDraft.value ?: return
        val updated = current.copy(
            updatedAt = System.currentTimeMillis(),
            isDirty = true
        )
        _currentDraft.value = updated
        updateDraftInList(updated)
    }

    override fun updateLayer(layer: CanvasLayer) {
        val current = _currentDraft.value ?: return
        pushHistory(current)
        val updated = current.copy(
            layers = current.layers.map { if (it.id == layer.id) layer else it },
            updatedAt = System.currentTimeMillis(),
            isDirty = true
        )
        _currentDraft.value = updated
        updateDraftInList(updated)
    }

    override fun removeLayer(layerId: String) {
        val current = _currentDraft.value ?: return
        pushHistory(current)
        val updated = current.copy(
            layers = current.layers.filterNot { it.id == layerId },
            updatedAt = System.currentTimeMillis(),
            isDirty = true
        )
        _currentDraft.value = updated
        updateDraftInList(updated)
    }

    override fun deleteDraft(draftId: String) {
        _drafts.value = _drafts.value.filterNot { it.id == draftId }
        if (_currentDraft.value?.id == draftId) {
            _currentDraft.value = null
        }
    }

    override fun renameDraft(draftId: String, newTitle: String) {
        _drafts.value = _drafts.value.map {
            if (it.id == draftId) it.copy(title = newTitle, updatedAt = System.currentTimeMillis()) else it
        }
        if (_currentDraft.value?.id == draftId) {
            _currentDraft.value = _currentDraft.value?.copy(title = newTitle, updatedAt = System.currentTimeMillis())
        }
    }

    override fun duplicateDraft(draftId: String): Draft? {
        val target = _drafts.value.firstOrNull { it.id == draftId } ?: return null
        val copy = target.copy(
            id = "draft_${System.currentTimeMillis()}",
            title = "${target.title} (副本)",
            updatedAt = System.currentTimeMillis()
        )
        _drafts.value = listOf(copy) + _drafts.value
        return copy
    }

    private fun updateDraftInList(draft: Draft) {
        _drafts.value = _drafts.value.map {
            if (it.id == draft.id) draft else it
        }
    }
}
