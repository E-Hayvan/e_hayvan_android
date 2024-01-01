package com.ehayvan.app.modules.viewpetprofile.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ehayvan.app.R
import com.ehayvan.app.appcomponents.base.BaseActivity
import com.ehayvan.app.databinding.ActivityViewPetProfileBinding
import com.ehayvan.app.modules.addpetprofile.ui.AddPetProfileActivity
import com.ehayvan.app.modules.editpetprofile.ui.EditPetProfileActivity
import com.ehayvan.app.modules.petownerdashboard.data.model.ListPetRowModel
import com.ehayvan.app.modules.petownerdashboard.ui.PetOwnerDashboardActivity
import com.ehayvan.app.modules.viewpetprofile.`data`.model.ContentRowModel
import com.ehayvan.app.modules.viewpetprofile.`data`.viewmodel.ViewPetProfileVM
import org.json.JSONObject
import kotlin.Int
import kotlin.String
import kotlin.Unit

class ViewPetProfileActivity :
    BaseActivity<ActivityViewPetProfileBinding>(R.layout.activity_view_pet_profile) {
  private val viewModel: ViewPetProfileVM by viewModels<ViewPetProfileVM>()
  private lateinit var petID : String
  private lateinit var requestQueue: RequestQueue
  private lateinit var owner: String
  override fun onInitialized(): Unit {
    requestQueue = Volley.newRequestQueue(this)
    viewModel.navArguments = intent.extras?.getBundle("bundle")
    viewModel.viewPetProfileModel.value?.txtName = intent.extras?.getString("petName")
    viewModel.viewPetProfileModel.value?.txtAge = intent.extras?.getString("age")
    petID = intent.extras?.getString("petID").toString()
    owner = intent.extras?.getString("ownerID").toString()
    val petType = intent.extras?.getString("petTypeID")
    viewModel.viewPetProfileModel.value?.txtType = petType
    binding.viewPetProfileVM = viewModel
  }

  override fun setUpClicks(): Unit {
    binding.imageArrowleft.setOnClickListener {
      onBackPressed()
    }
    binding.btnDeletePet.setOnClickListener {
      val url = "http://192.168.0.19:8080/api/pets/$petID"
      val jsonObjectRequest = JsonObjectRequest(
        Request.Method.DELETE, url, null,
        { response ->
          // Handle the response
          if (response != null) {
            val bundle = Bundle()
            val intent = Intent(this, PetOwnerDashboardActivity::class.java)
            bundle.putString("ownerID", owner)
            intent.putExtras(bundle)
            startActivity(intent)
            finishAffinity()
          }
        },
        { error ->
          val bundle = Bundle()
          val intent = Intent(this, PetOwnerDashboardActivity::class.java)
          bundle.putString("ownerID", owner)
          intent.putExtras(bundle)
          startActivity(intent)
          finishAffinity()
          Log.e("YourActivity", "Error: ${error.message}")
        })

      requestQueue.add(jsonObjectRequest)
    }
    binding.btnEditPetProfileOne.setOnClickListener {
      val bundle = Bundle()
      val intent = Intent(this, EditPetProfileActivity::class.java)
      bundle.putString("ownerID", owner)
      bundle.putString("petID", petID)
      intent.putExtras(bundle)
      startActivity(intent)
    }
  }

  companion object {
    const val TAG: String = "VIEW_PET_PROFILE_ACTIVITY"

  }
}
