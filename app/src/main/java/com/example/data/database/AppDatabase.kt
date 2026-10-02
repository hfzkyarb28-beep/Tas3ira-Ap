package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    ProductEntity::class,
    CategoryEntity::class,
    PriceHistoryEntity::class,
    CustomerRequestEntity::class,
    AppSettingsEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun productDao(): ProductDao
  abstract fun categoryDao(): CategoryDao
  abstract fun priceHistoryDao(): PriceHistoryDao
  abstract fun customerDao(): CustomerDao
  abstract fun appSettingsDao(): AppSettingsDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "ahlulbayt_pricing_database"
        )
          .addCallback(AppDatabaseCallback(scope))
          .build()
        INSTANCE = instance
        instance
      }
    }
  }

  private class AppDatabaseCallback(
    private val scope: CoroutineScope
  ) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
      super.onCreate(db)
      INSTANCE?.let { database ->
        scope.launch(Dispatchers.IO) {
          populateInitialData(database)
        }
      }
    }

    private suspend fun populateInitialData(db: AppDatabase) {
      // 1. Initial Categories
      val catDao = db.categoryDao()
      val cat1 = CategoryEntity(id = 1, name = "كتب مكتبة أهل البيت ع", isMain = true, sortOrder = 1)
      val cat2 = CategoryEntity(id = 2, name = "كتب غير مكتبة أهل البيت", isMain = true, sortOrder = 2)
      val cat3 = CategoryEntity(id = 3, name = "القرطاسية", isMain = true, sortOrder = 3)
      catDao.insertCategories(listOf(cat1, cat2, cat3))

      // 2. Initial Settings
      val settingsDao = db.appSettingsDao()
      settingsDao.setSetting(AppSettingsEntity("library_name", "مكتبة أهل البيت ع"))
      settingsDao.setSetting(AppSettingsEntity("currency", "الريال اليمني"))
      settingsDao.setSetting(AppSettingsEntity("currency_symbol", "ر.ي"))
      settingsDao.setSetting(AppSettingsEntity("admin_pin", "1234"))
      settingsDao.setSetting(AppSettingsEntity("auto_sync", "true"))
      settingsDao.setSetting(AppSettingsEntity("last_sync_time", "غير متزامن"))

      // 3. Initial Sample Products (Realistic and authentic for testing)
      val prodDao = db.productDao()
      val sampleProducts = listOf(
        ProductEntity(
          code = "AB-001",
          name = "نهج البلاغة - شرح وتحقيق كامل",
          normalizedSearch = ArabicSearchHelper.normalize("نهج البلاغة - شرح وتحقيق كامل"),
          imageUrl = "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400&q=80",
          category = "كتب مكتبة أهل البيت ع",
          subcategory = "عقائد وحديث",
          wholesalePrice = 4500.0,
          retailPrice = 5500.0,
          notes = "تجليد فاخر مذهب، ورق شاموا أصفر، 850 صفحة، إصدار مكتبة أهل البيت ع",
          availability = "AVAILABLE"
        ),
        ProductEntity(
          code = "AB-002",
          name = "الصحيفة السجادية الكاملة مع الأدعية الملحقة",
          normalizedSearch = ArabicSearchHelper.normalize("الصحيفة السجادية الكاملة مع الأدعية الملحقة"),
          imageUrl = "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400&q=80",
          category = "كتب مكتبة أهل البيت ع",
          subcategory = "أدعية وزيارات",
          wholesalePrice = 2800.0,
          retailPrice = 3500.0,
          notes = "حجم جيبي وسطي، خط واضح بتشكيل كامل، غلاف جلد كرتوني",
          availability = "AVAILABLE"
        ),
        ProductEntity(
          code = "AB-003",
          name = "مفاتيح الجنان - طبعة مصححة ومعتمدة",
          normalizedSearch = ArabicSearchHelper.normalize("مفاتيح الجنان - طبعة مصححة ومعتمدة"),
          imageUrl = "https://images.unsplash.com/photo-1532012164546-f432f2e37b73?w=400&q=80",
          category = "كتب مكتبة أهل البيت ع",
          subcategory = "أدعية وزيارات",
          wholesalePrice = 5200.0,
          retailPrice = 6500.0,
          notes = "طبعة ملونة مع فواصل أشرطة مذهبة، خط عثماني بديع",
          availability = "AVAILABLE"
        ),
        ProductEntity(
          code = "AB-004",
          name = "قصد السبيل في معرفة أصول الفقه والدليل",
          normalizedSearch = ArabicSearchHelper.normalize("قصد السبيل في معرفة أصول الفقه والدليل"),
          imageUrl = "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=400&q=80",
          category = "كتب مكتبة أهل البيت ع",
          subcategory = "أصول وفقه",
          wholesalePrice = 3800.0,
          retailPrice = 4800.0,
          notes = "كتاب منهجي لطلاب العلوم الدينية، مجلد واحد",
          availability = "AVAILABLE"
        ),
        ProductEntity(
          code = "AB-005",
          name = "قصد السائل في مسائل الحلال والحرام",
          normalizedSearch = ArabicSearchHelper.normalize("قصد السائل في مسائل الحلال والحرام"),
          imageUrl = "https://images.unsplash.com/photo-1495446815901-a7297e633e8d?w=400&q=80",
          category = "كتب مكتبة أهل البيت ع",
          subcategory = "فقه ومعاملات",
          wholesalePrice = 3200.0,
          retailPrice = 4000.0,
          notes = "فتاوى وأحكام فقهية مبسطة للعامة والشباب",
          availability = "AVAILABLE"
        ),
        ProductEntity(
          code = "GEN-101",
          name = "سيرة ابن هشام - تهذيب وتحقيق معاصر",
          normalizedSearch = ArabicSearchHelper.normalize("سيرة ابن هشام - تهذيب وتحقيق معاصر"),
          imageUrl = "https://images.unsplash.com/photo-1476275466078-4007374efbbe?w=400&q=80",
          category = "كتب غير مكتبة أهل البيت",
          subcategory = "تاريخ وسيرة",
          wholesalePrice = 6000.0,
          retailPrice = 7500.0,
          notes = "مجلدان اثنان، ورق أبيض 70 جرام، دار النشر التراثية",
          availability = "AVAILABLE"
        ),
        ProductEntity(
          code = "GEN-102",
          name = "معجم لسان العرب - المختار والمفهرس",
          normalizedSearch = ArabicSearchHelper.normalize("معجم لسان العرب - المختار والمفهرس"),
          imageUrl = "https://images.unsplash.com/photo-1457369804613-52c61a468e7d?w=400&q=80",
          category = "كتب غير مكتبة أهل البيت",
          subcategory = "لغة ومعاجم",
          wholesalePrice = 8500.0,
          retailPrice = 10500.0,
          notes = "مجلد كبير مع فهارس هجائية سريعة",
          availability = "PAUSED"
        ),
        ProductEntity(
          code = "ST-201",
          name = "دفتر سلك جامعي فاخر 200 ورقة مسطر",
          normalizedSearch = ArabicSearchHelper.normalize("دفتر سلك جامعي فاخر 200 ورقة مسطر"),
          imageUrl = "https://images.unsplash.com/photo-1586075010923-2dd4570fb338?w=400&q=80",
          category = "القرطاسية",
          subcategory = "دفاتر وكشاكيل",
          wholesalePrice = 1200.0,
          retailPrice = 1600.0,
          notes = "غلاف بلاستيكي مقوى مقاوم للماء، ورق عالي البياض 80 جرام",
          availability = "AVAILABLE"
        ),
        ProductEntity(
          code = "ST-202",
          name = "طقم أقلام جاف أزرق جودة يابانية (علبة 12 قلم)",
          normalizedSearch = ArabicSearchHelper.normalize("طقم أقلام جاف أزرق جودة يابانية علبة 12 قلم"),
          imageUrl = "https://images.unsplash.com/photo-1585336261026-778dfcb7b1c3?w=400&q=80",
          category = "القرطاسية",
          subcategory = "أقلام وأدوات كتابة",
          wholesalePrice = 1800.0,
          retailPrice = 2400.0,
          notes = "سن 0.7 مم، حبر انسيابي ثابت لا يبهت",
          availability = "AVAILABLE"
        ),
        ProductEntity(
          code = "ST-203",
          name = "حافظة مصحف وكتب جلدية منقوشة",
          normalizedSearch = ArabicSearchHelper.normalize("حافظة مصحف وكتب جلدية منقوشة"),
          imageUrl = "https://images.unsplash.com/photo-1544717305-2782549b5136?w=400&q=80",
          category = "القرطاسية",
          subcategory = "إكسسوارات وهدايا",
          wholesalePrice = 3000.0,
          retailPrice = 4200.0,
          notes = "جلد صناعي فاخر مع سحاب قوي وبطانة مخملية",
          availability = "UNAVAILABLE"
        )
      )
      prodDao.insertProducts(sampleProducts)

      // 4. Initial Demo Customer Request
      val custDao = db.customerDao()
      custDao.insertCustomer(
        CustomerRequestEntity(
          phoneNumber = "+967770123456",
          fullName = "أحمد محمد الكبسي",
          storeName = "مكتبة النور الحديثة",
          city = "صنعاء",
          status = "APPROVED",
          requestDate = System.currentTimeMillis() - 86400000L,
          approvedDate = System.currentTimeMillis() - 43200000L,
          notes = "عميل جملة معتمد"
        )
      )
      custDao.insertCustomer(
        CustomerRequestEntity(
          phoneNumber = "+967771987654",
          fullName = "عبدالله يحيى المتوكل",
          storeName = "قرطاسية الإيمان",
          city = "ذمار",
          status = "PENDING",
          requestDate = System.currentTimeMillis() - 7200000L,
          notes = "طلب جديد قيد الانتظار"
        )
      )
    }
  }
}
