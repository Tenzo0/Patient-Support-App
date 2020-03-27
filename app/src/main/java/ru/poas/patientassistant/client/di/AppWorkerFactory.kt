package ru.poas.patientassistant.client.di

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import ru.poas.patientassistant.client.patient.workers.ChildWorkerFactory
import ru.poas.patientassistant.client.patient.workers.DrugsWorker
import javax.inject.Inject
import javax.inject.Provider
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.reflect.KClass

class AppWorkerFactory @Inject constructor(
    private val workerFactories: Map<Class<out ListenableWorker>,
            @JvmSuppressWildcards Provider<ChildWorkerFactory>>): WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        val worker = workerFactories.entries
            .find {
            Class.forName(workerClassName).isAssignableFrom(it.key) }?.value
            ?: throw IllegalArgumentException("unknown workers class name: $workerClassName")

        return worker.get().create(appContext, workerParameters)
    }
}

@Module
interface DrugsWorkerModule {

    @Binds
    @IntoMap
    @CoroutineWorkerKey(DrugsWorker::class)
    fun bindDrugsWorker(factory: DrugsWorker.Factory): ChildWorkerFactory
}

@Target(AnnotationTarget.FUNCTION)
@Retention(RUNTIME)
@MapKey
annotation class CoroutineWorkerKey(val value: KClass<out ListenableWorker>)