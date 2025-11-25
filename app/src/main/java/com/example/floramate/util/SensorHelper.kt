package com.example.floramate.util

import android.content.Context
import androidx.room.Room
import com.example.floramate.data.local.dao.PlantDao
import com.example.floramate.data.local.database.PlantDatabase
import com.example.floramate.data.remote.api.PlantApiService
import com.example.floramate.data.repository.PlantRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun providePlantApiService(okHttpClient: OkHttpClient): PlantApiService {
        return Retrofit.Builder()
            .baseUrl("https://plant.id/api/v3/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PlantApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePlantDatabase(@ApplicationContext context: Context): PlantDatabase {
        return Room.databaseBuilder(
            context,
            PlantDatabase::class.java,
            "plant_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun providePlantDao(database: PlantDatabase): PlantDao {
        return database.plantDao()
    }

    @Provides
    @Singleton
    fun providePlantRepository(
        plantDao: PlantDao,
        plantApiService: PlantApiService
    ): PlantRepository {
        return PlantRepository(plantDao, plantApiService)
    }
}