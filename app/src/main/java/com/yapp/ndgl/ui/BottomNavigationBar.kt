package com.yapp.ndgl.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.core.ui.util.dropShadow
import com.yapp.ndgl.navigation.BottomNavTab
import com.yapp.ndgl.navigation.Route

@Composable
internal fun BottomNavigationBar(
    currentTab: Route,
    onTabSelected: (Route) -> Unit,
) {
    val tabs = BottomNavTab.entries
    val selectedIndex = tabs.indexOfFirst { it.route == currentTab }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 20.dp)
            .padding(horizontal = 24.dp)
            .height(68.dp)
            .dropShadow(
                shape = RoundedCornerShape(34.dp),
                color = Color.Black.copy(alpha = 0.15f),
                offsetX = 1.dp,
                offsetY = 6.dp,
                blur = 12.dp,
            )
            .background(NDGLTheme.colors.white, RoundedCornerShape(34.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        val totalWidth = maxWidth
        val selectedWidth = maxWidth * 0.56f
        val unselectedWidth = (totalWidth - selectedWidth) / (tabs.size - 1)
        val indicatorOffset by animateDpAsState(
            targetValue = when (selectedIndex) {
                0 -> 0.dp
                1 -> unselectedWidth
                else -> totalWidth - selectedWidth
            },
            animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        )

        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(selectedWidth)
                .fillMaxHeight()
                .background(NDGLTheme.colors.black900, CircleShape),
        )

        Row(modifier = Modifier.fillMaxSize()) {
            tabs.forEach { tab ->
                val isSelected = currentTab == tab.route

                Row(
                    modifier = Modifier
                        .width(if (isSelected) selectedWidth else unselectedWidth)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { onTabSelected(tab.route) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = ImageVector.vectorResource(id = tab.icon),
                        contentDescription = tab.label,
                        tint = if (isSelected) NDGLTheme.colors.white else NDGLTheme.colors.black600,
                    )

                    AnimatedVisibility(
                        visible = isSelected,
                        enter = fadeIn() + expandHorizontally(),
                        exit = fadeOut() + shrinkHorizontally(),
                    ) {
                        Text(
                            text = tab.label,
                            color = NDGLTheme.colors.white,
                            style = NDGLTheme.typography.bodyLgMedium,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomNavigationBarPreview() {
    BottomNavigationBar(currentTab = BottomNavTab.HOME.route, onTabSelected = {})
}
