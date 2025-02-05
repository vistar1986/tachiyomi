package eu.kanade.tachiyomi.ui.browse.extension.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.browse.ExtensionDetailsScreen
import eu.kanade.presentation.components.LoadingScreen
import eu.kanade.presentation.util.LocalRouter
import eu.kanade.tachiyomi.ui.base.controller.pushController
import kotlinx.coroutines.flow.collectLatest

class ExtensionDetailsScreen(
    private val pkgName: String,
) : Screen {

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val screenModel = rememberScreenModel { ExtensionDetailsScreenModel(pkgName = pkgName, context = context) }
        val state by screenModel.state.collectAsState()

        if (state.isLoading) {
            LoadingScreen()
            return
        }

        val router = LocalRouter.currentOrThrow
        val uriHandler = LocalUriHandler.current

        ExtensionDetailsScreen(
            navigateUp = router::popCurrentController,
            state = state,
            onClickSourcePreferences = { router.pushController(SourcePreferencesController(it)) },
            onClickWhatsNew = { uriHandler.openUri(screenModel.getChangelogUrl()) },
            onClickReadme = { uriHandler.openUri(screenModel.getReadmeUrl()) },
            onClickEnableAll = { screenModel.toggleSources(true) },
            onClickDisableAll = { screenModel.toggleSources(false) },
            onClickClearCookies = { screenModel.clearCookies() },
            onClickUninstall = { screenModel.uninstallExtension() },
            onClickSource = { screenModel.toggleSource(it) },
        )

        LaunchedEffect(Unit) {
            screenModel.events.collectLatest { event ->
                if (event is ExtensionDetailsEvent.Uninstalled) {
                    router.popCurrentController()
                }
            }
        }
    }
}
