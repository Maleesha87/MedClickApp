package com.example.medclick

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.google.android.material.button.MaterialButton

/**
 * Custom Dialog class to show the success message after sign-up.
 *
 * @param context The context (usually the SignUpActivity).
 * @param onWelcomeClicked Callback function to execute when the "Welcome" button is clicked (e.g., navigate to Dashboard).
 */
class SuccessDialog(context: Context, private val onWelcomeClicked: () -> Unit) : Dialog(context) {

    init {
        // Remove the default dialog title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        // Set the custom layout
        setContentView(R.layout.dialog_success)
        // Make the dialog background transparent so the CardView corners show properly
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // Prevent closing the dialog by clicking outside of it
        setCanceledOnTouchOutside(false)

        // Find the Welcome button and set its listener
        findViewById<MaterialButton>(R.id.btn_welcome).setOnClickListener {
            dismiss() // Close the dialog
            onWelcomeClicked() // Execute the navigation logic provided by the Activity
        }
    }
}