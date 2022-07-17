package tech.tennoji.dronewatch.droneimage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import tech.tennoji.dronewatch.R

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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[DroneImageViewModel::class.java]
    }
}