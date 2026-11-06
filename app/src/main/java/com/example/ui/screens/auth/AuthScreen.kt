package com.example.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.auth.GoogleAuthHelper
import com.example.data.model.UserProfile
import com.example.ui.theme.*
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    onSignInWithEmail: (email: String, password: String, onSuccess: (UserProfile) -> Unit, onError: (String) -> Unit) -> Unit,
    onCreateAccountWithEmail: (fullName: String, email: String, password: String, className: String, section: String, studentId: String, onSuccess: (UserProfile) -> Unit, onError: (String) -> Unit) -> Unit,
    onGoogleSignInSuccess: (firebaseUser: FirebaseUser) -> Unit,
    onErrorOccurred: (String) -> Unit
) {
    // Mode: false = Sign In, true = Create Account
    var isCreateAccountMode by remember { mutableStateOf(false) }

    // Form inputs
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var className by remember { mutableStateOf("10th") }
    var section by remember { mutableStateOf("A") }
    var studentId by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    var isGoogleSigningIn by remember { mutableStateOf(false) }
    var showSha1Dialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("auth_screen"),
        color = BackgroundLight
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // Official School Logo
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(2.5.dp, PrimaryGreen, CircleShape)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.school_logo),
                        contentDescription = "St. Mary's School Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // App Name
                Text(
                    text = "Mary's Connect",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Subtitle
                Text(
                    text = "St. Mary's Sr. Sec. School, Gursarai",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Tagline
                Text(
                    text = "School Events • Competitions • Activities",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Centered Login / Registration Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Color(0x1A087A43))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp)
                    ) {
                        // Card Header with Mode Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isCreateAccountMode) "Create Account" else "Sign In",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )

                            TextButton(
                                onClick = {
                                    isCreateAccountMode = !isCreateAccountMode
                                    errorMessage = null
                                },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isCreateAccountMode) "Sign In instead" else "New account?",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PrimaryGreen
                                )
                            }
                        }

                        Text(
                            text = if (isCreateAccountMode)
                                "Register with your school email to discover & participate"
                            else
                                "Enter your registered credentials to access your dashboard",
                            fontSize = 11.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(bottom = 14.dp)
                        )

                        // If in Create Account Mode: Full Name Field
                        if (isCreateAccountMode) {
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = {
                                    fullName = it
                                    errorMessage = null
                                },
                                label = { Text("Full Name") },
                                placeholder = { Text("e.g. Harsh Patel", fontSize = 12.sp) },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
                                },
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_name_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryGreen,
                                    focusedLabelColor = PrimaryGreen,
                                    cursorColor = PrimaryGreen
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // Email Field
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                errorMessage = null
                            },
                            label = { Text("Email Address") },
                            placeholder = {
                                Text(
                                    if (isCreateAccountMode) "e.g. yourname@stmarysgursarai.edu.in"
                                    else "e.g. a82076505@gmail.com or student email",
                                    fontSize = 12.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_email_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryGreen,
                                focusedLabelColor = PrimaryGreen,
                                cursorColor = PrimaryGreen
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Password Field
                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                errorMessage = null
                            },
                            label = { Text("Password") },
                            placeholder = { Text(if (isCreateAccountMode) "Min. 6 characters" else "Enter password", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                        tint = TextMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_password_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryGreen,
                                focusedLabelColor = PrimaryGreen,
                                cursorColor = PrimaryGreen
                            )
                        )

                        // If in Create Account Mode: Class, Section and Student ID
                        if (isCreateAccountMode) {
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedTextField(
                                    value = className,
                                    onValueChange = { className = it },
                                    label = { Text("Class") },
                                    placeholder = { Text("10th", fontSize = 12.sp) },
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PrimaryGreen,
                                        focusedLabelColor = PrimaryGreen,
                                        cursorColor = PrimaryGreen
                                    )
                                )

                                OutlinedTextField(
                                    value = section,
                                    onValueChange = { section = it },
                                    label = { Text("Section") },
                                    placeholder = { Text("A", fontSize = 12.sp) },
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PrimaryGreen,
                                        focusedLabelColor = PrimaryGreen,
                                        cursorColor = PrimaryGreen
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = studentId,
                                onValueChange = { studentId = it },
                                label = { Text("Student / Roll ID (Optional)") },
                                placeholder = { Text("e.g. SMSG-1042", fontSize = 12.sp) },
                                leadingIcon = {
                                    Icon(Icons.Default.Badge, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
                                },
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryGreen,
                                    focusedLabelColor = PrimaryGreen,
                                    cursorColor = PrimaryGreen
                                )
                            )
                        }

                        // Error Banner
                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                color = Color(0xFFFEE2E2),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = errorMessage ?: "",
                                        color = Color(0xFFB91C1C),
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                    if (errorMessage?.contains("SHA-1", ignoreCase = true) == true ||
                                        errorMessage?.contains("reauth", ignoreCase = true) == true ||
                                        errorMessage?.contains("Firebase", ignoreCase = true) == true) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "View SHA-1 Fingerprint details ➔",
                                            color = PrimaryGreen,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.clickable { showSha1Dialog = true }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Primary Action Button (Sign In or Create Account)
                        Button(
                            onClick = {
                                if (isCreateAccountMode) {
                                    if (fullName.isBlank()) {
                                        errorMessage = "Please enter your full name."
                                    } else if (email.isBlank() || !email.contains("@")) {
                                        errorMessage = "Please enter a valid email address."
                                    } else if (password.length < 6) {
                                        errorMessage = "Password must be at least 6 characters."
                                    } else {
                                        isSubmitting = true
                                        errorMessage = null
                                        onCreateAccountWithEmail(
                                            fullName.trim(),
                                            email.trim(),
                                            password,
                                            className.trim(),
                                            section.trim(),
                                            studentId.trim(),
                                            { _ -> isSubmitting = false },
                                            { error ->
                                                isSubmitting = false
                                                errorMessage = error
                                            }
                                        )
                                    }
                                } else {
                                    if (email.isBlank()) {
                                        errorMessage = "Please enter your registered email address."
                                    } else if (password.isBlank()) {
                                        errorMessage = "Please enter your password."
                                    } else {
                                        isSubmitting = true
                                        errorMessage = null
                                        onSignInWithEmail(
                                            email.trim(),
                                            password,
                                            { _ -> isSubmitting = false },
                                            { error ->
                                                isSubmitting = false
                                                errorMessage = error
                                            }
                                        )
                                    }
                                }
                            },
                            enabled = !isSubmitting && !isGoogleSigningIn,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryGreen,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("auth_submit_btn")
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = if (isCreateAccountMode) "Create Account" else "Sign In",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // OR Divider and Google Sign-In (shown on Sign-In screen)
                        if (!isCreateAccountMode) {
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                HorizontalDivider(
                                    modifier = Modifier.weight(1f),
                                    color = BorderLight
                                )
                                Text(
                                    text = "OR",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextMuted,
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )
                                HorizontalDivider(
                                    modifier = Modifier.weight(1f),
                                    color = BorderLight
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedButton(
                                onClick = {
                                    isGoogleSigningIn = true
                                    errorMessage = null
                                    coroutineScope.launch {
                                        val result = GoogleAuthHelper.signInWithGoogle(context)
                                        isGoogleSigningIn = false
                                        result.onSuccess { firebaseUser ->
                                            onGoogleSignInSuccess(firebaseUser)
                                        }.onFailure { exception ->
                                            val msg = exception.localizedMessage ?: "Google Sign-In failed"
                                            if (msg.contains("16") || msg.contains("reauth", ignoreCase = true) || msg.contains("SHA-1", ignoreCase = true)) {
                                                errorMessage = "Google Sign-In requires the SHA-1 certificate in Firebase Console. You can also sign in or create an account using your email above."
                                                showSha1Dialog = true
                                            } else if (!msg.contains("cancelled", ignoreCase = true)) {
                                                errorMessage = msg
                                                onErrorOccurred(msg)
                                            }
                                        }
                                    }
                                },
                                enabled = !isSubmitting && !isGoogleSigningIn,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.White,
                                    contentColor = TextDark
                                ),
                                border = BorderStroke(1.dp, BorderLight),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("google_signin_btn")
                            ) {
                                if (isGoogleSigningIn) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = PrimaryGreen,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_google_logo),
                                            contentDescription = "Google Logo",
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "Continue with Google",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TextDark
                                        )
                                    }
                                }
                            }
                        }

                        // Toggle button at bottom of card
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isCreateAccountMode) "Already have an account?" else "New student or member?",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isCreateAccountMode) "Sign In" else "Create an Account",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGreen,
                                modifier = Modifier.clickable {
                                    isCreateAccountMode = !isCreateAccountMode
                                    errorMessage = null
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Footnote
                Text(
                    text = "Contact School Administrator for account access",
                    fontSize = 11.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }

    // --- SHA-1 Setup Help Dialog ---
    if (showSha1Dialog) {
        AlertDialog(
            onDismissRequest = { showSha1Dialog = false },
            title = {
                Text(
                    text = "Firebase Google Sign-In Setup",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextDark
                )
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = "To allow Google Play Services to complete Google Sign-In, your app's SHA-1 certificate must be registered in your Firebase project (st-marys-school-app):",
                        fontSize = 12.sp,
                        color = TextMuted,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Package Name:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    SelectionContainer {
                        Text("com.stmarysschool.gursarai", fontSize = 11.sp, color = PrimaryGreen)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("SHA-1 Fingerprint:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Surface(
                        color = BackgroundLight,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        SelectionContainer {
                            Text(
                                text = "56:9B:9C:7A:E2:09:1F:F6:0B:93:B4:76:5B:4B:2E:DD:9F:EE:8A:28",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextDark,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text("Easy Steps in Firebase Console:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text(
                        text = "1. Open Firebase Console > Project Settings\n2. Under 'Your apps' select Android (com.stmarysschool.gursarai)\n3. Click 'Add fingerprint' and paste this SHA-1\n4. You can also sign in right now with your email above!",
                        fontSize = 11.sp,
                        color = TextMuted,
                        lineHeight = 16.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString("56:9B:9C:7A:E2:09:1F:F6:0B:93:B4:76:5B:4B:2E:DD:9F:EE:8A:28"))
                        Toast.makeText(context, "SHA-1 copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Text("Copy SHA-1")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSha1Dialog = false }) {
                    Text("Close", color = TextMuted)
                }
            }
        )
    }
}
