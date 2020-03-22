package ru.poas.patientassistant.client.patient.domain

data class DrugItem (
    val id: Long,
    val dose: Double,
    val doseTypeName: String,
    val dateOfMedicationReception: String,
    val timeOfMedicationReception: String,
    val name: String,
    val description: String,
    val manufacturer: String
)