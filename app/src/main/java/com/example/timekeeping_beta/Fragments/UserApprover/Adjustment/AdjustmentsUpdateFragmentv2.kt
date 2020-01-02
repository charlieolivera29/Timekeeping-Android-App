package com.example.timekeeping_beta.Fragments.UserApprover.Adjustment

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestAdjustment.AdjusmentFragments.RequestFragment
import com.example.timekeeping_beta.Globals.CustomClasses.Flag
import com.example.timekeeping_beta.Globals.Models.Pagination
import com.example.timekeeping_beta.R
import es.dmoral.toasty.Toasty
import java.util.*

class AdjustmentsUpdateFragmentv2 : Fragment() {


    //Data
    private lateinit var Adjustments: List<Adjustment>
    private lateinit var pagination: Pagination
    private lateinit var AdjustmentAdapter: AdjustmentsAdapter

    private var status: String = "pending"
    private var search: String = ""
    private var page: Int = 1
    private var show: Int = 10
    //Data

    private lateinit var v: View
    private lateinit var AdjustmentsUpdateViewModel: AdjustmentsUpdateViewModel

    private lateinit var rv_list: RecyclerView
    private lateinit var srl_refresh: SwipeRefreshLayout
    private lateinit var tv_no_data: TextView
    private lateinit var fab_make_request: FloatingActionButton
    private lateinit var bvn_navigation_overtimes: BottomNavigationView
    private lateinit var FilterDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AdjustmentsUpdateViewModel = ViewModelProviders.of(this).get(com.example.timekeeping_beta.Fragments.UserApprover.Adjustment.AdjustmentsUpdateViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_list_v2, container, false)

        initViews(v)
        setListeners()

        loadData(status, search, page, show)
        return v
    }

    fun initViews(v: View) {
        rv_list = v.findViewById(R.id.rv_list)

        rv_list.setHasFixedSize(true)
        rv_list.layoutManager = LinearLayoutManager(context)


        tv_no_data = v.findViewById(R.id.tv_no_data)
        fab_make_request = v.findViewById(R.id.fab_make_request)
        bvn_navigation_overtimes = v.findViewById(R.id.bvn_navigation_overtimes)
        srl_refresh = v.findViewById(R.id.srl_refresh)

        bvn_navigation_overtimes.setOnNavigationItemSelectedListener(navListener)

        FilterDialog = Dialog(context!!)
        FilterDialog.setContentView(R.layout.dialog_filter)
        FilterDialog.setOnDismissListener { search = "" }
        filterDialog()
    }

    private fun filterDialog() {

        val tv_search = FilterDialog.findViewById<TextView>(R.id.tv_search)

        val spnnr_show = FilterDialog.findViewById<Spinner>(R.id.spnnr_show)

        //val options = arrayOf(10, 20, 50, 100)
        val options = context!!.resources.getStringArray(R.array.list_options)

        val spinnerArrayAdapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, options)
        spnnr_show.adapter = spinnerArrayAdapter
        spnnr_show.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                show = options.get(position).toInt()
            }
        }

        val btn_filter = FilterDialog.findViewById<Button>(R.id.btn_filter)

        tv_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                search = p0.toString()
            }
        })


        btn_filter.setOnClickListener {
            loadData(status, search, page, show)
        }
    }

    private fun setListeners() {
        AdjustmentsUpdateViewModel.adjustments.observe(this, Observer<List<Adjustment>> { it ->

            if (it != null) {
                Adjustments = it

                if (Adjustments.size > 0) {

                    AdjustmentAdapter = AdjustmentsAdapter(it)
                    Collections.reverse(Adjustments)
                    rv_list.adapter = AdjustmentAdapter
                    AdjustmentAdapter.notifyDataSetChanged()

                    success()
                } else {
                    empty()
                }
            } else {
                Toasty.error(context!!, context!!.resources.getString(R.string.api_request_error), Toasty.LENGTH_LONG).show()
            }
        })

        AdjustmentsUpdateViewModel.pagination.observe(this, Observer<Pagination> { it ->

            val prev_button = activity!!.findViewById<ImageView>(R.id.iv_next_page)
            val next_button = activity!!.findViewById<ImageView>(R.id.iv_prev_page)

            if (it != null) {
                pagination = it

                if (pagination.prev_page_url != "null" && pagination.next_page_url != "null") {
                    prev_button.setOnClickListener { AdjustmentsUpdateViewModel.retrievePaginated(Flag.PREV_PAGE, status, search, show) }
                    next_button.setOnClickListener { AdjustmentsUpdateViewModel.retrievePaginated(Flag.NEXT_PAGE, status, search, show) }
                } else if (pagination.prev_page_url == "null" && pagination.next_page_url != "null") {
                    prev_button.setOnClickListener { null }
                    next_button.setOnClickListener { AdjustmentsUpdateViewModel.retrievePaginated(Flag.NEXT_PAGE, status, search, show) }
                } else if (pagination.prev_page_url != "null" && pagination.next_page_url == "null") {
                    prev_button.setOnClickListener { AdjustmentsUpdateViewModel.retrievePaginated(Flag.PREV_PAGE, status, search, show) }

                    next_button.setOnClickListener { null }
                } else {
                    prev_button.setOnClickListener { null }
                    next_button.setOnClickListener { null }
                }
            } else {
                prev_button.setOnClickListener { null }
                next_button.setOnClickListener { null }
            }
        })

        srl_refresh.setOnRefreshListener {
            loadData(status, search, page, show)
        }

        fab_make_request.setOnClickListener {
            fragmentManager!!
                    .beginTransaction().add(R.id.fragment_container, RequestFragment(), "RequestFragment")
                    .addToBackStack("RequestFragment")
                    .commit()

        }
    }

    private val navListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.nav_pending -> {
                status = "pending"
                loadData(status, search, page, show)
            }

            R.id.nav_approve -> {
                status = "approved"
                loadData(status, search, page, show)
            }

            R.id.nav_decline -> {
                status = "declined"
                loadData(status, search, page, show)
            }

            R.id.nav_filter -> FilterDialog.show()
        }


        true
    }


    private fun loadData(status: String, search: String, page: Int, show: Int) {
        loading()
        AdjustmentsUpdateViewModel.retrieveAllAdjustments(status, search, page, show)
    }

    private fun loading() {

        if (FilterDialog.isShowing) FilterDialog.dismiss()

        srl_refresh.isRefreshing = true
    }

    private fun empty() {

        if (FilterDialog.isShowing) FilterDialog.dismiss()

        srl_refresh.isRefreshing = false
        rv_list.visibility = GONE
        tv_no_data.visibility = VISIBLE
    }

    private fun success() {

        if (FilterDialog.isShowing) FilterDialog.dismiss()

        srl_refresh.isRefreshing = false
        tv_no_data.visibility = GONE
        rv_list.visibility = VISIBLE
    }
}