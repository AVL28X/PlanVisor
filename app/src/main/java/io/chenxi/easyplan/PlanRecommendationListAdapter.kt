package io.chenxi.easyplan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import io.chenxi.easyplan.rpc.client.DataPlanMsg


class PlanRecommendationListAdapter(context: Context, LayoutResourceId: Int,
                                    itemList: List<DataPlanMsg>) : ArrayAdapter<DataPlanMsg>(context, LayoutResourceId, itemList) {

    companion object {
        enum class ISP {
            Verizon, ATT, Sprint, TMobile
        }

        private val iconResourceIds: HashMap<ISP, Int> = HashMap()

        init {
            iconResourceIds.put(ISP.Verizon, R.mipmap.ic_verizon)
            iconResourceIds.put(ISP.ATT, R.mipmap.ic_att)
        }

    }

    private val mResourceId: Int = LayoutResourceId
    private val mListObjects: List<DataPlanMsg> = itemList
    private val mInflater = LayoutInflater.from(context)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ListRowHolder
        if (convertView == null) {
            view = mInflater.inflate(this.mResourceId, parent, false)
            viewHolder = ListRowHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ListRowHolder
        }
        val dataPlanMsg: DataPlanMsg = getItem(position)

        //dataPlanMsg.
        viewHolder.imageViewLogo.setImageResource(R.mipmap.ic_verizon)
        viewHolder.textViewName.text = dataPlanMsg.name
        viewHolder.textViewDesc.text = dataPlanMsg.description
        viewHolder.textViewPrice.text = "%lf for \n$%lf/mo".format(dataPlanMsg.quota, dataPlanMsg.price)

//
//        when (position%3) {
//            0 -> {
//                viewHolder.imageViewLogo.setImageResource(R.mipmap.ic_verizon)
//                viewHolder.textViewName.text = "Verizon Sample Plan"
//                viewHolder.textViewDesc.text = "Some details about the plan,\n at most 3 lines, expandable"
//                viewHolder.textViewPrice.text = "∞ for \n$50/mo"
//            }
//            1 -> {
//                viewHolder.imageViewLogo.setImageResource(R.mipmap.ic_att)
//                viewHolder.textViewName.text = "AT&T Sample Plan"
//                viewHolder.textViewDesc.text = "Some details about the plan"
//                viewHolder.textViewPrice.text = "2GB for \n$35/mo"
//            }
//            2 -> {
//                viewHolder.imageViewLogo.setImageResource(R.mipmap.ic_tmobile)
//                viewHolder.textViewName.text = "T-mobile Sample Plan"
//                viewHolder.textViewDesc.text = "Some details about the plan\n the list can be very long"
//                viewHolder.textViewPrice.text = "1.5GB for \n$20/mo"
//            }
//        }
        return view
    }

    private class ListRowHolder(row: View?) {
        val imageViewLogo: ImageView = row?.findViewById<View>(R.id.icon) as ImageView
        val textViewName: TextView = row?.findViewById<View>(R.id.item_list_plan_name) as TextView
        val textViewDesc: TextView = row?.findViewById<View>(R.id.item_list_plan_desc) as TextView
        val textViewPrice: TextView = row?.findViewById<View>(R.id.item_list_plan_price) as TextView
    }
}

