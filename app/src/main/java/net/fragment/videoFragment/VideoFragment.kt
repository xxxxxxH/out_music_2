package net.fragment.videoFragment

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.layout_fragment_video.*
import net.adapter.VideoAdapter
import net.basicmodel.R
import net.basicmodel.VideoPlayerActivity
import net.event.MessageEvent
import net.fragment.BaseFragment
import net.utils.CommonUtils
import net.utils.DataManager
import org.greenrobot.eventbus.EventBus

class VideoFragment : BaseFragment() {
    override fun getLayout(): Int {
        return R.layout.layout_fragment_video
    }

    override fun initView() {
        val data = DataManager.get().getAllVideos(requireContext())
        val videoAdapter = VideoAdapter(data)
        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter = videoAdapter
        videoAdapter.addChildClickViewIds(R.id.lin_menu)
        videoAdapter.setOnItemClickListener { adapter, view, position ->
            val entity = data[position]
            val intent = Intent(context, VideoPlayerActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("url", entity.filePath)
            intent.putExtra("name", entity.title)
            EventBus.getDefault().post(MessageEvent("pause"))
            startActivity(intent)
        }
        videoAdapter.setOnItemChildClickListener { adapter, view, position ->
            val entity = data[position]
            CommonUtils.get().popupWindow(
                view, requireActivity(), entity, position, "video",
                adapter as VideoAdapter
            )
        }
    }
}