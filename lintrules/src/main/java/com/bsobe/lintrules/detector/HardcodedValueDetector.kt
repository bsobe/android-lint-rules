package com.bsobe.lintrules.detector

import com.android.tools.lint.detector.api.*
import org.w3c.dom.Attr

class HardcodedValueDetector : LayoutDetector() {


    override fun getApplicableAttributes(): Collection<String>? {
        return ALL
    }

    override fun visitAttribute(context: XmlContext, attribute: Attr) {
        val isHardCoded = isHardCoded(attribute.value)
        if (isHardCoded && context.isEnabled(ISSUE)) {
            val messageLocation = context.getLocation(attribute)
            context.report(ISSUE, attribute, messageLocation, ISSUE_MESSAGE)
        }
    }

    private fun isHardCoded(value: String): Boolean {
        if (value[0] == '0') {
            // 0px or any other type is fine.
            return false
        }
        val valueType = findValueType(value)
        return if (valueType == null) {
            false
        } else {
            value.matches(("\\d+$valueType").toRegex())
        }
    }

    private fun findValueType(value: String): String? {
        if (value.endsWith("mm")) {
            return "mm"
        }
        if (value.endsWith("in")) {
            return "in"
        }
        if (value.endsWith("pt")) {
            return "pt"
        }
        if (value.endsWith("px")) {
            return "px"
        }
        if (value.endsWith("dp")) {
            return "dp"
        }
        if (value.endsWith("dip")) {
            return "dip"
        }
        if (value.endsWith("sp")) {
            return "sp"
        }
        return null
    }

    companion object {

        private const val ISSUE_ID = "HardcodedValueUsage"
        private const val ISSUE_DESCRIPTION = "Looks for use of the hardcoded dimension value"
        private const val ISSUE_MESSAGE = "Prefer read value from dimens.xml"
        private const val ISSUE_EXPLANATION =
            "To reusability of dimensions, supporting different dimension on various screen types and reading in from code do not use hardcoded dimension value"
        private const val ISSUE_PRIORITY = 4

        val ISSUE = Issue.create(
            id = ISSUE_ID,
            briefDescription = ISSUE_DESCRIPTION,
            explanation = ISSUE_EXPLANATION,
            category = Category.CORRECTNESS,
            priority = ISSUE_PRIORITY,
            severity = Severity.WARNING,
            implementation = Implementation(
                HardcodedValueDetector::class.java,
                Scope.RESOURCE_FILE_SCOPE
            )
        ).addMoreInfo("https://stackoverflow.com/a/47320767/7001152")
    }
}
