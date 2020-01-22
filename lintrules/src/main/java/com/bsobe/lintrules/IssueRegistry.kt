package com.bsobe.lintrules

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.Issue
import com.bsobe.lintrules.detector.BackgroundVectorDetector

class IssueRegistry(
    override val issues: List<Issue> = listOf(
        BackgroundVectorDetector.ISSUE
    )
) : IssueRegistry()