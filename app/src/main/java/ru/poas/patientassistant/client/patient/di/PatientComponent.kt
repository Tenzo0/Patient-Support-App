

import dagger.Subcomponent
import ru.poas.patientassistant.client.patient.di.PatientModule

@Subcomponent(modules = [PatientModule::class])
interface PatientComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): PatientComponent
    }

}