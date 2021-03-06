import com.avito.utils.logging.ciLogger
import com.google.gson.GsonBuilder
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

@CacheableTask
abstract class ProsectorReleaseAnalysisTask : DefaultTask() {

    @InputFile
    @PathSensitive(PathSensitivity.RELATIVE)
    lateinit var apk: File

    @Input
    lateinit var meta: ReleaseAnalysisMeta

    @Input
    lateinit var host: String

    @Internal
    var debug: Boolean = false

    private val apiClient by lazy {
        Retrofit.Builder()
            .baseUrl(host)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(
                OkHttpClient.Builder().apply {
                    if (debug) {
                        addInterceptor(HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                            override fun log(message: String) {
                                project.ciLogger.info(message)
                            }
                        }).apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        })
                    }
                }
                    .build()
            )
            .build()
            .create(ProsectorApi::class.java)
    }

    @TaskAction
    fun doWork() {
        try {
            val result = apiClient.releaseAnalysis(
                meta = meta,
                apk = MultipartBody.Part.createFormData(
                    "build_after",
                    apk.name,
                    apk.asRequestBody(MultipartBody.FORM)
                )
            ).execute()

            //todo prosector service not so stable now, should not fail build
            //require(result.isSuccessful) { "${result.message()} ${result.errorBody()?.string()}" }
            //require(result.body()?.result == "ok") { "Service should return {result:ok} normally" }

            ciLogger.info(
                "isSuccessful = ${result.isSuccessful}; body = ${result.body()?.result}; errorBody = ${result.errorBody()
                    ?.string()}"
            )
        } catch (e: Throwable) {
            ciLogger.critical("Prosector upload failed", e)
        }
    }
}
