package io.bambosan.mbloader

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*
import java.nio.file.Files
import java.util.*
import java.util.zip.ZipFile

class MainViewModel : ViewModel() {
    // 使用 mutableStateOf 规范写法
    private val _isLoading = mutableStateOf(true)
    private val _logs = mutableStateListOf<String>()

    // 对外暴露不可变版本
    val isLoading: Boolean get() = _isLoading.value
    val logs: List<String> get() = _logs
    private val handler = Handler(Looper.getMainLooper())
    fun initialize(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true
                val cacheDexDir = File(context.codeCacheDir, "dex")

                handleCacheCleaning(cacheDexDir, context)

                val mcInfo = context.packageManager.getApplicationInfo(
                    MC_PACKAGE_NAME,
                    PackageManager.GET_META_DATA
                )

                val pathList = getPathList(context.classLoader)
                processDexFiles(mcInfo, cacheDexDir, pathList!!, context)
                processNativeLibraries(mcInfo, pathList, context)
                launchMinecraft(context, mcInfo)
            } catch (e: Exception) {
                addLog("Critical Error: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleCacheCleaning(cacheDexDir: File, context: Context) {
        addLog("Starting cache cleanup...")
        if (cacheDexDir.exists() && cacheDexDir.isDirectory) {
            addLog("${cacheDexDir.absolutePath} not empty, cleaning...")
            cacheDexDir.listFiles()?.forEach { file ->
                if (file.delete()) {
                    addLog("Deleted: ${file.name}")
                }
            }
        } else {
            addLog("Cache directory is empty, skip cleaning")
            if (!cacheDexDir.mkdirs()) {
                addLog("Failed to create cache directory")
            }
        }
    }

    private fun getPathList(classLoader: ClassLoader): Any? {
        return classLoader.javaClass.superclass?.getDeclaredField("pathList")?.let {
            it.isAccessible = true
            it.get(classLoader)
        }
    }

    private fun processDexFiles(
        mcInfo: ApplicationInfo,
        cacheDexDir: File,
        pathList: Any,
        context: Context
    ) {
        try {
            val addDexPath = pathList.javaClass.getDeclaredMethod(
                "addDexPath",
                String::class.java,
                File::class.java
            )

            // Process launcher.dex
            val launcherDex = File(cacheDexDir, LAUNCHER_DEX_NAME)
            copyAssetFile(context, LAUNCHER_DEX_NAME, launcherDex)
            addDexPath.invoke(pathList, launcherDex.absolutePath, null)
            addLog("Added $LAUNCHER_DEX_NAME to path")

            // Process Minecraft dex files
            ZipFile(mcInfo.sourceDir).use { zipFile ->
                for (i in 2 downTo 0) {
                    val dexName = "classes${if (i == 0) "" else i}.dex"
                    zipFile.getEntry(dexName)?.let { entry ->
                        val mcDex = File(cacheDexDir, dexName)
                        copyStreamToFile(zipFile.getInputStream(entry), mcDex)
                        addDexPath.invoke(pathList, mcDex.absolutePath, null)
                        addLog("Added $dexName to path")
                    }
                }
            }
        } catch (e: Exception) {
            addLog("DEX Processing Error: ${e.localizedMessage}")
        }
    }

    private fun processNativeLibraries(
        mcInfo: ApplicationInfo,
        pathList: Any,
        context: Context
    ) {
        try {
            val addNativePath = pathList.javaClass.getDeclaredMethod(
                "addNativePath",
                MutableCollection::class.java
            )

            val libDirList = mutableListOf(mcInfo.nativeLibraryDir)
            addNativePath.invoke(pathList, libDirList)
            addLog("Added native library path: ${mcInfo.nativeLibraryDir}")
        } catch (e: Exception) {
            addLog("Native Library Error: ${e.localizedMessage}")
        }
    }

    private fun launchMinecraft(context: Context, mcInfo: ApplicationInfo) {
        try {
            val launcherClass = context.classLoader.loadClass("com.mojang.minecraftpe.Launcher")
            val intent = Intent(context, launcherClass).apply {
                putExtra("MC_SRC", mcInfo.sourceDir)
                mcInfo.splitSourceDirs?.let { splitDirs ->
                    putExtra("MC_SPLIT_SRC", ArrayList(splitDirs.toList()))
                }
            }
            context.startActivity(intent)
            (context as? ComponentActivity)?.finish()
        } catch (e: Exception) {
            addLog("Launch Error: ${e.localizedMessage}")
        }
    }

    private fun addLog(message: String) {
        handler.post {
            _logs.add("${System.currentTimeMillis().toString().takeLast(4)}: $message")
            if (_logs.size > 50) _logs.removeFirst()
        }
    }

    private fun copyAssetFile(context: Context, assetName: String, destFile: File) {
        try {
            context.assets.open(assetName).use { input ->
                copyStreamToFile(input, destFile)
            }
        } catch (e: IOException) {
            addLog("Asset Copy Failed: $assetName")
        }
    }

    private fun copyStreamToFile(input: InputStream, destFile: File) {
        try {
            BufferedInputStream(input).use { bis ->
                BufferedOutputStream(Files.newOutputStream(destFile.toPath())).use { bos ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    while (bis.read(buffer).also { bytesRead = it } != -1) {
                        bos.write(buffer, 0, bytesRead)
                    }
                }
            }
            destFile.setReadOnly()
        } catch (e: IOException) {
            addLog("File Copy Error: ${e.localizedMessage}")
        }
    }
    fun cleanup() {
        handler.removeCallbacksAndMessages(null)
        _logs.clear()
        _isLoading.value = false
    }

    override fun onCleared() {
        cleanup()
        super.onCleared()
    }
    companion object {
        private const val MC_PACKAGE_NAME = "com.mojang.minecraftpe"
        private const val LAUNCHER_DEX_NAME = "launcher.dex"
    }
}
