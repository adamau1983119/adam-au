package com.example.wtsaskingforsignature.data

import com.example.wtsaskingforsignature.BuildConfig
import com.example.wtsaskingforsignature.data.api.WtsApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ServiceLocator {
	private val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
	private val okHttp: OkHttpClient = OkHttpClient.Builder().addInterceptor(logging).build()
	private val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
	private val retrofit: Retrofit = Retrofit.Builder()
		.baseUrl(BuildConfig.API_BASE_URL)
		.client(okHttp)
		.addConverterFactory(MoshiConverterFactory.create(moshi))
		.build()

	val api: WtsApi by lazy { retrofit.create(WtsApi::class.java) }
	private val remoteRepository: WtsRepository by lazy { WtsRepository(api) }
	private val localRepository: LocalRepository by lazy { LocalRepository() }

	// 切換資料來源：true=使用遠端，false=使用本地
	var useRemote: Boolean = false

	val repository: Repository
		get() = if (useRemote) remoteRepository else localRepository
}
