package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CategoryEntity
import com.example.data.model.CustomerRequestEntity
import com.example.data.model.PriceHistoryEntity
import com.example.data.model.ProductEntity
import com.example.ui.components.AvailabilityBadge
import com.example.ui.components.formatCurrency
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.StatusAvailable
import com.example.ui.theme.StatusPaused
import com.example.ui.theme.StatusUnavailable
import com.example.ui.viewmodel.AdminTab
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val currentTab by viewModel.adminTab.collectAsState()
  val allProducts by viewModel.activeProducts.collectAsState()
  val categories by viewModel.categories.collectAsState()
  val customers by viewModel.allCustomers.collectAsState()
  val priceHistory by viewModel.priceHistory.collectAsState()

  var showAddProductDialog by remember { mutableStateOf(false) }
  var productToEdit by remember { mutableStateOf<ProductEntity?>(null) }
  var productForPriceEdit by remember { mutableStateOf<ProductEntity?>(null) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "لوحة إدارة مكتبة أهل البيت ع",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 16.sp
            )
            Text(
              text = "المدير العام • صلاحيات كاملة",
              fontSize = 11.sp,
              color = GoldSecondary
            )
          }
        },
        actions = {
          IconButton(onClick = { viewModel.logoutAdmin() }) {
            Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "خروج", tint = Color.Gray)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    },
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
      ) {
        val navItems = listOf(
          Triple(AdminTab.PRODUCTS, "الأصناف", Icons.Default.Inventory),
          Triple(AdminTab.PRICES, "الأسعار", Icons.Default.PriceChange),
          Triple(AdminTab.CUSTOMERS, "العملاء", Icons.Default.People),
          Triple(AdminTab.CATEGORIES, "الأقسام", Icons.Default.Category),
          Triple(AdminTab.IMPORT_EXPORT, "البيانات", Icons.Default.FileUpload),
          Triple(AdminTab.SETTINGS, "الإعدادات", Icons.Default.Settings)
        )

        navItems.forEach { (tab, label, icon) ->
          val selected = currentTab == tab
          NavigationBarItem(
            selected = selected,
            onClick = { viewModel.setAdminTab(tab) },
            icon = { Icon(imageVector = icon, contentDescription = label) },
            label = { Text(label, fontSize = 10.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = EmeraldPrimary,
              selectedTextColor = EmeraldPrimary,
              indicatorColor = EmeraldPrimary.copy(alpha = 0.15f)
            )
          )
        }
      }
    },
    floatingActionButton = {
      if (currentTab == AdminTab.PRODUCTS) {
        FloatingActionButton(
          onClick = { showAddProductDialog = true },
          containerColor = EmeraldPrimary,
          contentColor = Color.White
        ) {
          Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة صنف جديد")
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(MaterialTheme.colorScheme.background)
    ) {
      when (currentTab) {
        AdminTab.PRODUCTS -> AdminProductsTab(
          products = allProducts,
          onEdit = { productToEdit = it },
          onDelete = { viewModel.deleteProduct(it.id) },
          onEditPrice = { productForPriceEdit = it },
          onAddNew = { showAddProductDialog = true }
        )
        AdminTab.PRICES -> AdminPricesTab(
          products = allProducts,
          priceHistory = priceHistory,
          onEditPrice = { productForPriceEdit = it }
        )
        AdminTab.CUSTOMERS -> AdminCustomersTab(
          customers = customers,
          onUpdateStatus = { id, status -> viewModel.updateCustomerStatus(id, status) },
          onDeleteCustomer = { id -> viewModel.deleteCustomer(id) }
        )
        AdminTab.CATEGORIES -> AdminCategoriesTab(
          categories = categories,
          onSaveCategory = { cat -> viewModel.saveCategory(cat) {} },
          onDeleteCategory = { id -> viewModel.deleteCategory(id) }
        )
        AdminTab.IMPORT_EXPORT -> AdminImportExportTab(
          products = allProducts,
          onImportCsv = { csv -> viewModel.importCsv(csv) { _, _ -> } }
        )
        AdminTab.SETTINGS -> AdminSettingsTab(
          viewModel = viewModel
        )
      }
    }
  }

  // Add / Edit Product Dialog
  if (showAddProductDialog || productToEdit != null) {
    ProductFormDialog(
      initialProduct = productToEdit,
      categories = categories.map { it.name },
      onDismiss = {
        showAddProductDialog = false
        productToEdit = null
      },
      onSave = { product, onSuccess, onWarning ->
        viewModel.saveProduct(product, onSuccess = {
          showAddProductDialog = false
          productToEdit = null
          onSuccess()
        }, onWarning = onWarning)
      }
    )
  }

  // Edit Single Price Dialog
  productForPriceEdit?.let { product ->
    EditPriceDialog(
      product = product,
      onDismiss = { productForPriceEdit = null },
      onConfirm = { wholesale, retail, note ->
        viewModel.updateProductPrice(product.id, wholesale, retail)
        productForPriceEdit = null
      }
    )
  }
}

// -------------------------------------------------------------
// TAB 1: Admin Products
// -------------------------------------------------------------
@Composable
fun AdminProductsTab(
  products: List<ProductEntity>,
  onEdit: (ProductEntity) -> Unit,
  onDelete: (ProductEntity) -> Unit,
  onEditPrice: (ProductEntity) -> Unit,
  onAddNew: () -> Unit
) {
  var searchQuery by remember { mutableStateOf("") }
  var productToDelete by remember { mutableStateOf<ProductEntity?>(null) }

  val filtered = remember(products, searchQuery) {
    if (searchQuery.isBlank()) products
    else products.filter {
      it.name.contains(searchQuery, ignoreCase = true) ||
        it.code.contains(searchQuery, ignoreCase = true) ||
        it.category.contains(searchQuery, ignoreCase = true)
    }
  }

  Column(modifier = Modifier.fillMaxSize()) {
    // Stats Summary Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      AdminStatMiniCard(
        title = "إجمالي الأصناف",
        value = "${products.size}",
        color = EmeraldPrimary,
        modifier = Modifier.weight(1f)
      )
      AdminStatMiniCard(
        title = "متوفر بالمخزن",
        value = "${products.count { it.availability == "AVAILABLE" }}",
        color = StatusAvailable,
        modifier = Modifier.weight(1f)
      )
      AdminStatMiniCard(
        title = "نفد أو متوقف",
        value = "${products.count { it.availability != "AVAILABLE" }}",
        color = StatusUnavailable,
        modifier = Modifier.weight(1f)
      )
    }

    // Search bar
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      placeholder = { Text("بحث في الأصناف بالكود أو الاسم...") },
      leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { searchQuery = "" }) {
            Icon(imageVector = Icons.Default.Clear, contentDescription = "مسح")
          }
        }
      },
      singleLine = true,
      shape = RoundedCornerShape(12.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 4.dp)
    )

    Spacer(modifier = Modifier.height(4.dp))

    // List of products
    LazyColumn(
      contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxSize()
    ) {
      items(filtered, key = { it.id }) { item ->
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = EmeraldPrimary.copy(alpha = 0.1f)
                ) {
                  Text(
                    text = item.code,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
                Spacer(modifier = Modifier.width(6.dp))
                AvailabilityBadge(status = item.availability)
              }

              Row {
                IconButton(onClick = { onEditPrice(item) }, modifier = Modifier.size(32.dp)) {
                  Icon(imageVector = Icons.Default.PriceChange, contentDescription = "تعديل السعر", tint = GoldSecondary)
                }
                IconButton(onClick = { onEdit(item) }, modifier = Modifier.size(32.dp)) {
                  Icon(imageVector = Icons.Default.Edit, contentDescription = "تعديل", tint = EmeraldPrimary)
                }
                IconButton(onClick = { productToDelete = item }, modifier = Modifier.size(32.dp)) {
                  Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", tint = StatusUnavailable)
                }
              }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = item.name,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )

            Text(
              text = "${item.category} • ${item.subcategory}",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "جملة: ${formatCurrency(item.wholesalePrice)}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = EmeraldPrimary
              )
              Text(
                text = "تجزئة: ${formatCurrency(item.retailPrice)}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = GoldSecondary
              )
            }
          }
        }
      }
    }
  }

  productToDelete?.let { prod ->
    AlertDialog(
      onDismissRequest = { productToDelete = null },
      title = { Text("تأكيد حذف الصنف") },
      text = { Text("هل أنت متأكد من حذف (${prod.name})؟ لن يظهر للعملاء.") },
      confirmButton = {
        Button(
          onClick = {
            onDelete(prod)
            productToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = StatusUnavailable)
        ) {
          Text("نعم، حذف")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { productToDelete = null }) {
          Text("إلغاء")
        }
      }
    )
  }
}

// -------------------------------------------------------------
// TAB 2: Admin Prices & Price History
// -------------------------------------------------------------
@Composable
fun AdminPricesTab(
  products: List<ProductEntity>,
  priceHistory: List<PriceHistoryEntity>,
  onEditPrice: (ProductEntity) -> Unit
) {
  var selectedSubTab by remember { mutableStateOf(0) }
  var historySearchQuery by remember { mutableStateOf("") }

  Column(modifier = Modifier.fillMaxSize()) {
    TabRow(
      selectedTabIndex = selectedSubTab,
      containerColor = MaterialTheme.colorScheme.surface
    ) {
      Tab(
        selected = selectedSubTab == 0,
        onClick = { selectedSubTab = 0 },
        text = { Text("تعديل الأسعار", fontWeight = FontWeight.Bold) }
      )
      Tab(
        selected = selectedSubTab == 1,
        onClick = { selectedSubTab = 1 },
        text = { Text("سجل التغييرات (${priceHistory.size})", fontWeight = FontWeight.Bold) }
      )
    }

    if (selectedSubTab == 0) {
      // Direct price editor
      LazyColumn(
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(products, key = { it.id }) { product ->
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = product.name,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Text(
                  text = "${product.code} • ${product.category}",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                  Text(
                    text = "جملة: ${formatCurrency(product.wholesalePrice)}  |  تجزئة: ${formatCurrency(product.retailPrice)}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EmeraldPrimary
                  )
                }
              }

              Button(
                onClick = { onEditPrice(product) },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text("تعديل", color = EmeraldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
              }
            }
          }
        }
      }
    } else {
      // Price Change History Log
      val filteredHistory = remember(priceHistory, historySearchQuery) {
        if (historySearchQuery.isBlank()) priceHistory
        else priceHistory.filter {
          it.productName.contains(historySearchQuery, ignoreCase = true) ||
            it.productCode.contains(historySearchQuery, ignoreCase = true)
        }
      }

      Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
          value = historySearchQuery,
          onValueChange = { historySearchQuery = it },
          placeholder = { Text("بحث في سجل الأسعار...") },
          leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
        )

        if (filteredHistory.isEmpty()) {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("لا توجد تعديلات سابقة مسجلة في السجل", color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        } else {
          LazyColumn(
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
          ) {
            items(filteredHistory, key = { it.id }) { record ->
              val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
              val dateStr = sdf.format(Date(record.timestamp))

              Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(
                      text = record.productName,
                      fontWeight = FontWeight.Bold,
                      fontSize = 13.sp,
                      modifier = Modifier.weight(1f)
                    )
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = GoldSecondary.copy(alpha = 0.15f)
                    ) {
                      Text(
                        text = record.priceType,
                        color = GoldSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(6.dp))

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = formatCurrency(record.oldPrice),
                        fontSize = 12.sp,
                        color = StatusUnavailable,
                        fontWeight = FontWeight.SemiBold
                      )
                      Text(text = " ⬅ ", fontSize = 12.sp)
                      Text(
                        text = formatCurrency(record.newPrice),
                        fontSize = 13.sp,
                        color = StatusAvailable,
                        fontWeight = FontWeight.Bold
                      )
                    }
                    Text(
                      text = dateStr,
                      fontSize = 11.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------
// TAB 3: Admin Customers
// -------------------------------------------------------------
@Composable
fun AdminCustomersTab(
  customers: List<CustomerRequestEntity>,
  onUpdateStatus: (Long, String) -> Unit,
  onDeleteCustomer: (Long) -> Unit
) {
  var selectedStatusFilter by remember { mutableStateOf("ALL") }

  val filteredCustomers = remember(customers, selectedStatusFilter) {
    when (selectedStatusFilter) {
      "PENDING" -> customers.filter { it.status == "PENDING" }
      "APPROVED" -> customers.filter { it.status == "APPROVED" }
      "REJECTED" -> customers.filter { it.status == "REJECTED" }
      "BLOCKED" -> customers.filter { it.status == "BLOCKED" }
      else -> customers
    }
  }

  Column(modifier = Modifier.fillMaxSize()) {
    // Filter Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 14.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      CustomerFilterChip("الكل (${customers.size})", selectedStatusFilter == "ALL") { selectedStatusFilter = "ALL" }
      CustomerFilterChip("طلبات جديدة (${customers.count { it.status == "PENDING" }})", selectedStatusFilter == "PENDING") { selectedStatusFilter = "PENDING" }
      CustomerFilterChip("المعتمدون (${customers.count { it.status == "APPROVED" }})", selectedStatusFilter == "APPROVED") { selectedStatusFilter = "APPROVED" }
      CustomerFilterChip("المرفوضون (${customers.count { it.status == "REJECTED" }})", selectedStatusFilter == "REJECTED") { selectedStatusFilter = "REJECTED" }
      CustomerFilterChip("المحظورون (${customers.count { it.status == "BLOCKED" }})", selectedStatusFilter == "BLOCKED") { selectedStatusFilter = "BLOCKED" }
    }

    if (filteredCustomers.isEmpty()) {
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("لا يوجد عملاء في هذه القائمة حالياً", color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    } else {
      LazyColumn(
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(filteredCustomers, key = { it.id }) { customer ->
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = customer.fullName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                  )
                  Text(
                    text = customer.phoneNumber,
                    fontSize = 12.sp,
                    color = EmeraldPrimary,
                    fontWeight = FontWeight.SemiBold
                  )
                }

                val (statusLabel, statusColor) = when (customer.status) {
                  "APPROVED" -> Pair("معتمد", StatusAvailable)
                  "REJECTED" -> Pair("مرفوض", StatusUnavailable)
                  "BLOCKED" -> Pair("محظور", StatusUnavailable)
                  else -> Pair("قيد المراجعة", StatusPaused)
                }
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = statusColor.copy(alpha = 0.15f)
                ) {
                  Text(
                    text = statusLabel,
                    color = statusColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }

              if (customer.storeName.isNotBlank() || customer.city.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "المحل: ${customer.storeName}  |  المدينة: ${customer.city}",
                  fontSize = 12.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Admin Action Buttons
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                if (customer.status != "APPROVED") {
                  Button(
                    onClick = { onUpdateStatus(customer.id, "APPROVED") },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusAvailable),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                  ) {
                    Text("موافقة", fontSize = 11.sp)
                  }
                }

                if (customer.status != "REJECTED") {
                  OutlinedButton(
                    onClick = { onUpdateStatus(customer.id, "REJECTED") },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                  ) {
                    Text("رفض", fontSize = 11.sp, color = StatusUnavailable)
                  }
                }

                if (customer.status != "BLOCKED") {
                  OutlinedButton(
                    onClick = { onUpdateStatus(customer.id, "BLOCKED") },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                  ) {
                    Text("حظر", fontSize = 11.sp, color = Color.Gray)
                  }
                }

                IconButton(
                  onClick = { onDeleteCustomer(customer.id) },
                  modifier = Modifier.size(36.dp)
                ) {
                  Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", tint = Color.Gray)
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun CustomerFilterChip(title: String, isSelected: Boolean, onClick: () -> Unit) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
    modifier = Modifier.clickable(onClick = onClick)
  ) {
    Text(
      text = title,
      fontSize = 11.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
      color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
    )
  }
}

// -------------------------------------------------------------
// TAB 4: Admin Categories
// -------------------------------------------------------------
@Composable
fun AdminCategoriesTab(
  categories: List<CategoryEntity>,
  onSaveCategory: (CategoryEntity) -> Unit,
  onDeleteCategory: (Long) -> Unit
) {
  var showAddCategoryDialog by remember { mutableStateOf(false) }
  var categoryNameInput by remember { mutableStateOf("") }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(14.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "إدارة الأقسام الرئيسية (${categories.size})",
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp
      )
      Button(
        onClick = { showAddCategoryDialog = true },
        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
        shape = RoundedCornerShape(10.dp)
      ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("قسم جديد", fontSize = 12.sp)
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    LazyColumn(
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxSize()
    ) {
      items(categories, key = { it.id }) { cat ->
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Category,
                contentDescription = null,
                tint = EmeraldPrimary,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(12.dp))
              Text(
                text = cat.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            }

            IconButton(
              onClick = { onDeleteCategory(cat.id) },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", tint = StatusUnavailable)
            }
          }
        }
      }
    }
  }

  if (showAddCategoryDialog) {
    AlertDialog(
      onDismissRequest = { showAddCategoryDialog = false },
      title = { Text("إضافة قسم رئيسي جديد") },
      text = {
        OutlinedTextField(
          value = categoryNameInput,
          onValueChange = { categoryNameInput = it },
          label = { Text("اسم القسم") },
          placeholder = { Text("مثال: كتب أطفال وإصدارات ناشئة") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
      },
      confirmButton = {
        Button(
          onClick = {
            if (categoryNameInput.isNotBlank()) {
              onSaveCategory(CategoryEntity(name = categoryNameInput.trim()))
              categoryNameInput = ""
              showAddCategoryDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
        ) {
          Text("حفظ")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { showAddCategoryDialog = false }) {
          Text("إلغاء")
        }
      }
    )
  }
}

// -------------------------------------------------------------
// TAB 5: Admin Import & Export CSV
// -------------------------------------------------------------
@Composable
fun AdminImportExportTab(
  products: List<ProductEntity>,
  onImportCsv: (String) -> Unit
) {
  var csvInputText by remember { mutableStateOf("") }
  var exportedText by remember { mutableStateOf("") }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
      .verticalScroll(rememberScrollState())
  ) {
    Text(
      text = "استيراد وتصدير الأصناف والأسعار (CSV)",
      fontWeight = FontWeight.Bold,
      fontSize = 16.sp,
      color = EmeraldPrimary
    )

    Spacer(modifier = Modifier.height(14.dp))

    // Export Card
    Card(
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, tint = EmeraldPrimary)
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "تصدير التسعيرة الحالية (${products.size} صنف)", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "تصدير كامل بيانات الأصناف والأسعار بصيغة CSV جاهزة للفتح في Excel.",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
          onClick = {
            val sb = StringBuilder()
            sb.append("رمز الصنف,اسم الصنف,القسم,التصنيف الفرعي,سعر الجملة (ر.ي),سعر التجزئة (ر.ي),حالة التوفر\n")
            products.forEach { p ->
              val avail = if (p.availability == "AVAILABLE") "متوفر" else "غير متوفر"
              sb.append("${p.code},\"${p.name}\",\"${p.category}\",\"${p.subcategory}\",${p.wholesalePrice},${p.retailPrice},$avail\n")
            }
            exportedText = sb.toString()
          },
          colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("توليد ملف CSV")
        }

        if (exportedText.isNotEmpty()) {
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = exportedText,
            onValueChange = {},
            readOnly = true,
            label = { Text("بيانات CSV المصدرة") },
            maxLines = 6,
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Import Card
    Card(
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(imageVector = Icons.Default.FileUpload, contentDescription = null, tint = GoldSecondary)
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "استيراد أصناف من CSV", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "الصيغة المطلوبة:\nرمز الصنف,اسم الصنف,القسم,التصنيف الفرعي,سعر الجملة,سعر التجزئة,الحالة",
          fontSize = 11.sp,
          lineHeight = 16.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
          value = csvInputText,
          onValueChange = { csvInputText = it },
          placeholder = { Text("الصق أسطر CSV هنا...") },
          maxLines = 5,
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
          onClick = {
            if (csvInputText.isNotBlank()) {
              onImportCsv(csvInputText)
              csvInputText = ""
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = GoldSecondary),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("بدء الاستيراد للبيانات المحلية")
        }
      }
    }
  }
}

// -------------------------------------------------------------
// TAB 6: Admin Settings & Firebase Guide
// -------------------------------------------------------------
@Composable
fun AdminSettingsTab(
  viewModel: MainViewModel
) {
  val scrollState = rememberScrollState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
      .verticalScroll(scrollState)
  ) {
    Text(
      text = "إعدادات المكتبة والتهيئة السحابية",
      fontWeight = FontWeight.Bold,
      fontSize = 17.sp,
      color = EmeraldPrimary
    )

    Spacer(modifier = Modifier.height(14.dp))

    // Library Info
    Card(
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Text(text = "بيانات المكتبة الرسمية", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text(text = "اسم المكتبة:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Text(text = "مكتبة أهل البيت ع", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text(text = "العملة المعتمدة:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Text(text = "الريال اليمني (ر.ي)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text(text = "وضع العمل:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Text(text = "Offline First (تخزين محلي + مزامنة)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Firebase Integration Guide
    Card(
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Text(
          text = "دليل ربط Firebase وقواعد الأمان (Firestore Rules)",
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = GoldSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "لربط التطبيق بمشروع Firebase الحقيقي لـ Firestore و Authentication، قمنا بتجهيز ملف قواعد الأمان الحقيقية في مسار firestore.rules لحماية الأسعار من القراءة غير المصرح بها:\n\n" +
            "1. العميل غير المعتمد: ممنوع من قراءة الأسعار.\n" +
            "2. العميل العادي: ممنوع من تعديل الأصناف أو الأسعار.\n" +
            "3. المدير (Admin): صلاحية كاملة للكتابة والتعديل.",
          fontSize = 11.sp,
          lineHeight = 17.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Button(
      onClick = { viewModel.logoutAdmin() },
      colors = ButtonDefaults.buttonColors(containerColor = StatusUnavailable),
      shape = RoundedCornerShape(10.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Text("تسجيل خروج من الإدارة")
    }
  }
}

// -------------------------------------------------------------
// Sub-components & Form Dialogs
// -------------------------------------------------------------
@Composable
fun AdminStatMiniCard(
  title: String,
  value: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = color)
    }
  }
}

@Composable
fun ProductFormDialog(
  initialProduct: ProductEntity?,
  categories: List<String>,
  onDismiss: () -> Unit,
  onSave: (ProductEntity, () -> Unit, (String) -> Unit) -> Unit
) {
  var code by remember { mutableStateOf(initialProduct?.code ?: "") }
  var name by remember { mutableStateOf(initialProduct?.name ?: "") }
  var category by remember { mutableStateOf(initialProduct?.category ?: (categories.firstOrNull() ?: "كتب مكتبة أهل البيت ع")) }
  var subcategory by remember { mutableStateOf(initialProduct?.subcategory ?: "") }
  var wholesalePrice by remember { mutableStateOf(initialProduct?.wholesalePrice?.toString() ?: "") }
  var retailPrice by remember { mutableStateOf(initialProduct?.retailPrice?.toString() ?: "") }
  var notes by remember { mutableStateOf(initialProduct?.notes ?: "") }
  var availability by remember { mutableStateOf(initialProduct?.availability ?: "AVAILABLE") }
  var imageUrl by remember { mutableStateOf(initialProduct?.imageUrl ?: "") }

  var warningText by remember { mutableStateOf<String?>(null) }
  var isSaving by remember { mutableStateOf(false) }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
          .verticalScroll(rememberScrollState())
      ) {
        Text(
          text = if (initialProduct == null) "إضافة صنف جديد" else "تعديل الصنف",
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp,
          color = EmeraldPrimary
        )

        warningText?.let { warn ->
          Spacer(modifier = Modifier.height(8.dp))
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = StatusPaused.copy(alpha = 0.2f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = StatusPaused, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(text = warn, fontSize = 11.sp, color = Color(0xFF6B4E00), fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = code,
          onValueChange = { code = it; warningText = null },
          label = { Text("رمز الصنف (Item Code)") },
          placeholder = { Text("مثال: AB-105") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = name,
          onValueChange = { name = it; warningText = null },
          label = { Text("اسم الصنف الكامل") },
          placeholder = { Text("مثال: كتاب قصد السبيل...") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Category dropdown
        var catExpanded by remember { mutableStateOf(false) }
        Box(modifier = Modifier.fillMaxWidth()) {
          OutlinedTextField(
            value = category,
            onValueChange = {},
            readOnly = true,
            label = { Text("القسم الرئيسي") },
            modifier = Modifier
              .fillMaxWidth()
              .clickable { catExpanded = true }
          )
          DropdownMenu(
            expanded = catExpanded,
            onDismissRequest = { catExpanded = false }
          ) {
            categories.forEach { cat ->
              DropdownMenuItem(
                text = { Text(cat) },
                onClick = { category = cat; catExpanded = false }
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = subcategory,
          onValueChange = { subcategory = it },
          label = { Text("التصنيف الفرعي") },
          placeholder = { Text("عقائد، فقه، دفاتر...") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = wholesalePrice,
            onValueChange = { wholesalePrice = it },
            label = { Text("سعر الجملة (ر.ي)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.weight(1f)
          )

          OutlinedTextField(
            value = retailPrice,
            onValueChange = { retailPrice = it },
            label = { Text("سعر التجزئة (ر.ي)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Availability selector
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf("AVAILABLE" to "متوفر", "UNAVAILABLE" to "غير متوفر", "PAUSED" to "متوقف").forEach { (key, lbl) ->
            val sel = availability == key
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (sel) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .weight(1f)
                .clickable { availability = key }
            ) {
              Text(
                text = lbl,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                color = if (sel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = imageUrl,
          onValueChange = { imageUrl = it },
          label = { Text("رابط الصورة (URL)") },
          placeholder = { Text("https://...") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("الملاحظات والمواصفات") },
          placeholder = { Text("عدد الصفحات، التجليد، الطبعة...") },
          maxLines = 3,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Button(
            onClick = {
              if (code.isBlank() || name.isBlank()) {
                warningText = "يرجى كتابة رمز واسم الصنف"
                return@Button
              }
              val wholesale = wholesalePrice.toDoubleOrNull() ?: 0.0
              val retail = retailPrice.toDoubleOrNull() ?: 0.0

              val entity = ProductEntity(
                id = initialProduct?.id ?: 0L,
                code = code.trim(),
                name = name.trim(),
                normalizedSearch = "",
                category = category,
                subcategory = subcategory.trim(),
                wholesalePrice = wholesale,
                retailPrice = retail,
                availability = availability,
                imageUrl = imageUrl.trim(),
                notes = notes.trim()
              )

              isSaving = true
              onSave(entity, { isSaving = false }, { warn ->
                warningText = warn
                isSaving = false
              })
            },
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f)
          ) {
            Text("حفظ الصنف", fontWeight = FontWeight.Bold)
          }

          OutlinedButton(
            onClick = onDismiss,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f)
          ) {
            Text("إلغاء")
          }
        }
      }
    }
  }
}

@Composable
fun EditPriceDialog(
  product: ProductEntity,
  onDismiss: () -> Unit,
  onConfirm: (Double, Double, String) -> Unit
) {
  var wholesale by remember { mutableStateOf(product.wholesalePrice.toString()) }
  var retail by remember { mutableStateOf(product.retailPrice.toString()) }
  var note by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Column {
        Text("تعديل سعر الصنف", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Text(product.name, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
          value = wholesale,
          onValueChange = { wholesale = it },
          label = { Text("سعر الجملة الجديد (ر.ي)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = retail,
          onValueChange = { retail = it },
          label = { Text("سعر التجزئة الجديد (ر.ي)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val w = wholesale.toDoubleOrNull() ?: product.wholesalePrice
          val r = retail.toDoubleOrNull() ?: product.retailPrice
          onConfirm(w, r, note)
        },
        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
      ) {
        Text("حفظ وتسجيل في السجل")
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("إلغاء")
      }
    }
  )
}
