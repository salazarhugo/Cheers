package com.salazar.cheers.auth.ui.signup

import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.R
import com.salazar.cheers.core.ui.ui.AppBar
import com.salazar.cheers.core.ui.ui.CheersNavigationActions
import kotlinx.coroutines.delay

/**
 * Stateful composable that displays the Navigation route for the SignUp screen.
 *
 * @param signUpViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun SignUpRoute(
    signUpViewModel: SignUpViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by signUpViewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isSignedIn) {
//        CheersSplashScreen()
        LaunchedEffect(Unit) {
            navActions.navigateToMain()
        }
    } else
        Scaffold(
            topBar = {
                AppBar(
                    title = "Sign Up",
                    center = true,
                    backNavigation = true,
                    onNavigateBack = { navActions.navigateBack() })
            }
        ) {
            Column(
                modifier = Modifier.padding(it),
            ) {
                when (uiState.page) {
                    0 -> EmailScreen(
                        email = uiState.email,
                        isLoading = uiState.isLoading,
                        onEmailChanged = signUpViewModel::onEmailChange,
                        onNextClicked = signUpViewModel::verifyEmail,
                    )
                    1 -> SentSignInLinkToEmailScreen()
//                    2 -> PasswordScreen(
//                        password = uiState.password,
//                        onPasswordChanged = signUpViewModel::onPasswordChange,
//                        onNextClicked = signUpViewModel::verifyPassword,
//                    )
//                    2 -> CreateAccountScreen(
//                        username = uiState.username,
//                        isLoading = uiState.isLoading,
//                        acceptTerms = uiState.acceptTerms,
//                        onSignUp = signUpViewModel::createAccount,
//                        onAcceptTermsChange = signUpViewModel::onAcceptTermsChange,
//                    )
                }
            }
        }
}


@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun SentSignInLinkToEmailScreen() {
    Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
        val image = AnimatedImageVector.animatedVectorResource(R.drawable.avd_done)
        var atEnd by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            atEnd = !atEnd
            delay(1000)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.offset(y = (-50).dp)
        ) {
            Icon(
                painter = rememberAnimatedVectorPainter(image, atEnd),
                contentDescription = "Your content description",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(com.salazar.cheers.core.share.ui.GreenSurface)
                    .clickable {
                        atEnd = !atEnd
                    }
                    .padding(8.dp),
                tint = com.salazar.cheers.core.share.ui.Green
            )
            Spacer(Modifier.height(32.dp))
            Text("A sign in link has been sent to your email account")
        }
    }
}