package fernandocostagomes.routes

const val versionConst = "/v1"
const val idConst = "id"
const val invalidConst = "Invalid ID"
const val emailConst = "email"

const val addressConst = "$versionConst/address"
const val addressMoreIdConst = "$versionConst/address/{id}"

const val groupConst = "$versionConst/group"
const val groupMoreIdConst = "$versionConst/group/{id}"

const val parameterConst = "$versionConst/parameter"
const val parameterMoreIdConst = "$versionConst/parameter/{id}"

const val userConst = "$versionConst/user"
const val userMoreIdConst = "$versionConst/user/{id}"
const val userEmail = "$versionConst/email/{email}"
const val userUpdate = "$versionConst/update/{pwd}"

const val actionConst = "$versionConst/action"
const val actionMoreIdConst = "$versionConst/action/{id}"

const val permissionConst = "$versionConst/permission"
const val permissionMoreIdConst = "$versionConst/permission/{id}"

const val roleConst = "$versionConst/role"
const val roleMoreIdConst = "$versionConst/role/{id}"

const val loginConst = "$versionConst/login"
const val helloConst = "$versionConst/hello"

const val cgdConst = "$versionConst/cgd"

const val playerConst = "$cgdConst/player"
const val playerMoreIdConst = "$cgdConst/player/{id}"

const val tribeConst = "$cgdConst/tribe"
const val tribeMoreIdConst = "$cgdConst/tribe/{id}"

const val gameConst = "$cgdConst/game"
const val gameMoreIdConst = "$cgdConst/game/{id}"

const val punctuationConst = "/cgd/punctuation"
const val punctuationMoreIdConst = "/cgd/punctuation/{id}"

const val playerTribeConst = "$cgdConst/playerTribe"
const val playerTribeMoreIdConst = "$cgdConst/playerTribe/{id}"