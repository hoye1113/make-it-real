package com.eufymake.real.repository

import com.eufymake.real.model.Announcement
import com.eufymake.real.model.Project
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface CommunityRepository {
    val announcements: StateFlow<List<Announcement>>
    val projects: StateFlow<List<Project>>
    fun toggleLike(projectId: String)
    fun forkProject(projectId: String)
    fun publishProject(title: String, description: String, substrateId: String, substrateName: String): Project
}

class MockCommunityRepository : CommunityRepository {

    private val _announcements = MutableStateFlow<List<Announcement>>(
        listOf(
            Announcement(
                id = "contest_halloween",
                title = "Halloween Spooky UV Relief Challenge",
                subtitle = "Share glow-in-the-dark & multi-layer tactile designs to win \$5000 in UV consumables!",
                tag = "CONTEST 2026",
                gradientStart = 0xFFFF512F,
                gradientEnd = 0xFFDD2476
            ),
            Announcement(
                id = "firmware_v14",
                title = "Firmware v1.4.12 Released",
                subtitle = "Up to 35% faster cylinder rotary printing & enhanced micro-relief sharpness.",
                tag = "OFFICIAL UPDATE",
                gradientStart = 0xFF1D2671,
                gradientEnd = 0xFFC33764
            ),
            Announcement(
                id = "workshop_gloss",
                title = "Mastering Spot Gloss & Varnish",
                subtitle = "Official step-by-step tutorial: create luxury metallic and glass reflections.",
                tag = "TUTORIAL",
                gradientStart = 0xFF11998E,
                gradientEnd = 0xFF38EF7D
            )
        )
    )
    override val announcements: StateFlow<List<Announcement>> = _announcements.asStateFlow()

    private val _projects = MutableStateFlow<List<Project>>(
        listOf(
            Project(
                id = "proj_cyber_samurai",
                title = "Cyber Samurai 3D Tactile Relief",
                description = "2.4mm maximum relief height with selective gloss on the katana blade and visor. Printed on dark acrylic bed.",
                authorName = "NeoMaker_99",
                likesCount = 842,
                forkCount = 138,
                substrateId = "sheet",
                substrateName = "Acrylic Sheet",
                coverGradientStart = 0xFF654ea3,
                coverGradientEnd = 0xFFeaafc8,
                tags = listOf("Relief", "Cyberpunk", "Acrylic"),
                isStaffPick = true,
                category = "Art"
            ),
            Project(
                id = "proj_botanical_coaster",
                title = "Botanical Fern & Monstera Coaster Set",
                description = "4-piece ceramic drink coasters with white underbase and water-repellent UV clear varnish.",
                authorName = "FloraStudio",
                likesCount = 523,
                forkCount = 94,
                substrateId = "coaster",
                substrateName = "Drink Coaster",
                coverGradientStart = 0xFF134E5E,
                coverGradientEnd = 0xFF71B280,
                tags = listOf("Home", "Ceramic", "Gloss"),
                isStaffPick = true,
                category = "Home"
            ),
            Project(
                id = "proj_rotary_tumbler",
                title = "Aurora Borealis 360 Gradient Tumbler",
                description = "Seamless cylinder UV rotary print. Stunning deep purples and emerald greens with starlight spot relief.",
                authorName = "ArcticCrafts",
                likesCount = 1205,
                forkCount = 312,
                substrateId = "cup",
                substrateName = "Cylinder Tumbler",
                coverGradientStart = 0xFF0F2027,
                coverGradientEnd = 0xFF2C5364,
                tags = listOf("Rotary", "Tumbler", "Gradient"),
                isStaffPick = true,
                category = "Creative"
            ),
            Project(
                id = "proj_retro_keycard",
                title = "VIP Golden Circuit Access Card",
                description = "Matte black PVC card with embossed golden circuitry patterns and glossy holographic logo.",
                authorName = "TechArtisan",
                likesCount = 389,
                forkCount = 47,
                substrateId = "card",
                substrateName = "Member Card",
                coverGradientStart = 0xFF000000,
                coverGradientEnd = 0xFF434343,
                tags = listOf("Card", "Emboss", "Tech"),
                category = "Tech"
            ),
            Project(
                id = "proj_galaxy_golfball",
                title = "Cosmic Galaxy Spherical Golf Balls",
                description = "Spherical fixture multi-angle UV curing. High durability impact-resistant ink formula.",
                authorName = "OrbitalMaker",
                likesCount = 294,
                forkCount = 35,
                substrateId = "balls",
                substrateName = "Sphere & Balls",
                coverGradientStart = 0xFF8A2387,
                coverGradientEnd = 0xFFE94057,
                tags = listOf("Rotary", "Sphere", "Sports"),
                category = "Creative"
            ),
            Project(
                id = "proj_cat_magnet",
                title = "Steampunk Feline Fridge Magnet",
                description = "Flexible magnetic vinyl cutout with heavy brass gear texture and mechanical lens detail.",
                authorName = "WhiskersDesign",
                likesCount = 671,
                forkCount = 115,
                substrateId = "magnet",
                substrateName = "Fridge Magnet",
                coverGradientStart = 0xFF4B6CB7,
                coverGradientEnd = 0xFF182848,
                tags = listOf("Magnet", "Steampunk", "Cute"),
                category = "Art"
            ),
            Project(
                id = "proj_holographic_sticker",
                title = "Holographic Cyber Skull Decal",
                description = "UV printed on silver foil vinyl with gloss highlight contouring. Completely weatherproof.",
                authorName = "GhostPrints",
                likesCount = 488,
                forkCount = 82,
                substrateId = "sticker",
                substrateName = "Die-cut Sticker",
                coverGradientStart = 0xFFee0979,
                coverGradientEnd = 0xFFff6a00,
                tags = listOf("Sticker", "Decal", "Vinyl"),
                category = "Creative"
            ),
            Project(
                id = "proj_oil_landscape",
                title = "Mountain Sunrise Impasto Oil Texture",
                description = "Simulated palette knife thick oil paint strokes using 2.8mm layered UV white ink build-up.",
                authorName = "ClaudeMonetFan",
                likesCount = 1530,
                forkCount = 420,
                substrateId = "canvas",
                substrateName = "Art Canvas",
                coverGradientStart = 0xFFf12711,
                coverGradientEnd = 0xFFf5af19,
                tags = listOf("Canvas", "Impasto", "FineArt"),
                isStaffPick = true,
                category = "Art"
            )
        )
    )
    override val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    override fun toggleLike(projectId: String) {
        _projects.value = _projects.value.map { proj ->
            if (proj.id == projectId) {
                val newLiked = !proj.isLiked
                val delta = if (newLiked) 1 else -1
                proj.copy(isLiked = newLiked, likesCount = (proj.likesCount + delta).coerceAtLeast(0))
            } else {
                proj
            }
        }
    }

    override fun forkProject(projectId: String) {
        _projects.value = _projects.value.map { proj ->
            if (proj.id == projectId) {
                proj.copy(forkCount = proj.forkCount + 1)
            } else {
                proj
            }
        }
    }

    override fun publishProject(title: String, description: String, substrateId: String, substrateName: String): Project {
        val newProj = Project(
            id = "proj_${System.currentTimeMillis()}",
            title = title,
            description = description,
            authorName = "You (Maker)",
            likesCount = 1,
            isLiked = true,
            substrateId = substrateId,
            substrateName = substrateName,
            coverGradientStart = 0xFF2193b0,
            coverGradientEnd = 0xFF6dd5ed,
            tags = listOf("New", substrateName),
            createdAt = System.currentTimeMillis()
        )
        _projects.value = listOf(newProj) + _projects.value
        return newProj
    }
}
