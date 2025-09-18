fun main() {
    // 模拟CSV数据
    val csvData = """
"id","title","summary","content"
"1","第01靈籤:姜公封相","求籤吉凶：上上靈籤","算命籤詩：
靈籤求得第一枝 龍虎風雲際會時
一旦凌霄揚自樂 任君來往赴瑤池"
"2","第02靈籤：王道真誤入桃源","求籤吉凶：上吉靈籤","算命籤詩：
枯木逢春盡發新 花看葉茂蝶來頻"
    """.trimIndent()
    
    println("原始CSV数据:")
    println(csvData)
    println("\n" + "=".repeat(50) + "\n")
    
    // 测试CSV解析
    val lines = csvData.lines()
    val header = lines.first().removePrefix("\uFEFF").trim()
    println("表头: $header")
    
    val expected = listOf("id","title","summary","content")
    val headerCells = splitCsvLine(header)
    println("解析后的表头: $headerCells")
    println("表头匹配: ${headerCells.map { it.lowercase() } == expected}")
    
    println("\n" + "=".repeat(50) + "\n")
    
    // 测试第一行数据
    val firstDataLine = lines.drop(1).first()
    println("第一行数据: $firstDataLine")
    
    val cols = splitCsvLine(firstDataLine)
    println("解析后的列数: ${cols.size}")
    println("各列内容:")
    cols.forEachIndexed { i, col ->
        println("  列$i: '${col.take(50)}${if (col.length > 50) "..." else ""}'")
    }
}

fun splitCsvLine(line: String): List<String> {
    val result = mutableListOf<String>()
    val sb = StringBuilder()
    var inQuotes = false
    var i = 0
    while (i < line.length) {
        val ch = line[i]
        when (ch) {
            '"' -> {
                if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
                    // 轉義雙引號
                    sb.append('"')
                    i++
                } else {
                    inQuotes = !inQuotes
                }
            }
            ',' -> {
                if (inQuotes) {
                    sb.append(ch)
                } else {
                    result += sb.toString().trim()
                    sb.clear()
                }
            }
            else -> sb.append(ch)
        }
        i++
    }
    // 添加最后一个字段
    result += sb.toString().trim()
    return result
}
