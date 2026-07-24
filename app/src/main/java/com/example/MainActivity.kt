package com.example

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Message
import android.provider.Settings
import android.webkit.*
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Science
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.ui.theme.MyApplicationTheme
import android.view.WindowManager
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Brush
import kotlinx.coroutines.delay

private const val PRIMARY_URL = "https://zahangirschemistry.netlify.app/"
private const val SECONDARY_URL = "https://zahangirchemistry.netlify.app/"
private const val TERTIARY_URL = "https://zahangirchemistry.com/"

class MainActivity : ComponentActivity() {

    private var webView: WebView? = null
    
    // States for reactive Jetpack Compose UI
    private var isOffline by mutableStateOf(false)
    private var showErrorState by mutableStateOf(false)
    private var errorMsg by mutableStateOf("")
    private var isLoadingState by mutableStateOf(true)
    private var loadProgress by mutableStateOf(0)

    // File Chooser state
    private var uploadMessage: ValueCallback<Array<Uri>>? = null

    // Callbacks for dynamic runtime permission requests inside WebView
    private var pendingPermissionRequest: PermissionRequest? = null
    private var pendingLocationCallback: GeolocationPermissions.Callback? = null
    private var pendingLocationOrigin: String? = null

    // Multi-permission launcher
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle Camera & Microphone permission results
        pendingPermissionRequest?.let { request ->
            val resources = request.resources
            val grantedResources = mutableListOf<String>()
            if (resources.contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE) &&
                permissions[Manifest.permission.CAMERA] == true
            ) {
                grantedResources.add(PermissionRequest.RESOURCE_VIDEO_CAPTURE)
            }
            if (resources.contains(PermissionRequest.RESOURCE_AUDIO_CAPTURE) &&
                permissions[Manifest.permission.RECORD_AUDIO] == true
            ) {
                grantedResources.add(PermissionRequest.RESOURCE_AUDIO_CAPTURE)
            }

            if (grantedResources.isNotEmpty()) {
                request.grant(grantedResources.toTypedArray())
            } else {
                request.deny()
            }
            pendingPermissionRequest = null
        }

        // Handle Geolocation permission result
        pendingLocationCallback?.let { callback ->
            val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            callback.invoke(pendingLocationOrigin, granted, false)
            pendingLocationCallback = null
            pendingLocationOrigin = null
        }
    }

    // File selection launcher
    private val fileChooserLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val results = if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                val clipData = data.clipData
                if (clipData != null) {
                    val uris = mutableListOf<Uri>()
                    for (i in 0 until clipData.itemCount) {
                        uris.add(clipData.getItemAt(i).uri)
                    }
                    uris.toTypedArray()
                } else {
                    val uri = data.data
                    if (uri != null) arrayOf(uri) else null
                }
            } else {
                null
            }
        } else {
            null
        }
        uploadMessage?.onReceiveValue(results)
        uploadMessage = null
    }

    // Connectivity manager callbacks
    private var connectivityManager: ConnectivityManager? = null
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            runOnUiThread {
                if (isOnline(this@MainActivity)) {
                    isOffline = false
                    if (showErrorState) {
                        showErrorState = false
                        webView?.reload()
                    }
                }
            }
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            runOnUiThread {
                if (!isOnline(this@MainActivity)) {
                    isOffline = true
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Use WindowCompat to unfit system windows decor for true edge-to-edge underlay
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        // Apply WindowInsetsControllerCompat to ensure status/navigation bar icons are clearly readable on dark background
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false // False for white text/icons on dark background
            isAppearanceLightNavigationBars = false // False for white icons on dark background
        }
        
        // Ensure web content goes edge-to-edge even in display cutouts (notches / punch-holes)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        enableEdgeToEdge()

        // Pre-create Chromium Code Cache directories to prevent log warnings/errors on folder enumeration
        try {
            val jsDir = java.io.File(cacheDir, "WebView/Default/HTTP Cache/Code Cache/js")
            val wasmDir = java.io.File(cacheDir, "WebView/Default/HTTP Cache/Code Cache/wasm")
            if (!jsDir.exists()) {
                jsDir.mkdirs()
            }
            if (!wasmDir.exists()) {
                wasmDir.mkdirs()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Initialize connection status as online initially to allow WebView to attempt loading
        isOffline = false

        // Register Network Callback for offline tracking
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        try {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            connectivityManager?.registerNetworkCallback(request, networkCallback)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        setContent {
            MyApplicationTheme {
                var showSplash by remember { mutableStateOf(true) }

                LaunchedEffect(isLoadingState) {
                    // Check initial network state immediately on splash
                    if (showSplash) {
                        isOffline = !isOnline(this@MainActivity)
                        if (!isLoadingState) {
                            delay(500) // Small delay to ensure smooth transition
                            showSplash = false
                        }
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0A0A0A)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Edge-to-edge container for the WebView with safe area padding to prevent notch & bar overlap
                        WebViewContainer(
                            url = PRIMARY_URL,
                            onCreated = { initializedWebView ->
                                webView = initializedWebView
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .safeDrawingPadding()
                        )

                        // Elegant overlay loader with layout-preserving exit transition (Framer Motion style)
                        AnimatedVisibility(
                            visible = isLoadingState && !showErrorState && !isOffline && !showSplash,
                            enter = fadeIn(animationSpec = tween(durationMillis = 300)) + scaleIn(initialScale = 0.98f, animationSpec = tween(durationMillis = 300)),
                            exit = fadeOut(animationSpec = tween(durationMillis = 400)) + scaleOut(targetScale = 1.02f, animationSpec = tween(durationMillis = 400))
                        ) {
                            LoadingScreen(progress = loadProgress)
                        }

                        // Beautiful full-screen Material 3 offline/error handler
                        if ((isOffline || showErrorState) && !showSplash) {
                            ErrorScreen(
                                errorMsg = if (isOffline) "No connection. Please verify your connection status and try again." else errorMsg,
                                onRetry = {
                                    isOffline = !isOnline(this@MainActivity)
                                    if (!isOffline) {
                                        showErrorState = false
                                        webView?.reload()
                                    }
                                },
                                onOpenSettings = {
                                    try {
                                        startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                                    } catch (e: Exception) {
                                        try {
                                            startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                                        } catch (ex: Exception) {
                                            Toast.makeText(this@MainActivity, "Cannot open settings", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            )
                        }

                        // Beautiful Splash Screen overlay
                        AnimatedVisibility(
                            visible = showSplash,
                            enter = fadeIn(animationSpec = tween(durationMillis = 200)),
                            exit = fadeOut(animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)) + scaleOut(targetScale = 1.05f, animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing))
                        ) {
                            SplashScreen()
                        }
                    }

                    val haptics = LocalHapticFeedback.current
                    // Native-like back button navigation handler
                    BackHandler(enabled = !showSplash) {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        val currentWebView = webView
                        if (currentWebView != null && currentWebView.canGoBack()) {
                            // If we can navigate back inside the WebView history, do so
                            currentWebView.goBack()
                        } else {
                            // Otherwise exit the app cleanly
                            finish()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        try {
            connectivityManager?.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        webView?.let {
            it.stopLoading()
            it.destroy()
        }
        webView = null
        super.onDestroy()
    }

    @Composable
    private fun WebViewContainer(
        url: String,
        onCreated: (WebView) -> Unit,
        modifier: Modifier = Modifier
    ) {
        AndroidView(
            modifier = modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    
                    onCreated(this)
                    
                    // Hardware Acceleration & Performance (bound cleanly for glowing styling & smooth transitions)
                    setLayerType(WebView.LAYER_TYPE_HARDWARE, null)
                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
                    isVerticalScrollBarEnabled = true
                    isHorizontalScrollBarEnabled = false

                    // Modern web settings for responsive CSS, Flexbox, and viewport calibrations
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        textZoom = 90
                        mediaPlaybackRequiresUserGesture = false
                        setSupportMultipleWindows(true)
                        
                        // Service Worker cache optimization & persistent storage
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            android.webkit.ServiceWorkerController.getInstance().serviceWorkerWebSettings.apply {
                                allowContentAccess = true
                                cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                            }
                        }
                        
                        // Security enhancements
                        allowFileAccess = false
                        allowContentAccess = true
                        safeBrowsingEnabled = true

                        // Viewport Scaling (Strict calibration to handle full view bounds)
                        useWideViewPort = true
                        loadWithOverviewMode = true
                        setSupportZoom(false)
                        builtInZoomControls = false
                        displayZoomControls = false

                        // Geolocation
                        setGeolocationEnabled(true)
                        
                        // Persistent storage and cache optimization (loads content immediately from local cache if present, else fetches)
                        cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                    }

                    // Multi-session cookies preservation
                    val cookieManager = CookieManager.getInstance()
                    cookieManager.setAcceptCookie(true)
                    cookieManager.setAcceptThirdPartyCookies(this, true)

                    // WebViewClient for managing URLs, loading events, and security exceptions
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val requestUrl = request?.url?.toString() ?: return false
                            
                            // Let HTTP/HTTPS requests stay within WebView if they are to Netlify or Zahangir's Chem
                            return if (requestUrl.contains("netlify.app") || requestUrl.contains("zahangirchemistry") || requestUrl.contains("zahangirschemistry") || requestUrl.startsWith("http://") || requestUrl.startsWith("https://")) {
                                false
                            } else {
                                // Redirect other schemes (tel:, mailto:, market:, etc.) to external apps
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(requestUrl))
                                    context.startActivity(intent)
                                    true
                                } catch (e: Exception) {
                                    true
                                }
                            }
                        }

                        override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            if (!showErrorState) {
                                isLoadingState = true
                            }
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isLoadingState = false
                            // Inject Javascript to enforce no-zoom
                            val js = "javascript:(function() { " +
                                     "var meta = document.querySelector('meta[name=\"viewport\"]'); " +
                                     "if (meta) { meta.setAttribute('content', 'width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0'); } " +
                                     "else { meta = document.createElement('meta'); meta.name = 'viewport'; meta.content = 'width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0'; document.getElementsByTagName('head')[0].appendChild(meta); } " +
                                     "})();"
                            view?.evaluateJavascript(js, null)
                        }

                        override fun onReceivedSslError(
                            view: WebView?,
                            handler: SslErrorHandler?,
                            error: android.net.http.SslError?
                        ) {
                            // Enforce strict security: Cancel connection, don't proceed
                            handler?.cancel()
                            showErrorState = true
                            errorMsg = "Security Verification Failed (SSL Error). Unable to establish a safe connection."
                        }

                        private fun tryLoadFallback(view: WebView?, reqUrl: String) {
                            val cleanUrl = reqUrl.trimEnd('/')
                            val nextUrl = when {
                                cleanUrl.startsWith(PRIMARY_URL.trimEnd('/')) -> SECONDARY_URL
                                cleanUrl.startsWith(SECONDARY_URL.trimEnd('/')) -> TERTIARY_URL
                                cleanUrl.startsWith(TERTIARY_URL.trimEnd('/')) -> PRIMARY_URL
                                else -> PRIMARY_URL
                            }
                            runOnUiThread {
                                view?.loadUrl(nextUrl)
                            }
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?
                        ) {
                            super.onReceivedError(view, request, error)
                            if (request?.isForMainFrame == true) {
                                // Explicitly check actual network connectivity to prevent false offline messages
                                val online = isOnline(this@MainActivity)
                                val reqUrl = request.url?.toString() ?: ""
                                if (!online) {
                                    isOffline = true
                                    showErrorState = true
                                    errorMsg = "Internet Disconnected. Please check your network connection and try again."
                                } else {
                                    // If we are actually online, check the specific error code
                                    val errorCode = error?.errorCode ?: 0
                                    if (errorCode == ERROR_HOST_LOOKUP || errorCode == ERROR_CONNECT || errorCode == ERROR_TIMEOUT) {
                                        if (reqUrl.contains("zahangirchemistry") || reqUrl.contains("zahangirschemistry")) {
                                            tryLoadFallback(view, reqUrl)
                                        } else {
                                            showErrorState = true
                                            errorMsg = "Unable to connect to Home of Chemistry. Please try again later."
                                        }
                                    } else {
                                        // Ignore non-critical loading errors or transient warnings when online
                                        // to ensure the WebView accurately reflects the Netlify deployment
                                    }
                                }
                            }
                        }

                        override fun onReceivedHttpError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            errorResponse: WebResourceResponse?
                        ) {
                            super.onReceivedHttpError(view, request, errorResponse)
                            if (request?.isForMainFrame == true) {
                                val statusCode = errorResponse?.statusCode ?: 0
                                // Check actual connectivity
                                val online = isOnline(this@MainActivity)
                                val reqUrl = request.url?.toString() ?: ""
                                if (!online) {
                                    isOffline = true
                                    showErrorState = true
                                    errorMsg = "Internet Disconnected. Please check your network connection and try again."
                                } else if (statusCode == 404) {
                                    // Handle 404 Netlify Page Not Found spelling fallback
                                    if (reqUrl.contains("zahangirchemistry") || reqUrl.contains("zahangirschemistry")) {
                                        tryLoadFallback(view, reqUrl)
                                    }
                                } else if (statusCode >= 500) {
                                    // Only show error for critical server-side issues (5xx), ignoring 401/403/404 etc if online,
                                    // so the Web app can handle its own routing/error states if needed.
                                    showErrorState = true
                                    errorMsg = "Server returned an error (HTTP Status $statusCode)."
                                }
                            }
                        }
                    }

                    // WebChromeClient for handling permissions, downloads, multi-windows, and progress
                    webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            super.onProgressChanged(view, newProgress)
                            loadProgress = newProgress
                            if (newProgress >= 100) {
                                isLoadingState = false
                            }
                        }

                        // Popups and multiple window support
                        override fun onCreateWindow(
                            view: WebView?,
                            isDialog: Boolean,
                            isUserGesture: Boolean,
                            resultMsg: Message?
                        ): Boolean {
                            val transport = resultMsg?.obj as? WebView.WebViewTransport ?: return false
                            transport.webView = view
                            resultMsg.sendToTarget()
                            return true
                        }

                        // Dynamic WebRTC (Camera/Microphone capture) permission prompt
                        override fun onPermissionRequest(request: PermissionRequest?) {
                            if (request == null) return
                            val resources = request.resources
                            val permissionsToRequest = mutableListOf<String>()
                            if (resources.contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE)) {
                                permissionsToRequest.add(Manifest.permission.CAMERA)
                            }
                            if (resources.contains(PermissionRequest.RESOURCE_AUDIO_CAPTURE)) {
                                permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
                            }

                            if (permissionsToRequest.isEmpty()) {
                                request.grant(resources)
                            } else {
                                val allGranted = permissionsToRequest.all {
                                    ContextCompat.checkSelfPermission(this@MainActivity, it) == PackageManager.PERMISSION_GRANTED
                                }
                                if (allGranted) {
                                    request.grant(resources)
                                } else {
                                    pendingPermissionRequest = request
                                    permissionLauncher.launch(permissionsToRequest.toTypedArray())
                                }
                            }
                        }

                        // Dynamic Geolocation permission prompt
                        override fun onGeolocationPermissionsShowPrompt(
                            origin: String?,
                            callback: GeolocationPermissions.Callback?
                        ) {
                            if (callback == null) return
                            if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                callback.invoke(origin, true, false)
                            } else {
                                pendingLocationCallback = callback
                                pendingLocationOrigin = origin
                                permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
                            }
                        }

                        // Dynamic File Chooser for Uploading Files/Images
                        override fun onShowFileChooser(
                            webView: WebView?,
                            filePathCallback: ValueCallback<Array<Uri>>?,
                            fileChooserParams: FileChooserParams?
                        ): Boolean {
                            uploadMessage?.onReceiveValue(null)
                            uploadMessage = filePathCallback

                            val intent = fileChooserParams?.createIntent() ?: Intent(Intent.ACTION_GET_CONTENT).apply {
                                type = "*/*"
                                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                            }

                            return try {
                                fileChooserLauncher.launch(intent)
                                true
                            } catch (e: Exception) {
                                uploadMessage?.onReceiveValue(null)
                                uploadMessage = null
                                Toast.makeText(this@MainActivity, "Could not open file selection.", Toast.LENGTH_SHORT).show()
                                false
                            }
                        }
                    }

                    // Native download interceptor with DownloadManager progress notifications
                    setDownloadListener { url, userAgent, contentDisposition, mimetype, _ ->
                        val filename = URLUtil.guessFileName(url, contentDisposition, mimetype)
                        
                        // Scoped-storage handles modern downloads directly.
                        // On legacy devices (Android P/9, API 28 and below), request storage permission.
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                            if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(this@MainActivity, "Write permission required for downloads", Toast.LENGTH_SHORT).show()
                                permissionLauncher.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                                return@setDownloadListener
                            }
                        }
                        
                        startDownload(url, userAgent, mimetype, filename)
                    }

                    loadUrl(url)
                }
            },
            update = { webViewInstance ->
                if (webViewInstance.url != url) {
                    webViewInstance.loadUrl(url)
                }
            }
        )
    }

    private fun startDownload(url: String, userAgent: String, mimetype: String, filename: String) {
        try {
            val request = DownloadManager.Request(Uri.parse(url)).apply {
                setMimeType(mimetype)
                addRequestHeader("User-Agent", userAgent)
                setDescription("Downloading file from Zahangir's Chemistry")
                setTitle(filename)
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
            }
            val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
            Toast.makeText(this, "Downloading file: $filename", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Download failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

@Composable
fun SplashScreen() {
    var startAnimation by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        startAnimation = true
    }
    
    val progress by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 2400, easing = LinearEasing),
        label = "progress_anim"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F12)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_splash_banner),
                contentDescription = "Splash Banner",
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(1.4f)
                    .wrapContentHeight(),
                contentScale = ContentScale.Fit
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            LinearProgressIndicator(
                progress = { progress },
                color = Color(0xFFE91E63),
                trackColor = Color(0xFFE91E63).copy(alpha = 0.2f),
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "${(progress * 100).toInt()}%",
                color = Color(0xFFE91E63),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
            )
        }
    }
}

@Composable
fun LoadingScreen(progress: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )

    // Layout representation of Zahangir's Chemistry Dashboard Skeleton
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F12)) // Deep modern science lab charcoal background
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        // Top Info & Progress bar combined elegantly
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Circular branding logo / profile container
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFDB2777).copy(alpha = alpha * 0.2f))
                        .border(
                            BorderStroke(1.dp, Color(0xFFDB2777).copy(alpha = 0.3f)),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🧪",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    // Title skeleton bar
                    Box(
                        modifier = Modifier
                            .width(140.dp)
                            .height(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White.copy(alpha = alpha * 0.15f))
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    // Subtitle skeleton bar
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(10.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color.White.copy(alpha = alpha * 0.10f))
                    )
                }
            }
            
            // Progress percentage on the right
            if (progress > 0) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFFDB2777).copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "$progress%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFDB2777)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(20.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = alpha * 0.10f))
                )
            }
        }

        // Thin beautiful progress bar spanning the top of the content
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(Color(0xFF1E1E24), shape = RoundedCornerShape(2.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(if (progress > 0) progress / 100f else 0.4f)
                    .fillMaxHeight()
                    .background(Color(0xFFDB2777), shape = RoundedCornerShape(2.dp))
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Search Bar Skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF16161B))
                .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = alpha * 0.10f))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .height(12.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color.White.copy(alpha = alpha * 0.08f))
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Prominent Chemistry Hero Card Skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1E1E24))
                .border(BorderStroke(1.dp, Color(0xFFDB2777).copy(alpha = 0.15f)), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFDB2777).copy(alpha = alpha * 0.25f))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .width(220.dp)
                            .height(18.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White.copy(alpha = alpha * 0.18f))
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .width(160.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color.White.copy(alpha = alpha * 0.12f))
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(24.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFDB2777).copy(alpha = alpha * 0.3f))
                    )
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = alpha * 0.10f))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Grid Title
        Box(
            modifier = Modifier
                .width(150.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White.copy(alpha = alpha * 0.15f))
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 2x2 Grid of Quick Tools
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 1
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF16161B))
                        .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.04f)), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFDB2777).copy(alpha = alpha * 0.15f))
                        )
                        Box(
                            modifier = Modifier
                                .width(70.dp)
                                .height(10.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color.White.copy(alpha = alpha * 0.12f))
                        )
                    }
                }
                // Card 2
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF16161B))
                        .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.04f)), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF3B82F6).copy(alpha = alpha * 0.15f))
                        )
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(10.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color.White.copy(alpha = alpha * 0.12f))
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 3
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF16161B))
                        .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.04f)), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF10B981).copy(alpha = alpha * 0.15f))
                        )
                        Box(
                            modifier = Modifier
                                .width(65.dp)
                                .height(10.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color.White.copy(alpha = alpha * 0.12f))
                        )
                    }
                }
                // Card 4
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF16161B))
                        .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.04f)), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFF59E0B).copy(alpha = alpha * 0.15f))
                        )
                        Box(
                            modifier = Modifier
                                .width(75.dp)
                                .height(10.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color.White.copy(alpha = alpha * 0.12f))
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Recent Lessons / Feed Heading
        Box(
            modifier = Modifier
                .width(130.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White.copy(alpha = alpha * 0.15f))
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Feed list item
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF16161B))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White.copy(alpha = alpha * 0.12f))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .height(11.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color.White.copy(alpha = alpha * 0.14f))
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(8.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = alpha * 0.08f))
                )
            }
        }
    }
}

@Composable
fun ErrorScreen(
    errorMsg: String,
    onRetry: () -> Unit,
    onOpenSettings: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedElement by remember { mutableStateOf<PeriodicElement?>(PeriodicTableData.elements.first()) }

    val categories = listOf(
        "Alkali Metal", "Alkaline Earth", "Transition Metal", 
        "Post-Transition Metal", "Metalloid", "Nonmetal", 
        "Halogen", "Noble Gas", "Lanthanide", "Actinide"
    )

    fun getCategoryColor(category: String): Color {
        return when (category.lowercase()) {
            "alkali metal" -> Color(0xFFEF4444)
            "alkaline earth" -> Color(0xFFF59E0B)
            "transition metal" -> Color(0xFF3B82F6)
            "post-transition metal" -> Color(0xFF10B981)
            "metalloid" -> Color(0xFF84CC16)
            "nonmetal" -> Color(0xFFEC4899)
            "halogen" -> Color(0xFFA855F7)
            "noble gas" -> Color(0xFF06B6D4)
            "lanthanide" -> Color(0xFFF43F5E)
            "actinide" -> Color(0xFFD946EF)
            else -> Color(0xFF9CA3AF)
        }
    }

    val filteredElements = remember(searchQuery, selectedCategory) {
        PeriodicTableData.elements.filter { element ->
            val matchesSearch = element.name.contains(searchQuery, ignoreCase = true) ||
                    element.symbol.contains(searchQuery, ignoreCase = true) ||
                    element.number.toString() == searchQuery

            val matchesCategory = selectedCategory == null || element.category.equals(selectedCategory, ignoreCase = true)

            matchesSearch && matchesCategory
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0F)) // Deep dark scientific dashboard background
    ) {
        // High-fidelity top notification banner for connection status & quick retry action keys
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF14141A))
                .border(BorderStroke(1.dp, Color(0xFFDB2777).copy(alpha = 0.25f)))
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFEF4444).copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WifiOff,
                            contentDescription = "Offline Mode",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "OFFLINE STUDY STATION",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                            color = Color.White
                        )
                        Text(
                            text = "Interactive Periodic Table Active",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }

                // Small quick reconnect action buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val haptics = LocalHapticFeedback.current
                    Button(
                        onClick = { 
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onRetry() 
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFDB2777),
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("retry_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Retry", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Retry", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { 
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onOpenSettings() 
                        },
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("settings_button")
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", modifier = Modifier.size(14.dp), tint = Color.White.copy(alpha = 0.8f))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Settings", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
        }

        // Search Bar, view switcher toggle, and category selector
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Search field (fully spans the horizontal width for mobile)
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Find element name, symbol, number...", color = Color.White.copy(alpha = 0.4f), style = MaterialTheme.typography.bodyMedium) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White.copy(alpha = 0.4f)) },
                    trailingIcon = {
                        val haptics = LocalHapticFeedback.current
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { 
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                searchQuery = "" 
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.White.copy(alpha = 0.6f))
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF15151C),
                        unfocusedContainerColor = Color(0xFF15151C),
                        focusedBorderColor = Color(0xFFDB2777),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                )
            }

            // Category filter chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                item {
                    val isSelected = selectedCategory == null
                    val haptics = LocalHapticFeedback.current
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color.White.copy(alpha = 0.15f) else Color(0xFF15151C))
                            .border(
                                BorderStroke(1.dp, if (isSelected) Color.White else Color.White.copy(alpha = 0.05f)),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { 
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                selectedCategory = null 
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("All Series", color = Color.White, style = MaterialTheme.typography.labelMedium)
                    }
                }

                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    val catColor = getCategoryColor(category)
                    val haptics = LocalHapticFeedback.current
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) catColor.copy(alpha = 0.25f) else Color(0xFF15151C))
                            .border(
                                BorderStroke(1.dp, if (isSelected) catColor else Color.White.copy(alpha = 0.05f)),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { 
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                selectedCategory = category 
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(catColor, RoundedCornerShape(2.dp))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(category, color = Color.White, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }

        // Main table presentation view (Standard list layout optimized for mobile)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (filteredElements.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Science, contentDescription = "No match", tint = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No matching elements found", color = Color.White.copy(alpha = 0.5f), style = MaterialTheme.typography.titleMedium)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredElements, key = { it.number }) { element ->
                        val isSelected = selectedElement?.number == element.number
                        val catColor = getCategoryColor(element.category)
                        val haptics = LocalHapticFeedback.current
                        
                        Row(
                            modifier = Modifier
                                .animateItem()
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = if (isSelected) listOf(
                                            catColor.copy(alpha = 0.25f),
                                            catColor.copy(alpha = 0.05f)
                                        ) else listOf(
                                            Color(0xFF1A1A24),
                                            Color(0xFF12121A)
                                        )
                                    )
                                )
                                .border(
                                    BorderStroke(1.dp, if (isSelected) catColor.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.05f)),
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { 
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    selectedElement = element 
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Cell Icon Box
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                catColor,
                                                catColor.copy(alpha = 0.7f)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                    Text(element.number.toString(), style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.ExtraBold), color = Color.White.copy(alpha = 0.9f))
                                    Text(element.symbol, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black), color = Color.White)
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = element.name, 
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp), 
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(androidx.compose.foundation.shape.CircleShape)
                                            .background(catColor)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = element.category.uppercase(), 
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp), 
                                        color = Color.White.copy(alpha = 0.6f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(start = 12.dp)) {
                                // Prominent display of Configuration!
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(catColor.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = element.config,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        ),
                                        color = catColor,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "${element.weight} u",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Expanded Bottom Console Sheet displaying full Electronic Configuration & properties of the selected element
        selectedElement?.let { element ->
            val catColor = getCategoryColor(element.category)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color(0xFF111116))
                    .border(
                        BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                        RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(catColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = element.number.toString(),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                    color = Color.Black
                                )
                                Text(
                                    text = element.symbol,
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = element.name,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .background(catColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = element.phase,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        color = catColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = "${element.category} • Group ${if (element.group > 0) element.group else "N/A"}, Period ${element.period}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "ATOMIC MASS",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold),
                            color = Color.White.copy(alpha = 0.4f)
                        )
                        Text(
                            text = "${element.weight} u",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Electron Configuration Highlight Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1B1B22))
                        .border(BorderStroke(1.dp, catColor.copy(alpha = 0.25f)), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ELECTRONIC CONFIGURATION (SUBSHELL MATRIX)",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                color = catColor
                            )
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = "Subshell Config",
                                tint = catColor.copy(alpha = 0.6f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = element.config,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontStyle = FontStyle.Italic
                            ),
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = element.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.75f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }
        }
    }
}
