package com.designlife.justdo.common.di

import com.designlife.justdo.common.data.datastore.AppStore
import com.designlife.justdo.common.data.network.SoftwareUpdateService
import com.designlife.justdo.common.data.network.retrofit.RetrofitBuilder
import com.designlife.justdo.common.data.room.dao.SetworkDatabase
import com.designlife.justdo.common.domain.repeat.RepeatRepository
import com.designlife.justdo.common.domain.repositories.CategoryRepository
import com.designlife.justdo.common.domain.repositories.DeckRepository
import com.designlife.justdo.common.domain.repositories.NoteRepository
import com.designlife.justdo.common.domain.repositories.SoftwareUpdateRepository
import com.designlife.justdo.common.domain.repositories.TodoCategoryRepository
import com.designlife.justdo.common.domain.repositories.TodoRepository
import com.designlife.justdo.common.domain.repositories.WidgetRepository
import com.designlife.justdo.common.domain.repositories.appstore.AppStoreRepository
import com.designlife.justdo.common.domain.repositories.appstore.IAppStoreRepository
import com.designlife.justdo.common.utils.update.SoftwareUpdateManager
import com.designlife.justdo_provider.SetworkProvider
import com.designlife.orchestrator.SchedulingEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.InternalSerializationApi
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

@OptIn(InternalSerializationApi::class)
val appModule = module{
    single {
        SetworkDatabase.getDatabase(androidContext())
    }

    // DAOs
    single { get<SetworkDatabase>().categoryDao() }
    single { get<SetworkDatabase>().todoDao() }
    single { get<SetworkDatabase>().noteDao() }
    single { get<SetworkDatabase>().deckDao() }
    single { get<SetworkDatabase>().todoCategoryJunctionDao() }
    single { get<SetworkDatabase>().widgetDao() }

    // Repositories
    single {
        CategoryRepository(get())
    }

    single {
        TodoRepository(get())
    }

    single {
        NoteRepository(get())
    }

    single {
        DeckRepository(get())
    }

    single {
        TodoCategoryRepository(
            get(), // TodoCategoryJunctionDao
            get(), // TodoDao
            get()  // CategoryDao
        )
    }

    single {
        RepeatRepository()
    }

    single<IAppStoreRepository> {
        IAppStoreRepository(AppStore.getDataStore(androidContext()))
    }

    single<AppStoreRepository>{
        get<IAppStoreRepository>()
    }

    single {
        WidgetRepository(get())
    }

    // Network
    single {
        RetrofitBuilder.networkBuilder()
            .create(SoftwareUpdateService::class.java)
    }

    single {
        SoftwareUpdateRepository(
            softwareUpdateService = get()
        )
    }

    // Background scope dedicated to software updates
    single(named("softwareUpdateScope")) {
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    // Notification scheduler
    single {
        SchedulingEngine(
            androidContext().applicationContext
        ).notificationScheduler()
    }

    // Software update manager
    single {
        SoftwareUpdateManager(
            context = androidContext().applicationContext,
            scope = get(named("softwareUpdateScope")),
            updateRepository = get(),
            notificationScheduler = get()
        )
    }

    // Setwork provider
    single {
        SetworkProvider(androidContext().applicationContext)
    }
}