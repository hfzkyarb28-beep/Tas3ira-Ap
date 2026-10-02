package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "products",
  indices = [
    Index(value = ["code"], unique = true),
    Index(value = ["category"]),
    Index(value = ["normalizedSearch"])
  ]
)
data class ProductEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val code: String,
  val name: String,
  val normalizedSearch: String,
  val imageUrl: String = "",
  val category: String,
  val subcategory: String = "",
  val wholesalePrice: Double,
  val retailPrice: Double,
  val notes: String = "",
  val availability: String = "AVAILABLE", // AVAILABLE, UNAVAILABLE, PAUSED
  val updatedAt: Long = System.currentTimeMillis(),
  val isDeleted: Boolean = false
)

@Entity(tableName = "categories")
data class CategoryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val isMain: Boolean = true,
  val sortOrder: Int = 0
)

@Entity(
  tableName = "price_history",
  indices = [Index(value = ["productCode"])]
)
data class PriceHistoryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val productCode: String,
  val productName: String,
  val oldPrice: Double,
  val newPrice: Double,
  val priceType: String, // "سعر الجملة" أو "سعر التجزئة"
  val timestamp: Long = System.currentTimeMillis(),
  val adminName: String = "المدير العام"
)

@Entity(
  tableName = "customer_requests",
  indices = [Index(value = ["phoneNumber"], unique = true)]
)
data class CustomerRequestEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val phoneNumber: String,
  val fullName: String,
  val storeName: String = "",
  val city: String = "",
  val status: String = "PENDING", // PENDING, APPROVED, REJECTED, BLOCKED
  val requestDate: Long = System.currentTimeMillis(),
  val approvedDate: Long? = null,
  val notes: String = ""
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
  @PrimaryKey val key: String,
  val value: String
)
