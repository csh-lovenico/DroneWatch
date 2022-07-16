package tech.tennoji.dronewatch.subscription

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import tech.tennoji.dronewatch.R

class SubscriptionFragment : Fragment() {

    companion object {
        fun newInstance() = SubscriptionFragment()
    }

    private lateinit var viewModel: SubscriptionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_subscription, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[SubscriptionViewModel::class.java]
        val token = arguments?.getString("token")
        if (token != null) {
            viewModel.setToken(token)
        }
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            context?.getString(R.string.main_manage_subscription_text)
        val menuHost: MenuHost = requireActivity()
        val swipeRefreshLayout =
            view.findViewById<SwipeRefreshLayout>(R.id.subscription_manage_swipe)
        val adapter = SubscriptionItemAdapter(SubscriptionItemListener { })
        val recyclerView = view.findViewById<RecyclerView>(R.id.subscription_manage_list)
        recyclerView.adapter = adapter
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.subscription_manage_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.subscription_menu_add -> {
                        Log.i(this.javaClass.toString(), "Add subscription")
                        val bundle = bundleOf("token" to viewModel.token.value)
                        findNavController().navigate(
                            R.id.action_subscriptionFragment_to_addSubscriptionFragment,
                            bundle
                        )
                        true
                    }
                    R.id.subscription_menu_refresh -> {
                        viewModel.refreshList()
                        true
                    }
                    else -> false
                }
            }

        }, viewLifecycleOwner)

        viewModel.token.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.refreshList()
            }
        }

        viewModel.areaList.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(it)
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
    }
}