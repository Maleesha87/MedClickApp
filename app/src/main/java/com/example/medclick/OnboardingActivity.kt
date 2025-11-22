package com.example.medclick

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var btnNext: Button
    private lateinit var dotsIndicator: LinearLayout

    // Data structure for all onboarding pages
    private val onboardingPages = listOf(
        OnboardingPage(
            R.raw.first,
            "Request for an\nAmbulance",
            "Quick and reliable service at your fingertips.\nYour safety is our priority."
        ),
        // Add more pages here for a complete onboarding flow
        OnboardingPage(
            R.raw.second,
            "Join the Lifesaving\nCommunity",
            "Upgrade your Health Services.\nGet Feedback on your work"
        ),
        OnboardingPage(
            R.raw.third,
            "Book Your Favourite Doctor\nAnytime!",
            "With MedClick, book your preferred\ndoctor anytime with convenience."
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.view_pager)
        btnNext = findViewById(R.id.btn_next)
        dotsIndicator = findViewById(R.id.dots_indicator)

        // Set up the ViewPager with the adapter
        viewPager.adapter = OnboardingAdapter(this, onboardingPages)

        // Initial setup for the indicator dots
        setupDotsIndicator(onboardingPages.size)
        // Update the dots when the page changes
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateDotsIndicator(position)
                updateButtonText(position)
            }
        })

        // Handle the Next button click
        btnNext.setOnClickListener {
            val currentItem = viewPager.currentItem
            if (currentItem < onboardingPages.size - 1) {
                // Move to the next page
                viewPager.currentItem = currentItem + 1
            } else {
                // Last page, transition to the main app flow
                navigateToLoginRegisterActivity()
            }
        }

        // Ensure initial button text and dots are correct for page 0
        updateButtonText(0)
        updateDotsIndicator(0)
    }


    data class OnboardingPage(
        val illustrationRes: Int, // This now holds the R.raw.* ID
        val title: String,
        val subtitle: String
    )

    /**
     * Adapter to link the ViewPager2 to the list of fragments.
     */
    private class OnboardingAdapter(
        activity: AppCompatActivity,
        private val pages: List<OnboardingPage>
    ) : FragmentStateAdapter(activity) {

        override fun getItemCount(): Int = pages.size

        override fun createFragment(position: Int): Fragment {
            val page = pages[position]
            return OnboardingFragment.newInstance(
                page.illustrationRes,
                page.title,
                page.subtitle
            )
        }
    }

    // --- Indicator Dots Logic ---

    private fun setupDotsIndicator(count: Int) {
        dotsIndicator.removeAllViews()
        for (i in 0 until count) {
            val dot = View(this).apply {
                // Initial size and margin for the dot
                val dotSize = resources.getDimensionPixelSize(R.dimen.dot_size)
                val dotMargin = resources.getDimensionPixelSize(R.dimen.dot_margin)
                layoutParams = LinearLayout.LayoutParams(dotSize, dotSize).apply {
                    setMargins(dotMargin, 0, dotMargin, 0)
                }
                // Set the initial shape/color (inactive)
                setBackgroundResource(R.drawable.dot_inactive)
            }
            dotsIndicator.addView(dot)
        }
    }

    private fun updateDotsIndicator(currentPosition: Int) {
        for (i in 0 until dotsIndicator.childCount) {
            val dot = dotsIndicator.getChildAt(i)
            // Use the purple color for active dot and gray for inactive
            val drawableRes = if (i == currentPosition) R.drawable.dot_active else R.drawable.dot_inactive
            dot.setBackgroundResource(drawableRes)
        }
    }

    private fun updateButtonText(position: Int) {
        if (position == onboardingPages.size - 1) {
            btnNext.text = "Get Started"
        } else {
            btnNext.text = "Next"
        }
    }

    private fun navigateToLoginRegisterActivity() {
        startActivity(Intent(this, LoginRegisterActivity::class.java))
        finish()
    }
}