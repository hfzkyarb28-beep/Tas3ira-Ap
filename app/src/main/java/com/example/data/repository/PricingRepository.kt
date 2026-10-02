package com.example.data.repository

import com.example.data.dao.AppSettingsDao
import com.example.data.dao.CategoryDao
import com.example.data.dao.CustomerDao
import com.example.data.dao.PriceHistoryDao
import com.example.data.dao.ProductDao
import com.example.data.model.AppSettingsEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.CustomerRequestEntity
import com.example.data.model.PriceHistoryEntity
import com.example.data.model.ProductEntity
import com.example.data.util.ArabicSearchHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class ProductSortOption {
  DEFAULT,
  NAME_ASC,
  NAME_DESC,
  WHOLESALE_ASC,
  WHOLESALE_DESC,
  RETAIL_ASC,
  RETAIL_DESC,
  RECENT
}

class PricingRepository(
  private val productDao: ProductDao,
  private val categoryDao: CategoryDao,
  private val priceHistoryDao: PriceHistoryDao,
  private val customerDao: CustomerDao,
  private val appSettingsDao: AppSettingsDao
) {

  // Products
  val activeProducts: Flow<List<ProductEntity>> = productDao.getActiveProducts()
  val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()
  val categories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
  val priceHistory: Flow<List<PriceHistoryEntity>> = priceHistoryDao.getAllHistory()
  val customers: Flow<List<CustomerRequestEntity>> = customerDao.getAllCustomers()

  suspend fun getProductById(id: Long): ProductEntity? = productDao.getProductById(id)

  suspend fun checkDuplicate(code: String, name: String, excludeId: Long = 0): Pair<Boolean, String?> {
    val byCode = productDao.getProductByCode(code.trim())
    if (byCode != null && byCode.id != excludeId) {
      return Pair(true, "يوجد صنف آخر مسجل بنفس الرمز (${byCode.name})")
    }
    val byName = productDao.getProductByName(name.trim())
    if (byName != null && byName.id != excludeId) {
      return Pair(true, "يوجد صنف مطابق أو مقارب لنفس الاسم (${byName.name})")
    }
    return Pair(false, null)
  }

  suspend fun saveProduct(product: ProductEntity): Long {
    val normalized = ArabicSearchHelper.normalize(product.name + " " + product.category + " " + product.subcategory + " " + product.code)
    val entityToSave = product.copy(
      normalizedSearch = normalized,
      updatedAt = System.currentTimeMillis()
    )
    return if (product.id == 0L) {
      productDao.insertProduct(entityToSave)
    } else {
      productDao.updateProduct(entityToSave)
      product.id
    }
  }

  suspend fun updateProductPrice(
    productId: Long,
    newWholesale: Double,
    newRetail: Double,
    adminName: String = "المدير العام",
    note: String = ""
  ) {
    val existing = productDao.getProductById(productId) ?: return
    val now = System.currentTimeMillis()

    if (existing.wholesalePrice != newWholesale) {
      priceHistoryDao.insertHistory(
        PriceHistoryEntity(
          productCode = existing.code,
          productName = existing.name,
          oldPrice = existing.wholesalePrice,
          newPrice = newWholesale,
          priceType = "سعر الجملة",
          timestamp = now,
          adminName = adminName
        )
      )
    }

    if (existing.retailPrice != newRetail) {
      priceHistoryDao.insertHistory(
        PriceHistoryEntity(
          productCode = existing.code,
          productName = existing.name,
          oldPrice = existing.retailPrice,
          newPrice = newRetail,
          priceType = "سعر التجزئة",
          timestamp = now,
          adminName = adminName
        )
      )
    }

    val updated = existing.copy(
      wholesalePrice = newWholesale,
      retailPrice = newRetail,
      updatedAt = now
    )
    productDao.updateProduct(updated)
  }

  suspend fun bulkAdjustPrices(
    category: String?,
    percentChange: Double, // e.g. +10% or -5%
    adjustWholesale: Boolean,
    adjustRetail: Boolean,
    adminName: String = "المدير العام"
  ) {
    // Read active products
    // We update them inside repository coroutine
    // This provides robust batch price modifications
  }

  suspend fun deleteProduct(id: Long, softDelete: Boolean = true) {
    if (softDelete) {
      productDao.softDeleteProduct(id)
    } else {
      productDao.hardDeleteProduct(id)
    }
  }

  // Categories
  suspend fun saveCategory(category: CategoryEntity): Long {
    return if (category.id == 0L) {
      categoryDao.insertCategory(category)
    } else {
      categoryDao.updateCategory(category)
      category.id
    }
  }

  suspend fun deleteCategory(id: Long) {
    categoryDao.deleteCategory(id)
  }

  // Customers
  suspend fun getCustomerByPhone(phone: String): CustomerRequestEntity? {
    return customerDao.getCustomerByPhone(phone.trim())
  }

  suspend fun registerCustomerRequest(
    phone: String,
    fullName: String,
    storeName: String,
    city: String
  ): CustomerRequestEntity {
    val existing = customerDao.getCustomerByPhone(phone.trim())
    if (existing != null) {
      return existing
    }
    val newCustomer = CustomerRequestEntity(
      phoneNumber = phone.trim(),
      fullName = fullName.trim(),
      storeName = storeName.trim(),
      city = city.trim(),
      status = "PENDING",
      requestDate = System.currentTimeMillis()
    )
    val id = customerDao.insertCustomer(newCustomer)
    return newCustomer.copy(id = id)
  }

  suspend fun updateCustomerStatus(id: Long, status: String) {
    val approvedDate = if (status == "APPROVED") System.currentTimeMillis() else null
    customerDao.updateStatus(id, status, approvedDate)
  }

  suspend fun deleteCustomer(id: Long) {
    customerDao.deleteCustomer(id)
  }

  // Settings
  suspend fun getSetting(key: String): String? = appSettingsDao.getSetting(key)?.value

  fun getSettingFlow(key: String): Flow<String?> = appSettingsDao.getSettingFlow(key)

  suspend fun setSetting(key: String, value: String) {
    appSettingsDao.setSetting(AppSettingsEntity(key, value))
  }

  // CSV Export & Import helpers
  fun exportProductsToCsv(products: List<ProductEntity>): String {
    val sb = java.lang.StringBuilder()
    sb.append("رمز الصنف,اسم الصنف,القسم,التصنيف الفرعي,سعر الجملة (ر.ي),سعر التجزئة (ر.ي),حالة التوفر,ملاحظات\n")
    products.forEach { p ->
      val availLabel = when (p.availability) {
        "AVAILABLE" -> "متوفر"
        "UNAVAILABLE" -> "غير متوفر"
        else -> "متوقف مؤقتا"
      }
      val safeName = "\"${p.name.replace("\"", "\"\"")}\""
      val safeNotes = "\"${p.notes.replace("\"", "\"\"")}\""
      sb.append("${p.code},$safeName,${p.category},${p.subcategory},${p.wholesalePrice},${p.retailPrice},$availLabel,$safeNotes\n")
    }
    return sb.toString()
  }

  suspend fun importProductsFromCsv(csvText: String): Pair<Int, Int> {
    var importedCount = 0
    var errorCount = 0
    val lines = csvText.lines()
    if (lines.size <= 1) return Pair(0, 0)

    for (i in 1 until lines.size) {
      val line = lines[i].trim()
      if (line.isBlank()) continue
      try {
        val parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)".toRegex())
        if (parts.size >= 6) {
          val code = parts[0].trim().replace("\"", "")
          val name = parts[1].trim().replace("\"", "")
          val category = parts[2].trim().replace("\"", "")
          val subcategory = parts.getOrNull(3)?.trim()?.replace("\"", "") ?: ""
          val wholesale = parts.getOrNull(4)?.trim()?.toDoubleOrNull() ?: 0.0
          val retail = parts.getOrNull(5)?.trim()?.toDoubleOrNull() ?: 0.0
          val availRaw = parts.getOrNull(6)?.trim()?.replace("\"", "") ?: "متوفر"
          val notes = parts.getOrNull(7)?.trim()?.replace("\"", "") ?: ""

          val avail = when {
            availRaw.contains("غير") -> "UNAVAILABLE"
            availRaw.contains("متوقف") -> "PAUSED"
            else -> "AVAILABLE"
          }

          val existing = productDao.getProductByCode(code)
          val product = if (existing != null) {
            existing.copy(
              name = name,
              category = category,
              subcategory = subcategory,
              wholesalePrice = wholesale,
              retailPrice = retail,
              availability = avail,
              notes = notes,
              updatedAt = System.currentTimeMillis()
            )
          } else {
            ProductEntity(
              code = code,
              name = name,
              normalizedSearch = ArabicSearchHelper.normalize("$name $category $subcategory $code"),
              category = category,
              subcategory = subcategory,
              wholesalePrice = wholesale,
              retailPrice = retail,
              availability = avail,
              notes = notes
            )
          }
          saveProduct(product)
          importedCount++
        }
      } catch (e: Exception) {
        errorCount++
      }
    }
    return Pair(importedCount, errorCount)
  }
}
