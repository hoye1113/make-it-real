package com.eufymake.real.model

import kotlinx.serialization.Serializable

enum class SubstrateShape {
    RECTANGLE,
    ROUNDED_RECTANGLE,
    CIRCLE,
    CYLINDER,
    PHONE_CASE
}

@Serializable
data class Substrate(
    val id: String,
    val name: String,
    val nameZh: String,
    val description: String,
    val shape: SubstrateShape,
    val widthMm: Float,
    val heightMm: Float,
    val thicknessMm: Float = 2.0f,
    val bleedMarginMm: Float = 2.0f,
    val iconName: String = "category",
    val defaultReliefDepth: Float = 1.0f,
    val supportRotary: Boolean = false,
    val defaultMaterial: String = "亚克力",
    val presets: List<String> = emptyList()
) {
    val displayName: String
        get() = if (com.eufymake.real.i18n.LanguageManager.currentLanguage == com.eufymake.real.i18n.AppLanguage.ZH) nameZh else name

    val aspectRatio: Float
        get() = if (heightMm > 0) widthMm / heightMm else 1f

    companion object {
        val ALL: List<Substrate> = listOf(
            Substrate(
                id = "scan",
                name = "Scan Substrate",
                nameZh = "扫描基材",
                description = "AI自动识别物理轮廓与摄像头标定",
                shape = SubstrateShape.ROUNDED_RECTANGLE,
                widthMm = 120f,
                heightMm = 120f,
                iconName = "document_scanner"
            ),
            Substrate(
                id = "customize",
                name = "Custom",
                nameZh = "自定义",
                description = "自定义长宽高与安全打印边距",
                shape = SubstrateShape.RECTANGLE,
                widthMm = 300f,
                heightMm = 400f,
                thicknessMm = 2.0f,
                iconName = "edit"
            ),
            Substrate(
                id = "magnet",
                name = "Fridge Magnet",
                nameZh = "冰箱贴",
                description = "磁吸亚克力/木质工艺冰箱贴",
                shape = SubstrateShape.ROUNDED_RECTANGLE,
                widthMm = 80f,
                heightMm = 80f,
                thicknessMm = 3.0f,
                iconName = "layers"
            ),
            Substrate(
                id = "canvas",
                name = "Canvas",
                nameZh = "画布",
                description = "纹理油画布与木框UV浮雕",
                shape = SubstrateShape.RECTANGLE,
                widthMm = 200f,
                heightMm = 150f,
                thicknessMm = 15.0f,
                iconName = "palette",
                defaultReliefDepth = 1.5f
            ),
            Substrate(
                id = "sticker",
                name = "Sticker",
                nameZh = "贴纸",
                description = "高光亮油防水UV转印水晶标/贴纸",
                shape = SubstrateShape.CIRCLE,
                widthMm = 65f,
                heightMm = 65f,
                thicknessMm = 0.5f,
                iconName = "stars"
            ),
            Substrate(
                id = "coaster",
                name = "Coaster",
                nameZh = "杯垫",
                description = "软木/陶瓷吸水圆形茶杯垫",
                shape = SubstrateShape.CIRCLE,
                widthMm = 100f,
                heightMm = 100f,
                thicknessMm = 5.0f,
                iconName = "circle"
            ),
            Substrate(
                id = "sheet",
                name = "Sheet",
                nameZh = "薄片",
                description = "亚克力、实木板与金属铭牌",
                shape = SubstrateShape.RECTANGLE,
                widthMm = 300f,
                heightMm = 400f,
                thicknessMm = 2.0f,
                iconName = "crop_portrait",
                defaultReliefDepth = 2.0f,
                presets = listOf("Acrylic Sheet", "Wood Sheet", "Metal Sheet")
            ),
            Substrate(
                id = "card",
                name = "Card",
                nameZh = "卡片",
                description = "PVC会员卡、异形贺卡与亚克力立牌",
                shape = SubstrateShape.ROUNDED_RECTANGLE,
                widthMm = 85.6f,
                heightMm = 53.98f,
                thicknessMm = 0.8f,
                iconName = "credit_card"
            ),
            Substrate(
                id = "cup",
                name = "Mug",
                nameZh = "杯子",
                description = "360度旋转圆柱杯身展开印花（带把手避让安全区）",
                shape = SubstrateShape.CYLINDER,
                widthMm = 240f, // perimeter
                heightMm = 120f,
                thicknessMm = 4.0f,
                iconName = "local_cafe",
                supportRotary = true
            ),
            Substrate(
                id = "tech",
                name = "Tech Accessories",
                nameZh = "科技配件",
                description = "手机保护壳、耳机保护套与手柄外壳",
                shape = SubstrateShape.PHONE_CASE,
                widthMm = 78f,
                heightMm = 160f,
                thicknessMm = 8.0f,
                iconName = "smartphone",
                presets = listOf(
                    "iPhone 16",
                    "iPhone 16 Plus",
                    "iPhone 16 Pro",
                    "iPhone 16 Pro Max",
                    "iPhone 17",
                    "iPhone 17 Pro",
                    "AirPods 4 Charging Case",
                    "AirPods Pro 3 Charging Case",
                    "Switch Joy-Con Controller"
                )
            )
        )

        val DEFAULT: Substrate = ALL[6] // Sheet (薄片) default
    }
}
