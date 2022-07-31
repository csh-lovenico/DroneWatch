package tech.tennoji.dronewatch.droneimage

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import coil.load
import tech.tennoji.dronewatch.R
import tech.tennoji.dronewatch.network.BASE_URL
import java.time.format.DateTimeFormatter

class DroneImageFragment : Fragment() {

    companion object {
        fun newInstance() = DroneImageFragment()
    }

    private lateinit var viewModel: DroneImageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_drone_image, container, false)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[DroneImageViewModel::class.java]
        val droneId = arguments?.getString("droneId")
        val area = arguments?.getString("area")
        if (droneId != null && area != null) {
            viewModel.setDroneId(droneId, area)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isReady.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    viewModel.launch()
                }
            }
        }

        view.findViewById<Button>(R.id.pause_button).setOnClickListener {
            viewModel.end()
        }

        view.findViewById<Button>(R.id.resume_button).setOnClickListener {
            viewModel.launch()
        }

        viewModel.record.observe(viewLifecycleOwner) {
            it?.let {
                val imageUrl = BASE_URL + "/files/" + it.imagePath
                view.findViewById<ImageView>(R.id.drone_image).load(imageUrl)
                view.findViewById<TextView>(R.id.drone_id_text).text =
                    "Drone ID: " + it.metadata.droneId
                view.findViewById<TextView>(R.id.last_update_text).text =
                    "Last update: " + it.timestamp.format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    )
            }
        }

        viewModel.isPaused.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    view.findViewById<Button>(R.id.pause_button).isEnabled = false
                    view.findViewById<Button>(R.id.resume_button).isEnabled = true
                } else {
                    view.findViewById<Button>(R.id.pause_button).isEnabled = true
                    view.findViewById<Button>(R.id.resume_button).isEnabled = false
                }
            }
        }

        viewModel.showErrDialog.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    NoUpdateDialog().show(parentFragmentManager, "noUpdateDialog")
                }
            }
        }

        viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                viewModel.end()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                viewModel.end()
            }
        })
    }

    class NoUpdateDialog : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return activity?.let {
                val builder = AlertDialog.Builder(it)
                builder.setMessage("No image updates.\nThe drone has left this area.")
                    .setPositiveButton("OK") { _, _ ->
                        findNavController().popBackStack()
                    }
                builder.create()
            } ?: throw IllegalStateException("Activity cannot be null")
        }
    }
}