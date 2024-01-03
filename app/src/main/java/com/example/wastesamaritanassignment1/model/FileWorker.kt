package com.example.wastesamaritanassignment1.model

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class FileWorker(
    applicationContext: Context,
    private val params: WorkerParameters
): CoroutineWorker(applicationContext, params) {
    override suspend fun doWork(): Result {
        val inputData = params.inputData
        val fileTaskType = inputData.getString(FILE_TASK_KEY)?.let { FileTaskType.valueOf(it) }
            ?: return Result.failure()

        return withContext(Dispatchers.IO) {
            when(fileTaskType) {
                FileTaskType.DELETE -> {
                    val filePath = inputData.getString(FILE_PATH_KEY)
                        ?: return@withContext Result.failure()

                    if(!File(filePath).delete()) return@withContext Result.failure()
                    return@withContext Result.success()
                }
                FileTaskType.DELETE_LIST -> {
                    val filePathList = inputData.getStringArray(FILE_PATH_LIST_KEY)
                        ?: return@withContext Result.failure()

                    var success = true
                    for (filePath in filePathList) {
                        success = (File(filePath).delete()) and success
                    }
                    return@withContext if (success) Result.success() else Result.failure()
                }
            }
        }
    }

    companion object {
        const val FILE_TASK_KEY = "FILE_TASK_KEY"
        const val FILE_PATH_KEY = "FILE_PATH_KEY"
        const val FILE_PATH_LIST_KEY = "FILE_PATH_LIST_KEY"
    }
}