package tech.tennoji.dronewatch.areadrone

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import tech.tennoji.dronewatch.R
import tech.tennoji.dronewatch.subscription.SubscriptionItemAdapter
import tech.tennoji.dronewatch.subscription.SubscriptionItemListener

class AreaDroneFragment : Fragment() {

    companion object {
        fun newInstance() = AreaDroneFragment()
    }

    private lateinit var viewModel: AreaDroneViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_area_drone, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[AreaDroneViewModel::class.java]
        val token = arguments?.getString("areaName")
        if (token != null) {
            viewModel.setArea(token)
        }
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()
        val swipeRefreshLayout =
            view.findViewById<SwipeRefreshLayout>(R.id.area_drone_swipe)
        val adapter =
            SubscriptionItemAdapter(SubscriptionItemListener { drone -> viewModel.navigate(drone) })
        val recyclerView = view.findViewById<RecyclerView>(R.id.area_drone_list)
        recyclerView.adapter = adapter

        menuHost.addMenuProvider( object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.subscription_add_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.subscription_add_refresh -> {
                        viewModel.refreshList()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)

        viewModel.areaName.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.refreshList()
            }
        }

        viewModel.droneId.observe(viewLifecycleOwner) {
            it?.let {
                val bundle = bundleOf("droneId" to it)
                findNavController().navigate(
                    R.id.action_areaDroneFragment_to_droneImageFragment,
                    bundle
                )
                viewModel.navigateComplete()
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            it?.let {
                swipeRefreshLayout.isRefreshing = it
            }
        }

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshList()
        }

        viewModel.droneList.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(it)
            }
        }
    }
}