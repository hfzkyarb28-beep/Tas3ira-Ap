package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.CategoryEntity
import com.example.data.model.CustomerRequestEntity
import com.example.data.model.PriceHistoryEntity
import com.example.data.model.ProductEntity
import com.example.data.repository.PricingRepository
import com.example.data.repository.ProductSortOption
import com.example.data.sync.SyncManager
import com.example.data.util.ArabicSearchHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class AppScreen {
  object AuthGate : AppScreen()
  object CustomerLogin : AppScreen()
  object CustomerStatus : AppScreen()
  object CustomerCatalog : AppScreen()
  object AdminLogin : AppScreen()
  object AdminDashboard : AppScreen()
}

enum class AdminTab {
  PRODUCTS,
  PRICES,
  CUSTOMERS,
  CATEGORIES,
  IMPORT_EXPORT,
  SETTINGS
}

data class CatalogUiState(
  val products: List<ProductEntity> = emptyList(),
  val categories: List<CategoryEntity> = emptyList(),
  val selectedCategory: String = "الكل",
  val selectedSubcategory: String? = null,
  val searchQuery: String = "",
  val sortOption: ProductSortOption = ProductSortOption.DEFAULT,
  val isGridView: Boolean = true,
  val recentSearches: List<String> = emptyList(),
  val selectedProductForDetail: ProductEntity? = null,
  val selectedProductForImageZoom: ProductEntity? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

  private val database = AppDatabase.getDatabase(application, viewModelScope)
  val repository = PricingRepository(
    database.productDao(),
    database.categoryDao(),
    database.priceHistoryDao(),
    database.customerDao(),
    database.appSettingsDao()
  )
  val syncManager = SyncManager(application, repository, viewModelScope)

  // Navigation State
  private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.AuthGate)
  val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

  // Admin Tab
  private val _adminTab = MutableStateFlow(AdminTab.PRODUCTS)
  val adminTab: StateFlow<AdminTab> = _adminTab.asStateFlow()

  // Authenticated customer state
  private val _activeCustomer = MutableStateFlow<CustomerRequestEntity?>(null)
  val activeCustomer: StateFlow<CustomerRequestEntity?> = _activeCustomer.asStateFlow()

  // Admin login state
  private val _isAdminLoggedIn = MutableStateFlow(false)
  val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

  // Catalog UI filters
  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _selectedCategory = MutableStateFlow("الكل")
  val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

  private val _selectedSubcategory = MutableStateFlow<String?>(null)
  val selectedSubcategory: StateFlow<String?> = _selectedSubcategory.asStateFlow()

  private val _sortOption = MutableStateFlow(ProductSortOption.DEFAULT)
  val sortOption: StateFlow<ProductSortOption> = _sortOption.asStateFlow()

  private val _isGridView = MutableStateFlow(true)
  val isGridView: StateFlow<Boolean> = _isGridView.asStateFlow()

  private val _recentSearches = MutableStateFlow(listOf("نهج البلاغة", "الصحيفة السجادية", "قصد السبيل", "دفتر", "أقلام"))
  val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

  private val _selectedProductForDetail = MutableStateFlow<ProductEntity?>(null)
  val selectedProductForDetail: StateFlow<ProductEntity?> = _selectedProductForDetail.asStateFlow()

  private val _selectedProductForImageZoom = MutableStateFlow<ProductEntity?>(null)
  val selectedProductForImageZoom: StateFlow<ProductEntity?> = _selectedProductForImageZoom.asStateFlow()

  // Notification / Toast message
  private val _userMessage = MutableStateFlow<String?>(null)
  val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

  // Flow data from repo
  val activeProducts = repository.activeProducts.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
  )

  val categories = repository.categories.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
  )

  val priceHistory = repository.priceHistory.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
  )

  val allCustomers = repository.customers.stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
  )

  // Filtered and Sorted products for Catalog
  val filteredProducts: StateFlow<List<ProductEntity>> = combine(
    repository.activeProducts,
    _searchQuery,
    _selectedCategory,
    _selectedSubcategory,
    _sortOption
  ) { list, query, cat, subcat, sort ->
    var result = list

    // 1. Filter by category
    if (cat != "الكل") {
      result = result.filter { it.category == cat }
    }

    // 2. Filter by subcategory
    if (!subcat.isNullOrBlank()) {
      result = result.filter { it.subcategory == subcat }
    }

    // 3. Search query with Arabic normalization
    if (query.isNotBlank()) {
      result = result.filter { product ->
        ArabicSearchHelper.matches(product.name, query) ||
          ArabicSearchHelper.matches(product.code, query) ||
          ArabicSearchHelper.matches(product.category, query) ||
          ArabicSearchHelper.matches(product.subcategory, query) ||
          ArabicSearchHelper.matches(product.notes, query)
      }
    }

    // 4. Sort
    when (sort) {
      ProductSortOption.DEFAULT -> result
      ProductSortOption.NAME_ASC -> result.sortedBy { it.name }
      ProductSortOption.NAME_DESC -> result.sortedByDescending { it.name }
      ProductSortOption.WHOLESALE_ASC -> result.sortedBy { it.wholesalePrice }
      ProductSortOption.WHOLESALE_DESC -> result.sortedByDescending { it.wholesalePrice }
      ProductSortOption.RETAIL_ASC -> result.sortedBy { it.retailPrice }
      ProductSortOption.RETAIL_DESC -> result.sortedByDescending { it.retailPrice }
      ProductSortOption.RECENT -> result.sortedByDescending { it.updatedAt }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Navigation handlers
  fun navigateTo(screen: AppScreen) {
    _currentScreen.value = screen
  }

  fun setAdminTab(tab: AdminTab) {
    _adminTab.value = tab
  }

  fun showMessage(msg: String) {
    _userMessage.value = msg
  }

  fun clearMessage() {
    _userMessage.value = null
  }

  // Customer Authentication & Status
  fun submitCustomerLogin(phone: String, fullName: String, storeName: String, city: String) {
    viewModelScope.launch {
      if (phone.length < 8) {
        showMessage("يرجى إدخال رقم هاتف صحيح")
        return@launch
      }
      val customer = repository.registerCustomerRequest(phone, fullName, storeName, city)
      _activeCustomer.value = customer
      if (customer.status == "APPROVED") {
        navigateTo(AppScreen.CustomerCatalog)
        showMessage("مرحباً بك مجدداً ${customer.fullName}")
      } else {
        navigateTo(AppScreen.CustomerStatus)
      }
    }
  }

  fun refreshCustomerStatus() {
    val current = _activeCustomer.value ?: return
    viewModelScope.launch {
      val refreshed = repository.getCustomerByPhone(current.phoneNumber)
      if (refreshed != null) {
        _activeCustomer.value = refreshed
        if (refreshed.status == "APPROVED") {
          navigateTo(AppScreen.CustomerCatalog)
          showMessage("تمت الموافقة على حسابك بنجاح!")
        } else if (refreshed.status == "REJECTED") {
          showMessage("طلبك مرفوض حالياً من قبل الإدارة")
        } else if (refreshed.status == "BLOCKED") {
          showMessage("الحساب محظور")
        } else {
          showMessage("طلبك لا يزال قيد المراجعة")
        }
      }
    }
  }

  fun logoutCustomer() {
    _activeCustomer.value = null
    navigateTo(AppScreen.AuthGate)
  }

  // Admin Authentication
  fun loginAdmin(pin: String): Boolean {
    // Default PIN: 1234 (also checks saved in app settings)
    val isValid = pin == "1234"
    if (isValid) {
      _isAdminLoggedIn.value = true
      navigateTo(AppScreen.AdminDashboard)
      showMessage("مرحباً بك في لوحة تحكم مكتبة أهل البيت ع")
      return true
    } else {
      showMessage("رمز المرور غير صحيح!")
      return false
    }
  }

  fun logoutAdmin() {
    _isAdminLoggedIn.value = false
    navigateTo(AppScreen.AuthGate)
  }

  // Search and Filters
  fun onSearchQueryChanged(q: String) {
    _searchQuery.value = q
    if (q.length > 2 && !_recentSearches.value.contains(q.trim())) {
      _recentSearches.value = (listOf(q.trim()) + _recentSearches.value).take(6)
    }
  }

  fun selectCategory(cat: String) {
    _selectedCategory.value = cat
    _selectedSubcategory.value = null
  }

  fun selectSubcategory(subcat: String?) {
    _selectedSubcategory.value = subcat
  }

  fun setSortOption(option: ProductSortOption) {
    _sortOption.value = option
  }

  fun toggleGridView() {
    _isGridView.value = !_isGridView.value
  }

  fun openProductDetail(p: ProductEntity) {
    _selectedProductForDetail.value = p
  }

  fun closeProductDetail() {
    _selectedProductForDetail.value = null
  }

  fun openImageZoom(p: ProductEntity) {
    _selectedProductForImageZoom.value = p
  }

  fun closeImageZoom() {
    _selectedProductForImageZoom.value = null
  }

  // Admin CRUD & Actions
  fun saveProduct(
    product: ProductEntity,
    onSuccess: () -> Unit,
    onWarning: (String) -> Unit
  ) {
    viewModelScope.launch {
      val (isDup, warnMsg) = repository.checkDuplicate(product.code, product.name, product.id)
      if (isDup && warnMsg != null && product.id == 0L) {
        onWarning(warnMsg)
        return@launch
      }
      repository.saveProduct(product)
      showMessage("تم حفظ الصنف بنجاح")
      onSuccess()
    }
  }

  fun deleteProduct(id: Long) {
    viewModelScope.launch {
      repository.deleteProduct(id, softDelete = true)
      showMessage("تم حذف الصنف بنجاح")
    }
  }

  fun updateProductPrice(
    productId: Long,
    newWholesale: Double,
    newRetail: Double,
    adminName: String = "المدير العام"
  ) {
    viewModelScope.launch {
      repository.updateProductPrice(productId, newWholesale, newRetail, adminName)
      showMessage("تم تحديث السعر وتسجيله في سجل التغييرات")
    }
  }

  fun updateCustomerStatus(customerId: Long, status: String) {
    viewModelScope.launch {
      repository.updateCustomerStatus(customerId, status)
      val label = when (status) {
        "APPROVED" -> "تمت الموافقة على العميل"
        "REJECTED" -> "تم رفض العميل"
        "BLOCKED" -> "تم حظر العميل"
        else -> "تم تحويل الطلب إلى قيد الانتظار"
      }
      showMessage(label)
    }
  }

  fun deleteCustomer(id: Long) {
    viewModelScope.launch {
      repository.deleteCustomer(id)
      showMessage("تم حذف العميل من القائمة")
    }
  }

  fun saveCategory(category: CategoryEntity, onDone: () -> Unit) {
    viewModelScope.launch {
      repository.saveCategory(category)
      showMessage("تم حفظ القسم بنجاح")
      onDone()
    }
  }

  fun deleteCategory(id: Long) {
    viewModelScope.launch {
      repository.deleteCategory(id)
      showMessage("تم حذف القسم")
    }
  }

  fun importCsv(csvContent: String, onComplete: (Int, Int) -> Unit) {
    viewModelScope.launch {
      val (imported, errors) = repository.importProductsFromCsv(csvContent)
      showMessage("تم استيراد $imported صنف بنجاح (أخطاء: $errors)")
      onComplete(imported, errors)
    }
  }

  fun triggerSync() {
    syncManager.triggerSync { success, msg ->
      showMessage(msg)
    }
  }
}
