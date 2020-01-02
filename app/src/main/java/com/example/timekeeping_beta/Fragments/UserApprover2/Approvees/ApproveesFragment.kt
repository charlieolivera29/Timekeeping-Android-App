package com.example.timekeeping_beta.Fragments.UserApprover2.Approvees

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
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
import com.example.timekeeping_beta.Fragments.UserApprover.Approvee.ApproveesAdapter
import com.example.timekeeping_beta.Fragments.UserApprover.Approvee.Models.Approvee
import com.example.timekeeping_beta.Fragments.UserApprover.ApproveeDetails.ApproveeDetailFragment
import com.example.timekeeping_beta.Globals.CustomClasses.Interface.RecyclerViewClickListener
import com.example.timekeeping_beta.R
import es.dmoral.toasty.Toasty

class ApproveesFragment : Fragment(), RecyclerViewClickListener {


    override fun onItemClick(position: Int, flag: Int) {
        val bundle = Bundle()
        bundle.putString("user_id", Approvees[position].approveeId)

        val ApproveeDetailFragment = ApproveeDetailFragment()
        ApproveeDetailFragment.arguments = bundle

        activity!!
                .supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .add(R.id.fragment_container, ApproveeDetailFragment, "ApproveeDetailFragmentFromApproveesFragment")
                .addToBackStack("ApproveeDetailFragmentFromApproveesFragment")
                .commit()
    }


    //Data
    private lateinit var Approvees: ArrayList<Approvee>
    private lateinit var Adapter: ApproveesAdapter

    private var status: String = "all"
    private var search: String = ""
    private var page: Int = 1
    private var show: Int = 10
    //Data

    private lateinit var v: View
    private lateinit var ApproveeViewModel: ApproveeViewModel

    private lateinit var rv_list: RecyclerView
    private lateinit var srl_refresh: SwipeRefreshLayout
    private lateinit var tv_no_data: TextView
    private lateinit var FilterDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ApproveeViewModel = ViewModelProviders.of(this).get(com.example.timekeeping_beta.Fragments.UserApprover2.Approvees.ApproveeViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_list_v2_list_only, container, false)

        initViews(v)
        setListeners()

        loadData()
        return v
    }

    fun initViews(v: View) {
        rv_list = v.findViewById(R.id.rv_list)

        rv_list.setHasFixedSize(true)
        rv_list.layoutManager = LinearLayoutManager(context)


        tv_no_data = v.findViewById(R.id.tv_no_data)
        srl_refresh = v.findViewById(R.id.srl_refresh)

        FilterDialog = Dialog(context)
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

    }

    private fun setListeners() {

        val that = this

        ApproveeViewModel.Approvees.observe(this, Observer<ArrayList<Approvee>> { it ->

            if (it != null) {
                Approvees = it

                if (Approvees.size > 0) {

                    val array = arrayOfNulls<Approvee>(it.size)
                    it.toArray(array)

                    Adapter = ApproveesAdapter(array, that)
                    //Collections.reverse(Adjustments);
                    rv_list.adapter = Adapter
                    Adapter.notifyDataSetChanged()

                    success()
                } else {
                    empty()
                }
            } else {
                Toasty.error(context!!, context!!.resources.getString(R.string.api_request_error), Toasty.LENGTH_LONG).show()
            }
        })

        srl_refresh.setOnRefreshListener {
            loadData()
        }
    }

    private fun loadData() {
        loading()
        ApproveeViewModel.retrieveData()
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