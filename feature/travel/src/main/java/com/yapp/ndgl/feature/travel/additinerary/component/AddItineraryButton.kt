package com.yapp.ndgl.feature.travel.additinerary.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yapp.ndgl.core.ui.R
import com.yapp.ndgl.core.ui.designsystem.NDGLCTAButton
import com.yapp.ndgl.core.ui.designsystem.NDGLCTAButtonAttr
import com.yapp.ndgl.core.ui.theme.NDGLTheme

@Composable
internal fun AddItineraryButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    clickAddItinerary: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(NDGLTheme.colors.white)
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        NDGLCTAButton(
            modifier = Modifier.fillMaxWidth(),
            type = NDGLCTAButtonAttr.Type.PRIMARY,
            size = NDGLCTAButtonAttr.Size.LARGE,
            status = if (enabled) NDGLCTAButtonAttr.Status.ACTIVE else NDGLCTAButtonAttr.Status.DISABLED,
            label = stringResource(R.string.add_schedule),
            onClick = { if (enabled) clickAddItinerary() },
        )
    }
}
