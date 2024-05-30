package com.example.sallesforce

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.evergage.android.Campaign
import com.evergage.android.CampaignHandler
import com.evergage.android.Evergage
import com.evergage.android.Screen
import org.json.JSONException

class MainActivity : AppCompatActivity() {
    private var activeCampaign: Campaign? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Obtenha uma instância do Evergage
        val evergage = Evergage.getInstance()

        // Defina o ID do usuário autenticado assim que for conhecido
        evergage.setUserId("34522599862")

        // Obtenha uma instância do Screen
        val screen: Screen? = Evergage.getInstance().getScreenForActivity(this)

        // Registrar impressão
        activeCampaign?.let { screen?.trackImpression(it) }

        // Evento personalizado
        screen?.trackAction("visualizacao_detalhes_produto")

        // Atributos do cliente / Individual
        evergage.setUserAttribute("idade", "35")
        evergage.setUserAttribute("ultimo_acesso", "2024-05-30")
        evergage.setUserAttribute("genero", "masculino")

        // Atributos do cliente / Grupo
        evergage.setAccountAttribute("TipoCliente", "Teste")
        evergage.setAccountAttribute("segmento", "xpto")
        evergage.setAccountAttribute("classe", "D")

        // Obtenha uma referência para o botão
        val button: Button = findViewById(R.id.my_button)

        // Defina um ouvinte de clique para o botão
        button.setOnClickListener {
            // Rastreie o evento de clique
            screen?.trackAction("button_click")
        }

        Log.d("customdebug", "onCreate chamado")
        // Chame refreshScreen para inicializar o rastreamento de tela do Evergage
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
        Log.d("customdebug", "inicio $screen")

        screen?.let {
            val handler = object : CampaignHandler {
                override fun handleCampaign(campaign: Campaign) {
                    Log.d("customdebug", "Entrou no método handleCampaign")
                    try {
                        val featuredProductName = campaign.data.optString("gaEventName")
                        if (!featuredProductName.isNullOrEmpty()) {
                            Log.d("customdebug", "gaEventName não é nulo ou vazio")
                            if (activeCampaign == null || activeCampaign != campaign) {
                                Log.d("customdebug", "activeCampaign é nulo ou diferente da campanha atual")
                                screen.trackImpression(campaign)
                                if (!campaign.isControlGroup) {
                                    activeCampaign = campaign
                                    Log.d("customdebug", "Exibindo campanha: ${campaign.campaignName}, target: ${campaign.target}, data: ${campaign.data}")
                                    featuredProductTextView.text = "Our featured product is teste funcionou"
                                    val textView: TextView = findViewById(R.id.featuredProductTextView)
                                    textView.text = campaign.data.getString("url_banner")
                                }
                            }
                        } else {
                            Log.e("customdebug", "Campo 'featuredProductName' não encontrado ou vazio na campanha: ${campaign.campaignName}")
                        }
                    } catch (e: JSONException) {
                        Log.e("customdebug", "Erro ao analisar JSON da campanha: ${campaign.campaignName}", e)
                    }
                }
            }
            Log.d("customdebug", "Configurando handler de campanha")
            screen.setCampaignHandler(handler, "carrosselHome1")
            // Rastreie o evento de impressão ao carregar a tela
            screen.trackAction("screen_loaded")
        } ?: Log.e("TAG", "Screen é null")
    }
}