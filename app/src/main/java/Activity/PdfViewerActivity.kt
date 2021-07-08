package Activity

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.mjsiddiqui.campusmanagement.R
import dmax.dialog.SpotsDialog

class PdfViewerActivity : AppCompatActivity() {
    private lateinit var link:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)

        val pDialog = SpotsDialog.Builder().setContext(this).build()
        pDialog.setCancelable(true)
        pDialog.setMessage("Please Wait...")
        pDialog.show()

        link = intent.getStringExtra("Mj5")

        val webView:WebView = findViewById(R.id.pdfView)
        webView.settings.javaScriptEnabled = true
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                webView.loadUrl("javascript:(function() { " + "document.querySelector('[role=\"toolbar\"]').remove();})()")
                pDialog.dismiss()
            }
        }
        webView.loadUrl(link)
    }
}