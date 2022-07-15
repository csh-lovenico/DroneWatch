package tech.tennoji.dronewatch.mainscreen

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        val menuHost: MenuHost = requireActivity()
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.main_swipe_refresh)
        viewModel.token.observe(viewLifecycleOwner) {
            it?.let {
                if (it != "error") {
                    swipeRefreshLayout.isRefreshing = true
                    viewModel.fetchStatus()
                }
            }
        }

        viewModel.statusList.observe(viewLifecycleOwner) {
            swipeRefreshLayout.isRefreshing = false
            Log.i(this.javaClass.toString(), "status list changed")
            it?.let {
                Log.i(this.javaClass.toString(), it.toString())
            }
        }

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_screen_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    (R.id.main_list_refresh) -> {
                        swipeRefreshLayout.isRefreshing = true
                        viewModel.fetchStatus()
                        true
                    }
                    R.id.main_subscription_manage -> {
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