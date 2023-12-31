package com.ehayvan.app.modules.petownerdashboard.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ehayvan.app.R
import com.ehayvan.app.appcomponents.base.BaseActivity
import com.ehayvan.app.databinding.ActivityPetOwnerDashboardBinding
import com.ehayvan.app.modules.addpetprofile.ui.AddPetProfileActivity
import com.ehayvan.app.modules.petownerdashboard.data.model.ListPetRowModel
import com.ehayvan.app.modules.petownerdashboard.data.viewmodel.PetOwnerDashboardVM
import com.ehayvan.app.modules.veterinariandashboard.ui.VeterinarianDashboardActivity
import com.ehayvan.app.modules.vetsearchscreen.ui.VetSearchScreenActivity
import com.ehayvan.app.modules.viewpetprofile.ui.ViewPetProfileActivity
import com.ehayvan.app.modules.viewprofile.ui.ViewProfileActivity
import org.json.JSONException
import org.json.JSONObject
import org.w3c.dom.Text


class PetOwnerDashboardActivity :
  BaseActivity<ActivityPetOwnerDashboardBinding>(R.layout.activity_pet_owner_dashboard) {

  private val viewModel: PetOwnerDashboardVM by viewModels<PetOwnerDashboardVM>()
  private lateinit var requestQueue: RequestQueue
  private lateinit var owner: String
  private lateinit var listAdapter: ListPetAdapter
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    viewModel.navArguments = intent.extras?.getBundle("bundle")
    val ownerID = intent.extras?.getString("ownerID")
    if (ownerID != null) {
      owner = ownerID
    }
    requestQueue = Volley.newRequestQueue(this)

    getPets(ownerID)
  }
  override fun onInitialized(): Unit {

  }

  override fun setUpClicks(): Unit {
    // Set up any additional click listeners if needed
    val addBtn: AppCompatButton = findViewById(R.id.btnAddPet)
    addBtn.setOnClickListener {
      val bundle = Bundle()
      val intent = Intent(this, AddPetProfileActivity::class.java)
      bundle.putString("ownerID", owner)
      intent.putExtras(bundle)
      startActivity(intent)
    }
    val dummyList = listOf(ListPetRowModel("",""))
    listAdapter = ListPetAdapter(dummyList)
    listAdapter.setOnItemClickListener(object : ListPetAdapter.OnItemClickListener {
        override fun onItemClick(view: View, position: Int, item: ListPetRowModel) {
          val bundle = Bundle()
          val intent = Intent(view.context, ViewPetProfileActivity::class.java)
          bundle.putString("ownerID", owner)
          bundle.putString("petName", item.txtTitleOne)
          bundle.putString("petTypeID", item.txtPriceOne)
          bundle.putString("age", item.txtAge)
          bundle.putString("petID", item.petID)
          intent.putExtras(bundle)
          startActivity(intent)
        }
    })
    binding.petOwnerDashboardSearchView.setOnClickListener {
      val bundle = Bundle()
      val intent = Intent(this, VetSearchScreenActivity::class.java)
      bundle.putString("ownerID", owner)
      intent.putExtras(bundle)
      startActivity(intent)
    }
    binding.frameStackvector.setOnClickListener {
      val bundle = Bundle()
      val intent = Intent(this, ViewProfileActivity::class.java)
      bundle.putString("ownerID", owner)
      intent.putExtras(bundle)
      startActivity(intent)
    }
  }

  private fun getPets(ownerID: String?) {
    val url = "http://ehayvan.eu-north-1.elasticbeanstalk.com/api/petowners/$ownerID"
    val body1: ImageView = findViewById(R.id.imageEmptySpaceIll)
    val body2: TextView = findViewById(R.id.txtTitle)
    val body3: TextView = findViewById(R.id.txtBody)
    val recyclerList: RecyclerView = findViewById(R.id.recyclerListPet)
    val container : LinearLayout = findViewById(R.id.linearNavigationSe)
    val jsonObjectRequest = JsonObjectRequest(
      Request.Method.GET, url, null,
      { response ->
        // Handle the response
        if (response != null) {
          val jsonString = response.toString()
          val jsonObject = JSONObject(jsonString)
          val petsArray = jsonObject.getJSONArray("pets")
          val userName = jsonObject.getJSONObject("user")
          viewModel.petOwnerDashboardModel.value?.txtUser = userName.getString("name") + " " + userName.getString("surname")
          val petList = mutableListOf<ListPetRowModel>()

          for (i in 0 until petsArray.length()) {
            val petObject = petsArray.getJSONObject(i)

            // Extract relevant information from the petObject
            val petID = petObject.getString("petID")
            val petName = petObject.getString("petName")
            val age = petObject.getString("age")
            val petType = petObject.getInt("petTypeID")
            var petTypeString: String? = null
            if (petType == 1)
              petTypeString = "Cat"
            if (petType == 2)
              petTypeString = "Dog"
            if (petType == 3)
              petTypeString = "Bird"
            if (petType == 4)
              petTypeString = "Other"
            val listPetRowModel = ListPetRowModel(petName, petTypeString, age, petID)
            petList.add(listPetRowModel)
          }
          viewModel.petOwnerDashboardModel.value?.txtCount = petList.size.toString()
          listAdapter.updateData(petList)
          binding.recyclerListPet.adapter = listAdapter
          if (petList.isNotEmpty()) {
             body1.visibility = View.GONE
             body2.visibility = View.GONE
             body3.visibility = View.GONE
             container.visibility = View.VISIBLE
             recyclerList.visibility = View.VISIBLE
          } else {
            body1.visibility = View.VISIBLE
            body2.visibility = View.VISIBLE
            body3.visibility = View.VISIBLE
            container.visibility = View.GONE
            recyclerList.visibility = View.GONE
          }
        }

        binding.petOwnerDashboardVM = viewModel
      },
      { error ->
        // Handle errors
        Log.e("YourActivity", "Error: ${error.message}")
      })


    requestQueue.add(jsonObjectRequest)
  }

  override fun onStop() {
    super.onStop()
    // Cancel all requests when the activity is stopped to avoid memory leaks.
    requestQueue.cancelAll(this)
  }

  companion object {
    const val TAG: String = "PET_OWNER_DASHBOARD_ACTIVITY"
  }
}
