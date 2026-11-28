package com.designlife.setwork.common.di

import com.designlife.setwork.common.data.datastore.AppStore
import com.designlife.setwork.common.data.network.SoftwareUpdateService
import com.designlife.setwork.common.data.network.retrofit.RetrofitBuilder
import com.designlife.setwork.common.data.room.dao.SetworkDatabase
import com.designlife.setwork.common.domain.repeat.RepeatRepository
import com.designlife.setwork.common.domain.repositories.CategoryRepository
import com.designlife.setwork.common.domain.repositories.DeckRepository
import com.designlife.setwork.common.domain.repositories.NoteRepository
import com.designlife.setwork.common.domain.repositories.TodoCategoryRepository
import com.designlife.setwork.common.domain.repositories.TodoRepository
import com.designlife.setwork.common.domain.repositories.WidgetRepository
import com.designlife.setwork.common.domain.repositories.appstore.AppStoreRepository
import com.designlife.setwork.common.domain.repositories.appstore.IAppStoreRepository
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

    // Setwork provider
    single {
        SetworkProvider(androidContext().applicationContext)
    }
}