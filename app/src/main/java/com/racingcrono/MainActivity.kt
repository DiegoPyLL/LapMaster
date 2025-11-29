package com.racingcrono

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.core.view.WindowCompat
import com.racingcrono.ui.RacingCronoViewModel
import com.racingcrono.ui.model.Screen
import com.racingcrono.ui.screens.GraphScreen
import com.racingcrono.ui.screens.LapsScreen
import com.racingcrono.ui.screens.MenuScreen
import com.racingcrono.ui.screens.SectorsScreen
import com.racingcrono.ui.theme.RacingCronoTheme
import com.racingcrono.ui.theme.RacingGreenDark

private data class NavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.statusBarColor = android.graphics.Color.BLACK
        setContent {
            val vm: RacingCronoViewModel = viewModel()
            RacingCronoApp(viewModel = vm)
        }
    }
}

@Composable
private fun RacingCronoApp(viewModel: RacingCronoViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedScreen = uiState.selectedScreen

    RacingCronoTheme(useDarkTheme = uiState.settings.darkTheme) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Scaffold(
                topBar = {
                    TopMenuBar(
                        selectedScreen = selectedScreen,
                        onSelected = { viewModel.onScreenSelected(it) }
                    )
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    when (selectedScreen) {
                        Screen.LAPS -> LapsScreen(
                            state = uiState.laps,
                            weather = uiState.weather,
                            gps = uiState.gps,
                            summary = uiState.summary,
                            settings = uiState.settings,
                            onAddPilot = { viewModel.onAddPilot() }
                        )
                        Screen.MENU -> MenuScreen(
                            state = uiState.menu,
                            weather = uiState.weather,
                            gps = uiState.gps,
                            settings = uiState.settings,
                            onAddPilot = { viewModel.onAddPilot() },
                            onToggleTheme = { viewModel.onToggleTheme() },
                            onToggleHand = { viewModel.onToggleHandPreference() }
                        )
                        Screen.SECTORS -> SectorsScreen(
                            state = uiState.sectors,
                            settings = uiState.settings,
                            weather = uiState.weather,
                            gps = uiState.gps
                        )
                        Screen.GRAPHS -> GraphScreen(
                            state = uiState.graphs,
                            onYearSelected = { viewModel.onYearSelected(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopMenuBar(
    selectedScreen: Screen,
    onSelected: (Screen) -> Unit
) {
    val items = listOf(
        NavItem(Screen.MENU, "Menú", Icons.Outlined.Menu),
        NavItem(Screen.LAPS, "Tiempos", Icons.Outlined.Timer),
        NavItem(Screen.SECTORS, "Sectores", Icons.Outlined.ListAlt),
        NavItem(Screen.GRAPHS, "Gráficos", Icons.Outlined.Analytics)
    )
    val selectedIndex = items.indexOfFirst { it.screen == selectedScreen }.coerceAtLeast(0)

    TabRow(
        selectedTabIndex = selectedIndex,
        containerColor = Color(0xFF101010),
        contentColor = Color.White,
        modifier = Modifier.statusBarsPadding(),
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                color = RacingGreenDark
            )
        }
    ) {
        items.forEach { item ->
            val selected = item.screen == selectedScreen
            Tab(
                selected = selected,
                onClick = { onSelected(item.screen) },
                selectedContentColor = Color.White,
                unselectedContentColor = Color.LightGray,
                text = { Text(item.label, fontSize = 13.sp) },
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) }
            )
        }
    }
}
