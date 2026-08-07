package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyberGridBackground
import com.example.ui.screens.*
import com.example.ui.theme.CyberDialerTheme
import com.example.ui.theme.GlassBorderCyan
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.NeonCyan

data class CyberTabItem(
    val title: String,
    val icon: ImageVector
)

@Composable
fun MainContainer(viewModel: CyberViewModel) {
    val currentTheme by viewModel.themeMode.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    CyberDialerTheme(themeMode = currentTheme) {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "ScreenTransition"
        ) { screen ->
            when (screen) {
                is CyberScreen.Splash -> SplashScreen(viewModel = viewModel)
                is CyberScreen.Lock -> SecurityLockScreen(viewModel = viewModel)
                is CyberScreen.ActiveCall -> ActiveCallScreen(viewModel = viewModel)
                is CyberScreen.FakeChatDetail -> FakeChatDetailScreen(
                    chatId = screen.chatId,
                    contactName = screen.name,
                    viewModel = viewModel
                )
                is CyberScreen.Main -> {
                    CyberGridBackground {
                        Scaffold(
                            bottomBar = {
                                CyberBottomNavigationBar(
                                    selectedTab = selectedTab,
                                    onTabSelected = { selectedTab = it }
                                )
                            },
                            containerColor = Color.Transparent
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                when (selectedTab) {
                                    0 -> DashboardScreen(viewModel = viewModel, onNavigateTab = { selectedTab = it })
                                    1 -> DialPadScreen(viewModel = viewModel)
                                    2 -> CallLogsScreen(viewModel = viewModel)
                                    3 -> ContactsScreen(viewModel = viewModel)
                                    4 -> FakeCallScreen(viewModel = viewModel)
                                    5 -> FakeChatScreen(viewModel = viewModel)
                                    6 -> CallBlockerScreen(viewModel = viewModel)
                                    7 -> SettingsScreen(viewModel = viewModel)
                                    8 -> AboutScreen(viewModel = viewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CyberBottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf(
        CyberTabItem("Home", Icons.Default.Dashboard),
        CyberTabItem("Dial", Icons.Default.Dialpad),
        CyberTabItem("Logs", Icons.Default.Call),
        CyberTabItem("Contacts", Icons.Default.Contacts),
        CyberTabItem("Fake Call", Icons.Default.PhoneCallback),
        CyberTabItem("Fake Chat", Icons.Default.Chat),
        CyberTabItem("Blocker", Icons.Default.Block),
        CyberTabItem("Settings", Icons.Default.Settings),
        CyberTabItem("About", Icons.Default.Info)
    )

    ScrollableTabRow(
        selectedTabIndex = selectedTab,
        containerColor = GlassSurface,
        contentColor = NeonCyan,
        edgePadding = 8.dp,
        indicator = {},
        divider = {}
    ) {
        tabs.forEachIndexed { index, tab ->
            val isSelected = selectedTab == index
            Tab(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) NeonCyan.copy(alpha = 0.25f) else Color.Transparent)
                    .border(1.dp, if (isSelected) NeonCyan else Color.Transparent, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title,
                        tint = if (isSelected) NeonCyan else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = tab.title,
                        color = if (isSelected) NeonCyan else Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                    )
                }
            }
        }
    }
}
