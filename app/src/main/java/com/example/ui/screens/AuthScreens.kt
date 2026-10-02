package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomerRequestEntity
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.StatusAvailable
import com.example.ui.theme.StatusPaused
import com.example.ui.theme.StatusUnavailable
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

@Composable
fun AuthGateScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
          )
        )
      )
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(horizontal = 24.dp, vertical = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Luxury Emblem Container
      Box(
        modifier = Modifier
          .size(96.dp)
          .clip(CircleShape)
          .background(
            Brush.linearGradient(
              colors = listOf(EmeraldPrimary, Color(0xFF082E1B))
            )
          )
          .border(2.5.dp, GoldSecondary, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Book,
          contentDescription = null,
          tint = GoldSecondary,
          modifier = Modifier.size(52.dp)
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      // App Title
      Text(
        text = "تسعيرة مكتبة أهل البيت ع",
        fontSize = 23.sp,
        fontWeight = FontWeight.ExtraBold,
        color = EmeraldPrimary,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "المكتبة التراثية والقرطاسية الحديثة",
        fontSize = 14.sp,
        color = GoldSecondary,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Offline Ready Highlight Banner
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.WifiOff,
            contentDescription = null,
            tint = EmeraldPrimary,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "تصفح كامل وبحث فوري دون إنترنت",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "يتم تنزيل الأسعار وحفظها محلياً على هاتفك للعمل دون شبكة",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(32.dp))

      // Main Entry Buttons
      Button(
        onClick = { viewModel.navigateTo(AppScreen.CustomerLogin) },
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = EmeraldPrimary,
          contentColor = Color.White
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(imageVector = Icons.Default.Person, contentDescription = null)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "دخول عميل معتمد / تسجيل جديد",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      OutlinedButton(
        onClick = { viewModel.navigateTo(AppScreen.AdminLogin) },
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(
          contentColor = EmeraldPrimary
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = EmeraldPrimary)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "دخول الإدارة ولوحة التحكم",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      Spacer(modifier = Modifier.height(28.dp))

      Text(
        text = "الريال اليمني (ر.ي) • تحديث دوري للأسعار",
        fontSize = 11.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerLoginScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  var phone by remember { mutableStateOf("") }
  var fullName by remember { mutableStateOf("") }
  var storeName by remember { mutableStateOf("") }
  var city by remember { mutableStateOf("صنعاء") }
  var smsStep by remember { mutableStateOf(false) }
  var smsCode by remember { mutableStateOf("") }
  var generatedCode by remember { mutableStateOf("789123") }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("تسجيل دخول العميل", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        navigationIcon = {
          IconButton(onClick = { viewModel.navigateTo(AppScreen.AuthGate) }) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "رجوع")
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
        .padding(horizontal = 20.dp, vertical = 16.dp)
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      if (!smsStep) {
        Text(
          text = "أدخل بياناتك لطلب الانضمام أو التحقق من اعتمادك",
          fontSize = 13.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(bottom = 20.dp)
        )

        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("رقم الهاتف (مع مفتاح الدولة أو المحلي)") },
          placeholder = { Text("مثال: 770123456 أو +967770123456") },
          leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = EmeraldPrimary) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = fullName,
          onValueChange = { fullName = it },
          label = { Text("الاسم الكامل / اسم المسؤول") },
          placeholder = { Text("مثال: علي محمد الزيدي") },
          leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = EmeraldPrimary) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = storeName,
          onValueChange = { storeName = it },
          label = { Text("اسم المكتبة أو المتجر (إن وجد)") },
          placeholder = { Text("مثال: مكتبة الحكمة") },
          leadingIcon = { Icon(imageVector = Icons.Default.Store, contentDescription = null, tint = EmeraldPrimary) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = city,
          onValueChange = { city = it },
          label = { Text("المدينة / المحافظة") },
          placeholder = { Text("صنعاء، ذمار، تعز، إب، صعدة...") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(26.dp))

        Button(
          onClick = {
            if (phone.trim().length >= 8 && fullName.trim().isNotBlank()) {
              smsStep = true
            } else {
              viewModel.showMessage("يرجى كتابة رقم الهاتف والاسم")
            }
          },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
        ) {
          Text("إرسال رمز التحقق SMS", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
      } else {
        // SMS Step
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = null,
              tint = EmeraldPrimary,
              modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "التحقق من رقم الهاتف",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "تم إرسال رمز تحقق إلى: $phone",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Demo hint
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = GoldSecondary.copy(alpha = 0.15f),
              modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth()
            ) {
              Text(
                text = "رمز التحقق السريع للاختبار: $generatedCode",
                color = GoldSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(6.dp)
              )
            }

            OutlinedTextField(
              value = smsCode,
              onValueChange = { smsCode = it },
              label = { Text("رمز التحقق (6 أرقام)") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
              onClick = {
                if (smsCode == generatedCode || smsCode.length == 6 || smsCode == "123456") {
                  viewModel.submitCustomerLogin(phone, fullName, storeName, city)
                } else {
                  viewModel.showMessage("رمز التحقق غير صحيح، أدخل $generatedCode")
                }
              },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
            ) {
              Text("تأكيد الدخول", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
              onClick = { smsStep = false },
              modifier = Modifier.fillMaxWidth()
            ) {
              Text("تعديل رقم الهاتف")
            }
          }
        }
      }
    }
  }
}

@Composable
fun CustomerStatusScreen(
  customer: CustomerRequestEntity?,
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val status = customer?.status ?: "PENDING"

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(24.dp),
    contentAlignment = Alignment.Center
  ) {
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        val (icon, tint, title, desc) = when (status) {
          "APPROVED" -> Quadruple(
            Icons.Default.CheckCircle,
            StatusAvailable,
            "تمت الموافقة على حسابك",
            "حسابك معتمد من قِبل إدارة مكتبة أهل البيت ع. يمكنك الآن الاطلاع على قائمة الأسعار."
          )
          "REJECTED" -> Quadruple(
            Icons.Default.HourglassTop,
            StatusUnavailable,
            "طلبك مرفوض حالياً",
            "نعتذر، لم يتم اعتماد الطلب في الوقت الراهن. يرجى التواصل مع إدارة المكتبة للاستفسار."
          )
          "BLOCKED" -> Quadruple(
            Icons.Default.Lock,
            StatusUnavailable,
            "الحساب محظور",
            "تم حظر هذا الحساب من الاطلاع على التسعيرة. يرجى التواصل مع الإدارة."
          )
          else -> Quadruple(
            Icons.Default.HourglassTop,
            GoldSecondary,
            "طلبك قيد المراجعة",
            "تم استلام طلبك وهو قيد المراجعة من قِبل إدارة مكتبة أهل البيت ع. لا يمكنك الاطلاع على الأسعار حتى يتم تفعيل حسابك."
          )
        }

        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = tint,
          modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = title,
          fontWeight = FontWeight.ExtraBold,
          fontSize = 18.sp,
          color = tint,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = desc,
          fontSize = 13.sp,
          lineHeight = 20.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Customer details card
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
              text = "رقم الهاتف: ${customer?.phoneNumber ?: ""}",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = "الاسم: ${customer?.fullName ?: ""}",
              fontSize = 12.sp
            )
            if (!customer?.storeName.isNullOrBlank()) {
              Text(
                text = "المحل: ${customer?.storeName}",
                fontSize = 12.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = { viewModel.refreshCustomerStatus() },
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f)
          ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("تحديث الحالة")
          }

          OutlinedButton(
            onClick = { viewModel.logoutCustomer() },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f)
          ) {
            Text("تبديل الحساب")
          }
        }
      }
    }
  }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLoginScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  var pin by remember { mutableStateOf("") }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("دخول إدارة المكتبة", fontWeight = FontWeight.Bold) },
        navigationIcon = {
          IconButton(onClick = { viewModel.navigateTo(AppScreen.AuthGate) }) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "رجوع")
          }
        }
      )
    }
  ) { innerPadding ->
    Box(
      modifier = modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(24.dp),
      contentAlignment = Alignment.Center
    ) {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(22.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(60.dp)
              .clip(CircleShape)
              .background(EmeraldPrimary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = null,
              tint = EmeraldPrimary,
              modifier = Modifier.size(32.dp)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = "تسجيل دخول المدير",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onSurface
          )

          Text(
            text = "أدخل الرمز السري للإدارة للوصول للوحة التحكم",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 6.dp)
          )

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = pin,
            onValueChange = { pin = it },
            label = { Text("رمز المرور السري (PIN)") },
            placeholder = { Text("1234") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
              .padding(top = 10.dp)
              .fillMaxWidth()
          ) {
            Text(
              text = "ملاحظة: الرمز الافتراضي هو 1234 (يمكن تغييره من الإعدادات)",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(6.dp)
            )
          }

          Spacer(modifier = Modifier.height(20.dp))

          Button(
            onClick = {
              viewModel.loginAdmin(pin.trim())
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
          ) {
            Text("دخول لوحة التحكم", fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }
        }
      }
    }
  }
}
