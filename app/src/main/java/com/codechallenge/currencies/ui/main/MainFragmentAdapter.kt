package com.codechallenge.currencies.ui.main

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.size.Scale
import coil.transform.CircleCropTransformation
import com.codechallenge.currencies.R
import com.codechallenge.currencies.data.Rate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainFragmentAdapter @Inject constructor() :
    RecyclerView.Adapter<MainFragmentAdapter.MainFragmentViewHolder>() {

    private var rates: ArrayList<Rate> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainFragmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.curency_item, parent, false)
        return MainFragmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainFragmentViewHolder, position: Int) {
        val rate = rates[position]
        val firstCurrency = rate.symbol?.subSequence(0, 3)
        val secondCurrency = rate.symbol?.subSequence(3, 6)
        val oldPrice = holder.value.text.toString().toFloatOrNull()
        val newPrice = rate.price?.let { it.toFloatOrNull() }
        holder.value.setTextColor(countDifference(oldPrice, newPrice))
        newPrice?.run { holder.value.text = String.format("%.4f", this) }
        holder.firstImage.load(getResourceFrom(firstCurrency)) {
            scale(Scale.FIT)
            placeholder(R.drawable.question)
            transformations(CircleCropTransformation())
        }
        holder.secondImage.load(getResourceFrom(secondCurrency)) {
            scale(Scale.FIT)
            placeholder(R.drawable.question)
            transformations(CircleCropTransformation())
        }
    }

    private fun countDifference(oldPrice: Float?, newPrice: Float?): Int {
        if (oldPrice == null || newPrice == null) return Color.CYAN
        val difference = oldPrice - newPrice
        return when {
            difference <= 0 -> {
                Color.GREEN
            }
            else -> {
                Color.RED
            }
        }
    }

    private fun getResourceFrom(currency: CharSequence?): Int {
        return when (currency) {
            "EUR" -> R.drawable.eu
            "GBP" -> R.drawable.gb
            "CHF" -> R.drawable.sz
            "USD" -> R.drawable.us
            else -> R.drawable.question
        }
    }

    override fun getItemCount(): Int {
        return rates.size
    }

    fun setNewRates(newRates: ArrayList<Rate>) {
        rates = newRates
        notifyDataSetChanged()
    }

    class MainFragmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val firstImage: ImageView = itemView.findViewById(R.id.first_currency_iv)
        val secondImage: ImageView = itemView.findViewById(R.id.second_currency_iv)
        val value: TextView = itemView.findViewById(R.id.currency_value_tv)
    }
}