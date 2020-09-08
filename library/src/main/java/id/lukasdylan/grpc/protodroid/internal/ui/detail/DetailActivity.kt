package id.lukasdylan.grpc.protodroid.internal.ui.detail

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import id.lukasdylan.grpc.protodroid.R
import id.lukasdylan.grpc.protodroid.internal.Protodroid
import id.lukasdylan.grpc.protodroid.internal.repository.InternalProtodroidRepositoryImpl
import id.lukasdylan.grpc.protodroid.internal.viewmodel.DetailViewModel
import id.lukasdylan.grpc.protodroid.internal.viewmodel.DetailViewModelFactory
import kotlinx.android.synthetic.main.protodroid_activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.protodroid_activity_detail)

        toolbar?.apply {
            setSupportActionBar(this)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            val services = intent?.getStringExtra("service_name")?.split("/").orEmpty()
            supportActionBar?.title = services.getOrElse(1) {
                intent?.getStringExtra("service_name").orEmpty()
            }
        }

        val dao = Protodroid.getInstance(this).protodroidDao
        val repository = InternalProtodroidRepositoryImpl(dao)
        val selectedDataId = intent?.getLongExtra("id", 0L) ?: 0L

        ViewModelProvider(this, DetailViewModelFactory(repository, selectedDataId))
            .get(DetailViewModel::class.java)

        view_pager?.adapter = InformationPagerAdapter(supportFragmentManager)
        tab_layout?.setupWithViewPager(view_pager)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}