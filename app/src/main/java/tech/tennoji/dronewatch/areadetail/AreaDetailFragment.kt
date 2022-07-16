package tech.tennoji.dronewatch.areadetail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import tech.tennoji.dronewatch.R

class AreaDetailFragment : Fragment() {

    companion object {
        fun newInstance() = AreaDetailFragment()
    }

    private lateinit var viewModel: AreaDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_area_detail, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[AreaDetailViewModel::class.java]
        val areaName = arguments?.getString("areaName")
        val isAdd = arguments?.getBoolean("isAdd")
        val token = arguments?.getString("token")
        viewModel.initialize(areaName!!, token!!, isAdd!!)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.areaName.observe(viewLifecycleOwner) {
            it?.let {
                view.findViewById<TextView>(R.id.area_detail_name).text = it
            }
        }

        viewModel.isAdd.observe(viewLifecycleOwner) {
            it?.let { isAdd ->
                view.findViewById<Button>(R.id.area_detail_action).setOnClickListener {
                    viewModel.doAction(isAdd)
                }
                if (isAdd) {
                    view.findViewById<Button>(R.id.area_detail_action).text = "Subscribe"
                } else {
                    view.findViewById<Button>(R.id.area_detail_action).text = "Unsubscribe"
                }
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            it?.let {
                view.findViewById<Button>(R.id.area_detail_action).isEnabled = !it
            }
        }

        viewModel.success.observe(viewLifecycleOwner) {
            it?.let {
                if (it) { // success
                    if (viewModel.isAdd.value!!) {
                        Toast.makeText(context, "Subscribed!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Unsubscribed!", Toast.LENGTH_SHORT).show()
                    }
                    findNavController().popBackStack()
                } else { //fail
                    Toast.makeText(
                        context,
                        "Unable to process request: " + viewModel.message.value,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}