package id.lukasdylan.grpc.protodroid.internal.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import id.lukasdylan.grpc.protodroid.R
import id.lukasdylan.grpc.protodroid.internal.Protodroid
import id.lukasdylan.grpc.protodroid.internal.database.ProtodroidDataEntity
import id.lukasdylan.grpc.protodroid.internal.repository.InternalProtodroidRepositoryImpl
import id.lukasdylan.grpc.protodroid.internal.ui.detail.DetailActivity
import id.lukasdylan.grpc.protodroid.internal.viewmodel.MainViewModel
import id.lukasdylan.grpc.protodroid.internal.viewmodel.MainViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), Observer<List<ProtodroidDataEntity>> {

    private val dataResponseAdapter by lazy {
        DataResponseAdapter {
            Intent(this, DetailActivity::class.java).apply {
                putExtra("id", it.id)
                putExtra("service_name", it.serviceName)
            }.let(this::startActivity)
        }
    }

    private val mainViewModel by lazy {
        val dao = Protodroid.getInstance(this).protodroidDao
        val repository = InternalProtodroidRepositoryImpl(dao)

        return@lazy ViewModelProvider(this, MainViewModelFactory(repository))
            .get(MainViewModel::class.java)
    }

    private val linearLayoutManager = LinearLayoutManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkDeepLinkData()
        toolbar?.apply {
            setSupportActionBar(this)
            title = applicationInfo.loadLabel(packageManager)
        }
        rv_data?.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = dataResponseAdapter
        }
        observeViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                mainViewModel.deleteAllData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onChanged(data: List<ProtodroidDataEntity>?) {
        dataResponseAdapter.submitList(data)
        val position = linearLayoutManager.findFirstVisibleItemPosition()
        if (position != 0) {
            linearLayoutManager.scrollToPositionWithOffset(0, 0)
        }
    }

    private fun observeViewModel() {
        mainViewModel.dataResponseLiveData.observe(this, this)
    }

    private fun checkDeepLinkData() {
        intent?.let {
            if (it.hasExtra("open_detail") && it.getBooleanExtra("open_detail", false)) {
                val detailIntent = Intent(this, DetailActivity::class.java).apply {
                    putExtra("id", it.getLongExtra("id", 0L))
                    putExtra("service_name", it.getStringExtra("service_name").orEmpty())
                }
                startActivity(detailIntent)
            }
        }
    }
}
