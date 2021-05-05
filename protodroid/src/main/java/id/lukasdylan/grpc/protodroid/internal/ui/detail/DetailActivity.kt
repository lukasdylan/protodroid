package id.lukasdylan.grpc.protodroid.internal.ui.detail

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import id.lukasdylan.grpc.protodroid.R
import id.lukasdylan.grpc.protodroid.internal.Protodroid
import id.lukasdylan.grpc.protodroid.internal.repository.InternalProtodroidRepositoryImpl
import id.lukasdylan.grpc.protodroid.internal.ui.bind
import id.lukasdylan.grpc.protodroid.internal.viewmodel.DetailViewModel
import id.lukasdylan.grpc.protodroid.internal.viewmodel.DetailViewModelFactory

class DetailActivity : AppCompatActivity() {

    private val toolbar by bind<Toolbar>(R.id.toolbar)
    private val viewPager by bind<ViewPager>(R.id.view_pager)
    private val tabLayout by bind<TabLayout>(R.id.tab_layout)

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

        viewPager?.adapter = InformationPagerAdapter(supportFragmentManager)
        tabLayout?.setupWithViewPager(viewPager)
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