package com.bsobe.lintrules.detector

import com.android.SdkConstants.*
import com.android.ide.common.rendering.api.ResourceNamespace
import com.android.ide.common.resources.ResourceRepository
import com.android.resources.ResourceFolderType
import com.android.resources.ResourceType
import com.android.resources.ResourceUrl
import com.android.sdklib.AndroidVersion.VersionCodes.LOLLIPOP
import com.android.tools.lint.detector.api.*
import com.android.utils.XmlUtils
import com.google.common.collect.Sets
import org.w3c.dom.Attr
import org.w3c.dom.Element
import java.util.*
import java.util.function.Predicate

/*
    Reference Detector : https://android.googlesource.com/platform/tools/base/+/studio-master-dev/lint/libs/lint-checks/src/main/java/com/android/tools/lint/checks/VectorDrawableCompatDetector.java
 */
class BackgroundVectorDetector : ResourceXmlDetector() {

    private var skipChecks: Boolean = false
    private val vectors: HashSet<String> = Sets.newHashSet()

    override fun beforeCheckRootProject(context: Context) {
        val androidProjectModel = context.project.gradleProjectModel
        if (androidProjectModel == null) {
            skipChecks = true
            return
        }

        if (context.project.minSdk >= LOLLIPOP) {
            skipChecks = true
            return
        }

        if (context.project.gradleModelVersion == null || context.project.gradleModelVersion.major < 2) {
            skipChecks = true
            return
        }

        if (context.project.currentVariant == null) {
            skipChecks = true
            return
        }

        // TODO check gradle defaultConfig to support vector
        /*
        if (context.project.currentVariant.mergedFlavor.vectorDrawables.useSupportLibrary) {

        }
        */

    }

    override fun appliesTo(folderType: ResourceFolderType): Boolean {
        if (skipChecks) {
            return false
        }
        return folderType == ResourceFolderType.DRAWABLE || folderType == ResourceFolderType.LAYOUT
    }

    override fun getApplicableAttributes(): Collection<String>? =
        if (skipChecks) null else APPLICABLE_ATTRIBUTE_LIST

    override fun getApplicableElements(): Collection<String>? =
        if (skipChecks) null else APPLICABLE_ELEMENT_LIST

    /**
     * Saves names of all vector resources encountered. Because "drawable" is before "layout" in
     * alphabetical order, Lint will first call this on every vector, before calling {@link
     * #visitAttribute(XmlContext, Attr)} on every attribute.
     */
    override fun visitElement(context: XmlContext, element: Element) {
        if (skipChecks) {
            return
        }

        val resourceName = getBaseName(context.file.name)
        vectors.add(resourceName)
    }

    override fun visitAttribute(context: XmlContext, attribute: Attr) {
        if (skipChecks) {
            return
        }

        val isIncrementalMode = context.driver.scope.contains(Scope.ALL_RESOURCE_FILES).not()
        if (isIncrementalMode.not() && vectors.isEmpty()) {
            return
        }

        @Suppress("FoldInitializerAndIfToElvis")
        val isVector: Predicate<String> = if (isIncrementalMode.not()) {
            // TODO: Always use resources, once command-line client supports it.
            Predicate {
                vectors.contains(it)
            }
        } else {
            val lintClient = context.client
            val resourcesRepository =
                lintClient.getResourceRepository(
                    project = context.mainProject,
                    includeModuleDependencies = true,
                    includeLibraries = false
                )
            if (resourcesRepository == null) {
                // We only run on a single layout file, but have no access to the resources
                // database, there's no way we can perform the check.

                TODO("is this return statement is really necessary?")
                //return
            }
            Predicate {
                isVector(
                    resourcesRepository,
                    it
                )
            }
        }

        val attributeName = attribute.localName
        val attributeNamespaceUri = attribute.namespaceURI
        if (ATTR_BACKGROUND == attributeName && (ANDROID_URI == attributeNamespaceUri).not() ||
            ATTR_BACKGROUND == attributeName && (AUTO_URI == attributeNamespaceUri).not()
        ) {
            // Not the attribute we are looking for.
            //return
        }
        val resourceUrl = ResourceUrl.parse(attribute.value) ?: return

        if (ATTR_BACKGROUND == attributeName && isVector.test(resourceUrl.name)) {
            val location = context.getNameLocation(attribute)
            val message = ISSUE_EXPLANATION
            context.report(ISSUE, attribute, location, message)
        }
    }

    companion object {

        private val APPLICABLE_ELEMENT_LIST: List<String> = listOf(VIEW)
        private val APPLICABLE_ATTRIBUTE_LIST = listOf(ATTR_BACKGROUND)
        private const val ISSUE_ID = "BackgroundVectorUsage"
        private const val ISSUE_DESCRIPTION = "Usage of vector as view background"
        private const val ISSUE_EXPLANATION =
            "To support pre-lollipop devices do not use vector as view background"
        private const val ISSUE_PRIORITY = 10

        val ISSUE = Issue.create(
            id = ISSUE_ID,
            briefDescription = ISSUE_DESCRIPTION,
            explanation = ISSUE_EXPLANATION,
            category = Category.USABILITY,
            priority = ISSUE_PRIORITY,
            severity = Severity.ERROR,
            implementation = Implementation(
                BackgroundVectorDetector::class.java,
                Scope.RESOURCE_FILE_SCOPE
            )
        )

        fun isVector(resources: ResourceRepository, name: String): Boolean {
            val items =
                resources.getResources(ResourceNamespace.TODO(), ResourceType.DRAWABLE, name)
            items.forEach {
                val sourcePath = it.source ?: return false
                val sourceFile = sourcePath.toFile() ?: return false

                if (sourcePath.fileName.endsWith(DOT_XML)) {
                    val rootTagName = XmlUtils.getRootTagName(sourceFile)
                    return TAG_VECTOR == rootTagName || TAG_ANIMATED_VECTOR == rootTagName
                }
            }
            return false
        }
    }
}