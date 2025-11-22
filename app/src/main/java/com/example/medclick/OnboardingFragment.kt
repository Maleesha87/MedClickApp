package com.example.medclick

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RawRes
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.example.medclick.R

// Constants for argument keys
private const val ARG_ILLUSTRATION_RES = "illustration_res"
private const val ARG_TITLE = "title"
private const val ARG_SUBTITLE = "subtitle"

/**
 * A reusable fragment for displaying a single onboarding screen with Lottie animations.
 */
class OnboardingFragment : Fragment() {

    // Properties to hold the data for this specific page instance
    @RawRes private var illustrationRes: Int = 0
    private var title: String? = null
    private var subtitle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve the arguments passed to this fragment instance
        arguments?.let {
            illustrationRes = it.getInt(ARG_ILLUSTRATION_RES)
            title = it.getString(ARG_TITLE)
            subtitle = it.getString(ARG_SUBTITLE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_onboarding_page, container, false)
    }

    // Called immediately after onCreateView()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find and populate the views with the provided content
        val lottieView = view.findViewById<LottieAnimationView>(R.id.iv_illustration)

        // --- THIS IS THE KEY PART ---
        // It uses the raw resource ID passed from the Activity to load the animation.
        if (illustrationRes != 0) {
            lottieView.setAnimation(illustrationRes)
            lottieView.playAnimation()
        }

        view.findViewById<TextView>(R.id.tv_title)?.text = title
        view.findViewById<TextView>(R.id.tv_subtitle)?.text = subtitle
    }

    companion object {
        /**
         * Factory method to create a new instance of this fragment.
         * Recommended way to pass arguments to a Fragment.
         */
        @JvmStatic
        fun newInstance(
            @RawRes illustrationRes: Int,
            title: String,
            subtitle: String
        ) = OnboardingFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_ILLUSTRATION_RES, illustrationRes)
                putString(ARG_TITLE, title)
                putString(ARG_SUBTITLE, subtitle)
            }
        }
    }
}