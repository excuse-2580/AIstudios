package com.aistudio.app.di

import android.content.Context
import androidx.room.Room
import com.aistudio.app.data.local.AIDatabase
import com.aistudio.app.data.local.dao.AgentDao
import com.aistudio.app.data.local.dao.ChatDao
import com.aistudio.app.data.local.dao.GroupDao
import com.aistudio.app.data.remote.ApiConfig
import com.aistudio.app.data.remote.AIClient
import com.aistudio.app.data.remote.AIService
import com.aistudio.app.data.remote.OpenAIApi
import com.aistudio.app.data.remote.ClaudeApi
import com.aistudio.app.data.repository.AgentRepository
import com.aistudio.app.data.repository.ChatRepository
import com.aistudio.app.data.repository.GroupRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
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
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AIDatabase {
        return Room.databaseBuilder(
            context,
            AIDatabase::class.java,
            "ai_studio_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    @Singleton
    fun provideAgentDao(database: AIDatabase): AgentDao {
        return database.agentDao()
    }
    
    @Provides
    @Singleton
    fun provideChatDao(database: AIDatabase): ChatDao {
        return database.chatDao()
    }
    
    @Provides
    @Singleton
    fun provideGroupDao(database: AIDatabase): GroupDao {
        return database.groupDao()
    }
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    @Named("openai")
    fun provideOpenAIClient(okHttpClient: OkHttpClient, gson: Gson): OpenAIApi {
        return Retrofit.Builder()
            .baseUrl(ApiConfig.getOpenAIBaseUrl())
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(OpenAIApi::class.java)
    }
    
    @Provides
    @Singleton
    @Named("claude")
    fun provideClaudeClient(okHttpClient: OkHttpClient, gson: Gson): ClaudeApi {
        return Retrofit.Builder()
            .baseUrl(ApiConfig.getClaudeBaseUrl())
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ClaudeApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideAIClient(
        @Named("openai") openAIApi: OpenAIApi,
        @Named("claude") claudeApi: ClaudeApi
    ): AIClient {
        return AIClient(openAIApi, claudeApi)
    }
    
    @Provides
    @Singleton
    fun provideAIService(
        @Named("openai") openAIApi: OpenAIApi,
        @Named("claude") claudeApi: ClaudeApi
    ): AIService {
        return AIService(openAIApi, claudeApi)
    }
    
    @Provides
    @Singleton
    fun provideAgentRepository(
        agentDao: AgentDao,
        gson: Gson
    ): AgentRepository {
        return AgentRepository(agentDao, gson)
    }
    
    @Provides
    @Singleton
    fun provideChatRepository(
        chatDao: ChatDao,
        gson: Gson
    ): ChatRepository {
        return ChatRepository(chatDao, gson)
    }
    
    @Provides
    @Singleton
    fun provideGroupRepository(
        groupDao: GroupDao,
        gson: Gson
    ): GroupRepository {
        return GroupRepository(groupDao, gson)
    }
}
