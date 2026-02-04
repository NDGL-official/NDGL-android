package com.yapp.ndgl.core.ui.designsystem

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.ndgl.core.ui.R
import com.yapp.ndgl.core.ui.theme.NDGLTheme

object NDGLNavigationBarAttr {
    enum class TextAlignType {
        START,
        CENTER,
    }
}

@Composable
fun NDGLNavigationBar(
    textAlignType: NDGLNavigationBarAttr.TextAlignType,
    modifier: Modifier = Modifier,
    headline: String? = null,
    @DrawableRes leadingIcon: Int? = null,
    onLeadingIconClick: () -> Unit = {},
    trailingContents: (@Composable RowScope.() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 24.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leadingIcon?.let { icon ->
            NDGLNavigationIcon(
                icon = icon,
                onClick = onLeadingIconClick,
            )
        }

        headline?.let { text ->
            Text(
                text = text,
                modifier = Modifier.weight(1f),
                style = NDGLTheme.typography.bodyLgMedium,
                color = NDGLTheme.colors.black700,
                textAlign = when (textAlignType) {
                    NDGLNavigationBarAttr.TextAlignType.START -> TextAlign.Start
                    NDGLNavigationBarAttr.TextAlignType.CENTER -> TextAlign.Center
                },
            )
        } ?: Spacer(modifier = Modifier.weight(1f))

        if (trailingContents != null) {
            trailingContents()
        } else {
            Box(modifier = Modifier.size(40.dp))
        }
    }
}

@Composable
fun NDGLNavigationIcon(
    @DrawableRes icon: Int,
    onClick: () -> Unit = {},
) {
    Icon(
        imageVector = ImageVector.vectorResource(icon),
        contentDescription = null,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(6.dp),
        tint = NDGLTheme.colors.black600,
    )
}

@Preview(showBackground = true)
@Composable
private fun NDGLNavigationBarCenterPreview() {
    NDGLTheme {
        NDGLNavigationBar(
            textAlignType = NDGLNavigationBarAttr.TextAlignType.CENTER,
            headline = "미리보기",
            leadingIcon = R.drawable.ic_28_chevron_left,
            trailingContents = {
                NDGLNavigationIcon(
                    icon = R.drawable.ic_28_search,
                    onClick = {},
                )
                NDGLNavigationIcon(
                    icon = R.drawable.ic_28_settings,
                    onClick = {},
                )
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NDGLNavigationBarPreview() {
    NDGLTheme {
        NDGLNavigationBar(
            textAlignType = NDGLNavigationBarAttr.TextAlignType.CENTER,
            headline = "미리보기",
            leadingIcon = R.drawable.ic_28_chevron_left,
            trailingContents = {
                NDGLNavigationIcon(
                    icon = R.drawable.ic_28_search,
                    onClick = {},
                )
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NDGLNavigationBarNoTrailingPreview() {
    NDGLTheme {
        NDGLNavigationBar(
            textAlignType = NDGLNavigationBarAttr.TextAlignType.CENTER,
            headline = "미리보기",
            leadingIcon = R.drawable.ic_28_chevron_left,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NDGLNavigationBarStartPreview() {
    NDGLTheme {
        NDGLNavigationBar(
            textAlignType = NDGLNavigationBarAttr.TextAlignType.START,
            headline = "미리보기",
            leadingIcon = R.drawable.ic_28_chevron_left,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NDGLNavigationBarNoHeadlinePreview() {
    NDGLTheme {
        NDGLNavigationBar(
            textAlignType = NDGLNavigationBarAttr.TextAlignType.START,
            leadingIcon = R.drawable.ic_28_menu,
            trailingContents = {
                NDGLNavigationIcon(
                    icon = R.drawable.ic_28_search,
                    onClick = {},
                )
            },
        )
    }
}
