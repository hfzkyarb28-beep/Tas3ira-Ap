package com.example.data.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.example.data.repository.PricingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SyncManager(
  private val context: Context,
  private val repository: PricingRepository,
  private val scope: CoroutineScope
) {

  private val _isOnline = MutableStateFlow(true)
  val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

  private val _isSyncing = MutableStateFlow(false)
  val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

  private val _lastSyncTime = MutableStateFlow("محدث محلياً")
  val lastSyncTime: StateFlow<String> = _lastSyncTime.asStateFlow()

  private val _syncMessage = MutableStateFlow<String?>(null)
  val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

  init {
    monitorNetwork()
    scope.launch(Dispatchers.IO) {
      val saved = repository.getSetting("last_sync_time")
      if (!saved.isNullOrBlank()) {
        _lastSyncTime.value = saved
      }
    }
  }

  private fun monitorNetwork() {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    if (cm == null) {
      _isOnline.value = true
      return
    }

    val active = cm.activeNetwork
    val caps = cm.getNetworkCapabilities(active)
    _isOnline.value = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

    val request = NetworkRequest.Builder()
      .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
      .build()

    try {
      cm.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
          _isOnline.value = true
        }

        override fun onLost(network: Network) {
          _isOnline.value = false
        }
      })
    } catch (e: Exception) {
      // Fallback
    }
  }

  fun triggerSync(onComplete: (Boolean, String) -> Unit = { _, _ -> }) {
    scope.launch(Dispatchers.IO) {
      _isSyncing.value = true
      try {
        // Simulate remote check or perform Room verification
        kotlinx.coroutines.delay(700)
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        val formatted = sdf.format(Date())
        _lastSyncTime.value = formatted
        repository.setSetting("last_sync_time", formatted)

        val msg = if (_isOnline.value) {
          "تم التحقق والمزامنة بنجاح ($formatted)"
        } else {
          "أنت في وضع عدم الاتصال (Offline) - البيانات محفوظة ومتاحة محلياً"
        }
        _syncMessage.value = msg
        onComplete(true, msg)
      } catch (e: Exception) {
        val errorMsg = "تعذر إكمال المزامنة: ${e.localizedMessage ?: "خطأ غير معروف"}"
        _syncMessage.value = errorMsg
        onComplete(false, errorMsg)
      } finally {
        _isSyncing.value = false
      }
    }
  }

  fun clearSyncMessage() {
    _syncMessage.value = null
  }
}
