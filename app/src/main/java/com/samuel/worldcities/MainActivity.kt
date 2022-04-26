package com.samuel.worldcities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.samuel.worldcities.adapters.CityAdapter
import com.samuel.worldcities.databinding.ActivityMainBinding
import com.samuel.worldcities.utilities.MarkerManager
import com.samuel.worldcities.utilities.hideKeyboard
import com.samuel.worldcities.utilities.showKeyboard
import com.samuel.worldcities.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity(),
    OnMapReadyCallback, View.OnClickListener {

    private var job: Job? = null
    private lateinit var adapter: CityAdapter
    private var googleMap: GoogleMap? = null
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private var markerManager: MarkerManager? = null
    private lateinit var behavior: BottomSheetBehavior<RelativeLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        setLoading(true)
        changeFocus(false)
        mapFragment.getMapAsync(this)
        behavior = BottomSheetBehavior.from(binding.listLayout.searchContainer)
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    changeFocus(false)
                    binding.listLayout.mapViewRadioButton.isChecked = true
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    changeFocus(true)
                    binding.listLayout.listViewRadioButton.isChecked = true
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

        })
        adapter = CityAdapter()
        binding.listLayout.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.listLayout.recyclerView.adapter = adapter
        listenForChanges()
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                addMarkers(itemCount, adapter.itemCount == itemCount)
                toggleInfoTv("")
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {

            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {

            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                addMarkers(itemCount, true)
                toggleInfoTv(if (adapter.itemCount == 0) getString(R.string.no_cities) else "")
            }
        })

        binding.listLayout.searchEdt.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search()
                true
            }
            false
        }
        binding.listLayout.clearIV.setOnClickListener(this)
        binding.listLayout.searchEdt.setOnClickListener(this)
        binding.listLayout.searchEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                binding.listLayout.clearIV.visibility = View.VISIBLE
                search(editable.toString())
            }
        })
        binding.listLayout.mapTypRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.mapViewRadioButton && behavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else if (checkedId == R.id.listViewRadioButton && behavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }


    private fun toggleInfoTv(message: String) {
        if (message.isEmpty()) {
            binding.listLayout.infoTv.visibility = View.GONE
        } else {
            binding.listLayout.infoTv.text = message
            binding.listLayout.infoTv.visibility = View.VISIBLE
        }
    }

    private fun addMarkers(itemCount: Int, refresh: Boolean) {
        markerManager?.addMarkers(adapter.snapshot().items, refresh)
    }

    //show progress when fetching cities
    private fun listenForChanges() {
        lifecycleScope.launch {
            adapter.loadStateFlow
                .distinctUntilChangedBy { it.refresh }.collectLatest {
                    updateProgress(it.refresh)
                }
        }
    }

    private fun updateProgress(loadState: LoadState) {
        when (loadState) {
            is LoadState.Loading -> {
                setLoading(true)
            }
            is LoadState.NotLoading -> {
                setLoading(false)
            }
            is LoadState.Error -> {
                setLoading(false)
                val message =
                    "Failed to load cities. Please make sure you have an active internet connection and search again"
                if (adapter.itemCount == 0) {
                    toggleInfoTv(message)
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.let {
            it.uiSettings.setAllGesturesEnabled(true)
            it.uiSettings.isZoomControlsEnabled = false
            markerManager = MarkerManager(it, this)
        }
        search()
    }

    private fun search(filter: String? = null) {
        if (job != null) {
            job?.cancel()
        }
        job = lifecycleScope.launch {
            setLoading(true)
            viewModel.searchCities(filter?.trim()).collectLatest {
                adapter.submitData(it)
            }
        }
    }


    private fun setLoading(loading: Boolean) {
        binding.listLayout.searchIV.visibility = if (loading) View.INVISIBLE else View.VISIBLE
        binding.listLayout.searchPB.visibility = if (loading) View.VISIBLE else View.INVISIBLE
    }

    //change edittext focus
    private fun changeFocus(focusable: Boolean) {
        binding.listLayout.searchEdt.isFocusable = focusable
        binding.listLayout.searchEdt.isFocusableInTouchMode = focusable
        if (focusable) {
            binding.listLayout.searchEdt.showKeyboard()
            binding.listLayout.searchEdt.requestFocus()
        } else {
            binding.listLayout.searchEdt.hideKeyboard()
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.searchEdt -> {
                if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
            R.id.clearIV -> {
                binding.listLayout.searchEdt.text.clear()
                view.visibility = View.GONE
            }
        }
    }

    //remove focus when bottomsheet is collapsed
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val v = currentFocus
        if (v is EditText) {
            if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return super.dispatchTouchEvent(event)
    }
}