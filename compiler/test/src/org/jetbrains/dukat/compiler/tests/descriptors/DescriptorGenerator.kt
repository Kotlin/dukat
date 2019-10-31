package org.jetbrains.dukat.compiler.tests.descriptors

import org.jetbrains.dukat.descriptors.generateDefaultEnvironment
import org.jetbrains.dukat.descriptors.generateJSConfig
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.com.intellij.lang.Language
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.openapi.util.io.FileUtil
import org.jetbrains.kotlin.com.intellij.openapi.util.text.StringUtilRt
import org.jetbrains.kotlin.com.intellij.openapi.vfs.CharsetToolkit
import org.jetbrains.kotlin.com.intellij.psi.PsiFileFactory
import org.jetbrains.kotlin.com.intellij.psi.impl.PsiFileFactoryImpl
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.js.analyze.TopDownAnalyzerFacadeForJS
import org.jetbrains.kotlin.psi.KtFile
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

fun createFile(name: String, text: String, project: Project): KtFile {
    var shortName = name.substring(name.lastIndexOf('/') + 1)
    shortName = shortName.substring(shortName.lastIndexOf('\\') + 1)
    val virtualFile =
        LightVirtualFile(shortName, (KotlinLanguage.INSTANCE as Language), StringUtilRt.convertLineSeparators(text))

    virtualFile.charset = CharsetToolkit.UTF8_CHARSET
    val factory = PsiFileFactory.getInstance(project) as PsiFileFactoryImpl

    return factory.trySetupPsiForFile(virtualFile, (KotlinLanguage.INSTANCE as Language), true, false) as KtFile
}

fun doLoadFile(myFullDataPath: String, name: String): String {
    val fullName = myFullDataPath + File.separatorChar + name
    return doLoadFile(File(fullName))
}

fun doLoadFile(file: File): String {
    try {
        return FileUtil.loadFile(file, "UTF-8", true)
    } catch (fileNotFoundException: FileNotFoundException) {
        val messageWithFullPath = file.absolutePath + " (No such file or directory)"
        throw IOException(
            "Ensure you have your 'Working Directory' configured correctly as the root " +
                    "Kotlin project directory in your test configuration\n\t" +
                    messageWithFullPath,
            fileNotFoundException
        )
    }
}

fun analyze(
    files: List<KtFile>
): AnalysisResult {
    return TopDownAnalyzerFacadeForJS.analyzeFiles(files, generateJSConfig())
}

fun generateModuleDescriptor(files: List<File>): ModuleDescriptor {
    val environment = generateDefaultEnvironment()
    val project = environment.project
    val psiFiles = files.map { file ->
        val fileName = file.name
        val filePath = file.parent
        createFile(fileName, doLoadFile(filePath, fileName), project)
    }
    return analyze(psiFiles).moduleDescriptor
}
