package com.example.myapp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myapp.ui.theme.MyAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyAppTheme {
                MainScreen(
                    onOpenSecond = { startActivity(SecondActivity.intent(this, it)) },
                    onDial = { dial(it) },
                    onShare = { share(it) }
                )
            }
        }
    }

    private fun dial(raw: String) {
        val phone = raw.replace(Regex("[^0-9+]"), "")
        if (phone.count { it.isDigit() } < 5) {
            toast(getString(R.string.error_phone))
            return
        }
        try {
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
        } catch (e: ActivityNotFoundException) {
            toast(getString(R.string.error_no_dialer))
        }
    }

    private fun share(text: String) {
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        try {
            startActivity(Intent.createChooser(send, getString(R.string.share_chooser_title)))
        } catch (e: ActivityNotFoundException) {
            toast(getString(R.string.error_no_share_apps))
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}

@Composable
private fun MainScreen(
    onOpenSecond: (String) -> Unit,
    onDial: (String) -> Unit,
    onShare: (String) -> Unit,
) {
    var text by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf(false) }
    val ink = MaterialTheme.colorScheme.onSurface

    fun check(): Boolean {
        error = text.isBlank()
        return !error
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.main_title),
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                error = false
            },
            label = { Text(stringResource(R.string.input_label), color = ink) },
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = ink),
            isError = error,
            supportingText = if (error) {
                { Text(stringResource(R.string.error_empty)) }
            } else {
                null
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { if (check()) onOpenSecond(text.trim()) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.open_second))
        }

        OutlinedButton(
            onClick = { if (check()) onDial(text) },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = ink),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.dial))
        }

        OutlinedButton(
            onClick = { if (check()) onShare(text.trim()) },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = ink),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.share))
        }
    }
}