package com.eufymake.real.i18n

enum class AppLanguage(val code: String, val displayName: String) {
    ZH("zh", "简体中文"),
    EN("en", "English")
}

interface AppStrings {
    val nav: NavStrings
    val common: CommonStrings
    val craft: CraftStrings
    val canvas: CanvasStrings
    val device: DeviceStrings
    val profile: ProfileStrings
    val community: CommunityStrings
    val substrate: SubstrateStrings
    val dialog: DialogStrings
    val points: PointsStrings
    val ota: OtaStrings
}

interface NavStrings {
    val create: String
    val inspirations: String
    val devices: String
    val profile: String
}

interface CommonStrings {
    val cancel: String
    val confirm: String
    val save: String
    val delete: String
    val export: String
    val done: String
    val close: String
    val back: String
    val retry: String
    val search: String
    val loading: String
    val copy: String
    val copied: String
    val reset: String
    val edit: String
    val selectAll: String
    val undo: String
    val redo: String
    val viewAll: String
    fun minutesAgo(mins: Long): String
    fun hoursAgo(hours: Long): String
}

interface CraftStrings {
    val craftTitle: String
    val craftMode: String
    val layerMode: String
    val modeEngrave: String
    val modeCut: String
    val modeScore: String
    val mode3D: String
    val speed: String
    val power: String
    val passes: String
    val focus: String
    val preset: String
    val presetWood: String
    val presetAcrylic: String
    val presetLeather: String
    val startProcessing: String
    val pauseProcessing: String
    val stopProcessing: String
    val previewSimulation: String
    val layerList: String
    val selectedLayer: String
    val estimatedTime: String
    val safetyDoorWarning: String
    val exhaustActive: String
    val processingComplete: String
    val flat: String
    val flatEmboss: String
    val patternTexture: String
    val reliefTexture: String
    val inkMode: String
    val maxHeight: String
    val smoothness: String
    val inkSaving: String
    val whiteChoke: String
    val whiteChokeHelp: String
    val inkColorEmboss: String
    val inkGlossEmboss: String
    val inkWhiteAndColor: String
    val readyToPrint: String
    val calculatingRelief: String
    val substrateSpec: String
    val view2D: String
    val view3D: String
    val wireframe: String
    val aiPointsCost: String
    val doNotRemind: String
    val insufficientPointsBtn: String
    val craftFlatTitle: String
    val craftFlatSub: String
    val craftPatternTitle: String
    val craftPatternSub: String
    val craftReliefTitle: String
    val craftReliefSub: String
    val craft3DEmbossTitle: String
    val craft3DEmbossSub: String
    fun insufficientPoints(required: Int, available: Int): String
    fun layerIndex(index: Int, total: Int): String
    fun progressPercent(p: Int): String
    fun speedUnit(s: Int): String
    fun powerUnit(p: Int): String
}

interface CanvasStrings {
    val canvasStudio: String
    val newCanvas: String
    val canvasSize: String
    val layers: String
    val elements: String
    val upload: String
    val substrate: String
    val exportSvg: String
    val layerLocked: String
    val layerUnlocked: String
    val layerHidden: String
    val layerVisible: String
    val alignLeft: String
    val alignCenter: String
    val alignRight: String
    val alignTop: String
    val alignMiddle: String
    val alignBottom: String
    val group: String
    val ungroup: String
    val flipHorizontal: String
    val flipVertical: String
    val rotation: String
    val opacity: String
    val draftSaved: String
    val deleteDraftConfirm: String
    val draftBox: String
    val recentWorks: String
    val blankCanvas: String
    val cameraAligned: String
    val alignModeFlatbedCamera: String
    val alignModeFlatbedMini: String
    val alignModeRotaryJig: String
    val alignModeRollUvDtf: String
    val canvasReady: String
    val quickAddTip: String
    val uploadPhoto: String
    val addText: String
    val stickers: String
    val aiEmboss: String
    val recipePill: String
    val nextStep: String
    val skills: String
    val aiGenerate: String
    val crop: String
    val removeBg: String
    val upscale: String
    val snapCenter: String
    val snapH: String
    val snapV: String
    val mugHandleLeft: String
    val mugFront: String
    val mugHandleRight: String
    val mugHint: String
    val freeDesign: String
    val freeDesignDesc: String
    val selectSubstrate: String
    val selectSubstrateDesc: String
    val makeSimilar: String
    val noDevice: String
    val selectShapeTitle: String
    val shapeCircle: String
    val shapeRectangle: String
    val aiSubtitle: String
    val aiPlaceholder: String
    val aiButtonGenerate: String
    fun aiCreditsBadge(credits: Int): String
    fun aiInsufficientCredits(required: Int, available: Int): String
    fun deviceReady(name: String): String
    fun draftFromCourse(title: String): String
    fun draftFromProject(title: String): String

    val elementsCatAll: String
    val elementsCatHalloween: String
    val elementsCatTrendy: String
    val elementsCatRelief: String
    val elementsCatShapes: String
    val searchElementsPlaceholder: String
    fun elementsPackHalloween(count: Int): String
    fun elementsPackTrendy(count: Int): String
    fun elementsPackRelief(count: Int): String
    val elementsPackShapes: String

    val tabCommunityAssets: String
    val tabLocalAlbum: String
    val importFromComputer: String
    val importFormatsHint: String

    val stickerPumpkinCat: String
    val stickerDarkSkull: String
    val stickerGlowCastle: String
    val stickerGhostBadge: String
    val stickerPopAvatar: String
    val stickerCyberLetters: String
    val stickerVintageGears: String
    val stickerShibaInu: String
    val stickerTactilePattern: String
    val stickerGoldEmblem: String
    val stickerAcrylicPrint: String
    val shapeCircularBase: String
    val shapeOctagonalFrame: String
    val shapeStarlightHalo: String
    val shapeCrestShield: String
    fun elementsNoMatch(query: String): String

    val sampleWeddingArt: String
    val sampleBridesmaidCard: String
    val sampleDarkGothic: String
    val sampleDancerSeal: String
    val sampleOctopusMug: String
    val sampleStainedGlassBat: String
    val sampleCyberSkull: String
    val sampleWitchCatDecor: String
    val sampleMoonlightWitch: String
    val sampleFloralSkeleton: String
    val sampleGothicHalloween: String
    val sampleHalloweenDoor: String
    val albumPhoto1: String
    val albumGlowRelief: String
    val albumBadge: String
    val albumVintageArt: String
    val albumSelfieTransfer: String
    val albumMacroTexture: String
}

interface DeviceStrings {
    val devicesTitle: String
    val connectedDevice: String
    val statusIdle: String
    val statusBusy: String
    val statusOffline: String
    val statusPaused: String
    val statusPrinting: String
    val statusUvCuring: String
    val statusPreparing: String
    val statusCompleted: String
    val emergencyStop: String
    val laserTemp: String
    val bedTemp: String
    val bedTempLabel: String
    val fanSpeed: String
    val homing: String
    val moveZUp: String
    val moveZDown: String
    val maintenance: String
    val deviceFirmware: String
    val connectDevice: String
    val disconnectDevice: String
    val deviceSerial: String
    val laserHead: String
    val workArea: String
    val wifiConnected: String
    val uvLedLamp: String
    val rotaryModule: String
    val installedReady: String
    val executingJob: String
    val defaultJobName: String
    val sixColorInk: String
    val inkSubtext: String
    val maintenanceCenterSubtitle: String
    val enterMaintenance: String
    val wastePadStatusOk: String
    val chamberControl: String
    val alignmentLight: String
    val alignmentLightOn: String
    val alignmentLightOff: String
    val airFiltration: String
    val airFiltrationActive: String
    val airFiltrationIdle: String
    val stopJobTitle: String
    val stopJobDesc: String
    val confirmStop: String
    val continueJob: String
    fun jobProgress(p: Int): String
    fun timeRemaining(time: String): String
    fun timeFormat(mins: Int, secs: Int): String
    fun secsOnly(secs: Int): String
    fun wastePadUsage(p: Int): String
}

interface ProfileStrings {
    val profileTitle: String
    val userNick: String
    val userRole: String
    val drafts: String
    val favorites: String
    val myCreations: String
    val settings: String
    val language: String
    val currentLangName: String
    val themeMode: String
    val themeDark: String
    val themeLight: String
    val cloudSync: String
    val aboutApp: String
    val appVersion: String
    val selectLanguage: String
    val langZh: String
    val langEn: String
    val emptyDraftsHint: String
    val justEdited: String
}

interface CommunityStrings {
    val inspirationsTitle: String
    val searchPlaceholder: String
    val tabFeatured: String
    val tabTrending: String
    val tabLaser: String
    val tab3D: String
    val tabContest: String
    val comments: String
    val writeComment: String
    val contestBannerTitle: String
    val contestJoinNow: String
    val contestDeadline: String
    val creatorProfile: String
    val follow: String
    val following: String
    val noCommentsYet: String
    val writeCommentPlaceholder: String
    val followers: String
    val followingTab: String
    val likesAndCollects: String
    val tabCreations: String
    val tabCollections: String
    val tabLiked: String
    val noPublicCreations: String
    val recommendedDevice: String
    val bookmark: String
    val bookmarked: String
    val catAll: String
    val cat3DTexture: String
    val catHoliday: String
    val catGifts: String
    val catArtistDaily: String
    val tagAll: String
    val tagHalloween: String
    val tag3DRelief: String
    val tagAcrylic: String
    val tagMug: String
    val tagGloss: String
    fun likesCount(count: Int): String
    fun commentsTitle(count: Int): String
    fun forkCount(count: Int): String
    fun commentCount(count: Int): String
    fun compatibleSubstrate(substrate: String): String
}

interface SubstrateStrings {
    val substrateTitle: String
    val material: String
    val thickness: String
    val dimensions: String
    val width: String
    val length: String
    val unit: String
    val color: String
    val presetSize: String
    val customSize: String
    val woodBasswood: String
    val woodWalnut: String
    val acrylicClear: String
    val acrylicBlack: String
    val leatherCowhide: String
    val metalAluminum: String
    val dualToneBoard: String
    val materialDescWood: String
    val materialDescAcrylic: String
    val materialDescLeather: String
    val materialDescMetal: String
    val matAcrylic: String
    val matWood: String
    val matLeather: String
    val matMetal: String
    val matPvc: String
    val matCeramic: String
    val matGlass: String
    val createDesign: String
}

interface DialogStrings {
    val notifications: String
    val academy: String
    val maintenanceTitle: String
    val maintenanceCleanLens: String
    val maintenanceLubricate: String
    val maintenanceFilter: String
    val maintenanceCenterTitle: String
    val maintenanceComplete: String
    val maintenanceCompleteDetail: String
    val wasteInkTank: String
    val wasteInkStatusGood: String
    fun wasteInkLevel(percent: Int): String
    val cleanPrintheadTask: String
    val cleanPrintheadBtn: String
    val flushInkTask: String
    val flushInkBtn: String
    val academyTitle: String
    val academySubtitle: String
    val loadCourseProject: String
    val skillsTitle: String
    val skillTokyoNeon: String
    val skillVintageComic: String
    val skillWoodCarving: String
    val skillStainedGlass: String
    val skillClayMiniature: String
    val creatorLinkCopied: String
    val markAllRead: String
    val notifAll: String
    val notifLikes: String
    val notifComments: String
    val notifSystem: String
    val noNotifications: String
    val contestEnded: String
    val prizeRewards: String
    val prize1: String
    val prize2: String
    val prize3: String
    val submissionNotesTitle: String
    val submissionNotesPlaceholder: String
    val confirmSubmitEntry: String
    val submitSuccess: String
    val submitSuccessDetail: String
    fun maintenanceStep(step: Int, total: Int): String
    fun executingTask(task: String): String
    fun maintenanceInProgress(seconds: Int): String
    fun durationMinutes(mins: Int): String
    fun skillDepth(depth: Float): String
    fun directMessageChannel(name: String): String
    fun officialContest(prize: String): String
    fun daysRemaining(days: Long): String
    fun submissionCount(count: Int): String
}

interface PointsStrings {
    val pointsHubTitle: String
    val availableBalance: String
    val pts: String
    val aiReliefCredits: String
    val tabDailyTasks: String
    val tabRewardsMall: String
    val dailyCheckIn: String
    val checkInReward: String
    val claimed: String
    val checkIn: String
    val taskCenter: String
    val completed: String
    val goTask: String
    val redeemAssets: String
    val redeemed: String
    val redeem: String
    val soldOut: String
    val lowPoints: String
    val resetMockData: String
    fun pointsCost(pts: Int): String
    fun taskReward(pts: Int): String
}

interface OtaStrings {
    val newVersionFound: String
    val downloadingUpdate: String
    val downloadComplete: String
    val readyToInstallDesc: String
    val updateNotesTitle: String
    val defaultNotes: String
    val packageSize: String
    fun packageSizeMb(mb: Long): String
    val updateNow: String
    val installNow: String
    val later: String
    val retry: String
    val cancelDownload: String
    val checkUpdate: String
    val upToDateTitle: String
    val upToDateDesc: String
    val ok: String
    val updateErrorTitle: String
}
