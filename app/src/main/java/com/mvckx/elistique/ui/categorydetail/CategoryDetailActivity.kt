package com.mvckx.elistique.ui.categorydetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mvckx.elistique.R
import com.mvckx.elistique.ui.placedetail.PlaceDetailActivity
import kotlinx.android.synthetic.main.activity_categories.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class CategoryDetailActivity : AppCompatActivity() {

    private val viewModel = viewModel<CategoryDetailViewModel>()
    private val categoryDetailAdapter = CategoryDetailAdapter { placeClicked(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_detail)
        setupViews()
        setupViewModel()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_sort) {
            viewModel.value.sort()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_category_detail, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun setupViews() {
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.title = getCategoryName()
        }

        recyclerView.adapter = categoryDetailAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupViewModel() {
        val vm = viewModel.value
        vm.detailLiveData().observe(this, Observer {
            renderViewState(it)
        })
        vm.setCategoryId(getCategoryId())
    }

    private fun renderViewState(vs: CategoryDetailViewState) {
        progressBar.visibility = if (vs.loading) View.VISIBLE else View.GONE
        recyclerView.visibility = if (vs.loading) View.GONE else View.VISIBLE
        categoryDetailAdapter.submitList(vs.placeItems)
    }

    private fun placeClicked(placeId: String) {
        startActivity(PlaceDetailActivity.intent(this, placeId, getCategoryId()))
    }

    private fun getCategoryId() = intent.getStringExtra(EXTRA_CATEGORY_ID)
    private fun getCategoryName() = intent.getStringExtra(EXTRA_TITLE_ID)

    companion object {
        private const val EXTRA_TITLE_ID = "TITLE_ID"
        private const val EXTRA_CATEGORY_ID = "CATEGORY_ID"

        fun intent(context: Context, categoryId: String, title: String?): Intent {
            val intent = Intent(context, CategoryDetailActivity::class.java)
            intent.putExtra(EXTRA_CATEGORY_ID, categoryId)
            title?.let {
                intent.putExtra(EXTRA_TITLE_ID, it)
            }
            return intent
        }
    }
}