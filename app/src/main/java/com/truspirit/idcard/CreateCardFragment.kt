package com.truspirit.idcard

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.truspirit.idcard.databinding.FragmentCreateCardBinding
import com.truspirit.idcard.model.AppDatabase
import com.truspirit.idcard.model.EmployeeCard
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class CreateCardFragment : Fragment() {

    private var _binding: FragmentCreateCardBinding? = null
    private val binding get() = _binding!!

    private var photoUri: Uri? = null

    private val requestReadPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) pickLauncher.launch("image/*")
            else Toast.makeText(
                requireContext(),
                "Storage permission required to pick a photo",
                Toast.LENGTH_SHORT
            ).show()
        }

    private val pickLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { startCrop(it) }
        }

    private val cropLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { data ->
                    UCrop.getOutput(data)?.let { uri ->
                        photoUri = uri
                        binding.ivPhoto.setImageURI(uri)
                    }
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = FragmentCreateCardBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Populate designation spinner
        val designations = listOf("BUSINESS DEVELOPMENT OFFICER", "RELATIONSHIP OFFICER")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, designations)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDesignation.adapter = adapter

        // Photo picker
        binding.btnSelectPhoto.setOnClickListener {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                requestReadPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                pickLauncher.launch("image/*")
            }
        }

        // Generate card
        binding.btnGenerate.setOnClickListener {
            generateCard()
        }
    }

    private fun startCrop(sourceUri: Uri) {
        try {
            val dest = File(requireContext().cacheDir, "crop_${SystemClock.uptimeMillis()}.jpg")
            val intent = UCrop.of(sourceUri, Uri.fromFile(dest))
                .withAspectRatio(1f, 1f)
                .getIntent(requireContext())
            cropLauncher.launch(intent)
        } catch (e: Exception) {
            Log.e("CreateCardFragment", "Failed to launch UCrop", e)
            Toast.makeText(requireContext(), "Error starting crop: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun generateCard() {
        val name = binding.etName.text.toString().trim()
        val empNo = binding.etEmpNumber.text.toString().trim()
        val mobile = binding.etMobile.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val designation = binding.spinnerDesignation.selectedItem?.toString().orEmpty()
        val photo = photoUri

        // Validate
        if (name.isEmpty() || empNo.isEmpty() || mobile.isEmpty() || email.isEmpty()
            || designation.isEmpty() || photo == null
        ) {
            Toast.makeText(requireContext(), "Please fill all fields and select a photo", Toast.LENGTH_SHORT).show()
            return
        }

        // Populate preview values
        binding.tvNameValue.text = name
        // Underline the Name value
        binding.tvNameValue.paintFlags = binding.tvNameValue.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        binding.tvEmpValue.text = empNo
        binding.tvMobileValue.text = mobile
        binding.tvEmailValue.text = email

        // Use the new two-column ID for designation
        binding.tvDesignationValue.text = designation

        // Website is constant
        binding.tvWebsiteValue.text = "www.truspirit.in"

        // Render to bitmap
        val view = binding.cardPreview
        val bmp = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        view.draw(canvas)

        // Save PNG
        val pngFile = File(
            requireContext().getExternalFilesDir(null),
            "card_${SystemClock.uptimeMillis()}.png"
        )
        FileOutputStream(pngFile).use { out ->
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        // Persist record
        val record = EmployeeCard(
            name = name,
            empNumber = empNo,
            mobile = mobile,
            filePath = pngFile.absolutePath,
            createdAt = System.currentTimeMillis()
        )

        lifecycleScope.launch {
            AppDatabase.getInstance(requireContext()).employeeCardDao().insert(record)

            // Share
            val uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                pngFile
            )
            startActivity(
                Intent.createChooser(
                    Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "image/png"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    },
                    "Share ID Card"
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
