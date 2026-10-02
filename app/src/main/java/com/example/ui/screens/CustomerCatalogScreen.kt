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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProductEntity
import com.example.data.repository.ProductSortOption
import com.example.ui.components.ImageZoomDialog
import com.example.ui.components.ProductDetailDialog
import com.example.ui.components.ProductGridCard
import com.example.ui.components.ProductListItem
import com.example.ui.components.SyncStatusHeader
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerCatalogScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val products by viewModel.filteredProducts.collectAsState()
  val allActiveProducts by viewModel.activeProducts.collectAsState()
  val isOnline by viewModel.syncManager.isOnline.collectAsState()
  val lastSyncTime by viewModel.syncManager.lastSyncTime.collectAsState()
  val isSyncing by viewModel.syncManager.isSyncing.collectAsState()

  val searchQuery by viewModel.searchQuery.collectAsState()
  val selectedCategory by viewModel.selectedCategory.collectAsState()
  val selectedSubcategory by viewModel.selectedSubcategory.collectAsState()
  val sortOption by viewModel.sortOption.collectAsState()
  val isGridView by viewModel.isGridView.collectAsState()
  val recentSearches by viewModel.recentSearches.collectAsState()

  val selectedDetailProduct by viewModel.selectedProductForDetail.collectAsState()
  val selectedZoomProduct by viewModel.selectedProductForImageZoom.collectAsState()
  val customer by viewModel.activeCustomer.collectAsState()

  var showSortMenu by remember { mutableStateOf(false) }

  // Extract distinct subcategories for current category
  val availableSubcategories = remember(allActiveProducts, selectedCategory) {
    val items = if (selectedCategory == "الكل") allActiveProducts else allActiveProducts.filter { it.category == selectedCategory }
    items.map { it.subcategory }.filter { it.isNotBlank() }.distinct()
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "تسعيرة مكتبة أهل البيت ع",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 17.sp,
              color = EmeraldPrimary
            )
            customer?.let { c ->
              Text(
                text = "${c.fullName} • ${if (c.storeName.isNotBlank()) c.storeName else c.city}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        },
        actions = {
          // Grid / List toggle
          IconButton(onClick = { viewModel.toggleGridView() }) {
            Icon(
              imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
              contentDescription = "تبديل طريقة العرض",
              tint = EmeraldPrimary
            )
          }

          // Sort Menu
          Box {
            IconButton(onClick = { showSortMenu = true }) {
              Icon(imageVector = Icons.Default.Sort, contentDescription = "ترتيب", tint = EmeraldPrimary)
            }
            DropdownMenu(
              expanded = showSortMenu,
              onDismissRequest = { showSortMenu = false }
            ) {
              DropdownMenuItem(
                text = { Text("الترتيب الافتراضي") },
                onClick = { viewModel.setSortOption(ProductSortOption.DEFAULT); showSortMenu = false }
              )
              DropdownMenuItem(
                text = { Text("الاسم: أ إلى ي") },
                onClick = { viewModel.setSortOption(ProductSortOption.NAME_ASC); showSortMenu = false }
              )
              DropdownMenuItem(
                text = { Text("الاسم: ي إلى أ") },
                onClick = { viewModel.setSortOption(ProductSortOption.NAME_DESC); showSortMenu = false }
              )
              DropdownMenuItem(
                text = { Text("سعر الجملة: من الأقل للأعلى") },
                onClick = { viewModel.setSortOption(ProductSortOption.WHOLESALE_ASC); showSortMenu = false }
              )
              DropdownMenuItem(
                text = { Text("سعر الجملة: من الأعلى للأقل") },
                onClick = { viewModel.setSortOption(ProductSortOption.WHOLESALE_DESC); showSortMenu = false }
              )
              DropdownMenuItem(
                text = { Text("سعر التجزئة: من الأقل للأعلى") },
                onClick = { viewModel.setSortOption(ProductSortOption.RETAIL_ASC); showSortMenu = false }
              )
              DropdownMenuItem(
                text = { Text("سعر التجزئة: من الأعلى للأقل") },
                onClick = { viewModel.setSortOption(ProductSortOption.RETAIL_DESC); showSortMenu = false }
              )
              DropdownMenuItem(
                text = { Text("الأحدث تحديثاً") },
                onClick = { viewModel.setSortOption(ProductSortOption.RECENT); showSortMenu = false }
              )
            }
          }

          // Logout
          IconButton(onClick = { viewModel.logoutCustomer() }) {
            Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "تسجيل خروج", tint = Color.Gray)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    }
  ) { innerPadding ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(MaterialTheme.colorScheme.background)
    ) {
      // 1. Sync & Network Status Header
      SyncStatusHeader(
        isOnline = isOnline,
        lastSyncTime = lastSyncTime,
        isSyncing = isSyncing,
        onRefreshClick = { viewModel.triggerSync() },
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
      )

      // 2. Realtime Arabic Search Bar
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { viewModel.onSearchQueryChanged(it) },
        placeholder = { Text("ابحث باسم الكتاب، الرمز، التصنيف (مثال: قصد السبيل)...") },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = EmeraldPrimary
          )
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
              Icon(imageVector = Icons.Default.Clear, contentDescription = "مسح البحث")
            }
          }
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = MaterialTheme.colorScheme.surface,
          unfocusedContainerColor = MaterialTheme.colorScheme.surface,
          focusedBorderColor = EmeraldPrimary,
          unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 6.dp)
      )

      // Recent searches chips (if search is empty or focused)
      if (searchQuery.isEmpty() && recentSearches.isNotEmpty()) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 2.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "عمليات بحث سابقة:",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.width(6.dp))
          recentSearches.forEach { term ->
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .padding(end = 6.dp)
                .clickable { viewModel.onSearchQueryChanged(term) }
            ) {
              Text(
                text = term,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }
        }
      }

      // 3. The 4 Big Main Categories Cards
      MainCategoriesSection(
        selectedCategory = selectedCategory,
        onCategorySelect = { viewModel.selectCategory(it) }
      )

      // 4. Subcategory Pills (if available for current section)
      if (availableSubcategories.isNotEmpty()) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          SubcategoryChip(
            title = "كل التصنيفات",
            isSelected = selectedSubcategory == null,
            onClick = { viewModel.selectSubcategory(null) }
          )
          availableSubcategories.forEach { sub ->
            SubcategoryChip(
              title = sub,
              isSelected = selectedSubcategory == sub,
              onClick = { viewModel.selectSubcategory(sub) }
            )
          }
        }
      }

      // 5. Products Count & Active Filter Summary
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "الأصناف المعروضة: ${products.size}",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (searchQuery.isNotBlank() || selectedCategory != "الكل" || selectedSubcategory != null) {
          Text(
            text = "إعادة ضبط التصفية",
            fontSize = 11.sp,
            color = EmeraldPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable {
              viewModel.onSearchQueryChanged("")
              viewModel.selectCategory("الكل")
              viewModel.selectSubcategory(null)
            }
          )
        }
      }

      // 6. Products Grid or List
      if (products.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = Icons.Default.SearchOff,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
              modifier = Modifier.size(54.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "لا توجد نتائج مطابقة لبحثك",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "جرّب البحث بكلمة أخرى أو تغيير القسم المحدد",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center
            )
          }
        }
      } else if (isGridView) {
        LazyVerticalGrid(
          columns = GridCells.Adaptive(minSize = 160.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.fillMaxSize()
        ) {
          items(products, key = { it.id }) { product ->
            ProductGridCard(
              product = product,
              onCardClick = { viewModel.openProductDetail(product) },
              onImageClick = { viewModel.openImageZoom(product) }
            )
          }
        }
      } else {
        LazyColumn(
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxSize()
        ) {
          items(products, key = { it.id }) { product ->
            ProductListItem(
              product = product,
              onCardClick = { viewModel.openProductDetail(product) },
              onImageClick = { viewModel.openImageZoom(product) }
            )
          }
        }
      }
    }
  }

  // Dialogs
  selectedDetailProduct?.let { product ->
    ProductDetailDialog(
      product = product,
      onDismiss = { viewModel.closeProductDetail() },
      onImageClick = { viewModel.openImageZoom(product) }
    )
  }

  selectedZoomProduct?.let { product ->
    ImageZoomDialog(
      product = product,
      onDismiss = { viewModel.closeImageZoom() }
    )
  }
}

@Composable
fun MainCategoriesSection(
  selectedCategory: String,
  onCategorySelect: (String) -> Unit
) {
  val categories = listOf(
    Triple("كتب مكتبة أهل البيت ع", Icons.Default.Book, "إصدارات المكتبة"),
    Triple("كتب غير مكتبة أهل البيت", Icons.Default.LibraryBooks, "تراث وتاريخ وعام"),
    Triple("القرطاسية", Icons.Default.Create, "أقلام ودفاتر ولوازم"),
    Triple("الكل", Icons.Default.Category, "جميع الأصناف")
  )

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .horizontalScroll(rememberScrollState())
      .padding(horizontal = 12.dp, vertical = 6.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    categories.forEach { (catName, icon, subtitle) ->
      val isSelected = selectedCategory == catName
      Card(
        modifier = Modifier
          .width(155.dp)
          .clickable { onCategorySelect(catName) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
      ) {
        Column(
          modifier = Modifier.padding(10.dp),
          verticalArrangement = Arrangement.Center
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(
              imageVector = icon,
              contentDescription = null,
              tint = if (isSelected) GoldSecondary else EmeraldPrimary,
              modifier = Modifier.size(20.dp)
            )
            if (isSelected) {
              Surface(
                shape = CircleShape,
                color = GoldSecondary,
                modifier = Modifier.size(6.dp)
              ) {}
            }
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = catName,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 16.sp
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = subtitle,
            fontSize = 10.sp,
            color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }
    }
  }
}

@Composable
fun SubcategoryChip(
  title: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(20.dp),
    color = if (isSelected) GoldSecondary else MaterialTheme.colorScheme.surfaceVariant,
    modifier = Modifier
      .padding(end = 6.dp)
      .clickable(onClick = onClick)
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
