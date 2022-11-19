package com.example.mobv2.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.example.mobv2.R

class RatesGroup(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
    LinearLayout(context, attrs, defStyleAttr, defStyleRes)
{
    var rateUpButton: ImageButton = ImageButton(getContext())
        private set
    var ratesCountView: TextView? = TextView(getContext())
        private set
    var rateDownButton: ImageButton = ImageButton(getContext())
        private set

    @JvmOverloads
    constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : this(
        context,
        attrs,
        defStyleAttr,
        0)

    override fun onViewAdded(child: View)
    {
        when (child.id)
        {
            R.id.rate_up_button -> rateUpButton = child as ImageButton
            R.id.rates_count_view -> ratesCountView = child as TextView
            R.id.rate_down_button -> rateDownButton = child as ImageButton
        }
    }

    fun setOnRateUpClickListener(listener: OnClickListener)
    {
        rateUpButton.setOnClickListener { view ->
            onRateButtonClick(view)
            listener.onClick(view)
        }
    }

    fun setOnRateDownClickListener(listener: OnClickListener)
    {
        rateDownButton.setOnClickListener { view: View ->
            onRateButtonClick(view)
            listener.onClick(view)
        }
    }

    private fun onRateButtonClick(view: View)
    {
        val rateButton = view as ImageButton
        if (rateButton.isSelected)
        {
            rateButton.isSelected = false
        }
        else
        {
            deselectRateButtons()
            rateButton.isSelected = true
        }
    }

    private fun deselectRateButtons()
    {
        rateUpButton.isSelected = false
        rateDownButton.isSelected = false
    }
}