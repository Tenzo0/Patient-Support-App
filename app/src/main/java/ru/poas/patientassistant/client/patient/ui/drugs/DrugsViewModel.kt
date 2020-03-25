package ru.poas.patientassistant.client.patient.ui.drugs

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch
import okhttp3.Credentials
import ru.poas.patientassistant.client.patient.domain.DrugItem
import ru.poas.patientassistant.client.patient.domain.asNotificationItem
import ru.poas.patientassistant.client.patient.domain.drugsStartFromDate
import ru.poas.patientassistant.client.patient.repository.DrugsRepository
import ru.poas.patientassistant.client.preferences.PatientPreferences
import ru.poas.patientassistant.client.preferences.UserPreferences
import ru.poas.patientassistant.client.receivers.AlarmReceiver
import ru.poas.patientassistant.client.utils.DateConstants
import ru.poas.patientassistant.client.utils.DateConstants.DATABASE_DATE_FORMAT
import ru.poas.patientassistant.client.utils.setExactAlarmAndAllowWhileIdle
import ru.poas.patientassistant.client.viewmodel.BaseViewModel
import java.util.*
import javax.inject.Inject

class DrugsViewModel @Inject constructor(
    private val drugsRepository: DrugsRepository
) : BaseViewModel() {

    private var _selectedDate = Calendar.getInstance()
    val selectedDate: Calendar
        get() = _selectedDate

    val drugsList = drugsRepository.drugsList
    private var _drugsListForSelectedDate = MutableLiveData<List<DrugItem>>()
    val drugsListForSelectedDate: LiveData<List<DrugItem>>
        get() = _drugsListForSelectedDate

    fun updateDrugsListForSelectedDate() {
        _drugsListForSelectedDate.value = drugsList.value?.filter {
            it.dateOfDrugReception == DATABASE_DATE_FORMAT.format(selectedDate.time)
        }
    }

    init {
        _drugsListForSelectedDate.value = emptyList()
    }

    fun incSelectedDate() {
        _selectedDate.add(Calendar.DATE, 1)
        updateDrugsListForSelectedDate()
    }

    fun decSelectedDate() {
        _selectedDate.add(Calendar.DATE, -1)
        updateDrugsListForSelectedDate()
    }

    fun updateSelectedDate(year: Int, month: Int, day: Int) {
        _selectedDate.set(year, month, day)
        updateDrugsListForSelectedDate()
    }

    fun refreshDrugs() {
        viewModelScope.launch {
            _isProgressShow.value = true
            try {
                drugsRepository.refreshDrugs(
                    Credentials.basic(
                        UserPreferences.getPhone()!!,
                        UserPreferences.getPassword()!!
                    )
                )
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false
            } catch (e: Exception) {
                _eventNetworkError.value = true
            }
            _isProgressShow.value = false
        }
    }

    fun updateDrugsNotifications(context: Context, drugsList: List<DrugItem>) {
        if (drugsList.isNotEmpty()) {
            viewModelScope.launch {

                //setup notifications only for drugs that begin in current date
                val drugsStartFromCurrentDate = drugsList.drugsStartFromDate(Date())

                //update actual info about notifications version
                val currentVersion = PatientPreferences.getActualDrugNotificationVersion() + 1
                PatientPreferences.updateActualDrugNotificationsVersion(currentVersion)

                for (drug in drugsStartFromCurrentDate) {

                    //Get trigger time for notification from drug item
                    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                    val triggerTime = DateConstants.DATABASE_TIME_FORMAT.parse(drug.timeOfDrugReception).time +
                            DATABASE_DATE_FORMAT.parse(drug.dateOfDrugReception).time

                    //Create intent that contains notification type and current drug item
                    val notificationPendingIntent: PendingIntent = PendingIntent
                        .getBroadcast(
                            context,
                            drug.id.toInt(),
                            Intent(context, AlarmReceiver::class.java).apply {
                                putExtra("type", "notification")
                                putExtra("notificationType", "drugNotification")
                                putExtra("DrugNotificationItemBundle", Bundle().apply {
                                    putParcelable(
                                        "DrugNotificationItem",
                                        drug.asNotificationItem(currentVersion)
                                    )
                                })
                            },
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )

                    //Set exact alarm for current drug
                    setExactAlarmAndAllowWhileIdle(
                        context,
                        triggerTime,
                        notificationPendingIntent
                    )
                }
            }
        }
    }
}