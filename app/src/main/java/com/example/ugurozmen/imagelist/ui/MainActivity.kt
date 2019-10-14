package com.example.ugurozmen.imagelist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ugurozmen.imagelist.R
import com.example.ugurozmen.imagelist.ui.MainViewModel.Content.*
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasAndroidInjector {
    @Inject
    lateinit var injector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProvider.Factory

    private val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this, viewModelProviderFactory).get(MainViewModel::class.java)
    }

    private val panels by lazy {
        listOf(loadingPanel, errorPanel, listPanel, detailsPanel)
    }

    private fun showPanel(
        panel: View,
        title: String = getString(R.string.list_title),
        backEnabled: Boolean = false
    ) {
        panels.forEach {
            it.visibility = if (it == panel) View.VISIBLE else View.GONE
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(backEnabled)
        supportActionBar?.title = title
    }

    override fun androidInjector() = injector

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val itemsAdapter = ItemsAdapter()
        listPanel.adapter = itemsAdapter

        itemsAdapter.onClickedCallback = {
            viewModel.onItemSelected(it)
        }

        retryButton.setOnClickListener { viewModel.onRetryClicked() }

        viewModel.listItems.observe(this, Observer {
            itemsAdapter.items = it
        })

        viewModel.content.observe(this, Observer {
            when (it) {
                Loading -> {
                    showPanel(loadingPanel)
                }
                is Error -> {
                    val description =
                        if (it.description.isEmpty()) "" else getString(
                            R.string.error_detail,
                            it.description
                        )
                    errorDescription.text = getString(R.string.error_description, description)
                    showPanel(errorPanel)
                }
                is Loaded -> {
                    showPanel(listPanel)
                }
                is Detail -> {
                    showPanel(detailsPanel, it.title, true)
                    Glide.with(detailsPanel).load(it.imageUrl).into(detailsPanel)
                }
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean =
        viewModel.onBackPressed() || super.onSupportNavigateUp()

    override fun onBackPressed() {
        if (!viewModel.onBackPressed()) {
            super.onBackPressed()
        }
    }
}

@VisibleForTesting
class ItemViewHolder(val view: ImageView) : RecyclerView.ViewHolder(view) {
    fun setUri(uri: String) {
        Glide.with(view).load(uri).into(view)
    }

    fun setOnClickedCallback(callback: () -> Unit) {
        view.setOnClickListener { callback() }
    }
}

private class ItemsAdapter : RecyclerView.Adapter<ItemViewHolder>() {
    var onClickedCallback: (Int) -> Unit = {}

    var items = listOf<MainViewModel.Item>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_image,
                parent,
                false
            ) as ImageView
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.setUri(items[position].thumbnailUrl)
        holder.setOnClickedCallback { onClickedCallback(position) }
    }
}
