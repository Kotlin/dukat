package org.jetbrains.dukat.compiler.tests.descriptors

import org.jetbrains.dukat.descriptors.JsStdlibConfigContext
import org.jetbrains.dukat.translator.ModuleTranslationUnit
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.openapi.util.text.StringUtilRt
import org.jetbrains.kotlin.com.intellij.openapi.vfs.CharsetToolkit
import org.jetbrains.kotlin.com.intellij.psi.PsiFileFactory
import org.jetbrains.kotlin.com.intellij.psi.impl.PsiFileFactoryImpl
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.js.analyze.TopDownAnalyzerFacadeForJS
import org.jetbrains.kotlin.psi.KtFile

private fun createKtFile(name: String, text: String, project: Project): KtFile {
    val virtualFile =
        LightVirtualFile(name, KotlinLanguage.INSTANCE, StringUtilRt.convertLineSeparators(text))

    virtualFile.charset = CharsetToolkit.UTF8_CHARSET
    val factory = PsiFileFactory.getInstance(project) as PsiFileFactoryImpl

    return factory.trySetupPsiForFile(virtualFile, KotlinLanguage.INSTANCE, true, false) as KtFile
}

private fun analyze(
        ktFiles: Collection<KtFile>,
        configContext: JsStdlibConfigContext
): AnalysisResult {
    return TopDownAnalyzerFacadeForJS.analyzeFiles(ktFiles, configContext.generateJSConfig())
}

fun generateModuleDescriptor(moduleUnits: Collection<ModuleTranslationUnit>): ModuleDescriptor {
    val context = JsStdlibConfigContext()
    try {
        val environment = context.environment
        val project = environment.project
        val ktFiles = moduleUnits.map { moduleUnit ->
            createKtFile("${moduleUnit.name}.kt", moduleUnit.content, project)
        }
        return analyze(ktFiles, context).moduleDescriptor
    } finally {
        context.destroy()
    }
}
