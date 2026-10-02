package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.ProductEntity
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.StatusAvailable
import com.example.ui.theme.StatusPaused
import com.example.ui.theme.StatusUnavailable
import java.text.NumberFormat
import java.util.Locale

fun formatCurrency(amount: Double): String {
  val formatter = NumberFormat.getNumberInstance(Locale("ar", "YE"))
  return "${formatter.format(amount)} ر.ي"
}

@Composable
fun AvailabilityBadge(
  status: String,
  modifier: Modifier = Modifier
) {
  val (label, bg, fg, icon) = when (status) {
    "AVAILABLE" -> Quadruple("متوفر", StatusAvailable.copy(alpha = 0.12f), StatusAvailable, Icons.Default.CheckCircle)
    "UNAVAILABLE" -> Quadruple("غير متوفر", StatusUnavailable.copy(alpha = 0.12f), StatusUnavailable, Icons.Default.Close)
    else -> Quadruple("متوقف مؤقتاً", StatusPaused.copy(alpha = 0.15f), StatusPaused, Icons.Default.PauseCircle)
  }

  Surface(
    shape = RoundedCornerShape(8.dp),
    color = bg,
    modifier = modifier
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = fg,
        modifier = Modifier.size(13.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = label,
        color = fg,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun SyncStatusHeader(
  isOnline: Boolean,
  lastSyncTime: String,
  isSyncing: Boolean,
  onRefreshClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isOnline) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
      else MaterialTheme.colorScheme.surfaceVariant
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(if (isOnline) StatusAvailable else StatusPaused)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text(
            text = if (isOnline) "متصل بالسحابة (مزامنة فورية)" else "وضع بدون إنترنت (بيانات محلية)",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "آخر تحديث: $lastSyncTime",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      IconButton(
        onClick = onRefreshClick,
        enabled = !isSyncing,
        modifier = Modifier.size(36.dp)
      ) {
        if (isSyncing) {
          CircularProgressIndicator(
            modifier = Modifier.size(18.dp),
            strokeWidth = 2.dp,
            color = EmeraldPrimary
          )
        } else {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "تحديث التسعيرة",
            tint = EmeraldPrimary,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}

@Composable
fun ProductGridCard(
  product: ProductEntity,
  onCardClick: () -> Unit,
  onImageClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onCardClick),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
    ) {
      // Image Box
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(130.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant)
          .clickable(onClick = onImageClick),
        contentAlignment = Alignment.Center
      ) {
        if (product.imageUrl.isNotBlank()) {
          AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
              .data(product.imageUrl)
              .crossfade(true)
              .build(),
            contentDescription = product.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth()
          )
        } else {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.Book,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
              modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "مكتبة أهل البيت ع",
              fontSize = 10.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
          }
        }

        // Item code chip over image
        Surface(
          shape = RoundedCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
          color = Color(0xCC0B3820),
          modifier = Modifier.align(Alignment.TopStart)
        ) {
          Text(
            text = product.code,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Product Name
      Text(
        text = product.name,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        lineHeight = 18.sp,
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(4.dp))

      // Category & Availability
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (product.subcategory.isNotBlank()) product.subcategory else product.category,
          fontSize = 10.sp,
          color = GoldSecondary,
          fontWeight = FontWeight.SemiBold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f, fill = false)
        )
        AvailabilityBadge(status = product.availability)
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Pricing Cards
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "سعر الجملة:",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = formatCurrency(product.wholesalePrice),
              fontSize = 12.sp,
              fontWeight = FontWeight.ExtraBold,
              color = EmeraldPrimary
            )
          }
          Spacer(modifier = Modifier.height(2.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "سعر التجزئة:",
              fontSize = 10.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = formatCurrency(product.retailPrice),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = GoldSecondary
            )
          }
        }
      }
    }
  }
}

@Composable
fun ProductListItem(
  product: ProductEntity,
  onCardClick: () -> Unit,
  onImageClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onCardClick),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Thumbnail
      Box(
        modifier = Modifier
          .size(76.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant)
          .clickable(onClick = onImageClick),
        contentAlignment = Alignment.Center
      ) {
        if (product.imageUrl.isNotBlank()) {
          AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
              .data(product.imageUrl)
              .crossfade(true)
              .build(),
            contentDescription = product.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(76.dp)
          )
        } else {
          Icon(
            imageVector = Icons.Default.Book,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(28.dp)
          )
        }
      }

      Spacer(modifier = Modifier.width(10.dp))

      // Info
      Column(
        modifier = Modifier.weight(1f)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = product.code,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = EmeraldPrimary,
            modifier = Modifier
              .background(EmeraldPrimary.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
              .padding(horizontal = 4.dp, vertical = 1.dp)
          )
          AvailabilityBadge(status = product.availability)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = product.name,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          color = MaterialTheme.colorScheme.onSurface
        )

        Text(
          text = "${product.category} • ${product.subcategory}",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          horizontalArrangement = Arrangement.spacedBy(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row {
            Text(text = "جملة: ", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
              text = formatCurrency(product.wholesalePrice),
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = EmeraldPrimary
            )
          }
          Row {
            Text(text = "تجزئة: ", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
              text = formatCurrency(product.retailPrice),
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = GoldSecondary
            )
          }
        }
      }
    }
  }
}

@Composable
fun ProductDetailDialog(
  product: ProductEntity,
  onDismiss: () -> Unit,
  onImageClick: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp)
      ) {
        // Header with close button
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = EmeraldPrimary.copy(alpha = 0.15f)
            ) {
              Text(
                text = product.code,
                color = EmeraldPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            AvailabilityBadge(status = product.availability)
          }
          IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Image preview
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onImageClick),
          contentAlignment = Alignment.Center
        ) {
          if (product.imageUrl.isNotBlank()) {
            AsyncImage(
              model = ImageRequest.Builder(LocalContext.current)
                .data(product.imageUrl)
                .crossfade(true)
                .build(),
              contentDescription = product.name,
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxWidth()
            )
          } else {
            Icon(
              imageVector = Icons.Default.Book,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
              modifier = Modifier.size(54.dp)
            )
          }
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0x99000000),
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .padding(8.dp)
          ) {
            Text(
              text = "اضغط للتكبير",
              color = Color.White,
              fontSize = 10.sp,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Title
        Text(
          text = product.name,
          fontWeight = FontWeight.Bold,
          fontSize = 17.sp,
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Section & Subcategory
        Text(
          text = "${product.category}  |  ${product.subcategory}",
          color = GoldSecondary,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Price comparison box
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "سعر الجملة", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = formatCurrency(product.wholesalePrice),
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = EmeraldPrimary
              )
            }
            Box(
              modifier = Modifier
                .width(1.dp)
                .height(36.dp)
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "سعر التجزئة", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = formatCurrency(product.retailPrice),
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = GoldSecondary
              )
            }
          }
        }

        if (product.notes.isNotBlank()) {
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "الملاحظات والمواصفات:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = product.notes,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}

@Composable
fun ImageZoomDialog(
  product: ProductEntity,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = product.name,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
          )
          IconButton(onClick = onDismiss) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Black),
          contentAlignment = Alignment.Center
        ) {
          if (product.imageUrl.isNotBlank()) {
            AsyncImage(
              model = ImageRequest.Builder(LocalContext.current)
                .data(product.imageUrl)
                .crossfade(true)
                .build(),
              contentDescription = product.name,
              contentScale = ContentScale.Fit,
              modifier = Modifier.fillMaxWidth()
            )
          } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.Book,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(64.dp)
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "مكتبة أهل البيت ع",
                color = Color.White,
                fontSize = 14.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "${product.code} - سعر الجملة: ${formatCurrency(product.wholesalePrice)}",
          fontWeight = FontWeight.SemiBold,
          fontSize = 13.sp,
          color = EmeraldPrimary
        )
      }
    }
  }
}
