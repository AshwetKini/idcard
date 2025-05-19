package com.truspirit.idcard


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.truspirit.idcard.databinding.FragmentHistoryBinding
import com.truspirit.idcard.model.AppDatabase
import com.truspirit.idcard.model.EmployeeCard
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

class HistoryFragment : Fragment(), CardAdapter.OnShareClick {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CardAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = FragmentHistoryBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = CardAdapter(this)
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = adapter

        lifecycleScope.launch {
            AppDatabase.getInstance(requireContext())
                .employeeCardDao()
                .allCards()
                .collectLatest { cards ->
                    adapter.submitList(cards)
                }
        }
    }

    override fun onShare(card: EmployeeCard) {
        val file = File(card.filePath)
        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", file)
        val share = Intent().apply {
            action = Intent.ACTION_SEND
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(share, "Share ID Card"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
