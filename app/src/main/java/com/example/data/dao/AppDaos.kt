package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.data.model.AppSettingsEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.CustomerRequestEntity
import com.example.data.model.PriceHistoryEntity
import com.example.data.model.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
  @Query("SELECT * FROM products WHERE isDeleted = 0 ORDER BY updatedAt DESC")
  fun getActiveProducts(): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products ORDER BY updatedAt DESC")
  fun getAllProducts(): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
  suspend fun getProductById(id: Long): ProductEntity?

  @Query("SELECT * FROM products WHERE code = :code AND isDeleted = 0 LIMIT 1")
  suspend fun getProductByCode(code: String): ProductEntity?

  @Query("SELECT * FROM products WHERE name = :name AND isDeleted = 0 LIMIT 1")
  suspend fun getProductByName(name: String): ProductEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProduct(product: ProductEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProducts(products: List<ProductEntity>)

  @Update
  suspend fun updateProduct(product: ProductEntity)

  @Query("UPDATE products SET isDeleted = 1, updatedAt = :timestamp WHERE id = :id")
  suspend fun softDeleteProduct(id: Long, timestamp: Long = System.currentTimeMillis())

  @Query("DELETE FROM products WHERE id = :id")
  suspend fun hardDeleteProduct(id: Long)

  @Query("SELECT COUNT(*) FROM products WHERE isDeleted = 0")
  fun countActiveProducts(): Flow<Int>
}

@Dao
interface CategoryDao {
  @Query("SELECT * FROM categories ORDER BY sortOrder ASC, id ASC")
  fun getAllCategories(): Flow<List<CategoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategory(category: CategoryEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategories(categories: List<CategoryEntity>)

  @Update
  suspend fun updateCategory(category: CategoryEntity)

  @Query("DELETE FROM categories WHERE id = :id")
  suspend fun deleteCategory(id: Long)
}

@Dao
interface PriceHistoryDao {
  @Query("SELECT * FROM price_history ORDER BY timestamp DESC")
  fun getAllHistory(): Flow<List<PriceHistoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertHistory(record: PriceHistoryEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertHistories(records: List<PriceHistoryEntity>)
}

@Dao
interface CustomerDao {
  @Query("SELECT * FROM customer_requests ORDER BY requestDate DESC")
  fun getAllCustomers(): Flow<List<CustomerRequestEntity>>

  @Query("SELECT * FROM customer_requests WHERE phoneNumber = :phoneNumber LIMIT 1")
  suspend fun getCustomerByPhone(phoneNumber: String): CustomerRequestEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCustomer(customer: CustomerRequestEntity): Long

  @Update
  suspend fun updateCustomer(customer: CustomerRequestEntity)

  @Query("UPDATE customer_requests SET status = :status, approvedDate = :approvedDate WHERE id = :id")
  suspend fun updateStatus(id: Long, status: String, approvedDate: Long?)

  @Query("DELETE FROM customer_requests WHERE id = :id")
  suspend fun deleteCustomer(id: Long)
}

@Dao
interface AppSettingsDao {
  @Query("SELECT * FROM app_settings WHERE `key` = :key LIMIT 1")
  suspend fun getSetting(key: String): AppSettingsEntity?

  @Query("SELECT value FROM app_settings WHERE `key` = :key LIMIT 1")
  fun getSettingFlow(key: String): Flow<String?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun setSetting(setting: AppSettingsEntity)

  @Query("SELECT * FROM app_settings")
  fun getAllSettings(): Flow<List<AppSettingsEntity>>
}
