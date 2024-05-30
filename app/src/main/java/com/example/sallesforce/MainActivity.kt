package com.example.sallesforce

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.evergage.android.Campaign
import com.evergage.android.CampaignHandler
import com.evergage.android.Evergage
import com.evergage.android.Screen
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private var activeCampaigns: MutableList<Campaign> = mutableListOf()
    private val mobileDataCampaigns = listOf("carrosselHome1", "carrosselHome")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val evergage = Evergage.getInstance()
        evergage.setUserId("34522599862")

        val screen: Screen? = Evergage.getInstance().getScreenForActivity(this)

        activeCampaigns.forEach { screen?.trackImpression(it) }

        screen?.trackAction("visualizacao_detalhes_produto")

        evergage.setUserAttribute("idade", "35")
        evergage.setUserAttribute("ultimo_acesso", "2024-05-30")
        evergage.setUserAttribute("genero", "masculino")

        evergage.setAccountAttribute("TipoCliente", "Teste")
        evergage.setAccountAttribute("segmento", "xpto")
        evergage.setAccountAttribute("classe", "D")

        val button: Button = findViewById(R.id.my_button)

        button.setOnClickListener {
            screen?.trackAction("button_click")
        }

        Log.d("customdebug", "onCreate chamado")
        refreshScreen()
    }

    override fun onStart() {
        super.onStart()
        Log.d("customdebug", "onStart chamado")
        refreshScreen()
    }

    private fun refreshScreen() {
        val featuredProductTextView: TextView = findViewById(R.id.featured_product_text)
        featuredProductTextView.text = "Testeeee"

        val screen: Screen? = Evergage.getInstance().getScreenForActivity(this)
        Log.d("customdebug", "Screen instance: $screen")

        screen?.let {
            val handler = object : CampaignHandler {
                override fun handleCampaign(campaign: Campaign) {
                    Log.d("customdebug", "Entered handleCampaign method")
                    try {
                        val featuredProductName = campaign.data.optString("gaEventName")
                        Log.d("customdebug", "Featured product name: $featuredProductName")
                        if (!featuredProductName.isNullOrEmpty()) {
                            if (activeCampaigns.none { it == campaign }) {
                                screen.trackImpression(campaign)
                                Log.d("customdebug", "Tracked campaign impression")
                                if (!campaign.isControlGroup) {
                                    activeCampaigns.add(campaign)
                                    Log.d("customdebug", "Added campaign to active campaigns list")

                                    val textList: TextView = findViewById(R.id.textView)
                                    textList.text = campaign.data.toString()

                                    setupCarousel(campaign.data.toString())

                                    featuredProductTextView.text = "Our featured product is teste funcionou"
                                    val textView: TextView = findViewById(R.id.featuredProductTextView)
                                    textView.text = campaign.data.getString("url_banner")
                                } else {
                                    Log.d("customdebug", "Campaign is a control group")
                                }
                            } else {
                                Log.d("customdebug", "Campaign is already active")
                            }
                        } else {
                            Log.e("customdebug", "Featured product name is null or empty")
                        }
                    } catch (e: JSONException) {
                        Log.e("customdebug", "Error parsing campaign JSON", e)
                    }
                }
            }
            Log.d("customdebug", "Setting campaign handler")
            mobileDataCampaigns.forEach { campaignName ->
                screen.setCampaignHandler(handler, campaignName)
                Log.d("customdebug", "Set campaign handler for campaign: $campaignName")
            }
            screen.trackAction("screen_loaded")
            Log.d("customdebug", "Tracked screen loaded action")
        } ?: Log.e("TAG", "Screen is null")
    }

    private fun setupCarousel(data: String) {
        val items = extractData(data)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = CarouselAdapter(items)
    }

    private fun extractData(jsonString: String): List<String> {
        val jsonObject = JSONObject(jsonString)
        val keys = jsonObject.keys()
        val dataList = mutableListOf<String>()

        while (keys.hasNext()) {
            val key = keys.next()
            val value = jsonObject.getString(key)
            dataList.add("$key: $value")
        }

        Log.d("customdebug", "DataList: $dataList")
        return dataList
    }
}