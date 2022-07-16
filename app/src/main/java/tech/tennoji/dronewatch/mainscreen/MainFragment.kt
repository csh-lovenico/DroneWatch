package tech.tennoji.dronewatch.mainscreen

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import tech.tennoji.dronewatch.R

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            context?.getString(R.string.app_name)
        val menuHost: MenuHost = requireActivity()
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.main_swipe_refresh)
        val statusList = view.findViewById<RecyclerView>(R.id.areaStatusList)
        val adapter = MainListAdapter(
            MainListItemListener { area -> Log.i(this.javaClass.toString(), area) })
        statusList.adapter = adapter
        viewModel.statusList.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(it)
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            it?.let {
                swipeRefreshLayout.isRefreshing = it
            }
        }

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_screen_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    (R.id.main_list_refresh) -> {
                        viewModel.fetchStatus()
                        true
                    }
                    R.id.main_subscription_manage -> {
                        val bundle = bundleOf("token" to viewModel.token.value)
                        findNavController().navigate(
                            R.id.action_mainFragment_to_subscriptionFragment,
                            bundle
                        )
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)


        viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
            }
        })

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchStatus()
        }
    }

}