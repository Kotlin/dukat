package org.jetbrains.dukat.stdlibGenerator

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.rightMost
import org.jetbrains.dukat.astCommon.toNameEntity
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.FunctionTypeModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel
import org.jetbrains.dukat.astModel.visitors.LambdaParameterModel
import org.jetbrains.dukat.translatorString.translateModule
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.config.addKotlinSourceRoots
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentProvider
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.incremental.components.LookupTracker
import org.jetbrains.kotlin.js.resolve.JsPlatformAnalyzerServices
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.parents
import org.jetbrains.kotlin.resolve.scopes.getDescriptorsFiltered
import org.jetbrains.kotlin.serialization.deserialization.DeserializationConfiguration
import org.jetbrains.kotlin.serialization.js.KotlinJavascriptSerializationUtil.readModuleAsProto
import org.jetbrains.kotlin.serialization.js.createKotlinJavascriptPackageFragmentProvider
import org.jetbrains.kotlin.storage.LockBasedStorageManager
import org.jetbrains.kotlin.types.DynamicType
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.SimpleType
import org.jetbrains.kotlin.utils.KotlinJavascriptMetadata
import org.jetbrains.kotlin.utils.KotlinJavascriptMetadataUtils
import java.io.File

private fun KotlinJavascriptMetadata.calculatePackageFragmentProvider(): PackageFragmentProvider {
    val (header, packageFragmentProtos) = readModuleAsProto(body, version)

    val module = ModuleDescriptorImpl(Name.special("<dukat-empty>"), LockBasedStorageManager.NO_LOCKS, JsPlatformAnalyzerServices.builtIns)

    val provider = createKotlinJavascriptPackageFragmentProvider(
            LockBasedStorageManager("DukatStdLibGenerator"),
            module,
            header,
            packageFragmentProtos,
            version,
            DeserializationConfiguration.Default,
            LookupTracker.DO_NOTHING
    )

    module.initialize(provider)
    module.setDependencies(module, module.builtIns.builtInsModule)
    return provider
}

private fun getPackageFragmentProvider(metaFileName: String): PackageFragmentProvider {
    val metaFile = File(metaFileName)
    val srcDirs = listOf(metaFile)

    val configuration = CompilerConfiguration()
    configuration.put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
    configuration.addKotlinSourceRoots(srcDirs.map { it.path })

    val metadata = KotlinJavascriptMetadataUtils.loadMetadata(metaFile).first()

    return metadata.calculatePackageFragmentProvider()
}

private fun DeclarationDescriptor.convertToTopLevelModel(): TopLevelModel? {
    return when (this) {
        is ClassDescriptor -> convertToTopLevelModel()
        else -> null
    }
}

private fun SimpleType.convertToFunctionTypeModel(): FunctionTypeModel {
    return FunctionTypeModel(
            nullable = isMarkedNullable,
            parameters = listOf(LambdaParameterModel(null, arguments[0].type.convertToTypeModel())),
            type = arguments[1].type.convertToTypeModel(),
            metaDescription = null
    )
}

private fun KotlinType.convertToTypeModel(): TypeModel {
    return when (this) {
        is SimpleType -> {
            val name = constructor.toString()
            // TODO: this cries about introducing lowering
            if (name == "Function1") {
                convertToFunctionTypeModel()
            } else {
                TypeValueModel(IdentifierEntity(name), emptyList(), null, null, isMarkedNullable)
            }
        }
        is DynamicType -> TypeValueModel(IdentifierEntity("dynamic"), emptyList(), null, null, false)
        else -> throw Exception("unkown kotlin type: ${this.javaClass.simpleName}")
    }
}

private fun DeclarationDescriptor.convertToMemberModel(): MemberModel? {
    return when (this) {
        is PropertyDescriptor -> {
            val override = (overriddenDescriptors.firstOrNull()?.parents?.firstOrNull() as? ClassDescriptor)?.fqNameSafe?.toString()?.toNameEntity()
            PropertyModel(
                    name = IdentifierEntity(name.toString()),
                    typeParameters = emptyList(),
                    static = false,
                    override = override,
                    immutable = !isVar,
                    getter = getter != null,
                    setter = setter != null,
                    type = getType().convertToTypeModel(),
                    open = true
            )
        }
        else -> {
            //println("UNKOWNN ${this.javaClass.simpleName}")
            null
        }
    }
}

private fun ClassDescriptor.convertToClassModel(): ClassModel {
    return ClassModel(
            name = IdentifierEntity(this.name.toString()),
            members = unsubstitutedMemberScope.getDescriptorsFiltered().mapNotNull { it.convertToMemberModel() },
            companionObject = null,
            typeParameters = emptyList(),
            parentEntities = emptyList(),
            primaryConstructor = null,
            annotations = mutableListOf(),
            comment = null,
            external = isExternal,
            abstract = false,
            visibilityModifier = VisibilityModifierModel.PUBLIC
    )
}

private fun ClassDescriptor.convertToInterfaceModel(): InterfaceModel {
    return InterfaceModel(
            name = IdentifierEntity(this.name.toString()),
            members = unsubstitutedMemberScope.getDescriptorsFiltered().mapNotNull { it.convertToMemberModel() },
            companionObject = null,
            typeParameters = emptyList(),
            parentEntities = emptyList(),
            annotations = mutableListOf(),
            comment = null,
            external = isExternal,
            visibilityModifier = VisibilityModifierModel.PUBLIC
    )
}


private fun ClassDescriptor.convertToTopLevelModel(): TopLevelModel? {
    return when (kind) {
        ClassKind.CLASS -> convertToClassModel()
        ClassKind.INTERFACE -> convertToInterfaceModel()
        else -> null
    }
}

private fun PackageFragmentDescriptor.convertToModuleModel(): SourceFileModel {
    val fqName = fqName.toString().toNameEntity()
    val memberScope = getMemberScope()

    val moduleModel = ModuleModel(
            name = fqName,
            shortName = fqName.rightMost(),
            declarations = memberScope.getContributedDescriptors().mapNotNull {
                it.convertToTopLevelModel()
            },
            annotations = mutableListOf(),
            submodules = emptyList(),
            imports = mutableListOf(),
            comment = null
    )

    return SourceFileModel(root = moduleModel, name = null, referencedFiles = emptyList(), fileName = "fragment")
}

fun processPackageFragments(packageFragmentProvider: PackageFragmentProvider, fqName: String): List<SourceFileModel> {
    return packageFragmentProvider.getPackageFragments(FqName(fqName)).map {
        it.convertToModuleModel()
    }
}
