package id.lukasdylan.grpc.protodroid.internal.ui.detail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class InformationPagerAdapter(manager: FragmentManager) :
    FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragmentList: List<Pair<String, Fragment>> = listOf(
        "Overview" to InformationFragment.newInstance(InformationFragment.OVERVIEW_TYPE),
        "Request" to InformationFragment.newInstance(InformationFragment.REQUEST_TYPE),
        "Response" to InformationFragment.newInstance(InformationFragment.RESPONSE_TYPE)
    )

    override fun getItem(position: Int): Fragment = fragmentList[position].second

    override fun getCount(): Int = fragmentList.size

    override fun getPageTitle(position: Int): CharSequence = fragmentList[position].first

}