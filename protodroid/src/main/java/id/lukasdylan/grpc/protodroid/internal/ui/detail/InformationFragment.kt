package id.lukasdylan.grpc.protodroid.internal.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.lukasdylan.grpc.protodroid.R
import id.lukasdylan.grpc.protodroid.internal.ui.bind
import id.lukasdylan.grpc.protodroid.internal.viewmodel.DetailViewModel

class InformationFragment : Fragment(), Observer<List<Pair<String, String>>> {

    private val informationAdapter by lazy {
        InformationAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.protodroid_fragment_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rvInformation by bind<RecyclerView>(R.id.rv_information)
        rvInformation?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = informationAdapter
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val detailViewModel = activity?.let { ViewModelProvider(it).get(DetailViewModel::class.java) }
        detailViewModel?.let {
            when (arguments?.getInt("type") ?: OVERVIEW_TYPE) {
                OVERVIEW_TYPE -> {
                    it.overviewInfo.observe(viewLifecycleOwner, this)
                }
                REQUEST_TYPE -> {
                    it.requestInfo.observe(viewLifecycleOwner, this)
                }
                RESPONSE_TYPE -> {
                    it.responseInfo.observe(viewLifecycleOwner, this)
                }
            }
        }
    }

    override fun onChanged(t: List<Pair<String, String>>?) {
        informationAdapter.updateData(t.orEmpty())
    }

    companion object {
        const val OVERVIEW_TYPE = 0
        const val REQUEST_TYPE = 1
        const val RESPONSE_TYPE = 2

        fun newInstance(type: Int): InformationFragment {
            return InformationFragment().also {
                it.arguments = Bundle().apply {
                    putInt("type", type)
                }
            }
        }
    }
}