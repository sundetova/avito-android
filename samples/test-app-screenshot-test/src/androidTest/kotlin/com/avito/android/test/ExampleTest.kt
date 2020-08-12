package com.avito.android.test

import android.content.Context
import android.widget.Button
import android.widget.LinearLayout
import androidx.test.rule.ActivityTestRule
import com.avito.android.activity.ScreenshotTestActivity
import com.avito.android.test.annotations.ScreenshotTest
import com.avito.android.test.screenshot_test.test.BaseScreenshotTest
import com.avito.android.test.screenshot_test.test.IdlieableActivity
import com.avito.android.test.screenshot_test.test.TestTheme
import org.junit.Rule
import org.junit.Test

@ScreenshotTest
class ExampleTest: BaseScreenshotTest<Button>() {

    override val styleAttrs = listOf(0)

    override val themes = listOf(TestTheme("theme", android.R.attr.theme))

    @Rule
    @JvmField
    var activityRule: ActivityTestRule<ScreenshotTestActivity> = ActivityTestRule(
        ScreenshotTestActivity::class.java)

    override fun createView(
        context: Context,
        styleAttr: Int,
        applyActionToView: (view: Button) -> Unit
    ): Button {
        val button = Button(context, null, styleAttr)
        button.text = "Button"
        button.layoutParams = LinearLayout.LayoutParams(
            300,
            100
        )
        return button
    }

    override fun getActivity(): IdlieableActivity = activityRule.activity

    override fun createViewStates(): HashMap<String, (view: Button) -> Unit> {
        val states = HashMap<String, (view: Button) -> Unit>()
        states["Simple"] = {}
        return states
    }

    @Test
    fun runTest() {
        compareScreenshotsTest()
    }
}