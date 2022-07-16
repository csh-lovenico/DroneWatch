package tech.tennoji.dronewatch.subscription.add

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import tech.tennoji.dronewatch.R
import tech.tennoji.dronewatch.subscription.SubscriptionItemAdapter
import tech.tennoji.dronewatch.subscription.SubscriptionItemListener
import tech.tennoji.dronewatch.subscription.SubscriptionViewModel

class AddSubscriptionFragment : Fragment() {

    companion object {
        fun newInstance() = AddSubscriptionFragment()
    }

    private lateinit var viewModel: AddSubscriptionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_subscription, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[AddSubscriptionViewModel::class.java]
        val token = arguments?.getString("token")
        if (token != null) {
            viewModel.setToken(token)
        }
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            context?.getString(R.string.add_subscription_text)
        val menuHost: MenuHost = requireActivity()
        val swipeRefreshLayout =
            view.findViewById<SwipeRefreshLayout>(R.id.add_subscription_swipe)
        val recyclerView = view.findViewById<RecyclerView>(R.id.add_subscription_list)
        val adapter = SubscriptionItemAdapter(SubscriptionItemListener { })
        recyclerView.adapter = adapter

        menuHost.addMenuProvider(object : MenuProvider {
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