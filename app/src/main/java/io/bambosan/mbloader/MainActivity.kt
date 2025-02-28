package io.bambosan.mbloader

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.util.Collections
import java.util.Objects
import java.util.concurrent.Executors
import java.util.zip.ZipFile

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listener = findViewById<TextView>(R.id.listener)

        val handler = Handler(Looper.getMainLooper())
        Executors.newSingleThreadExecutor().execute {
            try {
                val cacheDexDir = File(codeCacheDir, "dex")
                handleCacheCleaning(cacheDexDir, handler, listener)
                val mcInfo = packageManager.getApplicationInfo(
                    MC_PACKAGE_NAME,
                    PackageManager.GET_META_DATA
                )
                val pathList = getPathList(classLoader)
                processDexFiles(mcInfo, cacheDexDir, pathList!!, handler, listener)
                processNativeLibraries(mcInfo, pathList, handler, listener)
                launchMinecraft(mcInfo)
            } catch (e: Exception) {
                val fallbackActivity =
                    Intent(this, Fallback::class.java)
                handleException(e, fallbackActivity)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleCacheCleaning(cacheDexDir: File, handler: Handler, listener: TextView) {
        if (cacheDexDir.exists() && cacheDexDir.isDirectory) {
            handler.post {
                listener.text =
                    "-> " + cacheDexDir.absolutePath + " not empty, do cleaning"
            }
            for (file in Objects.requireNonNull(cacheDexDir.listFiles())) {
                if (file.delete()) {
                    handler.post {
                        listener.append(
                            """
-> ${file.name} deleted"""
                        )
                    }
                }
            }
        } else {
            handler.post {
                listener.text =
                    "-> " + cacheDexDir.absolutePath + " is empty, skip cleaning"
            }
        }
    }

    @Throws(Exception::class)
    private fun getPathList(classLoader: ClassLoader): Any? {
        val pathListField =
            Objects.requireNonNull(classLoader.javaClass.superclass).getDeclaredField("pathList")
        pathListField.isAccessible = true
        return pathListField[classLoader]
    }

    @Throws(Exception::class)
    private fun processDexFiles(
        mcInfo: ApplicationInfo,
        cacheDexDir: File,
        pathList: Any,
        handler: Handler,
        listener: TextView
    ) {
        val addDexPath = pathList.javaClass.getDeclaredMethod(
            "addDexPath",
            String::class.java,
            File::class.java
        )
        val launcherDex = File(cacheDexDir, LAUNCHER_DEX_NAME)

        copyFile(assets.open(LAUNCHER_DEX_NAME), launcherDex)
        handler.post {
            listener.append(
                """
-> $LAUNCHER_DEX_NAME copied to ${launcherDex.absolutePath}"""
            )
        }

        if (launcherDex.setReadOnly()) {
            addDexPath.invoke(pathList, launcherDex.absolutePath, null)
            handler.post {
                listener.append(
                    """
-> $LAUNCHER_DEX_NAME added to dex path list"""
                )
            }
        }

        ZipFile(mcInfo.sourceDir).use { zipFile ->
            for (i in 2 downTo 0) {
                val dexName = "classes" + (if (i == 0) "" else i) + ".dex"
                val dexFile = zipFile.getEntry(dexName)
                if (dexFile != null) {
                    val mcDex = File(cacheDexDir, dexName)
                    copyFile(zipFile.getInputStream(dexFile), mcDex)
                    handler.post {
                        listener.append(
                            """
-> ${mcInfo.sourceDir}/$dexName copied to ${mcDex.absolutePath}"""
                        )
                    }
                    if (mcDex.setReadOnly()) {
                        addDexPath.invoke(pathList, mcDex.absolutePath, null)
                        handler.post { listener.append("\n-> $dexName added to dex path list") }
                    }
                }
            }
        }
    }

    @Throws(Exception::class)
    private fun processNativeLibraries(
        mcInfo: ApplicationInfo,
        pathList: Any,
        handler: Handler,
        listener: TextView
    ) {
        val addNativePath = pathList.javaClass.getDeclaredMethod(
            "addNativePath",
            MutableCollection::class.java
        )
        val libDirList = ArrayList<String>()
        libDirList.add(mcInfo.nativeLibraryDir)
        addNativePath.invoke(pathList, libDirList)
        handler.post {
            listener.append(
                """
-> ${mcInfo.nativeLibraryDir} added to native library directory path"""
            )
        }
    }

    @Throws(ClassNotFoundException::class)
    private fun launchMinecraft(mcInfo: ApplicationInfo) {
        val launcherClass = classLoader.loadClass("com.mojang.minecraftpe.Launcher")
        val mcActivity = Intent(this, launcherClass)
        mcActivity.putExtra("MC_SRC", mcInfo.sourceDir)

        if (mcInfo.splitSourceDirs != null) {
            val listSrcSplit = ArrayList<String?>()
            Collections.addAll(listSrcSplit, *mcInfo.splitSourceDirs)
            mcActivity.putExtra("MC_SPLIT_SRC", listSrcSplit)
        }
        startActivity(mcActivity)
        finish()
    }

    private fun handleException(e: Exception, fallbackActivity: Intent) {
        val logMessage = if (e.cause != null) e.cause.toString() else e.toString()
        fallbackActivity.putExtra("LOG_STR", logMessage)
        startActivity(fallbackActivity)
        finish()
    }

    companion object {
        private const val MC_PACKAGE_NAME = "com.mojang.minecraftpe"
        private const val LAUNCHER_DEX_NAME = "launcher.dex"

        @Throws(IOException::class)
        private fun copyFile(from: InputStream, to: File) {
            val parentDir = to.parentFile
            if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
                throw IOException("Failed to create directories")
            }
            if (!to.exists() && !to.createNewFile()) {
                throw IOException("Failed to create new file")
            }
            BufferedInputStream(from).use { input ->
                BufferedOutputStream(Files.newOutputStream(to.toPath())).use { output ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    while ((input.read(buffer).also { bytesRead = it }) != -1) {
                        output.write(buffer, 0, bytesRead)
                    }
                }
            }
        }
    }
}