package com.example.data.util

object ArabicSearchHelper {

  private val TASHKEEL_REGEX = Regex("[\\u064B-\\u065F\\u0670\\u06D6-\\u06ED]")
  private val TATWEEL_REGEX = Regex("\\u0640")

  /**
   * Normalizes Arabic text for flexible and robust search matching.
   * Strips Tashkeel, normalizes alefs, teh marbuta, and formats spaces.
   */
  fun normalize(input: String?): String {
    if (input.isNullOrBlank()) return ""

    var text = input.trim().lowercase()

    // Remove tashkeel (diacritics) & tatweel
    text = TASHKEEL_REGEX.replace(text, "")
    text = TATWEEL_REGEX.replace(text, "")

    // Normalize Alef variants
    text = text.replace('أ', 'ا')
      .replace('إ', 'ا')
      .replace('آ', 'ا')
      .replace('ٱ', 'ا')

    // Normalize Teh Marbuta
    text = text.replace('ة', 'ه')

    // Normalize Alif Maqsura
    text = text.replace('ى', 'ي')

    // Normalize Hamza carriers
    text = text.replace('ؤ', 'و')
      .replace('ئ', 'ي')

    // Normalize spaces
    text = text.replace(Regex("\\s+"), " ")

    return text.trim()
  }

  /**
   * Checks if normalized Arabic target contains normalized Arabic query.
   */
  fun matches(target: String?, query: String?): Boolean {
    if (query.isNullOrBlank()) return true
    if (target.isNullOrBlank()) return false

    val normTarget = normalize(target)
    val normQuery = normalize(query)

    val queryTokens = normQuery.split(" ").filter { it.isNotBlank() }
    if (queryTokens.isEmpty()) return true

    // All typed words should appear in target for precision
    return queryTokens.all { token -> normTarget.contains(token) }
  }
}
