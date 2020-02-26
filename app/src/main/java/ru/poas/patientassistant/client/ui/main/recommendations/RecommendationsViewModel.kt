package ru.poas.patientassistant.client.ui.main.recommendations

import ru.poas.patientassistant.client.db.RecommendationsDatabase
import ru.poas.patientassistant.client.repository.RecommendationsRepository
import ru.poas.patientassistant.client.viewmodel.BaseViewModel

class RecommendationsViewModel(private val dataSource: RecommendationsDatabase) : BaseViewModel() {
    private val repository: RecommendationsRepository = RecommendationsRepository(dataSource)
}
