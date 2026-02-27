package com.yapp.ndgl.core.ui.designsystem

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.ndgl.core.ui.R
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

object NDGLChipTabAttr {
    data class Tab(
        val tag: String,
        val name: String,
        @param:DrawableRes val leadingIcon: Int? = null,
    )
}

@Composable
fun NDGLChipTab(
    tabs: PersistentList<NDGLChipTabAttr.Tab>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding,
    ) {
        itemsIndexed(
            items = tabs,
            key = { index, tab -> "${index}_${tab.tag}" },
        ) { index, tab ->
            NDGLChipTabItem(
                isSelected = index == selectedIndex,
                name = tab.name,
                leadingIcon = tab.leadingIcon,
                onTabSelected = { onTabSelected(index) },
            )
        }
    }
}

@Composable
private fun NDGLChipTabItem(
    isSelected: Boolean,
    name: String,
    onTabSelected: () -> Unit,
    @DrawableRes leadingIcon: Int?,
) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .chipStyle(isSelected)
            .clickable(onClick = onTabSelected)
            .widthIn(min = 72.dp)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(
            space = 4.dp,
            alignment = Alignment.CenterHorizontally,
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leadingIcon?.let { icon ->
            Icon(
                imageVector = ImageVector.vectorResource(icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = isSelected.chipContentColor(),
            )
        }

        Text(
            text = name,
            style = NDGLTheme.typography.bodyMdMedium,
            color = isSelected.chipContentColor(),
        )
    }
}

@Composable
private fun Modifier.chipStyle(
    isSelected: Boolean,
): Modifier = this.then(
    if (isSelected) {
        Modifier.background(NDGLTheme.colors.black900, CircleShape)
    } else {
        Modifier
            .background(NDGLTheme.colors.white, CircleShape)
            .border(
                width = 1.dp,
                color = NDGLTheme.colors.black200,
                shape = CircleShape,
            )
    },
)

@Composable
private fun Boolean.chipContentColor() = if (this) {
    NDGLTheme.colors.white
} else {
    NDGLTheme.colors.black400
}

@Preview(showBackground = true)
@Composable
private fun NDGLChipTabPreview() {
    NDGLTheme {
        var selectedIndex by remember { mutableIntStateOf(0) }

        NDGLChipTab(
            tabs = persistentListOf(
                NDGLChipTabAttr.Tab(
                    tag = "1",
                    name = "1일차",
                    leadingIcon = R.drawable.ic_20_tv,
                ),
                NDGLChipTabAttr.Tab(
                    tag = "1",
                    name = "2일차",
                    leadingIcon = R.drawable.ic_20_tv,
                ),
                NDGLChipTabAttr.Tab(
                    tag = "1",
                    name = "3일차",
                ),
            ),
            selectedIndex = selectedIndex,
            onTabSelected = { selectedIndex = it },
        )
    }
}
